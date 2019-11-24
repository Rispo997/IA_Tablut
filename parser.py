import csv
import os
import glob
import statistics

with open('dataset.csv', mode='w') as dataset:
    
    camps = [3, 4, 5, 13, 27, 35, 36, 37, 43, 44, 45, 53, 67, 75, 76, 77]
    escapes = [1, 2, 6, 7, 9, 17, 18, 26, 54, 62, 63, 71, 73, 74, 78, 79]
    boardSize = 9
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
                    for i in range(boardSize):  
                        line = file.readline()
                        for j in range(len(line)):
                            char = line[j]
                            if char != "\n":
                                state.append(int(char == "B"))
                                state.append(int(char == "W"))
                                state.append(int(char == "K"))
                                state.append(int(char != "B" and char != "W" and char != "K"))
                                state.append(int((i*boardSize+j) in camps))
                                state.append(int((i*boardSize+j) in escapes))
                    file.readline()
                    #True se la mossa è del bianco
                    state.append(int(file.readline()[0] == "B"))
                    treshold = 15
                    state.append((evaluation/turns)**(turns/treshold)*winner)
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
                    