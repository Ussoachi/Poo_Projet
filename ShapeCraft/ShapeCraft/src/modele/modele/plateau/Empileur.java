package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

public class Empileur extends Machine {

    private Item itemSecondaire = null;
    private boolean dejaTraite = false;

    public void recevoirSecondaire(Item item) {
        if (itemSecondaire == null) {
            itemSecondaire = item;
        }
    }

    @Override
    public void work() {
        if (!current.isEmpty() && itemSecondaire != null && !dejaTraite) {
            if (current.get(0) instanceof ItemShape && itemSecondaire instanceof ItemShape) {
                ((ItemShape) current.get(0)).stack((ItemShape) itemSecondaire);
                itemSecondaire = null;
                dejaTraite = true;
            }
        }
    }

    @Override
    public void send() {
        if (dejaTraite && !current.isEmpty()) {
            super.send();
            if (current.isEmpty()) {
                dejaTraite = false;
            }
        }
    }
}
