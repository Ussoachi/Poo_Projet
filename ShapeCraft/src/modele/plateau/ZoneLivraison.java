package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

import java.io.Serializable;

public class ZoneLivraison extends Machine implements Serializable {
    private static final long serialVersionUID = 1L;

    private int compteur;
    private int objectif;

    public ZoneLivraison() {
        this(10);
    }

    public ZoneLivraison(int objectif) {
        super();
        this.objectif = objectif;
        this.compteur = 0;
    }

    public int getCompteur() {
        return compteur;
    }

    public int getObjectif() {
        return objectif;
    }

    public boolean objectifAtteint() {
        return compteur >= objectif;
    }

    public void recevoir(ItemShape shape) {
        if (shape != null) {
            compteur++;
        }
    }

    @Override
    protected boolean canReceiveItem(Item item) {
        return item != null;
    }

    @Override
    protected boolean ReceiveItem(Item item) {
        if (item == null) {
            return false;
        }
        compteur++;
        return true;
    }

    @Override
    public void work() {
    }

    @Override
    public void send() {
    }

    @Override
    public void run() {
    }
}