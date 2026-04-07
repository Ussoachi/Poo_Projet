package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

public class ZoneLivraison extends Machine {

    private ItemShape[] objectifs;
    private int[] quantitesRequises;
    private int[] quantitesLivrees;
    private int objectifCourant;
    private boolean partieGagnee;

    public ZoneLivraison(ItemShape[] _objectifs, int[] _quantitesRequises) {
        objectifs = _objectifs;
        quantitesRequises = _quantitesRequises;
        quantitesLivrees = new int[3];
        objectifCourant = 0;
        partieGagnee = false;
    }

    @Override
    public void work() {
        if (partieGagnee || current.isEmpty()) return;

        Item item = current.getFirst();
        if (item instanceof ItemShape) {
            ItemShape forme = (ItemShape) item;
            if (forme.estEgal(objectifs[objectifCourant])) {
                quantitesLivrees[objectifCourant]++;
                if (quantitesLivrees[objectifCourant] >= quantitesRequises[objectifCourant]) {
                    if (objectifCourant < 2) {
                        objectifCourant++;
                    } else {
                        partieGagnee = true;
                    }
                }
            }
        }
        current.removeFirst();
    }

    @Override
    public void send() {
        // la zone de livraison ne renvoie rien
    }

    public int getObjectifCourant() { return objectifCourant; }
    public ItemShape getObjectif(int i) { return objectifs[i]; }
    public int getQuantiteRequise(int i) { return quantitesRequises[i]; }
    public int getQuantiteLivree(int i) { return quantitesLivrees[i]; }
    public boolean isPartieGagnee() { return partieGagnee; }
}
