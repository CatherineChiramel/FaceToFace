import de.upb.isml.thegamef2f.engine.board.Game;
import de.upb.isml.thegamef2f.engine.player.Player;
import de.upb.isml.thegamef2f.engine.player.RandomPlayer;

public class Main {

    public static void main(String args[])
    {
        Game game = new Game(new RandomPlayer("a"), new MontecarloPlayer("b"));
        Player winner = game.simulate();
        game.getHistory().printHistory();


    }

}
