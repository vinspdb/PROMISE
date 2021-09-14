import numpy as np

def get_sequence(prova, max_trace, mean_trace, n_act):
    i = 0
    s = (max_trace)
    list_seq = []
    list_label = []
    while i < len(prova):
        list_temp = []
        seq = np.zeros(s)
        j = 0
        while j < (len(prova.iat[i, 0]) - 1):
            list_temp.append(prova.iat[i, 0][0 + j])
            new_seq = np.append(seq, list_temp)
            cut = len(list_temp)
            new_seq = new_seq[cut:]
            if (np.count_nonzero(new_seq[-mean_trace:]))<n_act:
                pass
            else:
                list_seq.append(new_seq[-mean_trace:])
                list_label.append(prova.iat[i, 0][j + 1])
            j = j + 1
        i = i + 1
    return list_seq, list_label