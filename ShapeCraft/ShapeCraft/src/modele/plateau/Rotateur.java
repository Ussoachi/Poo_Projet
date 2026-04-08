package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

public class Rotateur extends Machine {

    public Rotateur() {
        super();
    }

    @Override
    protected boolean canReceiveItem(Item item) {
        return current.isEmpty() && item instanceof ItemShape;
    }

    @Override
    public void work() {
        if (current.isEmpty() || !(current.getFirst() instanceof ItemShape)) return;

        ((ItemShape) current.getFirst()).rotate();
    }

    @Override
    public void send() {
        super.send();
    }
}