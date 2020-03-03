# The Game: Face To Face using MCTS
This is the implementation of an Artificial Intelligent agent which plays the card game
called The Game: Face To Face. The agent uses Monte Carlo Tree Search to play the game.

## Approach
A detailed explanation of Monte Carlo Tree Search for games is given in the article 
[General Game-Playing With Monte Carlo Tree Search](https://medium.com/@quasimik/monte-carlo-tree-search-applied-to-letterpress-34f41c86e238).
The implementation of the agent for this game was inspired by the aforementioned article. MCTS models a game as a tree. 
The nodes represent the states of the game and the edges represents the moves applied on the state to get to another state. 
The task is to find the best move possible in a state from the set of all legal moves. The tree representing a game can grow
 larger and larger as the number of available moves in a state increases. MCTS performs sampling of the available moves to 
 effectively deal with large trees. It does not try all, but many of the paths to find the best move. 
 As it tries new paths, it gains more knowledge about which paths are good and should be tried next.
 
 For this Monte Carlo does multiple simulations of the game from a given state until a winner is obtained. These are called 
 Monte Carlo simulations. The best move to be applied in a given state is obtained after many such simulations. 
 A state in our game consists of information about the current hand cards of the player, the set of cards in ascending 
 card piles of both the players and the set of cards in the descending card piles of both the players. From the game state 
 of the current player, the state of the opponent is assumed. These information are used to perform MCTS from the given state.
 
 The game states constitute the nodes in the tree created by MCTS. The edge in the tree corresponds to a legal move applicable 
 in that state. The successor node of a node denotes the resulting state after applying the move. 
 MCTS has 4 main phases: Selection, Expansion, Simulation and Back-propagation. These four phases of MCTS is implemented in the 
 [Montecarlo.java](https://github.com/CatherineChiramel/FaceToFace/blob/master/src/main/java/Montecarlo.java).
 
 ### Selection
 From a given node a successor node has to be selected from the available successor nodes. 
 This selection process is applied recursively until a leaf node is reached. The node with highest UCB1 
 value is selected as the successor node.
 
 
 UCB1<sub>i</sub> = w<sub>i</sub>i / s<sub>i</sub> + c*sqrt(ln s<sub>p</sub> / s<sub>i</sub>)
 
 UCB1<sub>i</sub> = UCB1 value for node i
 
 w<sub>i</sub> = number of wins for node i
 
 s<sub>i</sub> = number of simulations of node i
 
 s<sub>p</sub> = number of simulations of parent node
 
 c = exploration parameter
 
 ### Expansion
 If there are multiple unexpanded moves, we choose a move randomly and compute the child node 
 which corresponds to the next state.
 
 ### Simulation
 From the new node that was created during expansion phase, the game is played until it 
 finishes and a winner emerges.
 
 ### Backpropagation
 After the simulation phase, the s<sub>i</sub> and w<sub>i</sub> values for all the visited nodes are updated. 
 This information is used to calculate the UCB1 value of each of these nodes.
 
 
 
## Execution
The .jar file for the game engine is already added in the project directory of this repo. But in case if it is not enough, make sure to download the .jar game engine and add the corresponding dependency to pom.xml file. The code is written in Java 11.
