import de.upb.isml.thegamef2f.engine.GameState;
import de.upb.isml.thegamef2f.engine.Move;
import de.upb.isml.thegamef2f.engine.board.AscendingDiscardPile;
import de.upb.isml.thegamef2f.engine.board.Card;
import de.upb.isml.thegamef2f.engine.board.DescendingDiscardPile;
import de.upb.isml.thegamef2f.engine.player.Player;
import de.upb.isml.thegamef2f.engine.player.RandomPlayer;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class implementing {@link Player} which uses Monte Carlo Tree Search to select the move in a given state. *
 */

public class MontecarloPlayer implements Player {
    /**
     * {@link Random} used to store the seed for random generation
     */
    protected Random random;
    /**
     * Name of the player
     */
    protected String name;
    /**
     * The UBC1 parameter
     */
    protected Integer UCB1value = 2;
    /**
     * Policy which decides if Robust child or Max child should be used
     */
    protected String policy = "robust";

    /**
     * Constructor
     * @param name of the player
     */
    public MontecarloPlayer (String name) {
        this.name = name;
    }

    /**
     * Constructor that takes the UCB1 parameter as an argument
     * @param name
     * @param UCB1value exploration parameter
     */
    public MontecarloPlayer (String name, Integer UCB1value) {
        this.name = name;
        this.UCB1value = UCB1value;
    }

    /**
     * Constructor which takes the policy as an argument
     * @param name
     * @param policy Robust Child or Max Child
     */
    public MontecarloPlayer (String name, String policy) {
        this.name = name;
        this.policy = policy;
    }
    @Override
    public void initialize(long l) {
        this.random = new Random(l);
    }

    /**
     * Compute the move of the player for the given state
     * @param gameState
     * @return The selected move from the available moves
     */
    @Override
    public Move computeMove(GameState gameState) {
        Move play = null;
        List<Card> handCards = gameState.getHandCards();
        List<Card> cardsOnOwnAscendingDiscardPile = gameState.getCardsOnOwnAscendingDiscardPile();
        List<Card> cardsOnOwnDescendingDiscardPile = gameState.getCardsOnOwnDescendingDiscardPile();
        List<Card> cardsOnOpponentsAscendingDiscardPile = gameState.getCardsOnOpponentsAscendingDiscardPile();
        List<Card> cardsOnOpponentsDescendingDiscardPile = gameState.getCardsOnOpponentsDescendingDiscardPile();
        // Create the starting state from MCTS simulations

        GameStateMC stateMC = new GameStateMC(handCards,cardsOnOwnAscendingDiscardPile, cardsOnOwnDescendingDiscardPile, cardsOnOpponentsAscendingDiscardPile, cardsOnOpponentsDescendingDiscardPile);
        // Create temporary playerboards instances for both the players to be used in MCTS simulations

        Player player1 = new RandomPlayer("a");
        PlayerBoardMC playerBoard1, playerBoard2;


        GameMC gameMC;
        List<Move> moves = new ArrayList<>();
        Montecarlo montecarlo;
        try {
            playerBoard1 = this.getPlayerBoard(this.opponentGameState(stateMC), player1);
            playerBoard2 = this.getPlayerBoard(stateMC, this);
            // Create temporary instance of Game to be played in the MCTS simulations
            gameMC = new GameMC(player1, playerBoard1, this, playerBoard2);
            moves = gameMC.legalPlays(stateMC);
            if(moves.isEmpty()) {
                return null;
            }
            stateMC.player = false;
            // Start MCTS
            montecarlo = new Montecarlo(gameMC, this.UCB1value);
            montecarlo.runSearch(stateMC, 1);
            play = montecarlo.bestPlay(gameMC, stateMC, this.policy);
        }catch (Exception e){
            if(!moves.isEmpty())
                play = moves.get(0);
        }
        return play;
    }

    /**
     * Calculate the opponent game state MCTS simulations from the available information regarding the ascending and
     * descending card piles
     * @param ownGameState the game state of the current player from which the opponent game state has to be assumed
     * @return the game state of the opponent
     */
    public GameStateMC opponentGameState(GameStateMC ownGameState){
        int numberOfCards = ownGameState.getHandCards().size();
        List<Card> opponentHandCards = new ArrayList<>();
        List<Integer> availableCardNumbers = new ArrayList<>();
        for(int i=2; i<59; i++){
            availableCardNumbers.add(i);
        }
        // Remove all the cards in the ascending card pile from the draw pile
        for(int i=0; i<ownGameState.getCardsOnOpponentsAscendingDiscardPile().size(); i++)
            availableCardNumbers.remove(new Integer(ownGameState.getCardsOnOpponentsAscendingDiscardPile().get(i).getNumber()));
        // Remove all the cards in the ascending card pile from the draw pile.
        for(int i=0; i<ownGameState.getCardsOnOpponentsDescendingDiscardPile().size(); i++)
            availableCardNumbers.remove(new Integer(ownGameState.getCardsOnOpponentsDescendingDiscardPile().get(i).getNumber()));

        Constructor<Card> constructor = null;
        try {
            constructor = Card.class.getDeclaredConstructor(int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        constructor.setAccessible(true);
        Card card;
        // Assuming the number of hand cards is same as that of the other player
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

    /**
     * Calculate the player board for the given player required for MCTS game simulations
     * @param gameStateMC Game state of the player
     * @param player
     * @return the instance of Playerboard
     */
    public PlayerBoardMC getPlayerBoard(GameStateMC gameStateMC, Player player){
        List<Integer> availableCardNumbers = new ArrayList<>();
        for(int i=2; i< 60; i++){
            availableCardNumbers.add(i);
        }
        AscendingDiscardPile ascendingDiscardPile = new AscendingDiscardPile();
        for(int i=1; i<gameStateMC.getCardsOnOwnAscendingDiscardPile().size(); i++){
            ascendingDiscardPile.placeCard(gameStateMC.getCardsOnOwnAscendingDiscardPile().get(i));
            availableCardNumbers.remove(new Integer(gameStateMC.getCardsOnOwnAscendingDiscardPile().get(i).getNumber()));
        }
        DescendingDiscardPile descendingDiscardPile = new DescendingDiscardPile();
        for(int i=1; i<gameStateMC.getCardsOnOwnDescendingDiscardPile().size(); i++){
            descendingDiscardPile.placeCard(gameStateMC.getCardsOnOwnDescendingDiscardPile().get(i));
            availableCardNumbers.remove(new Integer(gameStateMC.getCardsOnOwnDescendingDiscardPile().get(i).getNumber()));
        }
        for(int i=0; i<gameStateMC.getHandCards().size(); i++){
            availableCardNumbers.remove(new Integer(gameStateMC.getHandCards().get(i).getNumber()));
        }
        Collections.shuffle(availableCardNumbers, new Random(1234L));
        DrawPileMC drawPileMC = new DrawPileMC(availableCardNumbers);
        PlayerBoardMC playerBoardMC = new PlayerBoardMC(player, ascendingDiscardPile, descendingDiscardPile, drawPileMC,gameStateMC.getHandCards(),  1234L);
        return playerBoardMC;
    }
}
