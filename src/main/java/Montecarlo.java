import de.upb.isml.thegamef2f.engine.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Montecarlo {
    protected GameMC gameMC;
    protected Integer ucb1Param = 2;
    protected Map<Integer, MontecarloNode> nodeMap;

    public Montecarlo( GameMC gameMC, Integer ucb1Param){
        this.gameMC = gameMC;
        this.ucb1Param = ucb1Param;
        this.nodeMap = new HashMap<>();
    }

    public void makeNode(GameStateMC stateMC){
        System.out.println("Inside makeNode Method");
        if(!this.nodeMap.containsKey(stateMC.hashCode())){
            //System.out.println("inside if");
            List<Move> unexpandedPlays = this.gameMC.legalPlays(stateMC);
            System.out.println("legalpalys are calculated");
            MontecarloNode node = new MontecarloNode(null, null, stateMC, unexpandedPlays);

            this.nodeMap.put(stateMC.hashCode(), node);
            //System.out.println("node has been created");
        }
    }

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

    public MontecarloNode expand(MontecarloNode node){
        List<Move> plays = node.unexpandedPlays();
        int index = (int) Math.floor(Math.random() * plays.size());
        Move play = plays.get(index);
        this.gameMC.nextStateAfterMove(node.stateMC,play);
        GameStateMC childState;
        if(node.stateMC.isPlayer(true)){
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

    public Integer simulate(MontecarloNode node){
        Integer win1 = null;
        //boolean win = false;
        //boolean winner = false;
//        if(this.gameMC.hasPlayer1Won() || this.gameMC.hasPlayer2Won()){
//            win = true;
//        }
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
           //         winner = false;
                }
                this.gameMC.applyMove(moveOfPlayer, true);
                if (this.gameMC.hasPlayer1Won()) {
                    win1 = 1;
             //       winner = true;
                }
            }
            else {
                if (!this.gameMC.isMoveValid(moveOfPlayer, false)) {
                    win1 = 1;
               //     winner = true;
                }
                this.gameMC.applyMove(moveOfPlayer, false);
                if (this.gameMC.hasPlayer2Won()) {
                    win1 = 2;
                 //   winner = false;
                }
            }
        }
        return win1;
    }

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

    public Move bestPlay(GameStateMC stateMC, String policy){
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
                if(childNode.nPlays > max){
                    bestplay = play;
                    max = childNode.nPlays;
                }
            }
        }else if(policy.equals("max")) {
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

    public MontecarloStatistics runSearch(GameStateMC stateMC, Integer timeout){
        System.out.println("Inside runsearch method, stateMC node has been created");
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
            System.out.println("inside runsearch before backpropogate");
            if(node.isLeaf() && winner == null){
                node = this.expand(node);
                winner = this.simulate(node);
            }

            this.backpropogate(node, winner);
            if(winner == 0)
                draws ++;
            totalSims ++;
        }
        MontecarloStatistics stats = new MontecarloStatistics(timeout, totalSims, draws);
        return stats;
    }

    public StateStats getStats(GameStateMC stateMC) {
        MontecarloNode node = this.nodeMap.get(stateMC.hashCode());
        ChildStats childStat;
        StateStats stats = new StateStats(node.nPlays, node.nWins, new ArrayList<>());
        for(Integer childKey: node.childNodeMap.keySet()){
            if(node.childNodeMap.get(childKey) == null){
                childStat = new ChildStats(node.childPlayMap.get(childKey), null, null);
                stats.children.add(childStat);
            }
            else {
                childStat = new ChildStats(node.childPlayMap.get(childKey), node.nPlays, node.nWins);
                stats.children.add(childStat);
            }

        }
        return stats;
    }
}
