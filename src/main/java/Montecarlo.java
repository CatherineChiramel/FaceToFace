import de.upb.isml.thegamef2f.engine.Move;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class implementing Monte Carlo Tree Search
 */
public class Montecarlo {
    /**
     *  {@link GameMC} The instance of game which can be used to get the rules and legal moves
     */
    protected GameMC gameMC;
    /**
     *  The square of the bias parameter in the UCB1 algorithm; defaults to 2
     */
    protected Integer ucb1Param = 2;
    /**
     *  The map that maps states to nodes in the Monte Carlo tree.
     */
    protected Map<Integer, MontecarloNode> nodeMap;
    /**
     * Constructor
     * @param gameMC Instatnce of game for MCTS simulations
     * @param ucb1Param The UCB1 explore parameter
     */
    public Montecarlo( GameMC gameMC, Integer ucb1Param){
        this.gameMC = gameMC;
        this.ucb1Param = ucb1Param;
        this.nodeMap = new HashMap<>();
    }
    /**
     * Create dangling nodes if the state is non-existent
     * @param stateMC The state from which the dangling nodes are to be created
     */
    public void makeNode(GameStateMC stateMC){
        if(!this.nodeMap.containsKey(stateMC.hashCode())){
            List<Move> unexpandedPlays = this.gameMC.legalPlays(stateMC);
            MontecarloNode node = new MontecarloNode(null, null, stateMC, unexpandedPlays);
            this.nodeMap.put(stateMC.hashCode(), node);
        }
    }
    /**
     * Selection phase of MCTS.
     * Select until node is not fully expanded or until a leaf node is reached
     * @param stateMC The root state to start selection from
     * @return {@link MontecarloNode} The selected node
     */
    public MontecarloNode select(GameStateMC stateMC){
        MontecarloNode node = this.nodeMap.get(stateMC.hashCode());
        while (node.isFullyExpanded() && !node.isLeaf()){
            List<Move> plays = node.allPlays();
            Move bestPlay = null;
            double bestUCB1 = -99999;
            for(Move play: plays){
                double childUCB1 = node.childNode(play).getUCB1(this.ucb1Param);
                if(childUCB1 > bestUCB1) {
                    bestPlay = play;
                    bestUCB1 = childUCB1;
                }
            }
            node = node.childNode(bestPlay);
        }
        return node;
    }
    /**
     * Expansion phase of MCTS
     * For the given node, expand a random unexpanded child node
     * @param  node {@link MontecarloNode} The node from which expansion has to be done
     * @return {@link MontecarloNode} The expanded child node
     */
    public MontecarloNode expand(MontecarloNode node){
        List<Move> plays = node.unexpandedPlays();
        int index = (int) Math.floor(Math.random() * plays.size());
        Move play = plays.get(index);
        this.gameMC.nextStateAfterMove(node.stateMC,play);
        GameStateMC childState;
        if(false){
            childState = this.gameMC.getGameState(false);
        }
        else{
            childState = this.gameMC.getGameState(true);
        }
        List<Move> childUnexpandedPlays = this.gameMC.legalPlays(childState);
        MontecarloNode childNode = node.expand(play, childState, childUnexpandedPlays);
        this.nodeMap.put(childState.hashCode(), childNode);
        return childNode;
    }
    /**
     * Simulation phase of MCTS
     * From given node, play the game until a terminal state
     * @param node {@link MontecarloNode} The node from which MCTS simulations has to start from
     * @return Integer denoting the winner
     */
    public Integer simulate(MontecarloNode node){
        Integer win1 = null;
        if(this.gameMC.hasPlayer1Won())
            win1 = 1;
        else if (this.gameMC.hasPlayer2Won())
            win1 = 2;
        while(win1 == null){
            Move moveOfPlayer = null;
            try {
                List<Move> plays = this.gameMC.legalPlays(node.stateMC);
                Move play = plays.get((int)Math.floor(Math.random() * plays.size()));
                moveOfPlayer = play;
            } catch (Exception var7) {
                System.err.println("Exception in move of player : " + var7.getMessage());
            }
            if(node.stateMC.isPlayer(true)) {
                if (!this.gameMC.isMoveValid(moveOfPlayer, true)) {
                    win1 = 2;
                }
                this.gameMC.applyMove(moveOfPlayer, true);
                if (this.gameMC.hasPlayer1Won()) {
                    win1 = 1;
                }
            }
            else {
                if (!this.gameMC.isMoveValid(moveOfPlayer, false)) {
                    win1 = 1;
                }
                this.gameMC.applyMove(moveOfPlayer, false);
                if (this.gameMC.hasPlayer2Won()) {
                    win1 = 2;
                }
            }
        }
        return win1;
    }
    /**
     * Backpropagation phase of MCTS
     * From given node, propagate plays and winner to ancestors' statistics
     * @param node {@link MontecarloNode} The node to backpropagate from (leaf node)
     * @param winner The winner to propagate
     */
    public void backpropogate(MontecarloNode node, Integer winner){
        while(node != null){
            node.nPlays +=1;
            boolean win;
            if(winner == 1)
                win = true;
            else
                win = false;
            if(node.stateMC.isPlayer(win)){
                node.nWins +=1;
            }
            node = node.parentNode;
        }
    }
    /**
     * From the available statistics, calculate the best move from the given state
     * @param stateMC {@link GameStateMC} The state to get the best play from
     * @param policy
     * @return {@link Move} The best move in the given state
     */
    public Move bestPlay( GameMC gameMC, GameStateMC stateMC, String policy){
        this.makeNode(stateMC);
        if(this.nodeMap.get(stateMC.hashCode()).isFullyExpanded() == false){
            System.err.println("Not enough information");
        }
        MontecarloNode node = this.nodeMap.get(stateMC.hashCode());
        List<Move> allPlays = node.allPlays();
        Move bestplay = null;
        if(policy.equals("robust")){
            int max = -999999;
            for(Move play: allPlays){
                MontecarloNode childNode = node.childNode(play);
                if(childNode != null){
                    if(childNode.nPlays > max){
                        bestplay = play;
                        max = childNode.nPlays;
                        //bestplay = gameMC.legalPlays(stateMC).get(1);
                    }
                } else {
                    bestplay = gameMC.legalPlays(stateMC).get(1);
                }
            }
        } else if(policy.equals("max")) {
            double max = -99999;
            for(Move play: allPlays){
                MontecarloNode childNode = node.childNode(play);
                double ratio = childNode.nWins / childNode.nPlays;
                if(ratio > max){
                    bestplay = play;
                    max = ratio;
                }
            }
        }
        return bestplay;
    }
    /**
     * From given state, run as many simulations as possible until the time limit, building statistics
     * @param stateMC {@link GameStateMC} The state to run the search from
     * @param timeout The time to run the simulations for, in seconds
     * @return {@link MontecarloStatistics} Search statistics
     */
    public MontecarloStatistics runSearch(GameStateMC stateMC, Integer timeout){
        this.makeNode(stateMC);
        Integer draws = 0;
        Integer totalSims = 0;
        long end = System.currentTimeMillis() + timeout * 1000;
        while(System.currentTimeMillis() < end){
            MontecarloNode node = this.select(stateMC);
            Integer winner = null;
            if(this.gameMC.hasPlayer1Won())
                winner = 1;
            else if (this.gameMC.hasPlayer2Won())
                winner = 2;
            if(node.isLeaf() && winner == null){
                node = this.expand(node);
                winner = this.simulate(node);
            }
            if(winner == null){
                winner = 2;
            }
            this.backpropogate(node, winner);
            if(winner == 0)
                draws ++;
            totalSims ++;
        }
        MontecarloStatistics stats = new MontecarloStatistics(timeout, totalSims, draws);
        return stats;
    }

//    public StateStats getStats(GameStateMC stateMC) {
//        MontecarloNode node = this.nodeMap.get(stateMC.hashCode());
//        ChildStats childStat;
//        StateStats stats = new StateStats(node.nPlays, node.nWins, new ArrayList<>());
//        for(Integer childKey: node.childNodeMap.keySet()){
//            if(node.childNodeMap.get(childKey) == null){
//                childStat = new ChildStats(node.childPlayMap.get(childKey), null, null);
//                stats.children.add(childStat);
//            }
//            else {
//                childStat = new ChildStats(node.childPlayMap.get(childKey), node.nPlays, node.nWins);
//                stats.children.add(childStat);
//            }
//
//        }
//        return stats;
//    }
}
