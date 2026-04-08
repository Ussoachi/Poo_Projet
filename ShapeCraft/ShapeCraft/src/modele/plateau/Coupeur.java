package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

public class Coupeur extends Machine {

    private Direction sortieGauche;
    private Direction sortieDroite;

    public Coupeur(Direction sortieGauche, Direction sortieDroite) {
        super();
        this.sortieGauche = sortieGauche;
        this.sortieDroite = sortieDroite;
    }


    @Override
    protected boolean canReceiveItem(Item item) {
        return current.isEmpty() && item instanceof ItemShape;
    }

    @Override
    public void work() {
        if (current.isEmpty() || !(current.getFirst() instanceof ItemShape)) return;
    }

    @Override
    public void send() {
        if (current.isEmpty()) return;
        if (!(current.getFirst() instanceof ItemShape)) return;

        ItemShape source = (ItemShape) current.getFirst();

        ItemShape droite = source.Cut();
        ItemShape gauche = source;

        boolean gaucheEnvoyee = envoyerVers(gauche, sortieGauche);
        boolean droiteEnvoyee = envoyerVers(droite, sortieDroite);

        if (gaucheEnvoyee || droiteEnvoyee) {
            current.removeFirst();
        }
    }

    private boolean envoyerVers(ItemShape item, Direction dir) {
        if (item == null || item.getNbActiveLayers() == 0) return true; // rien à envoyer
        Case nextCase = c.plateau.getCase(c, dir);
        if (nextCase == null) return false;
        Machine nextMachine = nextCase.getMachine();
        if (nextMachine == null) return false;
        return nextMachine.ReceiveItem(item);
    }
}