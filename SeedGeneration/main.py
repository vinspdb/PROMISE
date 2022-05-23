from read_file import SeedGeneneration
from hypopt_nn import NN_hyp
from smac_nn import NN_smac
import numpy as np
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import Model, load_model
from os import path, makedirs
import argparse

if __name__ == "__main__":
        
        parser = argparse.ArgumentParser(description='Coupling next activity prediction with process discovery.')

        parser.add_argument('-opt', type=int, help="optimizer: smac-hypopt")
        parser.add_argument('-event_log', type=str, help="Event log name")

        args = parser.parse_args()
        log_name = args.event_log
        opt = args.opt
        folder_results = path.join('log_' + log_name + '_seed')
        folder_models = path.join('models')
        makedirs(folder_models, exist_ok = True)
        makedirs(folder_results, exist_ok = True)
        obj = SeedGeneneration(log_name)
        log, max_trace, mapping, invmap = obj.import_log()#import log

        seed = obj.extract_seed(log) #extrac seed for summary
        if opt == 'smac':
            le = NN_smac(log_name).create_training_set() #train neural
            NN_smac(log_name).smac_opt()
        else:
            le = NN_hyp(log_name).create_training_set() #train neural
            NN_hyp(log_name).hyp_opt()

        n_act = len(seed[0])
        model = load_model('models/'+ log_name + 'model.h5')

        seed = pad_sequences(seed, maxlen=4, padding='pre', dtype=int)
        seed = seed.tolist()

        i = 0
        list_prediction = []
        while i < len(seed):
            list_seed_temp = seed[i]
            check = False
            list_pred = []
            checklen = n_act
            if invmap.get('END') in list_seed_temp:
                check = True
                print('seed->',i,' complete')
            else:
                while check == False:
                    y_pred = model.predict([list_seed_temp])
                    y_pred = y_pred.argmax(axis=1)
                    y_pred = le.inverse_transform([y_pred])
                    if y_pred == [invmap.get('END')]:
                        check = True
                    elif checklen == max_trace:
                        check = True
                    list_pred.append(y_pred)
                    list_seed_temp = np.append(list_seed_temp, y_pred)
                    list_seed_temp = np.delete(list_seed_temp, 0)
                    list_seed_temp = list_seed_temp.tolist()
                    checklen = checklen + 1
                print('seed->',i,' complete')
            list_prediction.append(list_pred)
            i = i + 1
        print('end seed generation')
        list_prediction = np.array(list_prediction)


        i = 0
        list_trace = []
        while i < len(list_prediction):
            prototype = list_prediction[i]
            startseed = seed[i]
            trace = np.append(startseed, prototype)
            trace = np.array(trace)
            trace = trace[trace != 0]
            trace = [mapping.get(item) for item in trace]
            list_trace.append(trace)
            i = i + 1

        k = 0
        while k < len(list_trace):
            case_proto = k + 1
            file_proto = open("log_" + log_name + '_seed/proto' + str(case_proto) + '.txt', 'w')
            file_proto.write('CaseID,event\n')
            if 'START' in list_trace[k]:
                list_trace[k].remove('START')
            if 'END' in list_trace[k]:
                list_trace[k].remove('END')
            z = 0
            while z < len(list_trace[k]):
                file_proto.write("Case" + str(case_proto) + "," + str(list_trace[k][z]) + "\n")
                z = z + 1
            file_proto.close()
            k = k + 1
