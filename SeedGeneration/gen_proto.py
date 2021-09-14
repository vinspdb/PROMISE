from keras.models import load_model
import numpy as np
from sklearn import preprocessing
import pandas as pd
import utility as ut

namedataset = "bpic2012"

df_fold = pd.read_csv('clearlog/'+namedataset+'.csv', header=0, sep=',') #,encoding='windows-1252')
cont_trace = df_fold['case:concept:name'].value_counts(dropna=False)
max_trace = max(cont_trace)
print("MAX Trace", max_trace)

unique_events = df_fold['concept:name'].nunique()
listOfevents = df_fold['concept:name'].unique()
listOfeventsInt = list(range(1, unique_events + 1))
mapping = dict(zip(listOfevents, listOfeventsInt))
print(mapping)

df_fold['concept:name'] = [mapping[item] for item in df_fold['concept:name']]
map_inv = dict(map(reversed,mapping.items()))
print(map_inv)

cont_trace = df_fold['case:concept:name'].value_counts(dropna=False)
mean_trace = 4

list_seed = []
if namedataset == 'bpic2012':
    list_seed = [[1., 2., 3., 23.],
                 [1., 2., 3., 4.],
                 [1., 2., 3., 21.],
                 [1., 2., 3., 25.]]
    n_act = 4
elif namedataset == 'bpic2018':
    list_seed =  [[0., 0., 1., 2.],
                  [0., 0., 1., 3.]]
    n_act = 2
elif namedataset == 'bpic2019':
    list_seed =  [[0., 0., 1., 35.],
                  [0., 0., 1., 41.],
                  [0., 0., 1., 9.],
                  [0., 0., 1., 39.],
                  [0., 0., 1., 36.],
                  [0., 0., 1., 2.],
                  [0., 0., 1., 18.],
                  [0., 0., 1., 10.]]
    n_act = 2
elif namedataset == 'hospital':
    list_seed =  [[0., 1., 2., 6.],
                  [0., 1., 2., 8.],
                  [0., 1., 2., 19.],
                  [0., 1., 2., 10.],
                  [0., 1., 2., 12.],
                  [0., 1., 2., 3.],
                  [0., 1., 2., 17.],
                  [0., 1., 2., 2.],
                  [0., 1., 2., 4.],
                  [0., 1., 2., 11.]]
    n_act = 3
elif namedataset == 'road':
    list_seed =  [[0., 1., 2., 13.],
                  [0., 1., 2., 9.],
                  [0., 1., 2., 8.],
                  [0., 1., 2., 3.]]
    n_act = 3
elif namedataset == 'sepsis':
    list_seed =  [[0., 0., 1., 2.],
                  [0., 0., 1., 3.],
                  [0., 0., 1., 4.],
                  [0., 0., 1., 6.],
                  [0., 0., 1., 7.],
                  [0., 0., 1., 8.]]
    n_act = 2

#group by activity by caseid
act = df_fold.groupby('case:concept:name', sort=False).agg({'concept:name': lambda x: list(x)})
X_train, y_train_prefix = ut.get_sequence(act, max_trace, mean_trace, n_act)

le = preprocessing.LabelEncoder()
y_train_prefix = le.fit_transform(y_train_prefix)
num_classes = le.classes_.size
print('Target')
print(list(le.classes_))
X_train = np.asarray(X_train)

model = load_model('model/generate_'+namedataset+'.h5')

list_seed = np.array(list_seed)

i = 0
list_prediction = []
while i<len(list_seed):
        list_seed_temp = list_seed[i]
        check = False
        list_pred = []
        checklen = n_act
        while check==False:
            y_pred = model.predict([[np.array(list_seed_temp)]])
            y_pred = y_pred.argmax(axis=1)
            y_pred = le.inverse_transform([y_pred])
            if y_pred==[mapping.get('END')]:
                check = True
            elif checklen == max_trace:
                check = True
            list_pred.append(y_pred)
            list_seed_temp =  np.append(list_seed_temp, y_pred)
            list_seed_temp = np.delete(list_seed_temp, 0)
            checklen = checklen + 1
        list_prediction.append(list_pred)
        i = i + 1


list_prediction = np.array(list_prediction)

i = 0 
list_trace = []
while i<len(list_prediction):
    prototype = list_prediction[i]
    startseed = list_seed[i]
    trace = np.append(startseed, prototype)
    trace = np.array(trace)
    trace = trace[trace != 0]
    trace = [map_inv[item] for item in trace]
    list_trace.append(trace)

    i = i + 1

k = 0
while k<len(list_trace):
    case_proto = k + 1
    file_proto = open("log_"+namedataset+'_seed_maxlen/single_seed/proto'+str(case_proto)+'.txt', 'w')
    file_proto.write('CaseID,event\n')
    list_trace[k].remove('START')
    if 'END' in list_trace[k]:
        list_trace[k].remove('END')
    z = 0
    while z<len(list_trace[k]):
        file_proto.write("Case"+str(case_proto)+","+list_trace[k][z]+"\n")
        z = z + 1
    file_proto.close()
    k = k + 1