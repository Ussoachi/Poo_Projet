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

        plateau.setMachine(12, 5, new ZoneLivraison(10));
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
        if (estProtegee(x, y)) {
            return;
        }

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
        if (lastX == -1 || lastY == -1) {
            return;
        }

        if (x == lastX && y == lastY) {
            return;
        }

        if (!estAdjacent(lastX, lastY, x, y)) {
            return;
        }

        if (estProtegee(x, y)) {
            return;
        }

        Direction mouvement = directionEntre(lastX, lastY, x, y);

        Machine machinePrecedente = plateau.getCases()[lastX][lastY].getMachine();
        if (!(machinePrecedente instanceof Tapis)) {
            return;
        }

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

        if (!(x == startX && y == startY)) {
            tapisCourant.setD(mouvement);
        }

        lastX = x;
        lastY = y;
    }

    public void tickAnimation(double step) {
        Case[][] cases = plateau.getCases();

        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                Machine m = cases[x][y].getMachine();

                if (!(m instanceof Tapis)) {
                    continue;
                }

                Item item = m.getCurrent();
                if (!(item instanceof ItemShape)) {
                    continue;
                }

                ItemShape shape = (ItemShape) item;

                double progression = shape.getProgressInCell() + step;

                if (progression >= 1.0) {
                    shape.setProgressInCell(1.0);

                    Direction dir = m.getD();
                    int nx = x + dir.dx;
                    int ny = y + dir.dy;

                    if (nx < 0 || nx >= Plateau.SIZE_X || ny < 0 || ny >= Plateau.SIZE_Y) {
                        continue;
                    }

                    Machine voisin = cases[nx][ny].getMachine();
                    if (voisin == null) {
                        continue;
                    }

                    if (voisin instanceof ZoneLivraison) {
                        ((ZoneLivraison) voisin).recevoir(shape);
                        m.setCurrent(null);
                    } else if (voisin instanceof Poubelle) {
                        m.setCurrent(null);
                    } else if (voisin instanceof Tapis && voisin.getCurrent() == null) {
                        m.setCurrent(null);
                        shape.setProgressInCell(0.0);
                        voisin.setCurrent(shape);
                    }
                } else {
                    shape.setProgressInCell(progression);
                }
            }
        }
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