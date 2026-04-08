import vuecontroleur.VueControleur;
import vuecontroleur.Controleur;
import modele.jeu.Jeu;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {


        Runnable r = new Runnable() {
            public void run() {
                Jeu jeu = new Jeu();
                Controleur ctrl = new Controleur(jeu);
                VueControleur vc = new VueControleur(jeu, ctrl);
                vc.setVisible(true);
            }
        };

        SwingUtilities.invokeLater(r);
    }
}
