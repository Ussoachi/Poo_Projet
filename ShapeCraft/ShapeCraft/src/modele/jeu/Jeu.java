package modele.jeu;

import modele.item.Color;
import modele.item.ItemShape;
import modele.plateau.*;

public class Jeu extends Thread{
    private Plateau plateau;
    private boolean enPause = false;

    // Outil sélectionné dans la boîte à outils
    public enum Outil {
        TAPIS_NORD, TAPIS_SUD, TAPIS_EST, TAPIS_OUEST,
        MINE, ROTATEUR, DECOUPEUR,
        PEINTURE_ROUGE, PEINTURE_VERT, PEINTURE_BLEU,
        EMPILEUR, SUPPRIMER
    }
    private Outil outilCourant = Outil.TAPIS_NORD;

    public Jeu() {
        plateau = new Plateau();

        // 3 objectifs de production consécutifs
        ItemShape[] objectifs = new ItemShape[3];
        int[] quantites = new int[3];
        objectifs[0] = new ItemShape("CrCrCrCr");
        quantites[0] = 5;
        objectifs[1] = new ItemShape("CbCbCbCb");
        quantites[1] = 5;
        objectifs[2] = new ItemShape("RgRgRgRg");
        quantites[2] = 3;
        ZoneLivraison zl = new ZoneLivraison(objectifs, quantites);
        plateau.setZoneLivraison(zl, 8, 8);

        start();
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public Outil getOutilCourant() {
        return outilCourant;
    }

    public void setOutilCourant(Outil o) {
        outilCourant = o;
    }

    public boolean isEnPause() {
        return enPause;
    }

    public synchronized void togglePause() {
        enPause = !enPause;
        if (!enPause) notifyAll();
    }

    public synchronized void reset() {
        // efface toutes les machines sauf la zone de livraison
        Case[][] cases = plateau.getCases();
        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                Machine m = cases[x][y].getMachine();
                if (m != null && !(m instanceof ZoneLivraison)) {
                    plateau.removeMachine(x, y);
                }
            }
        }
    }

    public void press(int x, int y) {
        // protéger la zone de livraison
        Machine existante = plateau.getCases()[x][y].getMachine();
        if (existante instanceof ZoneLivraison) return;

        switch (outilCourant) {
            case TAPIS_NORD: { Tapis t = new Tapis(); t.setDirection(Direction.North); plateau.setMachine(x, y, t); break; }
            case TAPIS_SUD:  { Tapis t = new Tapis(); t.setDirection(Direction.South); plateau.setMachine(x, y, t); break; }
            case TAPIS_EST:  { Tapis t = new Tapis(); t.setDirection(Direction.East);  plateau.setMachine(x, y, t); break; }
            case TAPIS_OUEST:{ Tapis t = new Tapis(); t.setDirection(Direction.West);  plateau.setMachine(x, y, t); break; }
            case MINE:       { Mine m = new Mine();   m.setDirection(Direction.South); plateau.setMachine(x, y, m); break; }
            case ROTATEUR:   { Rotateur r = new Rotateur(); r.setDirection(Direction.North); plateau.setMachine(x, y, r); break; }
            case DECOUPEUR:  { Decoupeur d = new Decoupeur(); d.setDirection(Direction.North); plateau.setMachine(x, y, d); break; }
            case PEINTURE_ROUGE: { Peinture p = new Peinture(Color.Red);   p.setDirection(Direction.North); plateau.setMachine(x, y, p); break; }
            case PEINTURE_VERT:  { Peinture p = new Peinture(Color.Green); p.setDirection(Direction.North); plateau.setMachine(x, y, p); break; }
            case PEINTURE_BLEU:  { Peinture p = new Peinture(Color.Blue);  p.setDirection(Direction.North); plateau.setMachine(x, y, p); break; }
            case EMPILEUR:   { Empileur e = new Empileur(); e.setDirection(Direction.North); plateau.setMachine(x, y, e); break; }
            case SUPPRIMER:  { plateau.removeMachine(x, y); break; }
            default: break;
        }
    }

    public void slide(int x, int y) {
        // glissement seulement pour les tapis et supprimer
        switch (outilCourant) {
            case TAPIS_NORD:
            case TAPIS_SUD:
            case TAPIS_EST:
            case TAPIS_OUEST:
            case SUPPRIMER:
                press(x, y);
                break;
            default:
                break;
        }
    }

    public void run() {
        jouerPartie();
    }

    public void jouerPartie() {
        while(true) {
            try {
                synchronized (this) {
                    while (enPause) wait();
                }
                plateau.run();
                Thread.sleep(800);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
