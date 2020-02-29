//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import de.upb.isml.thegamef2f.engine.Move;
import de.upb.isml.thegamef2f.engine.board.Card;
import java.util.List;

public class GameStateMC {
    private List<Card> handCards;
    private List<Card> cardsOnOwnAscendingDiscardPile;
    private List<Card> cardsOnOwnDescendingDiscardPile;
    private List<Card> cardsOnOpponentsAscendingDiscardPile;
    private List<Card> cardsOnOpponentsDescendingDiscardPile;
    public boolean player;

    public GameStateMC(List<Card> handCards, List<Card> cardsOnOwnAscendingDiscardPile, List<Card> cardsOnOwnDescendingDiscardPile, List<Card> cardsOnOpponentsAscendingDiscardPile, List<Card> cardsOnOpponentsDescendingDiscardPile) {
        this.handCards = handCards;
        this.cardsOnOwnAscendingDiscardPile = cardsOnOwnAscendingDiscardPile;
        this.cardsOnOwnDescendingDiscardPile = cardsOnOwnDescendingDiscardPile;
        this.cardsOnOpponentsAscendingDiscardPile = cardsOnOpponentsAscendingDiscardPile;
        this.cardsOnOpponentsDescendingDiscardPile = cardsOnOpponentsDescendingDiscardPile;

    }

    public List<Card> getHandCards() {
        return this.handCards;
    }

    public Card getTopCardOnOwnAscendingDiscardPile() {
        return (Card)this.cardsOnOwnAscendingDiscardPile.get(this.cardsOnOwnAscendingDiscardPile.size() - 1);
    }

    public Card getTopCardOnOwnDescendingDiscardPile() {
        return (Card)this.cardsOnOwnDescendingDiscardPile.get(this.cardsOnOwnDescendingDiscardPile.size() - 1);
    }

    public Card getTopCardOnOpponentsAscendingDiscardPile() {
        return (Card)this.cardsOnOpponentsAscendingDiscardPile.get(this.cardsOnOpponentsAscendingDiscardPile.size() - 1);
    }

    public Card getTopCardOnOpponentsDescendingDiscardPile() {
        return (Card)this.cardsOnOpponentsDescendingDiscardPile.get(this.cardsOnOpponentsDescendingDiscardPile.size() - 1);
    }

    public List<Card> getCardsOnOwnAscendingDiscardPile() {
        return this.cardsOnOwnAscendingDiscardPile;
    }

    public List<Card> getCardsOnOwnDescendingDiscardPile() {
        return this.cardsOnOwnDescendingDiscardPile;
    }

    public List<Card> getCardsOnOpponentsAscendingDiscardPile() {
        return this.cardsOnOpponentsAscendingDiscardPile;
    }

    public List<Card> getCardsOnOpponentsDescendingDiscardPile() {
        return this.cardsOnOpponentsDescendingDiscardPile;
    }

    public boolean isPlayer(boolean player){
        //System.out.println("inside isplayer method");
        try{
        if(this.player == player){
            //System.out.println("inside if");
            return true;
        }}catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public String hash(GameStateMC gameStateMC, Move move){
        String stateHashcode = Integer.toString(gameStateMC.hashCode());
        String moveHashcode = Integer.toString(move.hashCode());
        String hash = stateHashcode + moveHashcode;
        return hash;
    }
}
