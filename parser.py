import csv
import os
import glob
import statistics

with open('dataset.csv', mode='w') as dataset:
    
    durate = []

    mapping = {"B":0,"W":1}
    for filename in glob.glob('games/*.txt'):
        games = open(filename, "r").read()
        turns = games.count("FINE: Stato:")
        durate.append(turns)
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
                                state.append(int(char == "B"))
                                state.append(int(char == "W"))
                                state.append(int(char == "K"))
                    file.readline()
                    #True se la mossa è del bianco
                    state.append(int(file.readline()[0] == "B"))
                    treshold = 1
                    state.append((evaluation/turns)**(turns**2/treshold)*winner)
                    #state.append((evaluation/turns)*winner)
                    dataset_writer.writerow(state)
                first = False
"""        
    print("durata media:"+str(statistics.mean(durate)))              
    print("max:"+str(max(durate)))
    print("min:"+str(min(durate)))

    n = 0
    for i in range(len(durate)):
        if durate[i] <= 26:
            n+=1

    print(str(n) + " su " + str(len(durate)))
    #print(durate)
"""
                    