package modele.plateau;

import modele.item.ItemShape;

public class Decoupeur extends Machine {

    private ItemShape partDroite = null;
    private boolean dejaTraite = false;

    @Override
    public void work() {
        if (!current.isEmpty() && current.get(0) instanceof ItemShape) {
            if (!dejaTraite) {
                partDroite = ((ItemShape) current.get(0)).Cut();
                dejaTraite = true;
            }
        } else {
            dejaTraite = false;
        }
    }

    // direction secondaire : perpendiculaire à d (sens horaire)
    private Direction directionSecondaire() {
        switch (d) {
            case North: return Direction.East;
            case East:  return Direction.South;
            case South: return Direction.West;
            case West:  return Direction.North;
            default:    return Direction.East;
        }
    }

    @Override
    public void send() {
        // partie gauche vers direction principale
        if (!current.isEmpty()) {
            Case voisine = c.plateau.getCase(c, d);
            if (voisine != null && voisine.getMachine() != null && voisine.getMachine().current.isEmpty()) {
                voisine.getMachine().current.add(current.removeFirst());
                dejaTraite = false;
            }
        }
        // partie droite vers direction secondaire
        if (partDroite != null) {
            Case voisine2 = c.plateau.getCase(c, directionSecondaire());
            if (voisine2 != null && voisine2.getMachine() != null && voisine2.getMachine().current.isEmpty()) {
                voisine2.getMachine().current.add(partDroite);
                partDroite = null;
            }
        }
    }
}
