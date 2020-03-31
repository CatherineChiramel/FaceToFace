
import de.upb.isml.thegamef2f.engine.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a node in the Monte Carlo search tree.
 */
public class MontecarloNode {
    /**
     * {@link MontecarloNode} The parent node of the current node
     */
    protected MontecarloNode parentNode;
    /**
     * {@link Move} The move that resulted in this node
     */
    protected Move lastMove;
    /**
     * {@link GameStateMC} The state of the current node
     */
    protected GameStateMC stateMC;
    /**
     * List of legal {@link Move} in the current state which has not been performed yet
     */
    protected List<Move> unexpandedPlays;
    /**
     *  The number of moves and wins of the node. Required for updating monte carlo statistics
     */
    Integer nPlays, nWins ;
    /**
     * A map that maps an integer to a {@link Move}
     */
    Map<Integer, Move> childPlayMap;
    /**
     * A map that maps an integer associated with a move to the child node resulting from the move
     */
    Map<Integer, MontecarloNode> childNodeMap;

    /**
     * Constructor used to create a Monte Carlo tree node
     * @param parentNode
     * @param lastMove
     * @param stateMC
     * @param unexpandedPlays
     */
    public MontecarloNode(MontecarloNode parentNode, Move lastMove, GameStateMC stateMC, List<Move> unexpandedPlays) {
        childPlayMap = new HashMap<>();
        childNodeMap = new HashMap<>();
        this.parentNode = parentNode;
        this.lastMove = lastMove;
        this.stateMC = stateMC;
        this.unexpandedPlays = unexpandedPlays;
        this.nPlays = 0;
        this.nWins = 0;

        for(Move play: unexpandedPlays){

            childPlayMap.put(play.hashCode(),play);
            childNodeMap.put(play.hashCode(),null);
        }
    }

    /**
     * Get the child node resulting from applying the given play
     * @param play
     * @return {@link MontecarloNode} child node
     */
    public  MontecarloNode childNode(Move play){
        MontecarloNode child = this.childNodeMap.get(play.hashCode());
        return child;
    }

    /**
     * Apply the specified move and return the child node resulting from it
     * Add the node to the map associated with play {@link Move} and child nodes
     *
     * @param play The play to expand
     * @param childState The child state corresponding to the given play
     * @param unexpandedPlays The given child's unexpanded child plays; typically all of them
     * @return {@link MontecarloNode} child node
     */
    public MontecarloNode expand(Move play, GameStateMC childState, List<Move> unexpandedPlays){
        if(!this.childPlayMap.containsKey(play.hashCode())){
            return null;
        }
        MontecarloNode childNode = new MontecarloNode(this, play, childState, unexpandedPlays);
        this.childPlayMap.put(play.hashCode(), play);
        this.childNodeMap.put(play.hashCode(), childNode);
        return childNode;
    }

    /**
     * Get all legal plays {@link Move} from this node
     * @return list of all plays {@link Move}
     */
    public List<Move> allPlays() {
        List<Move> plays = new ArrayList<Move>();
        for(Integer key: this.childPlayMap.keySet()){
            plays.add(childPlayMap.get(key));
        }
        return plays;
    }

    /**
     * Get all the unexpanded legal plays {@link Move} for this node
     * @return list of unexpanded plays {@link Move}
     */
    public List<Move> unexpandedPlays(){
        List<Move> plays = new ArrayList<Move>();
        for(Integer key: this.childNodeMap.keySet()){
            if(this.childNodeMap.get(key) == null)
                plays.add(childPlayMap.get(key));
        }
        return  plays;
    }

    /**
     * Check if the node is fully expanded
     * @return {@link Boolean}true if it is fully expanded else  {@link Boolean}false
     */
    public boolean isFullyExpanded(){
        for(Integer key: this.childNodeMap.keySet()){
            if(this.childNodeMap.get(key) == null){
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the node is a leaf node in the tree
     * @return {@link Boolean} true if it is a leaf node else return {@link Boolean} false
     */
    public boolean isLeaf(){
        for(Integer key: this.childNodeMap.keySet()) {
            if(this.childNodeMap.get(key) != null)
                return false;
         }
        return true;
    }

    /**
     * Get the UCB1 value for this node
     * @param biasParam The square of the bias parameter in the UCB1 algorithm, defaults to 2
     * @return The UCB1 value of this node
     */
    public double getUCB1(double biasParam){
        return (this.nWins/this.nPlays) + Math.sqrt(biasParam * Math.log(this.parentNode.nPlays)/this.nPlays);
    }
}
