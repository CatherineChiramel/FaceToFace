
import de.upb.isml.thegamef2f.engine.Move;
import de.upb.isml.thegamef2f.engine.Placement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MontecarloNode {
    protected MontecarloNode parentNode;
    protected Move lastMove;
    protected GameStateMC stateMC;
    protected List<Move> unexpandedPlays;
    Integer nPlays, nWins;
    Map<Integer, Move> childPlayMap;
    Map<Integer, MontecarloNode> childNodeMap;

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
        //System.out.println("inside the montecarlonode constructor"+ unexpandedPlays.size());
    }

    public  MontecarloNode childNode(Move play){
        MontecarloNode child = this.childNodeMap.get(play.hashCode());
        if(child == null){
            System.out.println("Child is not expanded");
        }
        return child;
    }

    public MontecarloNode expand(Move play, GameStateMC childState, List<Move> unexpandedPlays){
        if(!this.childPlayMap.containsKey(play.hashCode())){
            System.out.println("No such play");
            return null;
        }
        MontecarloNode childNode = new MontecarloNode(this, play, childState, unexpandedPlays);
        this.childPlayMap.put(play.hashCode(), play);
        this.childNodeMap.put(play.hashCode(), childNode);
        return childNode;
    }

    public List<Move> allPlays() {
        List<Move> plays = new ArrayList<Move>();
        for(Integer key: this.childPlayMap.keySet()){
            plays.add(childPlayMap.get(key));
        }
        return plays;
    }

    public List<Move> unexpandedPlays(){
        List<Move> plays = new ArrayList<Move>();
        for(Integer key: this.childPlayMap.keySet()){
            if(this.childNodeMap.get(key) == null)
                plays.add(childPlayMap.get(key));
        }
        return  plays;
    }

    public boolean isFullyExpanded(){
        for(Integer key: this.childNodeMap.keySet()){
            if(this.childNodeMap.get(key) == null){
                return false;
            }
        }
        return true;
    }

    public boolean isLeaf(){
        if(this.childNodeMap.isEmpty()){
            return true;
        }
        return false;
    }

    public double getUCB1(double biasParam){
        return (this.nPlays/this.nWins) + Math.sqrt(biasParam * Math.log(this.parentNode.nPlays)/this.nPlays);
    }
}
