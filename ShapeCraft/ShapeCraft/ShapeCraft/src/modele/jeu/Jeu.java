package modele.jeu;

import modele.plateau.*;
import modele.item.*;

public class Jeu extends Thread {


    private Plateau plateau;

    private boolean enCours;
    private boolean enPause;

    private static final int DELAI_SIMULATION = 80;

    public Jeu() {
        plateau = new Plateau();

        Mine mine = new Mine();
        mine.setD(Direction.East);
        plateau.setMachine(2, 5, mine);

        plateau.setMachine(6, 5, new Coupeur(Direction.West, Direction.East));
        plateau.setMachine(8, 5, new Rotateur());

        ZoneLivraison zone = new ZoneLivraison(10, new ItemShape("CrCrCbCb"));
        plateau.setMachine(12, 5, zone);

        plateau.setMachine(5, 10, new Poubelle());

        enCours = true;
        enPause = false;

        start();
    }


    public Plateau getPlateau() {
        return plateau;
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
}
