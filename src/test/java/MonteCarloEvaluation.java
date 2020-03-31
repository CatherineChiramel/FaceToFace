import de.upb.isml.thegamef2f.engine.board.Game;
import de.upb.isml.thegamef2f.engine.player.Player;
import de.upb.isml.thegamef2f.engine.player.RandomPlayer;

import java.util.ArrayList;
import java.util.List;

public class MonteCarloEvaluation {

    /**
     * Playing the game a given number of times and find out how many games are won by the Monte Carlo player
     * @param number The number of times the game should be played
     */
    public void basicEvaluation(int number){
        int montecarloWin = 0;
        int randomWin = 0;
        for(int i=0; i<number; i++){
            RandomPlayer player1 = new RandomPlayer("a");
            MontecarloPlayer player2 = new MontecarloPlayer("b");
            Game game = new Game(player1, player2);
            Player winner = game.simulate();
            if(winner.equals(player1))
                randomWin ++;
            else
                montecarloWin ++;
        }
        System.out.println("The number of times random player won is: " + randomWin);
        System.out.println("The number of times monte carlo player won is: " + montecarloWin);

    }

    /**
     * Run the game the given number of times for each of the given UCB1 bias paramter
     * @param number of times the game should played
     * @param biasParameters The list of bias paramter values for which the games should be run
     */
    public void biasParameterEvaluation(int number, List<Integer> biasParameters) {

        for(int i=0; i<biasParameters.size(); i++){
            int montecarloWin = 0;
            int randomWin = 0;
            for(int j=0; j<number; j++){
                RandomPlayer player1 = new RandomPlayer("a");
                MontecarloPlayer player2 = new MontecarloPlayer("b", biasParameters.get(i));
                Game game = new Game(player1, player2);
                Player winner = game.simulate();
                if(winner.equals(player1))
                    randomWin ++;
                else
                    montecarloWin ++;
            }
            System.out.println("UCB1 parameter value: " + biasParameters.get(i));
            System.out.println("The number of times random player won is: " + randomWin);
            System.out.println("The number of times monte carlo player won is: " + montecarloWin);
            System.out.println();

        }
    }

    /**
     * Function to compare the performance of the player with Robust child nad Max child approach
     * @param number The number of times the game should be played
     */
    public void policyEvaluation(int number) {

        List<String> policies = new ArrayList<>();
        policies.add("robust");
        policies.add("max");
        for(int p=0; p<policies.size(); p++){
            int montecarloWin = 0;
            int randomWin = 0;
            for(int i=0; i<number; i++){
                RandomPlayer player1 = new RandomPlayer("a");
                MontecarloPlayer player2 = new MontecarloPlayer("b", policies.get(p));
                Game game = new Game(player1, player2);
                Player winner = game.simulate();
                if(winner.equals(player1))
                    randomWin ++;
                else
                    montecarloWin ++;
            }
            System.out.println("The policy is: " + policies.get(p));
            System.out.println("The number of times random player won is: " + randomWin);
            System.out.println("The number of times monte carlo player won is: " + montecarloWin);
        }

    }


    public static void main(String args[])
    {
        MonteCarloEvaluation evaluation = new MonteCarloEvaluation();
        evaluation.basicEvaluation(100  );
        List<Integer> biasParameters = new ArrayList<>();
        biasParameters.add(1);
        biasParameters.add(2);
        biasParameters.add(3);
        biasParameters.add(4);
        biasParameters.add(5);
        evaluation.biasParameterEvaluation(100, biasParameters);
        evaluation.policyEvaluation(100);


    }
}
