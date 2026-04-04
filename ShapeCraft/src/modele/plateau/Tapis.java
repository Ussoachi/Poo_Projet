package modele.plateau;

import modele.item.Item;

public class Tapis extends Machine {
    public static final int NB_ETAPES_ANIMATION = 14;

    private int etapeAnimation;
    private Direction entree;

    public Tapis() {
        super();
        etapeAnimation = 0;
        entree = null;
    }

    public int getEtapeAnimation() {
        return etapeAnimation;
    }

    public Direction getEntree() {
        return entree;
    }

    public void setEntree(Direction entree) {
        this.entree = entree;
    }

    public String getTypeForme() {
        Direction sortie = getD();

        if (entree == null || sortie == null) {
            return "top";
        }

        if (sortie == entree.getOpposite()) {
            return "top";
        }

        int inputDx = -entree.dx;
        int inputDy = -entree.dy;

        int det = inputDx * sortie.dy - inputDy * sortie.dx;

        if (det == 1) {
            return "right";
        }

        if (det == -1) {
            return "left";
        }

        return "top";
    }

    @Override
    protected boolean canReceiveItem(Item item) {
        return current.isEmpty();
    }

    @Override
    protected boolean ReceiveItem(Item item) {
        if (canReceiveItem(item)) {
            current.add(item);
            etapeAnimation = 0;
            return true;
        }
        return false;
    }

    @Override
    public void work() {
        if (!current.isEmpty() && etapeAnimation < NB_ETAPES_ANIMATION - 1) {
            etapeAnimation++;
        }
    }

    @Override
    public void send() {
        if (current.isEmpty()) {
            return;
        }

        if (etapeAnimation < NB_ETAPES_ANIMATION - 1) {
            return;
        }

        Case nextCase = c.plateau.getCase(c, getD());
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
            etapeAnimation = 0;
        }
    }

    @Override
    public void run() {
        work();
        send();
    }
}