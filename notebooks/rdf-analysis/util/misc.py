

def get_sequences(filename, sep=" ", min_len=1):
    for line in open(filename, "r"):
        sequence = line.split(sep)
        sequence[-1] = sequence[-1].replace("\n", "")
        if len(sequence) >= min_len:
            yield sequence
            
def get_types(filename, sep="\t"):
    typeof = dict()
    for line in open(filename, "r"):
        splitted_line = line.split(sep)
        typeof[splitted_line[0]] = splitted_line[1].replace("\n", "")
    return typeof