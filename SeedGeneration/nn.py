import pandas as pd
import numpy as np
import time
from datetime import datetime
seed = 123
np.random.seed(seed)
from tensorflow import set_random_seed
set_random_seed(seed)
from keras.layers import Embedding, Dense, Reshape, BatchNormalization, SpatialDropout1D, Dropout
from keras.models import Model
from keras.optimizers import Nadam
from keras.callbacks import EarlyStopping, ReduceLROnPlateau
from keras.layers import Input, LSTM
import numpy as np
from sklearn import preprocessing
import os
from keras.preprocessing.sequence import pad_sequences
import utility as ut

namedataset = "bpic2012"

df_fold = pd.read_csv('clearlog/'+namedataset+'.csv', header=0, sep=',') #,encoding='windows-1252')
cont_trace = df_fold['case:concept:name'].value_counts(dropna=False)
max_trace = max(cont_trace)

unique_events = df_fold['concept:name'].nunique()
listOfevents = df_fold['concept:name'].unique()
listOfeventsInt = list(range(1, unique_events + 1))
mapping = dict(zip(listOfevents, listOfeventsInt))
#mapping activity to integer value
print(mapping)
df_fold['concept:name'] = [mapping[item] for item in df_fold['concept:name']]
map_inv = dict(map(reversed,mapping.items()))
#reverse mapping
print(map_inv)


# group by activity by caseid
cont_trace = df_fold['case:concept:name'].value_counts(dropna=False)
mean_trace = 4

#group by activity and timestamp by caseid
act = df_fold.groupby('case:concept:name', sort=False).agg({'concept:name': lambda x: list(x)})

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

#generate prefix trace
X_train, y_train_prefix = ut.get_sequence(act, max_trace, mean_trace, n_act)

le = preprocessing.LabelEncoder()
y_train_prefix = le.fit_transform(y_train_prefix)
num_classes = le.classes_.size
print('Target')
print(list(le.classes_))

X_train = np.asarray(X_train)

from keras.utils import to_categorical
Y_train = to_categorical(y_train_prefix)

n_classes = len(np.unique(y_train_prefix))
print(n_classes)

input_diff2 = Input(shape=(mean_trace,), dtype='float32', name='input_diff2')
diff2 = Reshape((mean_trace, 1))(input_diff2)

size_act = (unique_events + 1) // 2
input_act = Input(shape=(mean_trace,), dtype='int32', name='input_act')
x_act = Embedding(output_dim=size_act, input_dim=unique_events+1, input_length=mean_trace)(input_act)

layer_in = x_act
layer_l = LSTM(16, return_sequences=True, kernel_initializer='glorot_uniform')(layer_in)#LSTM(units=100,  kernel_initializer='glorot_uniform', return_sequences=True, implementation=2)(x_act)
layer_l = BatchNormalization()(layer_l)
layer_l = LSTM(16, return_sequences=False, kernel_initializer='glorot_uniform')(layer_l)#LSTM(units=100, kernel_initializer='glorot_uniform', return_sequences=False, implementation=2)(layer_l)
layer_l = BatchNormalization()(layer_l)

outsize = n_classes
output = Dense(outsize, activation='softmax', name='act_output')(layer_l)
model = Model(inputs= input_act, outputs=output)

print(model.summary())
opt = Nadam(lr=0.0001)
model.compile(loss={'act_output': 'categorical_crossentropy'}, optimizer=opt, metrics=['accuracy'])
early_stopping = EarlyStopping(monitor='val_loss', patience=20)
lr_reducer = ReduceLROnPlateau(monitor='val_loss', factor=0.5, patience=10, verbose=0, mode='auto',
                                   min_delta=0.0001, cooldown=0, min_lr=0)

model.fit(X_train, Y_train, epochs=200, batch_size=512, verbose=1, callbacks=[early_stopping, lr_reducer], validation_split=0.2)

model.save("model/generate_"+namedataset+".h5")
print('END TRAINING')