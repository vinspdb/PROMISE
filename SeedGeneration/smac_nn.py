from smac.facade.smac_bo_facade import SMAC4BO
import logging
import warnings
import numpy as np
from ConfigSpace.hyperparameters import CategoricalHyperparameter, UniformFloatHyperparameter
from smac.configspace import ConfigurationSpace
from smac.scenario.scenario import Scenario
from sklearn import preprocessing
from keras.layers.core import Dense
from keras.optimizers import Nadam
from keras.layers import Input, BatchNormalization, Embedding, LSTM
from keras.models import Model
from keras.callbacks import EarlyStopping, ReduceLROnPlateau
from time import perf_counter
from smac.facade.hyperband_facade import HB4AC
import pandas as pd
import numpy as np
import time
from datetime import datetime
seed = 123
np.random.seed(seed)
from tensorflow import set_random_seed
set_random_seed(seed)
import utility as ut

def get_model(cfg):
    size_act = (unique_events + 1) // 2
    input_act = Input(shape=(lenght_seq,), dtype='int32', name='input_act')
    x_act = Embedding(output_dim=size_act, input_dim=unique_events+1, input_length=lenght_seq)(input_act)
    layer_l = LSTM(units=int(cfg["lstmsize1"]), kernel_initializer='glorot_uniform',return_sequences=True)(x_act)
    layer_l = BatchNormalization()(layer_l)
    layer_l = LSTM(units=int(cfg["lstmsize2"]), kernel_initializer='glorot_uniform',return_sequences=False)(layer_l)
    layer_l = BatchNormalization()(layer_l)

    out = Dense(n_classes, activation='softmax')(layer_l)
    opt = Nadam(lr=cfg['learning_rate_init'], beta_1=0.9, beta_2=0.999, epsilon=1e-08, schedule_decay=0.004,
                clipvalue=3)
    model = Model(inputs=input_act, outputs=out)

    model.compile(optimizer=opt, loss='categorical_crossentropy', metrics=['acc'])
    model.summary()
    return model


def fit_and_score(cfg):
    print(cfg)
    outfile2 = open("smac_"+namedataset+ ".txt", 'a')
    start_time = perf_counter()
    model = get_model(cfg)
    early_stopping = EarlyStopping(monitor='val_loss', patience=20)
    lr_reducer = ReduceLROnPlateau(monitor='val_loss', factor=0.5, patience=10, verbose=0, mode='auto',
                                   min_delta=0.0001, cooldown=0, min_lr=0)

    h = model.fit(X_train,Y_train, epochs=200, verbose=0, validation_split=0.2, callbacks=[early_stopping, lr_reducer], batch_size=cfg['batch_size'])
    scores = [h.history['val_loss'][epoch] for epoch in range(len(h.history['loss']))]
    score = min(scores)
    end_time = perf_counter()
    global best_score, best_model, best_time, best_numparameters
    ff = open("best_temp.txt", "r")
    best_score = float(ff.read())
    print("best_score->", best_score)
    print("score->", score)
    if best_score > score:
        best_score = score
        best_model = model
        outfile_temp = open("best_temp.txt", 'w')
        outfile_temp.write(str(best_score))
        outfile_temp.close()
        best_numparameters = model.count_params()
        best_time = end_time - start_time
        print("BEST SCORE", best_score)
        best_model.save("generate_"+namedataset+".h5")

    outfile2.write(str(score)+";"+str(len(h.history['loss']))+";"+str(model.count_params())+";"+str(end_time - start_time)+";"+ str(cfg['lstmsize1'])+";"+str(cfg['lstmsize2'])+";"+str(cfg['batch_size'])+";"+str(cfg['learning_rate_init'])+"\n")
    return score

namedataset = 'hospital'
lenght_seq = 4

# model selection
print('Starting model selection...')
best_score = np.inf
best_model = None
best_time = 0
best_numparameters = 0
outfile_temp = open("best_temp.txt", 'w')
outfile_temp.write(str(np.inf))
outfile_temp.close()

logger = logging.getLogger(namedataset)
logging.basicConfig(level=logging.INFO)

#To illustrate different parameter types,
#we use continuous, integer and categorical parameters.
cs = ConfigurationSpace()

# We can add multiple hyperparameters at once:
lstmsize1 = CategoricalHyperparameter("lstmsize1", [8, 16, 32], default_value=16)
lstmsize2 = CategoricalHyperparameter("lstmsize2", [8, 16, 32], default_value=16)
batch_size = CategoricalHyperparameter("batch_size", [512, 1024], default_value=512)
learning_rate_init = UniformFloatHyperparameter('learning_rate_init', 0.00001, 0.01, default_value=0.001, log=True)
cs.add_hyperparameters([lstmsize1, lstmsize2, batch_size, learning_rate_init])

# SMAC scenario object
# Scenario object
scenario = Scenario({"run_obj": "quality",  # we optimize quality (alternatively runtime)
                         "runcount-limit": 20,  # max. number of function evaluations;
                         "cs": cs,  # configuration space
                         "deterministic": "true",
                         "abort_on_first_run_crash": "false"
                         })

# train views
print("<--------------------------------->")
df_fold = pd.read_csv(namedataset+'.txt', header=0, sep=',') #,encoding='windows-1252')
cont_trace = df_fold['case:concept:name'].value_counts(dropna=False)
max_trace = max(cont_trace)
unique_events = df_fold['concept:name'].nunique()
listOfevents = df_fold['concept:name'].unique()
listOfeventsInt = list(range(1, unique_events + 1))
mapping = dict(zip(listOfevents, listOfeventsInt))
print(mapping)

df_fold['concept:name'] = [mapping[item] for item in df_fold['concept:name']]
map_inv = dict(map(reversed,mapping.items()))
#print("map inv---------------")
print(map_inv)

if namedataset == 'bpic2012':
    n_act = 4
elif namedataset == 'bpic2018':
    n_act = 2
elif namedataset == 'bpic2019':
    n_act = 2
elif namedataset == 'hospital':
    n_act = 3
elif namedataset == 'road':
    n_act = 3
elif namedataset == 'sepsis':
    n_act = 2

act = df_fold.groupby('case:concept:name', sort=False).agg({'concept:name': lambda x: list(x)})

#generate prefix trace
X_train, y_train_prefix = ut.get_sequence(act, max_trace, lenght_seq, n_act)

le = preprocessing.LabelEncoder()
y_train_prefix = le.fit_transform(y_train_prefix)
num_classes = le.classes_.size

X_train = np.asarray(X_train)

from keras.utils import to_categorical
Y_train = to_categorical(y_train_prefix)

n_classes = len(np.unique(y_train_prefix))
print(n_classes)

max_iters = 200
    # print("Default Value: %.2f" % def_value)
intensifier_kwargs = {'initial_budget': 20, 'max_budget': max_iters, 'eta': 3}
# Optimize, using a SMAC-object
print("Optimizing! Depending on your machine, this might take a few minutes.")
smac = HB4AC(scenario=scenario,
                 rng=np.random.RandomState(42),
                 tae_runner=fit_and_score,
                 intensifier_kwargs=intensifier_kwargs
                 )
incumbent = smac.optimize()