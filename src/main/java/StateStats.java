import java.util.List;

public class StateStats {
    Integer nPlays;
    Integer nWins;
    List<ChildStats> children;

    public StateStats(Integer nPlays, Integer nWins, List<ChildStats> children){
        this.nPlays = nPlays;
        this.nWins = nWins;
        this.children = children;
    }
}
