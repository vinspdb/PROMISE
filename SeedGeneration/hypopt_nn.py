from tensorflow.keras.layers import Dense, Dropout, Flatten, Embedding, LSTM, BatchNormalization
from tensorflow.keras.optimizers import Nadam
from tensorflow.keras.layers import Input, concatenate
from tensorflow.keras.models import Model
from tensorflow.keras.callbacks import EarlyStopping, ReduceLROnPlateau
from hyperopt import Trials, STATUS_OK, tpe, fmin, hp
import hyperopt
from time import perf_counter
import utility as ut
from sklearn import preprocessing
import tensorflow as tf
import numpy as np
from read_file import SeedGeneneration
seed = 123
np.random.seed(seed)
tf.random.set_seed(seed)

class NN_hyp:
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
        self.dim = 0

    def get_model(self, cfg):
        size_view = (self.view_size + 1) // 2
        input_act = Input(shape=(self._seq_length, ), dtype='int32', name='input_act')
        x = Embedding(output_dim=size_view, input_dim=self.view_size + 1, input_length=self._seq_length)(input_act)

        layer_l = tf.keras.layers.LSTM(units=int(cfg["lstmsize1"]), kernel_initializer='glorot_uniform', return_sequences=True)(x)
        layer_l = BatchNormalization()(layer_l)
        layer_l = tf.keras.layers.LSTM(units=int(cfg["lstmsize2"]), kernel_initializer='glorot_uniform', return_sequences=False)(layer_l)
        layer_l = BatchNormalization()(layer_l)

        out = Dense(self.n_classes, activation='softmax')(layer_l)
        opt = Nadam(learning_rate=cfg['learning_rate_init'], beta_1=0.9, beta_2=0.999, epsilon=1e-08, schedule_decay=0.004,
                    clipvalue=3)
        model = Model(inputs=input_act, outputs=out)
        model.compile(optimizer=opt, loss='categorical_crossentropy', metrics=['acc'])

        return model

    def fit_and_score(self, cfg):
        print(cfg)
        start_time = perf_counter()

        model = self.get_model(cfg)
        early_stopping = EarlyStopping(monitor='val_loss', patience=20)
        lr_reducer = ReduceLROnPlateau(monitor='val_loss', factor=0.5, patience=10, verbose=0, mode='auto',
                                       min_delta=0.0001, cooldown=0, min_lr=0)

        h = model.fit(self.list_act_view_train, self.y_train, epochs=200, verbose=0, validation_split=0.2, callbacks=[early_stopping, lr_reducer],
                      batch_size=cfg['batch_size'])

        scores = [h.history['val_loss'][epoch] for epoch in range(len(h.history['loss']))]
        score = min(scores)
        print(score)

        end_time = perf_counter()

        if self.best_score > score:
            self.best_score = score
            self.best_model = model
            self.best_numparameters = model.count_params()
            self.best_time = end_time - start_time
            self.best_model.save("models/"+self._event_log+"model.h5")


        return {'loss': score, 'status': STATUS_OK, 'n_epochs': len(h.history['loss']),
                'n_params': model.count_params(),
                'time': end_time - start_time}

    def create_training_set(self):
        lenght_seq = 4

        obj = SeedGeneneration(self._event_log)
        df_fold, max_trace, mapping, invmap = obj.import_log()
        self.view_size = df_fold['concept:name'].nunique()

        act = df_fold.groupby('case:concept:name', sort=False).agg({'concept:name': lambda x: list(x)})

        i = 0
        list_act = []
        while i < (len(act)):
            list_act.append(ut.remove_consecutive_duplicates(act.iat[i, 0])+[self.view_size])
            i = i + 1

        obj = SeedGeneneration(self._event_log)
        seed = obj.extract_seed(df_fold)
        n_act = len(seed[0])
        print('len n_act',(n_act))

        # generate prefix trace
        X_train, y_train_prefix = ut.get_sequence(list_act, max_trace, lenght_seq, n_act)
        self.dim = len(X_train)

        le = preprocessing.LabelEncoder()
        y_train_prefix = le.fit_transform(y_train_prefix)

        self.list_act_view_train = np.asarray(X_train)
        self.y_train = tf.keras.utils.to_categorical(y_train_prefix)
        self.n_classes = len(np.unique(y_train_prefix))
        return le

    def hyp_opt(self):
            # model selection
            print('Starting model selection...')
            self.best_score = np.inf
            self.best_model = None
            self.best_time = 0
            self.best_numparameters = 0
            
            outfile = open(self._event_log + '.log', 'w')

            # model selection
            print('Starting model selection...')
            self.create_training_set()
            
            if self.dim > 200000:
                space = {'lstmsize1': hp.choice('lstmsize1', [8, 16, 32]),
                         'lstmsize2': hp.choice('lstmsize2', [8, 16, 32]),
                         'batch_size': hp.choice("batch_size", [256, 512, 1024]),
                         'learning_rate_init': hp.loguniform("learning_rate_init", np.log(0.00001), np.log(0.01)),
                         }
            else:
                space = {'lstmsize1': hp.choice('lstmsize1', [8, 16, 32]),
                         'lstmsize2': hp.choice('lstmsize2', [8, 16, 32]),
                         'batch_size': hp.choice("batch_size", [32, 64, 128, 256, 512, 1024]),
                         'learning_rate_init': hp.loguniform("learning_rate_init", np.log(0.00001), np.log(0.01)),
                         }

            trials = Trials()
            best = fmin(self.fit_and_score, space, algo=tpe.suggest, max_evals=20, trials=trials,
                        rstate=np.random.RandomState(seed))
            best_params = hyperopt.space_eval(space, best)

            outfile.write("\nHyperopt trials")
            outfile.write(
                "\ntid,loss,learning_rate,batch_size,time,n_epochs,n_params,perf_time,lstm1,lstm2")
            for trial in trials.trials:
                outfile.write("\n%d,%f,%f,%d,%s,%d,%d,%f,%d,%d" % (trial['tid'],
                                                                         trial['result']['loss'],
                                                                         trial['misc']['vals']['learning_rate_init'][0],
                                                                         trial['misc']['vals']['batch_size'][0],
                                                                         (trial['refresh_time'] - trial[
                                                                             'book_time']).total_seconds(),
                                                                         trial['result']['n_epochs'],
                                                                         trial['result']['n_params'],
                                                                         trial['result']['time'],
                                                                         trial['misc']['vals']['lstmsize1'][0],
                                                                         trial['misc']['vals']['lstmsize2'][0]
                                                                         ))
            outfile.write("\n\nBest parameters:")
            print(best_params, file=outfile)
            outfile.write("\nModel parameters: %d" % self.best_numparameters)
            outfile.write('\nBest Time taken: %f' % self.best_time)
            outfile.close()
