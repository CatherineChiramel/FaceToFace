import de.upb.isml.thegamef2f.engine.*;

import de.upb.isml.thegamef2f.engine.board.Card;
import de.upb.isml.thegamef2f.engine.board.PlayerBoard;
import de.upb.isml.thegamef2f.engine.player.Player;

import java.util.*;
import java.util.stream.Collectors;


public class GameMC {
    protected Random random = new Random();
    protected GameHistory gameHistory;
    protected Player player1;
    protected PlayerBoardMC playerBoard1;
    protected Player player2;
    protected PlayerBoardMC playerBoard2;

    public GameMC (Player player1, PlayerBoardMC playerBoard1, Player player2, PlayerBoardMC playerBoard2) {
        this.player1 = player1;
        this.playerBoard1 = playerBoard1;
        this.player2 = player2;
        this.playerBoard2 = playerBoard2;
    }

    public List<Move> legalPlays(GameStateMC gameState){
        //System.out.println("inside legalPlays");
        GameStateMC currentGameState = gameState;
        boolean placedOnOpponentsPiles = false;
        List<Move> legalMoves = new ArrayList<Move>();
        ArrayList placementsOfMove;
        Placement randomPlacement;
        List<Placement> validPlacements = new ArrayList();
        Iterator var6 = currentGameState.getHandCards().iterator();

        // Find the valid placements in the given state
        while(var6.hasNext()) {
            Card card = (Card)var6.next();
            CardPosition[] var8 = CardPosition.values();
            int var9 = var8.length;
            for(int var10 = 0; var10 < var9; ++var10) {
                CardPosition position = var8[var10];
                Placement placement = new Placement(card, position);
                if (this.isPlacementValid(placement, currentGameState, !placedOnOpponentsPiles)) {
                    validPlacements.add(placement);
                }
            }
        }
        //System.out.println("validplacements have been calculated");
        if (validPlacements.isEmpty()) {
            return null;
        }
        // Calculate moves from each of the valid placements possible

        for(int i=0; i<validPlacements.size(); i++){
            placementsOfMove = new ArrayList();
            if (validPlacements.get(i).getPosition() == CardPosition.OPPONENTS_ASCENDING_DISCARD_PILE || validPlacements.get(i).getPosition() == CardPosition.OPPONENTS_DESCENDING_DISCARD_PILE) {
                placedOnOpponentsPiles = true;
            }
            placementsOfMove.add(validPlacements.get(i));
            randomPlacement = validPlacements.get(i);
            currentGameState = this.computeNewGameStateAfterPlacement(currentGameState, randomPlacement);
            for(;!currentGameState.getHandCards().isEmpty(); currentGameState = this.computeNewGameStateAfterPlacement(currentGameState, randomPlacement)) {
                //System.out.println("inside for loop");
                List<Placement> validPlacements1 = new ArrayList();
                Iterator var61 = currentGameState.getHandCards().iterator();
                while(var61.hasNext()) {
                    Card card = (Card)var61.next();
                    CardPosition[] var8 = CardPosition.values();
                    int var9 = var8.length;
                    for(int var10 = 0; var10 < var9; ++var10) {
                        CardPosition position = var8[var10];
                        Placement placement = new Placement(card, position);
                        if (this.isPlacementValid(placement, currentGameState, !placedOnOpponentsPiles)) {
                            validPlacements1.add(placement);
                        }
                    }
                }
                //System.out.println("after while");
                if (validPlacements1.isEmpty()) {
                    //System.out.println("validplacements1 is empty");
                     break;
                }
                //System.out.println("sfgdj" +this.random.nextInt(validPlacements1.size()));
                randomPlacement = (Placement)validPlacements1.get(this.random.nextInt(validPlacements1.size()));

                if (randomPlacement.getPosition() == CardPosition.OPPONENTS_ASCENDING_DISCARD_PILE || randomPlacement.getPosition() == CardPosition.OPPONENTS_DESCENDING_DISCARD_PILE) {
                    placedOnOpponentsPiles = true;
                }
                placementsOfMove.add(randomPlacement);
            }
            // Add the calculated move to the list of legal plays
            legalMoves.add(new Move(placementsOfMove));

        }
        return legalMoves;
    }

    public boolean isPlacementValid(Placement placement, GameStateMC gameState, boolean placingOnOpponentPilesAllowed) {
        switch(placement.getPosition()) {
            case OPPONENTS_ASCENDING_DISCARD_PILE:
                return placingOnOpponentPilesAllowed ? this.canPlaceCardOnOpponentsAscendingDiscardPile(placement.getCard(), gameState) : false;
            case OPPONENTS_DESCENDING_DISCARD_PILE:
                return placingOnOpponentPilesAllowed ? this.canPlaceCardOnOpponentsDescendingDiscardPile(placement.getCard(), gameState) : false;
            case OWN_ASCENDING_DISCARD_PILE:
                return this.canPlaceCardOnOwnAscendingDiscardPile(placement.getCard(), gameState);
            case OWN_DESCENDING_DISCARD_PILE:
                return this.canPlaceCardOnOwnDescendingDiscardPile(placement.getCard(), gameState);
            default:
                return false;
        }
    }

    private boolean canPlaceCardOnOwnAscendingDiscardPile(Card card, GameStateMC gameState) {
        return gameState.getTopCardOnOwnAscendingDiscardPile().isSmallerThan(card) || gameState.getTopCardOnOwnAscendingDiscardPile().is10LargerThan(card);
    }

    private boolean canPlaceCardOnOwnDescendingDiscardPile(Card card, GameStateMC gameState) {
        return card.isSmallerThan(gameState.getTopCardOnOwnDescendingDiscardPile()) || card.is10LargerThan(gameState.getTopCardOnOwnDescendingDiscardPile());
    }

    private boolean canPlaceCardOnOpponentsAscendingDiscardPile(Card card, GameStateMC gameState) {
        return card.isSmallerThan(gameState.getTopCardOnOpponentsAscendingDiscardPile());
    }

    private boolean canPlaceCardOnOpponentsDescendingDiscardPile(Card card, GameStateMC gameState) {
        return gameState.getTopCardOnOpponentsDescendingDiscardPile().isSmallerThan(card);
    }

    private GameStateMC computeNewGameStateAfterPlacement(GameStateMC gameStatePriorToPlacement, Placement placement) {
        List<Card> handCards = new ArrayList(gameStatePriorToPlacement.getHandCards());
        handCards.remove(placement.getCard());
        List<Card> cardsOnOwnAscendingDiscardPile = new ArrayList(gameStatePriorToPlacement.getCardsOnOwnAscendingDiscardPile());
        if (placement.getPosition() == CardPosition.OWN_ASCENDING_DISCARD_PILE) {
            cardsOnOwnAscendingDiscardPile.add(placement.getCard());
        }

        List<Card> cardsOnOwnDescendingDiscardPile = new ArrayList(gameStatePriorToPlacement.getCardsOnOwnDescendingDiscardPile());
        if (placement.getPosition() == CardPosition.OWN_DESCENDING_DISCARD_PILE) {
            cardsOnOwnDescendingDiscardPile.add(placement.getCard());
        }

        List<Card> cardsOnOpponentsAscendingDiscardPile = new ArrayList(gameStatePriorToPlacement.getCardsOnOpponentsAscendingDiscardPile());
        if (placement.getPosition() == CardPosition.OPPONENTS_ASCENDING_DISCARD_PILE) {
            cardsOnOpponentsAscendingDiscardPile.add(placement.getCard());
        }

        List<Card> cardsOnOpponentsDescendingDiscardPile = new ArrayList(gameStatePriorToPlacement.getCardsOnOpponentsDescendingDiscardPile());
        if (placement.getPosition() == CardPosition.OPPONENTS_ASCENDING_DISCARD_PILE) {
            cardsOnOpponentsDescendingDiscardPile.add(placement.getCard());
        }

        return new GameStateMC(handCards, cardsOnOwnAscendingDiscardPile, cardsOnOwnDescendingDiscardPile, cardsOnOpponentsAscendingDiscardPile, cardsOnOpponentsDescendingDiscardPile);
    }

    public void nextStateAfterMove(GameStateMC state, Move move){
        boolean player = state.isPlayer(true)? true : false;
        this.applyMove(move, player);

    }

    public void applyMove(Move move, boolean player1) {
        Player player = player1 ? this.player1 : this.player2;
        boolean drawFullCards = false;
        Iterator var5 = move.getPlacements().iterator();

        while(var5.hasNext()) {
            Placement placement = (Placement)var5.next();
            boolean helpedOpponent = this.applyPlacement(player1, placement);
            if (!drawFullCards && helpedOpponent) {
                drawFullCards = true;
            }
        }

        if (drawFullCards) {
            if (player1) {
                this.playerBoard1.drawFullCards();
            } else {
                this.playerBoard2.drawFullCards();
            }
        } else if (player1) {
            this.playerBoard1.drawTwoCards();
        } else {
            this.playerBoard2.drawTwoCards();
        }

        //this.gameHistory.addMove(player, move, this.generateGlobalGameState());
    }

    private boolean applyPlacement(boolean player1, Placement placement) {
        boolean helpedOpponent = false;
        Card card = placement.getCard();
        PlayerBoardMC ownPlayerBoard;
        PlayerBoardMC opponentsPlayerBoard;
        if (player1) {
            ownPlayerBoard = this.playerBoard1;
            opponentsPlayerBoard = this.playerBoard2;
        } else {
            ownPlayerBoard = this.playerBoard2;
            opponentsPlayerBoard = this.playerBoard1;
        }

        ownPlayerBoard.removeHandCard(card);
        switch(placement.getPosition()) {
            case OWN_ASCENDING_DISCARD_PILE:
                ownPlayerBoard.placeCardOnAscendingDiscardPile(card);
                break;
            case OWN_DESCENDING_DISCARD_PILE:
                ownPlayerBoard.placeCardOnDescendingDiscardPile(card);
                break;
            case OPPONENTS_ASCENDING_DISCARD_PILE:
                opponentsPlayerBoard.placeCardOnAscendingDiscardPile(card);
                helpedOpponent = true;
                break;
            case OPPONENTS_DESCENDING_DISCARD_PILE:
                opponentsPlayerBoard.placeCardOnDescendingDiscardPile(card);
                helpedOpponent = true;
        }

        return helpedOpponent;
    }

    private GlobalGameStateMC generateGlobalGameState() {
        List<Card> handCardsPlayer1 = Collections.unmodifiableList(new ArrayList(this.playerBoard1.getHandCards()));
        List<Card> handCardsPlayer2 = Collections.unmodifiableList(new ArrayList(this.playerBoard2.getHandCards()));
        Card topCardOnPlayer1AscendingDiscardPile = this.playerBoard1.getAscendingDiscardPile().getTopCard();
        Card topCardOnPlayer1DescendingDiscardPile = this.playerBoard1.getDescendingDiscardPile().getTopCard();
        Card topCardOnPlayer2AscendingDiscardPile = this.playerBoard2.getAscendingDiscardPile().getTopCard();
        Card topCardOnPlayer2DescendingDiscardPile = this.playerBoard2.getDescendingDiscardPile().getTopCard();
        return new GlobalGameStateMC(this.playerBoard1.getDrawPile().getSize(), this.playerBoard2.getDrawPile().getSize(), handCardsPlayer1, handCardsPlayer2, topCardOnPlayer1AscendingDiscardPile, topCardOnPlayer1DescendingDiscardPile, topCardOnPlayer2AscendingDiscardPile, topCardOnPlayer2DescendingDiscardPile);
    }

    public GameStateMC getGameState(boolean player1) {
        List handCards;
        List cardsOnOwnAscendingDiscardPile;
        List cardsOnOwnDescendingDiscardPile;
        List cardsOnOpponentsAscendingDiscardPile;
        List cardsOnOpponentsDescendingDiscardPile;
        if (player1) {
            handCards = Collections.unmodifiableList(new ArrayList(this.playerBoard1.getHandCards()));
            cardsOnOwnAscendingDiscardPile = this.playerBoard1.getAscendingDiscardPile().getCards();
            cardsOnOwnDescendingDiscardPile = this.playerBoard1.getDescendingDiscardPile().getCards();
            cardsOnOpponentsAscendingDiscardPile = this.playerBoard2.getAscendingDiscardPile().getCards();
            cardsOnOpponentsDescendingDiscardPile = this.playerBoard2.getDescendingDiscardPile().getCards();
            return new GameStateMC(handCards, cardsOnOwnAscendingDiscardPile, cardsOnOwnDescendingDiscardPile, cardsOnOpponentsAscendingDiscardPile, cardsOnOpponentsDescendingDiscardPile);
        } else {
            handCards = Collections.unmodifiableList(new ArrayList(this.playerBoard2.getHandCards()));
            cardsOnOwnAscendingDiscardPile = this.playerBoard2.getAscendingDiscardPile().getCards();
            cardsOnOwnDescendingDiscardPile = this.playerBoard2.getDescendingDiscardPile().getCards();
            cardsOnOpponentsAscendingDiscardPile = this.playerBoard1.getAscendingDiscardPile().getCards();
            cardsOnOpponentsDescendingDiscardPile = this.playerBoard1.getDescendingDiscardPile().getCards();
            return new GameStateMC(handCards, cardsOnOwnAscendingDiscardPile, cardsOnOwnDescendingDiscardPile, cardsOnOpponentsAscendingDiscardPile, cardsOnOpponentsDescendingDiscardPile);
        }
    }

    public boolean hasPlayer2Won() {
        return this.playerBoard2.getHandCards().isEmpty() && this.playerBoard2.getDrawPile().isEmpty();
    }

    public boolean hasPlayer1Won() {

        boolean a = this.playerBoard1.getHandCards().isEmpty() && this.playerBoard1.getDrawPile().isEmpty();
        System.out.println("inside hasplayer1won" + a);
        return this.playerBoard1.getHandCards().isEmpty() && this.playerBoard1.getDrawPile().isEmpty();
    }

    public boolean isMoveValid(Move move, boolean player1) {
        PlayerBoardMC playerBoard = player1 ? this.playerBoard1 : this.playerBoard2;
        if (move == null) {
            return false;
        } else if (move.getPlacements().size() <= 6 && (move.getPlacements().size() >= 2 || playerBoard.getHandCards().size() < 2)) {
            if (move.getPlacements().stream().filter((p) -> {
                return p.getPosition() == CardPosition.OPPONENTS_ASCENDING_DISCARD_PILE || p.getPosition() == CardPosition.OPPONENTS_DESCENDING_DISCARD_PILE;
            }).count() > 1L) {
                return false;
            } else if (!playerBoard.getHandCards().containsAll((Collection)move.getPlacements().stream().map((p) -> {
                return p.getCard();
            }).collect(Collectors.toSet()))) {
                return false;
            } else {
                boolean placingOnOpponentPilesAllowed = true;
                GameStateMC currentGameState = this.getGameState(player1);

                Placement placement;
                for(Iterator var6 = move.getPlacements().iterator(); var6.hasNext(); currentGameState = this.computeNewGameStateAfterPlacement(currentGameState, placement)) {
                    placement = (Placement)var6.next();
                    if (!this.isPlacementValid(placement, currentGameState, placingOnOpponentPilesAllowed)) {
                        return false;
                    }

                    if (placement.getPosition() == CardPosition.OPPONENTS_ASCENDING_DISCARD_PILE || placement.getPosition() == CardPosition.OPPONENTS_DESCENDING_DISCARD_PILE) {
                        placingOnOpponentPilesAllowed = false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

}
