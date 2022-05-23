import pandas as pd
import IM_exe as IM
from pm4py.objects.petri_net.importer import importer as pnml_importer
from pm4py.objects.conversion.log import converter as log_converter
from time import perf_counter
from pm4py.algo.evaluation.replay_fitness import evaluator as replay_fitness_evaluator
from pm4py.algo.evaluation.precision import evaluator as precision_evaluator
from pm4py.objects.log.importer.xes import importer as xes_importer

import os

def run_precision(log, net, initial_marking, final_marking):
    precision = precision_evaluator.apply(log, net, initial_marking, final_marking, variant=precision_evaluator.Variants.ALIGN_ETCONFORMANCE)
    return precision

def run_fitness(log, net, initial_marking, final_marking):
    fitness = replay_fitness_evaluator.apply(log, net, initial_marking, final_marking,variant=replay_fitness_evaluator.Variants.ALIGNMENT_BASED)
    return fitness

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Inductive Miner')

    parser.add_argument('-event_log', type=str, help="Event log name")
    args = parser.parse_args()
    
    dataset = args.event_log    #read event log
    log = xes_importer.apply('../SeedGeneration/XES/'+dataset+'.xes')
    print('[v] log loaded')

    #number of prototypes
    path, dirs, files = next(os.walk('../SeedGeneration/log_'+dataset+'_seed/'))
    file_count = len(files)

    #initialize variables
    j = 0
    num_of_proto = file_count
    check = False
    checkLstar = False
    subLstar = False
    global_bestfmeasure = 0

    #convert prototype to dataframe
    z = 0
    list_proto = []
    while z < num_of_proto:
        data_temp = pd.read_csv('../SeedGeneration/log_' + dataset + '_seed/proto' + str(z+1) + '.txt', sep=',')
        list_proto.append(data_temp)
        z = z + 1

    single_seed_proto = list_proto
    ind_p = 1
    best_fmeasure = 0

    #Feed forward process discovery
    while checkLstar == False:
        if subLstar == True:
            best = list_proto[bestproto]
            zz = 0
            new_list_proto = []
            while zz < len(single_seed_proto):
                if (str(single_seed_proto[zz]['case:concept:name'].iloc[0]) in (list(best['case:concept:name'].unique()))) == False:
                    temp = pd.concat([best, single_seed_proto[zz]], ignore_index=True, sort=False)
                    new_list_proto.append(temp)
                else:
                    print('prototype is in!')
                zz = zz + 1
            list_proto = new_list_proto

            if list_proto == []:
                check = True
                checkLstar = True
            else:
                check = False
                ind_p = ind_p + 1

        index = 0
        while check == False:
            if index > len(list_proto) - 1:
                check = True
                subLstar = True
            else:
                start_time = perf_counter()
                log_4_model = list_proto[index]
                log_4_model.rename(columns={'CaseID': 'case:concept:name', 'event': 'concept:name'}, inplace=True)
                log_4_model = log_converter.apply(log_4_model, variant=log_converter.Variants.TO_EVENT_LOG)

                from pm4py.objects.log.exporter.xes import exporter as xes_exporter

                xes_exporter.apply(log_4_model, dataset + "-" + str(index) + '-' + str(ind_p) + ".xes")
                add_class = '<classifier name="Event Name" keys="concept:name"/>'

                file = open(dataset + "-" + str(index) + '-' + str(ind_p) + ".xes", 'a')
                with open(dataset + "-" + str(index) + '-' + str(ind_p) + ".xes", "r") as in_file:
                    buf = in_file.readlines()

                with open(dataset + "-" + str(index) + '-' + str(ind_p) + ".xes", "w") as out_file:
                    for line in buf:
                        if line == '<log xes.version="1849-2016" xes.features="nested-attributes" xmlns="http://www.xes-standard.org/">\n':
                            line = line + add_class + "\n"
                        elif line == '<log>\n':
                            line = line + add_class + "\n"
                        out_file.write(line)

                IM.mining_structure(dataset + "-" + str(index) + '-' + str(ind_p))

                net, initial_marking, final_marking = pnml_importer.apply(
                    dataset + "-" + str(index) + '-' + str(ind_p) + '.pnml')

                try:
                    print("QUALITY METRICS PROTOTYPE->", index + 1)
                    precision = run_precision(log, net, initial_marking, final_marking)
                    fitness = run_fitness(log, net, initial_marking, final_marking)

                    print("END fitness and precision")

                    fmeasure = 2 * ((round((precision), 2) * round((fitness['averageFitness']), 2)) / (
                            round((precision), 2) + round((fitness['averageFitness']), 2)))
                    print("FMEASURE %.2f" % fmeasure)
                except:
                    fitness = 0
                    precision = 0
                    fmeasure = 0
                end_time = perf_counter()
                print("TOTAL Time")
                diff = end_time - start_time
                print(diff)

                print(fmeasure)
                print(best_fmeasure)
                if fmeasure > best_fmeasure:
                    best_fmeasure = fmeasure
                    bestproto = index
                    bestlog = list_proto[index]
                    best_time = diff

            index = index + 1
        if list_proto != []:
            if best_fmeasure > global_bestfmeasure:
                global_bestfmeasure = best_fmeasure
                print('******************* EVALUATION TERMINATE - Create Subgroup *******************')

            else:
                checkLstar = True
                bestlog.to_csv(dataset + 'bestsubLstar_IM.csv', index=False)
                print('******************* EVALUATION TERMINATE *******************')
