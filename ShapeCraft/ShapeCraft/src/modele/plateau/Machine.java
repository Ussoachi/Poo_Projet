package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

import java.util.LinkedList;
import java.util.Queue;

public abstract class Machine implements Runnable {
    LinkedList<Item> current;

    Case c;
    Direction d = Direction.North; // par défaut, pour commencer, tout est orienté au north

    public Machine()
    {
        current = new LinkedList<Item>();
    }

    public Machine(Item _item) {
        this();
        current.add(_item);
    }

    public void setCase(Case _c) {
        c= _c;
    }

    public Direction getDirection() {
        return d;
    }

    public void setDirection(Direction _d) {
        d = _d;
    }

    public Item getCurrent() {
        if (current.size() > 0) {
            return current.get(0);
        } else {
            return null;
        }
    }

    public void send() // la machine dépose un item sur sa sortie dans la direction d
    {
        Case voisine = c.plateau.getCase(c, d);
        if (voisine != null) {
            Machine m = voisine.getMachine();
            if (m != null && !current.isEmpty() && m.current.isEmpty()) {
                Item item = current.getFirst();
                m.current.add(item);
                current.remove(item);
            }
        }
    }

    public void work() {
        // aucune action par défaut
    }

    @Override
    public void run() {
        work();
        send();
    }



}
