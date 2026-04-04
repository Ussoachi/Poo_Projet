package modele.jeu;

import modele.plateau.Mine;
import modele.plateau.Plateau;
import modele.plateau.Poubelle;
import modele.plateau.Tapis;
import modele.item.ItemShape;
import modele.plateau.Direction;

public class Jeu extends Thread{
    private Plateau plateau;

    private boolean enPause;   // true = simulation arrêtée
    private boolean enCours;

    private ItemShape[] objectifs;
    private int[] quantites;
    private int indexObjectif;



    public Jeu() {
        plateau = new Plateau();
        enPause = false;
        enCours = true;
        initialiserObjectifs();

        plateau.setMachine(5, 10, new Mine());
        plateau.setMachine(5, 5, new Poubelle());

        start();

    }

    private void initialiserObjectifs() {
        objectifs = new ItemShape[3];
        quantites = new int[3];
        indexObjectif = 0;

        objectifs[0] = new ItemShape("CrCrCrCr"); // objectif 1
        objectifs[1] = new ItemShape("CbCbCbCb"); // objectif 2
        objectifs[2] = new ItemShape("Cr-b-bCr"); // objectif 3
        quantites[0] = 5;
        quantites[1] = 5;
        quantites[2] = 5;
    }
    public Plateau getPlateau() {
        return plateau;
    }

    public int getIndexObjectif() {
        return indexObjectif;
    }

    public synchronized void togglePause() {
        enPause = !enPause;
        if (!enPause) {
            notify(); // réveille le thread si il était en attente
        }
    }

    public boolean isEnPause() {
        return enPause;
    }

    /**
     * Remet le plateau à zéro.
     */
    public void reset() {
        enPause = true;
        plateau = new Plateau();
        indexObjectif = 0;

        enPause = false;
    }

    public void press(int x, int y) {

        plateau.setMachine(x, y, new Tapis());
    }

    public void slide(int x, int y) {
        plateau.setMachine(x, y, new Tapis());
    }


    public void run() {
        jouerPartie();
    }

    public void jouerPartie() {

        while(true) {
            try {
                plateau.run();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }




        public void placerMine(int x, int y) {
        plateau.setMachine(x, y, new Mine());
    }

    public void placerTapis(int x, int y, Direction d) {
        Tapis t = new Tapis();
        t.setDirection(d);
        plateau.setMachine(x, y, t);
    }

    public void placerPoubelle(int x, int y) {
        plateau.setMachine(x, y, new Poubelle());
    }

    public void supprimerMachine(int x, int y) {
        plateau.setMachine(x, y, null);
    }

}
