//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import de.upb.isml.thegamef2f.engine.board.AscendingDiscardPile;
import de.upb.isml.thegamef2f.engine.board.Card;
import de.upb.isml.thegamef2f.engine.board.DescendingDiscardPile;
import de.upb.isml.thegamef2f.engine.board.DrawPile;
import de.upb.isml.thegamef2f.engine.player.Player;
import java.util.ArrayList;
import java.util.List;

public class PlayerBoardMC {
    private Player player;
    private AscendingDiscardPile ascendingDiscardPile;
    private DescendingDiscardPile descendingDiscardPile;
    private DrawPileMC drawPile;
    private List<Card> handCards;

    public PlayerBoardMC(Player player, AscendingDiscardPile ascendingDiscardPile, DescendingDiscardPile descendingDiscardPile, DrawPileMC drawPile, List<Card> handCards, long randomSeed) {
        this.player = player;
        this.ascendingDiscardPile = ascendingDiscardPile;
        this.descendingDiscardPile = descendingDiscardPile;
        this.drawPile = drawPile;
        this.handCards = handCards;
        //this.drawFullCards();
    }

    public void removeHandCard(Card card) {
        this.handCards.remove(card);
    }

    public void placeCardOnAscendingDiscardPile(Card card) {
        this.ascendingDiscardPile.placeCard(card);
    }

    public void placeCardOnDescendingDiscardPile(Card card) {
        this.descendingDiscardPile.placeCard(card);
    }

    public void drawFullCards() {
        this.handCards.addAll(this.drawPile.drawCards(6 - this.handCards.size()));
    }

    public void drawTwoCards() {
        this.handCards.addAll(this.drawPile.drawCards(Math.min(2, 6 - this.handCards.size())));
    }

    public Player getPlayer() {
        return this.player;
    }

    public AscendingDiscardPile getAscendingDiscardPile() {
        return this.ascendingDiscardPile;
    }

    public DescendingDiscardPile getDescendingDiscardPile() {
        return this.descendingDiscardPile;
    }

    public DrawPileMC getDrawPile() {
        return this.drawPile;
    }

    public List<Card> getHandCards() {
        return this.handCards;
    }
}
