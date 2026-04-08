package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

import java.io.Serializable;
import java.util.LinkedList;

public abstract class Machine implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;

    protected LinkedList<Item> current;
    protected Case c;
    protected Direction d = Direction.North;

    public Machine() {
        current = new LinkedList<>();
    }

    public Machine(Item item) {
        this();
        current.add(item);
    }

    public void setCase(Case c) {
        this.c = c;
    }

    public Direction getD() {
        return d;
    }

    public void setD(Direction d) {
        this.d = d;
    }

    public Item getCurrent() {
        return current.isEmpty() ? null : current.getFirst();
    }

    public void setCurrent(Item item) {
        current.clear();
        if (item != null) {
            current.add(item);
        }
    }

    protected boolean canReceiveItem(Item item) {
        return true;
    }

    protected boolean ReceiveItem(Item item) {
        if (canReceiveItem(item)) {
            current.add(item);
            return true;
        }
        return false;
    }

    public void send() {
        if (current.isEmpty()) {
            return;
        }

        Case nextCase = c.plateau.getCase(c, d);
        if (nextCase == null) {
            return;
        }

        Machine nextMachine = nextCase.getMachine();
        if (nextMachine == null) {
            return;
        }

        Item item = current.getFirst();

        if (nextMachine.ReceiveItem(item)) {
            current.removeFirst();
        }
    }

    public void work() {
        if (!current.isEmpty() && current.getFirst() instanceof ItemShape) {
            ((ItemShape) current.getFirst()).rotate();
        }
    }

    @Override
    public void run() {
        work();
        send();
    }
}