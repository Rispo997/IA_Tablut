import csv
import os
import glob
with open('dataset.csv', mode='w') as dataset:
    
    mapping = {"O":0,"B":543,"W":88,"K":52,"T":2,"D":10}
    for filename in glob.glob('*.txt'):
        games = open(filename, "r").read()
        turns = games.count("FINE: Stato:")
        winner = -1 if games.count("Nero vince") else 1 if games.count("Bianco vince") else 0   
        dataset_writer = csv.writer(dataset,lineterminator = '\n', delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
        file = open(filename, "r")
        first = True
        evaluation = 0
        for line in file: 
            if "FINE: Stato:" in line:
                evaluation += 1
                if not first:
                    state = []
                    for i in range(9):  
                        line = file.readline()
                        for char in line:
                            if char != "\n":
                                state.append(mapping[char])
                    file.readline()
                    state.append(mapping[file.readline()[0]])
                    state.append(evaluation/turns*winner)
                    dataset_writer.writerow(state)
                first = False
               
                    
                    
                    