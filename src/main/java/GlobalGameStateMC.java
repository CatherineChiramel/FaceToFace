import de.upb.isml.thegamef2f.engine.board.Card;

import java.util.List;

/**
 * Class to keep track of the global game state in Monte Carlo simulations
 */
public class GlobalGameStateMC {
    private int sizeOfDrawPileOfPlayer1;
    private int sizeOfDrawPileOfPlayer2;
    private List<Card> handCardsPlayer1;
    private List<Card> handCardsPlayer2;
    private Card topCardOnPlayer1AscendingDiscardPile;
    private Card topCardOnPlayer1DescendingDiscardPile;
    private Card topCardOnPlayer2AscendingDiscardPile;
    private Card topCardOnPlayer2DescendingDiscardPile;
    protected boolean player;

    public GlobalGameStateMC(int sizeOfDrawPileOfPlayer1, int sizeOfDrawPileOfPlayer2, List<Card> handCardsPlayer1, List<Card> handCardsPlayer2, Card topCardOnPlayer1AscendingDiscardPile, Card topCardOnPlayer1DescendingDiscardPile, Card topCardOnPlayer2AscendingDiscardPile, Card topCardOnPlayer2DescendingDiscardPile) {
        this.sizeOfDrawPileOfPlayer1 = sizeOfDrawPileOfPlayer1;
        this.sizeOfDrawPileOfPlayer2 = sizeOfDrawPileOfPlayer2;
        this.handCardsPlayer1 = handCardsPlayer1;
        this.handCardsPlayer2 = handCardsPlayer2;
        this.topCardOnPlayer1AscendingDiscardPile = topCardOnPlayer1AscendingDiscardPile;
        this.topCardOnPlayer1DescendingDiscardPile = topCardOnPlayer1DescendingDiscardPile;
        this.topCardOnPlayer2AscendingDiscardPile = topCardOnPlayer2AscendingDiscardPile;
        this.topCardOnPlayer2DescendingDiscardPile = topCardOnPlayer2DescendingDiscardPile;
    }

    public int getSizeOfDrawPileOfPlayer1() {
        return this.sizeOfDrawPileOfPlayer1;
    }

    public int getSizeOfDrawPileOfPlayer2() {
        return this.sizeOfDrawPileOfPlayer2;
    }

    public List<Card> getHandCardsPlayer1() {
        return this.handCardsPlayer1;
    }

    public List<Card> getHandCardsPlayer2() {
        return this.handCardsPlayer2;
    }

    public Card getTopCardOnPlayer1AscendingDiscardPile() {
        return this.topCardOnPlayer1AscendingDiscardPile;
    }

    public Card getTopCardOnPlayer1DescendingDiscardPile() {
        return this.topCardOnPlayer1DescendingDiscardPile;
    }

    public Card getTopCardOnPlayer2AscendingDiscardPile() {
        return this.topCardOnPlayer2AscendingDiscardPile;
    }

    public Card getTopCardOnPlayer2DescendingDiscardPile() {
        return this.topCardOnPlayer2DescendingDiscardPile;
    }

    public String toString() {
        String var10000 = String.format("%-45s", "Handcards");
        String gameStateRepresentation = var10000 + "\t" + String.format("%-2s", "A") + "  " + String.format("%-2s", "D") + "\n";
        gameStateRepresentation = gameStateRepresentation + "player 1 (" + this.getSizeOfDrawPileOfPlayer1() + ") : " + String.format("%-25s", this.handCardsPlayer1.toString()) + "\t" + String.format("%-2s", this.topCardOnPlayer1AscendingDiscardPile) + "  " + String.format("%-2s", this.topCardOnPlayer1DescendingDiscardPile) + "\n";
        gameStateRepresentation = gameStateRepresentation + "player 2 (" + this.getSizeOfDrawPileOfPlayer2() + ") : " + String.format("%-25s", this.handCardsPlayer2.toString()) + "\t" + String.format("%-2s", this.topCardOnPlayer2AscendingDiscardPile) + "  " + String.format("%-2s", this.topCardOnPlayer2DescendingDiscardPile) + "\n";
        gameStateRepresentation = gameStateRepresentation + "---------------------------------";
        return gameStateRepresentation;
    }

    public boolean isPlayer(boolean player){
        if(this.player == player)
            return true;
        return false;
    }
}
