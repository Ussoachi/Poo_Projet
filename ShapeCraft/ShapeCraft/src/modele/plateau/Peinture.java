package modele.plateau;

import modele.item.Color;
import modele.item.ItemShape;

public class Peinture extends Machine {

    private Color couleur;
    private boolean dejaTraite = false;

    public Peinture(Color _couleur) {
        couleur = _couleur;
    }

    public Color getCouleur() {
        return couleur;
    }

    @Override
    public void work() {
        if (!current.isEmpty() && current.get(0) instanceof ItemShape) {
            if (!dejaTraite) {
                ((ItemShape) current.get(0)).peindre(couleur);
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
