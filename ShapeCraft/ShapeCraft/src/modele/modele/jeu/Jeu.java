package modele.jeu;

import modele.plateau.Case;
import modele.item.Item;
import modele.item.ItemShape;
import modele.plateau.Direction;
import modele.plateau.Machine;
import modele.plateau.Mine;
import modele.plateau.Plateau;
import modele.plateau.Poubelle;
import modele.plateau.Tapis;
import modele.plateau.ZoneLivraison;

public class Jeu extends Thread {
    private Plateau plateau;

    private boolean enCours;
    private boolean enPause;

    private static final int DELAI_SIMULATION = 80;

    private int startX = -1;
    private int startY = -1;
    private int lastX = -1;
    private int lastY = -1;

    public Jeu() {
        plateau = new Plateau();

        Mine mine = new Mine();
        mine.setD(Direction.East);
        plateau.setMachine(2, 5, mine);

        ZoneLivraison zone = new ZoneLivraison(10, new ItemShape("CrCrCbCb"));
        plateau.setMachine(12, 5, zone);
        plateau.setMachine(13, 5, zone);
        plateau.setMachine(12, 6, zone);
        plateau.setMachine(13, 6, zone);
        plateau.setMachine(5, 10, new Poubelle());

        enCours = true;
        enPause = false;

        start();
    }

    public Plateau getPlateau() {
        return plateau;
    }

    private Direction directionEntre(int x1, int y1, int x2, int y2) {
        if (x2 > x1) return Direction.East;
        if (x2 < x1) return Direction.West;
        if (y2 > y1) return Direction.South;
        return Direction.North;
    }

    private boolean estAdjacent(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) == 1;
    }

    private boolean estProtegee(int x, int y) {
        Machine m = plateau.getCases()[x][y].getMachine();
        return m instanceof Mine || m instanceof ZoneLivraison || m instanceof Poubelle;
    }

    public void press(int x, int y) {
        poserTapis(x, y);
    }

    public void poserTapis(int x, int y) {
        if (estProtegee(x, y)) {
            startX = -1;
            startY = -1;
            lastX = -1;
            lastY = -1;
            return;
        }

        Machine m = plateau.getCases()[x][y].getMachine();

        if (m == null) {
            Tapis t = new Tapis();
            plateau.setMachine(x, y, t);
        } else if (!(m instanceof Tapis)) {
            startX = -1;
            startY = -1;
            lastX = -1;
            lastY = -1;
            return;
        }

        startX = x;
        startY = y;
        lastX = x;
        lastY = y;
    }

    public void supprimerMachine(int x, int y) {
        if (estProtegee(x, y)) return;

        plateau.setMachine(x, y, null);

        if (lastX == x && lastY == y) {
            lastX = -1;
            lastY = -1;
        }

        if (startX == x && startY == y) {
            startX = -1;
            startY = -1;
        }
    }

    public void slide(int x, int y) {
        if (lastX == -1 || lastY == -1) return;
        if (x == lastX && y == lastY) return;
        if (!estAdjacent(lastX, lastY, x, y)) return;
        if (estProtegee(x, y)) return;

        Direction mouvement = directionEntre(lastX, lastY, x, y);

        Machine machinePrecedente = plateau.getCases()[lastX][lastY].getMachine();
        if (!(machinePrecedente instanceof Tapis)) return;

        Tapis tapisPrecedent = (Tapis) machinePrecedente;
        tapisPrecedent.setD(mouvement);

        Machine machineCourante = plateau.getCases()[x][y].getMachine();
        Tapis tapisCourant;

        if (machineCourante == null) {
            tapisCourant = new Tapis();
            plateau.setMachine(x, y, tapisCourant);
        } else if (machineCourante instanceof Tapis) {
            tapisCourant = (Tapis) machineCourante;
        } else {
            return;
        }

        tapisCourant.setEntree(mouvement.getOpposite());
        tapisCourant.setD(mouvement);

        lastX = x;
        lastY = y;
    }

    @Override
    public void run() {
        while (enCours) {
            try {
                if (!enPause) {
                    plateau.run();
                }
                Thread.sleep(DELAI_SIMULATION);
            } catch (InterruptedException e) {
                enCours = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    public void mettreEnPause() {
        enPause = true;
    }

    public void reprendre() {
        enPause = false;
    }

    public void arreterJeu() {
        enCours = false;
    }
}