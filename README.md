# OOP-Task-4
In this project we managed to build a Pokemon Go Program that trys to capture as many pokemons as it can on given graph

##The Game

This is the Frontend of the Project
![front](/Ex4/pics/pokefront.png)

## how to use
the main function is in <a href=/Ex4/src/ex4_java_client/StudentCode.java>StudentCode.java</a> and run in the Folder threw cmd this command:
you can enter to [Release](/Release) directory and there run the function for Pokemon Go:
before running our pokemon app you must start the server: 
Note! please make sure your internet connection is on
```
java -jar java -jar Ex4_Server_v0.0.jar 0
```
(the number at the end is the case num - can be [0-15])

to activate our app(client):
```
java /Ex4/src/ex4_java_client/StudentCode.java
```

## The Structure 
this is the UML design for the Project:

![uml4](/Ex4/pics/uml4.png)

### Explenation about the Classes

there are some interesting classes in here:
#### interfaces
- DirectedWeightedGraph - the weighted graph
- DirectedWeightedGraphAlgorithm - the algorithms that ca be applied on a directed weughted graph
- NodeData - node class
- EdgeData - edge class
- GeoLocation - point in 3d

#### The communication
- Client - a given code that connects to the server of the game
- StudentCode - the code that runs our program - the most important part runs 2 threads and one main thread.
Every 100 miliseconds the program runs and make a move while the project is running calculating and show us the graph

#### Implementations
- Agent - a class of agent contain all of its data from the server
- Agents - a class that has all the agents
- Edge, Node - implementations of the parts of the graph
- Pkemon, pokemons - the pokemons as individual and ias list
- GraphAlgorithm - all the algorithms on a certian graph

#### Simulator
- FrameAlgo - the main window of the GUI
- GraphZone - the Class that buils the graph presentation
- Canvas - a component that deals with all the Graph Presentations


