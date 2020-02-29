import de.upb.isml.thegamef2f.engine.board.Game;
import de.upb.isml.thegamef2f.engine.player.Player;
import de.upb.isml.thegamef2f.engine.player.RandomPlayer;

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


    public static void main(String args[])
    {
        MonteCarloEvaluation evaluation = new MonteCarloEvaluation();
        evaluation.basicEvaluation(10);


    }
}
