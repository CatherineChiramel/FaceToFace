import de.upb.isml.thegamef2f.engine.Move;

public class ChildStats {
    Move play;
    Integer nPlays;
    Integer nWins;

    public ChildStats(Move play, Integer nPlays, Integer nWins){
        this.play = play;
        this.nPlays = nPlays;
        this.nWins = nWins;
    }
}
