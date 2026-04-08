package vuecontroleur;


import modele.jeu.Jeu;
import modele.plateau.*;
import modele.item.ItemShape;

public class Controleur {

    private Jeu jeu;
    private Plateau plateau;

    public enum Outil {
        TAPIS, MINE, COUPEUR, ROTATEUR, POUBELLE
    }

    private Outil outilCourant = Outil.TAPIS;

    private int lastX = -1;
    private int lastY = -1;

    private boolean leftPressed = false;

    public Controleur(Jeu jeu) {
        this.jeu = jeu;
        this.plateau = jeu.getPlateau();
    }

    public void setOutil(Outil o) {
        outilCourant = o;
    }

    public Outil getOutilCourant() {
        return outilCourant;
    }


    public void press(int x, int y) {
        if (estProtegee(x, y)) return;

        switch (outilCourant) {

            case TAPIS:
                poserTapis(x, y);
                break;

            case MINE:
                Mine m = new Mine();
                m.setD(Direction.West);
                plateau.setMachine(x, y, m);
                break;

            case COUPEUR:
                plateau.setMachine(x, y, new Coupeur(Direction.West, Direction.East));
                break;

            case ROTATEUR:
                plateau.setMachine(x, y, new Rotateur());
                break;

            case POUBELLE:
                plateau.setMachine(x, y, new Poubelle());
                break;
        }
    }


    public void slide(int x, int y) {
        if (lastX == -1 || lastY == -1) return;
        if (x == lastX && y == lastY) return;
        if (!estAdjacent(lastX, lastY, x, y)) return;
        if (estProtegee(x, y)) return;

        Direction mouvement = directionEntre(lastX, lastY, x, y);

        Machine mPrev = plateau.getCases()[lastX][lastY].getMachine();
        if (!(mPrev instanceof Tapis)) return;

        Tapis prev = (Tapis) mPrev;
        prev.setD(mouvement);

        Machine mCurr = plateau.getCases()[x][y].getMachine();
        Tapis curr;

        if (mCurr == null) {
            curr = new Tapis();
            plateau.setMachine(x, y, curr);
        } else if (mCurr instanceof Tapis) {
            curr = (Tapis) mCurr;
        } else {
            return;
        }

        curr.setEntree(mouvement.getOpposite());
        curr.setD(mouvement);

        lastX = x;
        lastY = y;
    }

    public void supprimerMachine(int x, int y) {
        if (estProtegee(x, y)) return;
        plateau.setMachine(x, y, null);
    }

    public void rotateMachine(int x, int y) {
        Machine m = plateau.getCases()[x][y].getMachine();
        if (m == null) return;

        Direction d = m.getD();
        if (d == null) d = Direction.North;

        switch (d) {
            case North: m.setD(Direction.East);  break;
            case East:  m.setD(Direction.South); break;
            case South: m.setD(Direction.West);  break;
            case West:  m.setD(Direction.North); break;
        }

        plateau.notifierObservateurs();
    }


    public void setLeftPressed(boolean b) {
        leftPressed = b;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }


    private void poserTapis(int x, int y) {
        Machine m = plateau.getCases()[x][y].getMachine();

        if (m == null) {
            plateau.setMachine(x, y, new Tapis());
        } else if (!(m instanceof Tapis)) {
            return;
        }

        lastX = x;
        lastY = y;
    }


    private boolean estProtegee(int x, int y) {
        Machine m = plateau.getCases()[x][y].getMachine();
        return m instanceof Mine || m instanceof ZoneLivraison;
    }

    private boolean estAdjacent(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) == 1;
    }

    private Direction directionEntre(int x1, int y1, int x2, int y2) {
        if (x2 > x1) return Direction.East;
        if (x2 < x1) return Direction.West;
        if (y2 > y1) return Direction.South;
        return Direction.North;
    }
}
