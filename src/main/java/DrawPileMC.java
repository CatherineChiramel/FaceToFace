//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//



import de.upb.isml.thegamef2f.engine.board.Card;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DrawPileMC {
    private List<Card> cards = new ArrayList();

    public DrawPileMC(List<Integer> cardNumbers) {
        this.initialize(cardNumbers);
    }

    private void initialize(List<Integer> cardNumbers) {
        Constructor<Card> constructor = null;
        try {
            constructor = Card.class.getDeclaredConstructor(int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        constructor.setAccessible(true);
        Card card;
        for(int i=0; i<cardNumbers.size(); i++){
            try {
                card = constructor.newInstance(cardNumbers.get(i));
                cards.add(card);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

    public List<Card> drawCards(int amount) {
        List<Card> drawnCards = new ArrayList();
        int actualAmount = Math.min(amount, this.cards.size());

        for(int i = 0; i < actualAmount; ++i) {
            drawnCards.add((Card)this.cards.remove(this.cards.size() - 1));
        }

        return drawnCards;
    }

    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    public int getSize() {
        return this.cards.size();
    }
}
