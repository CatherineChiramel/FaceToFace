import de.upb.isml.thegamef2f.engine.GameState;
import de.upb.isml.thegamef2f.engine.Move;
import de.upb.isml.thegamef2f.engine.board.AscendingDiscardPile;
import de.upb.isml.thegamef2f.engine.board.Card;
import de.upb.isml.thegamef2f.engine.board.DescendingDiscardPile;
import de.upb.isml.thegamef2f.engine.board.PlayerBoard;
import de.upb.isml.thegamef2f.engine.player.Player;
import de.upb.isml.thegamef2f.engine.player.RandomPlayer;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MontecarloPlayer implements Player {
    protected Random random;
    protected String name;

    public MontecarloPlayer (String name) {
        this.name = name;
    }

    @Override
    public void initialize(long l) {
        this.random = new Random(l);
    }

    @Override
    public Move computeMove(GameState gameState) {
        System.out.println("Inside computeMove method");


        List<Card> handCards = gameState.getHandCards();
        List<Card> cardsOnOwnAscendingDiscardPile = gameState.getCardsOnOwnAscendingDiscardPile();
        List<Card> cardsOnOwnDescendingDiscardPile = gameState.getCardsOnOwnDescendingDiscardPile();
        List<Card> cardsOnOpponentsAscendingDiscardPile = gameState.getCardsOnOpponentsAscendingDiscardPile();
        List<Card> cardsOnOpponentsDescendingDiscardPile = gameState.getCardsOnOpponentsDescendingDiscardPile();
        boolean player = false;
        Player player1 = new RandomPlayer("a");

        GameStateMC stateMC = new GameStateMC(handCards,cardsOnOwnAscendingDiscardPile, cardsOnOwnDescendingDiscardPile, cardsOnOpponentsAscendingDiscardPile, cardsOnOpponentsDescendingDiscardPile);
        PlayerBoardMC playerBoard1 = this.getPlayerBoard(this.opponentGameState(stateMC), player1);
        PlayerBoardMC playerBoard2 = this.getPlayerBoard(stateMC, this);
        GameMC gameMC = new GameMC(player1, playerBoard1, this, playerBoard2);
        stateMC.player = false;
        Montecarlo montecarlo = new Montecarlo(gameMC, 2);
        System.out.println("The GameStateMC object is created");
        Move play;
        try {
            montecarlo.runSearch(stateMC, 1);
            System.out.println("Returned from runSearch method call");


            play = montecarlo.bestPlay(stateMC, "robust");
        }catch (Exception e){
            play = gameMC.legalPlays(stateMC).get(1);
        }
        return play;
    }

    public GameStateMC opponentGameState(GameStateMC ownGameState){
        int numberOfCards = ownGameState.getHandCards().size();
        List<Card> opponentHandCards = new ArrayList<>();
        List<Integer> availableCardNumbers = new ArrayList<>();
        for(int i=2; i< 59; i++){
            availableCardNumbers.add(i);
        }
        for(int i=0; i<ownGameState.getCardsOnOpponentsAscendingDiscardPile().size(); i++)
            availableCardNumbers.remove(ownGameState.getCardsOnOpponentsAscendingDiscardPile().get(i));
        for(int i=0; i<ownGameState.getCardsOnOpponentsDescendingDiscardPile().size(); i++)
            availableCardNumbers.remove(ownGameState.getCardsOnOpponentsDescendingDiscardPile().get(i));
        Constructor<Card> constructor = null;
        try {
            constructor = Card.class.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        constructor.setAccessible(true);
        Card card;
        if(availableCardNumbers.size() >= numberOfCards){
            for(int i=0; i<numberOfCards; i++){
                try {
                    card = constructor.newInstance(availableCardNumbers.get(i));
                    opponentHandCards.add(card);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
        else {
            for(int i=0; i<availableCardNumbers.size(); i++){
                try {
                    card = constructor.newInstance(availableCardNumbers.get(i));
                    opponentHandCards.add(card);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
        GameStateMC opponentGameStateMC = new GameStateMC(opponentHandCards, ownGameState.getCardsOnOpponentsAscendingDiscardPile(), ownGameState.getCardsOnOpponentsDescendingDiscardPile(), ownGameState.getCardsOnOwnAscendingDiscardPile(), ownGameState.getCardsOnOwnDescendingDiscardPile());
        return opponentGameStateMC;
    }

    public PlayerBoardMC getPlayerBoard(GameStateMC gameStateMC, Player player){
        List<Integer> availableCardNumbers = new ArrayList<>();
        for(int i=2; i< 59; i++){
            availableCardNumbers.add(i);
        }
        AscendingDiscardPile ascendingDiscardPile = new AscendingDiscardPile();
        for(int i=1; i<gameStateMC.getCardsOnOwnAscendingDiscardPile().size(); i++){
            ascendingDiscardPile.placeCard(gameStateMC.getCardsOnOwnDescendingDiscardPile().get(i));
            availableCardNumbers.remove(gameStateMC.getCardsOnOwnAscendingDiscardPile().get(i).getNumber());
        }
        DescendingDiscardPile descendingDiscardPile = new DescendingDiscardPile();
        for(int i=1; i<gameStateMC.getCardsOnOwnDescendingDiscardPile().size(); i++){
            descendingDiscardPile.placeCard(gameStateMC.getCardsOnOwnDescendingDiscardPile().get(i));
            availableCardNumbers.remove(gameStateMC.getCardsOnOwnDescendingDiscardPile().get(i).getNumber());
        }

        for(int i=0; i<gameStateMC.getHandCards().size(); i++){
            availableCardNumbers.remove(gameStateMC.getHandCards().get(i).getNumber());
        }

        Collections.shuffle(availableCardNumbers, new Random(1234L));
        DrawPileMC drawPileMC = new DrawPileMC(availableCardNumbers);

        PlayerBoardMC playerBoardMC = new PlayerBoardMC(player, ascendingDiscardPile, descendingDiscardPile, drawPileMC,gameStateMC.getHandCards(),  1234L);




        return playerBoardMC;
    }
}
