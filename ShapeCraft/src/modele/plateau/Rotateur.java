package modele.plateau;

import modele.item.ItemShape;

public class Rotateur extends Machine {

    private boolean dejaTraite = false;

    @Override
    public void work() {
        if (!current.isEmpty() && current.get(0) instanceof ItemShape) {
            if (!dejaTraite) {
                ((ItemShape) current.get(0)).rotate();
                dejaTraite = true;
            }
        } else {
            dejaTraite = false;
        }
    }

    @Override
    public void send() {
        super.send();
        if (current.isEmpty()) {
            dejaTraite = false;
        }
    }
}
