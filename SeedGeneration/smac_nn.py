import logging
import numpy as np
from ConfigSpace.hyperparameters import CategoricalHyperparameter, UniformFloatHyperparameter
from smac.configspace import ConfigurationSpace
from smac.scenario.scenario import Scenario
from sklearn import preprocessing
from tensorflow.keras.layers import Dense
from tensorflow.keras.optimizers import Nadam
from tensorflow.keras.layers import Input, concatenate, BatchNormalization, LSTM, Reshape, Embedding
from tensorflow.keras.models import Model
from tensorflow.keras.callbacks import EarlyStopping, ReduceLROnPlateau
from time import perf_counter
import pickle
from smac.facade.hyperband_facade import HB4AC
from read_file import SeedGeneneration
import utility as ut
import tensorflow as tf
seed = 123
np.random.seed(seed)
tf.random.set_seed(seed)
from ezprogress.progressbar import ProgressBar

class NN_smac:
    def __init__(self, eventlog):
        self._event_log = eventlog
        self._seq_length = 4
        self.list_act_view_train = []
        self.y_train = []
        self.n_classes = 0
        self.best_score = np.inf
        self.best_model = None
        self.best_time = 0
        self.best_numparameters = 0
        self.view_size = 0
        self.steps_needed = 10
        self.current_step = 0
        self.pb = ProgressBar(self.steps_needed, bar_length=100)
        self.dim = 0

    def get_model(self, cfg):
        size_view = self.view_size + 1 // 2
        input_act = Input(shape=(self._seq_length,), dtype='int32', name='activity')
        x = Embedding(output_dim=size_view, input_dim=self.view_size + 1, input_length=self._seq_length)(input_act)

        layer_l = LSTM(units=int(cfg["lstmsize1"]), kernel_initializer='glorot_uniform',
                       return_sequences=True)(x)
        layer_l = BatchNormalization()(layer_l)
        layer_l = LSTM(units=int(cfg["lstmsize2"]), kernel_initializer='glorot_uniform',
                       return_sequences=False)(layer_l)
        layer_l = BatchNormalization()(layer_l)

        out = Dense(self.n_classes, activation='softmax')(layer_l)
        opt = Nadam(learning_rate=cfg['learning_rate_init'], beta_1=0.9, beta_2=0.999, epsilon=1e-08, schedule_decay=0.004,
                    clipvalue=3)
        model = Model(inputs=input_act, outputs=out)
        model.compile(optimizer=opt, loss='categorical_crossentropy', metrics=['acc'])

        return model


    def fit_and_score(self, cfg):
        print(cfg)
        outfile2 = open(self._event_log + ".txt", 'a')
        start_time = perf_counter()
        model = self.get_model(cfg)
        early_stopping = EarlyStopping(monitor='val_loss', patience=20)
        lr_reducer = ReduceLROnPlateau(monitor='val_loss', factor=0.5, patience=10, verbose=0, mode='auto',
                                       min_delta=0.0001, cooldown=0, min_lr=0)

        h = model.fit(self.list_act_view_train,
            self.y_train, epochs=200, verbose=0, validation_split=0.2, callbacks=[early_stopping, lr_reducer],
            batch_size=cfg['batch_size'])

        scores = [h.history['val_loss'][epoch] for epoch in range(len(h.history['loss']))]
        score = min(scores)
        end_time = perf_counter()
        print(score)
        if self.best_score > score:
            self.best_score = score
            self.best_model = model
            self.best_numparameters = model.count_params()
            self.best_time = end_time - start_time
            print("BEST SCORE", self.best_score)
            self.best_model.save("models/"+self._event_log+"model.h5")
        outfile2.write(str(score)+";"+str(len(h.history['loss']))+";"+str(model.count_params())+";"+str(end_time - start_time)+";"+ str(cfg['lstmsize1'])+";"+str(cfg['lstmsize2'])+";"+str(cfg['batch_size'])+";"+str(cfg['learning_rate_init'])+"\n")
        # Increment counter
        self.current_step += 1
        self.pb.update(self.current_step)
        return score


    def create_training_set(self):
        lenght_seq = 4

        obj = SeedGeneneration(self._event_log)
        df_fold, max_trace, mapping, invmap = obj.import_log()
        print(mapping)
        print(invmap)
        self.view_size = df_fold['concept:name'].nunique()
        print(self.view_size)

        act = df_fold.groupby('case:concept:name', sort=False).agg({'concept:name': lambda x: list(x)})
        i = 0
        list_act = []
        while i < (len(act)):
            list_act.append(ut.remove_consecutive_duplicates(act.iat[i, 0])+[(self.view_size+1)])
            i = i + 1

        obj = SeedGeneneration(self._event_log)
        seed = obj.extract_seed(df_fold)
        n_act = len(seed[0])
        print('len n_act', (n_act))

        # generate prefix trace
        X_train, y_train_prefix = ut.get_sequence(list_act, max_trace, lenght_seq, n_act)
        self.dim = len(X_train)
        le = preprocessing.LabelEncoder()
        y_train_prefix = le.fit_transform(y_train_prefix)

        self.list_act_view_train = np.asarray(X_train)
        print(self.list_act_view_train.shape)
        self.y_train = tf.keras.utils.to_categorical(y_train_prefix)
        self.n_classes = len(np.unique(y_train_prefix))
        return le

    def smac_opt(self):
            # model selection
            print('Starting model selection...')
            self.best_score = np.inf
            self.best_model = None
            self.best_time = 0
            self.best_numparameters = 0
            
            logger = logging.getLogger(self._event_log + "_fold_")
            logging.basicConfig(level=logging.INFO)

            # Build Configuration Space which defines all parameters and their ranges.
            # To illustrate different parameter types,
            # we use continuous, integer and categorical parameters.
            cs = ConfigurationSpace()
            self.create_training_set()

            # We can add multiple hyperparameters at once:
            lstmsize1 = CategoricalHyperparameter("lstmsize1", [8, 16, 32])
            lstmsize2 = CategoricalHyperparameter("lstmsize2", [8, 16, 32])
            if self.dim > 200000:
                batch_size = CategoricalHyperparameter("batch_size", [256, 512, 1024])
            else:
                batch_size = CategoricalHyperparameter("batch_size", [32, 64, 128, 256, 512, 1024])

            learning_rate_init = UniformFloatHyperparameter('learning_rate_init', 0.00001, 0.01, default_value=0.001,
                                                            log=True)
            cs.add_hyperparameters([lstmsize1, lstmsize2, batch_size, learning_rate_init])

            # SMAC scenario object
            # Scenario object
            scenario = Scenario({"run_obj": "quality",  # we optimize quality (alternatively runtime)
                                 "runcount-limit": 10,  # max. number of function evaluations;
                                 "cs": cs,  # configuration space
                                 "deterministic": "true",
                                 "abort_on_first_run_crash": "false",
                                 "output_dir": self._event_log

                                 })


            self.pb.start()
            max_iters = 200
            intensifier_kwargs = {'initial_budget': 20, 'max_budget': max_iters, 'eta': 3}
            print("Optimizing! Depending on your machine, this might take a few minutes.")
            smac = HB4AC(scenario=scenario,
                         rng=np.random.RandomState(42),
                         tae_runner=self.fit_and_score,
                         intensifier_kwargs=intensifier_kwargs
                        )

            smac.optimize()



