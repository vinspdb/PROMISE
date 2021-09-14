import pandas as pd
import SM_exe as SM
import pm4py
from pm4py.algo.evaluation.replay_fitness import evaluator as replay_fitness_evaluator
from pm4py.algo.evaluation.precision import evaluator as precision_evaluator
import os
from time import perf_counter



import os

def run_precision(log, net, initial_marking, final_marking):
    precision = precision_evaluator.apply(log, net, initial_marking, final_marking, variant=precision_evaluator.Variants.ALIGN_ETCONFORMANCE)
    return precision

def run_fitness(log, net, initial_marking, final_marking):
    fitness = replay_fitness_evaluator.apply(log, net, initial_marking, final_marking,variant=replay_fitness_evaluator.Variants.ALIGNMENT_BASED)
    return fitness

if __name__ == '__main__':
    dataset = 'road'
    log = pd.read_csv("eventlog/"+dataset+'.csv', sep=',')
    print('[v] log loaded')

    import os
    path, dirs, files = next(os.walk('log_'+dataset+'_seed_maxlen/single_seed/'))
    file_count = len(files)
    print(file_count)

    j = 0
    num_of_proto = file_count
    check = False
    checkLstar = False
    subLstar = False
    global_bestfmeasure = 0

    list_time = []
    #############convert prototype to dataframe
    z = 0
    list_proto = []
    while z < num_of_proto:
        data_temp = pd.read_csv('log_' + dataset + '_seed_maxlen/single_seed/proto' + str(z+1) + '.txt', sep=',')
        list_proto.append(data_temp)
        z = z + 1
    ##############

    single_seed_proto = list_proto
    ind_p = 1
    best_fmeasure = 0
    best_diff = 0

    while checkLstar == False:
        if subLstar == True:
            best = list_proto[bestproto]
            zz = 0
            new_list_proto = []
            while zz < len(single_seed_proto):
                if (str(single_seed_proto[zz]['case:concept:name'].iloc[0]) in (list(best['case:concept:name'].unique())))==False:
                    temp = pd.concat([best, single_seed_proto[zz]], ignore_index=True, sort=False)
                    new_list_proto.append(temp)
                else:
                    print('prototype is in!')
                zz = zz + 1
            list_proto = new_list_proto
            if list_proto == []:
                check=True
                checkLstar=True
            else:
                check = False
                ind_p = ind_p + 1
        index = 0
        while check == False:
            if index >len(list_proto)-1:
                check = True
                subLstar = True
            else:
                start_time = perf_counter()
                log_4_model = list_proto[index]
                log_4_model.rename(columns={'CaseID': 'case:concept:name', 'event': 'concept:name'}, inplace=True)

                from pm4py.objects.log.exporter.xes import exporter as xes_exporter
                xes_exporter.apply(log_4_model, dataset+"_"+str(index)+'_'+str(ind_p)+".xes")

                SM.mining_structure(dataset+"_"+str(index)+'_'+str(ind_p))

                bpmn_graph = pm4py.read_bpmn(dataset+"_"+str(index)+'_'+str(ind_p)+".bpmn")

                from pm4py.objects.conversion.bpmn import converter as bpmn_converter
                net, initial_marking, final_marking = bpmn_converter.apply(bpmn_graph)

                print("QUALITY METRICS PROTOTYPE->", index + 1)
                precision = run_precision(log, net, initial_marking, final_marking)
                fitness = run_fitness(log, net, initial_marking, final_marking)

                print("END fitness and precision")

                fmeasure = 2 * ((round((precision), 2) * round((fitness['averageFitness']), 2)) / (
                        round((precision), 2) + round((fitness['averageFitness']), 2)))

                print("PRECISION %.2f" % precision)
                print("FITNESS %.2f" % fitness['averageFitness'])
                print("FMEASURE %.2f" % fmeasure)
                end_time = perf_counter()
                print("TOTAL Time")
                diff = end_time - start_time
                print(diff)


                if fmeasure>best_fmeasure:
                    best_fmeasure = fmeasure
                    bestproto = index
                    bestlog = list_proto[index]
                    best_time = diff

            index = index + 1
        if list_proto != []:
            if best_fmeasure > global_bestfmeasure:
                global_bestfmeasure = best_fmeasure
                print('******************* EVALUATION TERMINATE - Create Subgroup *******************')
                list_time.append(best_time)
            else:
                checkLstar = True
                bestlog.to_csv(dataset+'bestsubLstar_SM.csv', index=False)
                print("BEST FMEASURE->", global_bestfmeasure)
                print('******************* EVALUATION TERMINATE *******************')


    print(list_time)



