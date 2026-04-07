package modele.plateau;

import modele.item.Color;
import modele.item.Item;
import modele.item.ItemShape;
import modele.item.SubShape;

import java.io.Serializable;

public class ZoneLivraison extends Machine implements Serializable {
    private static final long serialVersionUID = 1L;

    private int compteur;
    private int objectif;
    private ItemShape formeCible;

    public ZoneLivraison() {
        this(10, null);
    }

    public ZoneLivraison(int objectif, ItemShape formeCible) {
        super();
        this.objectif = objectif;
        this.compteur = 0;
        this.formeCible = formeCible;
    }

    public ItemShape getFormeCible() {
        return formeCible;
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

    private boolean corresponda(ItemShape shape) {
        for (ItemShape.Layer l : ItemShape.Layer.values()) {
            SubShape[] s1 = formeCible.getSubShapes(l);
            SubShape[] s2 = shape.getSubShapes(l);
            Color[] c1 = formeCible.getColors(l);
            Color[] c2 = shape.getColors(l);
            for (int i = 0; i < 4; i++) {
                if (s1[i] != s2[i]) return false;
                if (s1[i] != SubShape.None && c1[i] != c2[i]) return false;
            }
        }
        return true;
    }

    @Override
    protected boolean ReceiveItem(Item item) {
        if (item == null) return false;
        if (formeCible == null) {
            compteur++;
            return true;
        }
        if (item instanceof ItemShape && corresponda((ItemShape) item)) {
            compteur++;
            return true;
        }
        return false;
    }

    @Override
    public void work() {}

    @Override
    public void send() {}

    @Override
    public void run() {}
}