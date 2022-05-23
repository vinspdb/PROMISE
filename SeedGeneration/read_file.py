from pm4py.objects.conversion.log import converter as log_converter
from pm4py.objects.log.importer.xes import importer as xes_importer


class SeedGeneneration:
    def __init__(self, eventlog):
        self._eventlog = eventlog

    @staticmethod
    def all_same(items):
        return all(x == items[0] for x in items)

    @staticmethod
    def remove_consecutive_duplicates(trace):
        temp = []
        cont = 0
        i = 0
        while i<len(trace):
            if i == 0:
                temp.append(trace[i])
            else:
                if trace[i-1] == trace[i]:
                    cont = cont + 1
                    if cont < 2:
                        temp.append(trace[i])
                else:
                    cont = 0
                    temp.append(trace[i])
            i = i + 1
        return temp

    def import_log(self):
        log = xes_importer.apply('XES/' + self._eventlog + '.xes')
        dataframe = log_converter.apply(log, variant=log_converter.Variants.TO_DATA_FRAME)
        unique = dataframe['concept:name'].unique()
        mapping = {i+1: unique[i] for i in range(0, len(unique),1)}
        mapping[len(unique)+1] = 'END'
        invmap = {v: k for k, v in mapping.items()}
        dataframe['concept:name'] = [invmap[item] for item in dataframe['concept:name']]
        dataframe = dataframe[["case:concept:name", "concept:name"]]
        cont_trace = dataframe['case:concept:name'].value_counts(dropna=False)
        max_trace = max(cont_trace)

        return dataframe, max_trace, mapping, invmap

    def extract_seed(self, log):

        act = log.groupby('case:concept:name', sort=False).agg({'concept:name': lambda x: list(x)})

        i, j, length = 0, 0, 0
        check = True
        list_act = []
        unique = log['concept:name'].nunique()

        while i < (len(act)):
            list_act.append(self.remove_consecutive_duplicates(act.iat[i, 0])+[unique+1])
            i = i + 1


        while check==True:
            lst = [item[j] for item in list_act]
            if self.all_same(lst) == False:
                check = False
            else:
                length = length + 1
            j = j + 1

        seed = [item[:length+1] for item in list_act]
        unique_seed = [list(i) for i in set(tuple(i) for i in seed)]
        return unique_seed
