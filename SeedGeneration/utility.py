import numpy as np

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

def get_sequence(sequence, max_trace, mean_trace, n_act):
    i = 0
    s = (max_trace)
    list_seq = []
    list_label = []
    while i < len(sequence):
        list_temp = []
        seq = np.zeros(s)
        j = 0
        while j < (len(sequence[i]) - 1):
            list_temp.append(sequence[i][j])
            new_seq = np.append(seq, list_temp)
            cut = len(list_temp)
            new_seq = new_seq[cut:]
            if (np.count_nonzero(new_seq[-mean_trace:]))<n_act:
                pass
            else:
                list_seq.append(new_seq[-mean_trace:])
                list_label.append(sequence[i][j + 1])
            j = j + 1
        i = i + 1
    return list_seq, list_label
