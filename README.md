# The Game: Face To Face using MCTS
This is the implementation of an Artificial Intelligent agent which plays the card game
called The Game: Face To Face. The agent uses Monte Carlo Tree Search to play the game.

##Approach
A detailed explanation of Monte Carlo Tree Search for games is given in the article 
[General Game-Playing With Monte Carlo Tree Search](https://medium.com/@quasimik/monte-carlo-tree-search-applied-to-letterpress-34f41c86e238).
The implementation of the agent for this game was inspired by the aforementioned article. MCTS models a game as a tree. 
The nodes represent the states of the game and the edges represents the moves applied on the state to get to another state. 
The task is to find the best move possible in a state from the set of all legal moves. The tree representing a game can grow
 larger and larger as the number of available moves in a state increases. MCTS performs sampling of the available moves to 
 effectively deal with large trees. It does not try all, but many of the paths to find the best move. 
 As it tries new paths, it gains more knowledge about which paths are good and should be tried next.
 
 For this Monte Carlo does multiple simulations of the game from a given state until a winner is obtained. These are called 
 Monte Carlo simulations. The best move to be applied in a given state is obtained after many such simulations. MCTS has 4 main steps: 
 Selection, Expansion, Simulation and Back-propagation. These four phases of MCTS is implemented in the 
 [Montecarlo.java](https://github.com/CatherineChiramel/FaceToFace/blob/master/src/main/java/Montecarlo.java).
 
##Execution
This source code can be executed by simply executing the Main() method.