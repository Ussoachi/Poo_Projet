package modele.plateau;

import modele.item.ItemShape;

import java.util.Random;

public class Mine extends Machine {


    @Override
    public void work() { // produit un item depuis le gisement de la case
        if (current.isEmpty()) {
            if (c != null && c.getGisement() instanceof ItemShape) {
                current.add(new ItemShape((ItemShape) c.getGisement()));
            } else {
                // pas de gisement : production par défaut
                if (new Random().nextInt(4) == 0) {
                    current.add(new ItemShape("CrCb--Cb"));
                }
            }
        }
    }

    @Override
    public void send() {
        super.send();
    }
}
