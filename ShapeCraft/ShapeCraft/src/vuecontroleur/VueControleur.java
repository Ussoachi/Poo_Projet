package vuecontroleur;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;


import modele.item.Item;
import modele.item.ItemColor;
import modele.item.ItemShape;
import modele.jeu.Jeu;
import modele.plateau.*;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle
 *
 */
public class VueControleur extends JFrame implements Observer {
    private Plateau plateau; // référence sur une classe de modèle
    private Jeu jeu;
    private final int sizeX; // taille de la grille affichée
    private final int sizeY;
    private static final int pxCase = 60; // nombre de pixel par case
    // icones affichées dans la grille
    private Image icoRouge;
    private Image icoTapisDroite;
    private Image icoTapisGauche;
    private Image icoTapisBas;
    private Image icoPoubelle;
    private Image icoMine;
    private Image icoRotateur;
    private Image icoDecoupeur;
    private Image icoPeinture;
    private Image icoEmpileur;
    private Image icoLivraison;

    private JComponent grilleIP;
    private JPanel panneauOutils;
    private JPanel panneauObjectifs;
    private JLabel labelObjectif;
    private JLabel labelProgression;
    private ImagePanel panneauFormeObjectif;
    private JButton btnPlayPause;

    private boolean mousePressed = false; // permet de mémoriser l'état de la souris

    private ImagePanel[][] tabIP; // cases graphiques

    private JButton[] boutonsOutils;
    private Jeu.Outil[] outilsBoutons;


    public VueControleur(Jeu _jeu) {
        jeu = _jeu;
        plateau = jeu.getPlateau();
        sizeX = plateau.SIZE_X;
        sizeY = plateau.SIZE_Y;

        chargerLesIcones();
        placerLesComposantsGraphiques();

        plateau.addObserver(this);

        mettreAJourAffichage();

    }


    private void chargerLesIcones() {

        icoRouge       = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/colors/red.png").getImage();
        icoTapisDroite = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/belt_top.png").getImage();
        icoTapisGauche = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/belt_left.png").getImage();
        icoTapisBas    = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/belt_right.png").getImage();
        icoPoubelle    = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/trash.png").getImage();
        icoMine        = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/miner.png").getImage();
        icoRotateur    = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/rotater.png").getImage();
        icoDecoupeur   = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/cutter.png").getImage();
        icoPeinture    = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/painter.png").getImage();
        icoEmpileur    = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/stacker.png").getImage();
        icoLivraison   = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/goal_acceptor.png").getImage();

    }



    private void placerLesComposantsGraphiques() {
        setTitle("ShapeCraft");
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- boîte à outils à gauche ---
        panneauOutils = creerPanneauOutils();
        add(panneauOutils, BorderLayout.WEST);

        // --- grille au centre (exactement comme dans l'original) ---
        grilleIP = new JPanel(new GridLayout(sizeY, sizeX));

        tabIP = new ImagePanel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                ImagePanel iP = new ImagePanel();

                tabIP[x][y] = iP;

                final int xx = x;
                final int yy = y;
                iP.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        mousePressed = false;
                        jeu.press(xx, yy);
                        System.out.println(xx + "-" + yy);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (mousePressed) {
                            jeu.slide(xx, yy);
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        mousePressed = true;
                        jeu.press(xx, yy);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        mousePressed = false;

                    }
                });


                grilleIP.add(iP);
            }
        }
        add(grilleIP, BorderLayout.CENTER);

        // --- panneau objectifs à droite ---
        panneauObjectifs = creerPanneauObjectifs();
        add(panneauObjectifs, BorderLayout.EAST);

        setSize(sizeX * pxCase + 220, sizeY * pxCase + 30);
        setLocationRelativeTo(null);
    }

    // --- Boîte à outils ---
    private JPanel creerPanneauOutils() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(40, 40, 45));
        p.setPreferredSize(new Dimension(110, sizeY * pxCase));

        JLabel titre = new JLabel("  Outils");
        titre.setForeground(Color.WHITE);
        titre.setFont(new Font("SansSerif", Font.BOLD, 12));
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(titre);
        p.add(Box.createVerticalStrut(4));

        // Boutons Play/Pause et Reset
        JPanel barreCtrl = new JPanel(new GridLayout(1, 2, 2, 0));
        barreCtrl.setBackground(new Color(40, 40, 45));
        barreCtrl.setMaximumSize(new Dimension(106, 26));

        btnPlayPause = new JButton("Pause");
        btnPlayPause.setFont(new Font("SansSerif", Font.PLAIN, 10));
        btnPlayPause.setBackground(new Color(60, 120, 60));
        btnPlayPause.setForeground(Color.WHITE);
        btnPlayPause.setFocusPainted(false);
        btnPlayPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeu.togglePause();
                if (jeu.isEnPause()) {
                    btnPlayPause.setText("Play");
                    btnPlayPause.setBackground(new Color(170, 110, 30));
                } else {
                    btnPlayPause.setText("Pause");
                    btnPlayPause.setBackground(new Color(60, 120, 60));
                }
            }
        });

        JButton btnReset = new JButton("Reset");
        btnReset.setFont(new Font("SansSerif", Font.PLAIN, 10));
        btnReset.setBackground(new Color(140, 40, 40));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeu.reset();
                btnPlayPause.setText("Pause");
                btnPlayPause.setBackground(new Color(60, 120, 60));
            }
        });

        barreCtrl.add(btnPlayPause);
        barreCtrl.add(btnReset);
        p.add(barreCtrl);
        p.add(Box.createVerticalStrut(6));

        // Définition des outils
        outilsBoutons = new Jeu.Outil[]{
            Jeu.Outil.TAPIS_NORD, Jeu.Outil.TAPIS_SUD,
            Jeu.Outil.TAPIS_EST,  Jeu.Outil.TAPIS_OUEST,
            Jeu.Outil.MINE,
            Jeu.Outil.ROTATEUR,   Jeu.Outil.DECOUPEUR,
            Jeu.Outil.PEINTURE_ROUGE, Jeu.Outil.PEINTURE_VERT, Jeu.Outil.PEINTURE_BLEU,
            Jeu.Outil.EMPILEUR,   Jeu.Outil.SUPPRIMER
        };
        String[] libelles = {
            "Tapis N", "Tapis S", "Tapis E", "Tapis O",
            "Mine",
            "Rotateur", "Decoupeur",
            "Peinture R", "Peinture V", "Peinture B",
            "Empileur", "Supprimer"
        };

        boutonsOutils = new JButton[outilsBoutons.length];
        for (int i = 0; i < outilsBoutons.length; i++) {
            final Jeu.Outil outil = outilsBoutons[i];
            final int idx = i;
            JButton btn = new JButton(libelles[i]);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(106, 24));
            btn.setFocusPainted(false);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 10));
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jeu.setOutilCourant(outil);
                    surbrillanceBouton(idx);
                }
            });
            boutonsOutils[i] = btn;
            p.add(btn);
            p.add(Box.createVerticalStrut(2));
        }
        surbrillanceBouton(0);
        return p;
    }

    private void surbrillanceBouton(int idx) {
        for (int i = 0; i < boutonsOutils.length; i++) {
            if (i == idx) {
                boutonsOutils[i].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
            } else {
                boutonsOutils[i].setBorder(UIManager.getBorder("Button.border"));
            }
        }
    }

    // --- Panneau objectifs ---
    private JPanel creerPanneauObjectifs() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(30, 30, 35));
        p.setPreferredSize(new Dimension(110, sizeY * pxCase));

        JLabel titre = new JLabel("  Objectif");
        titre.setForeground(Color.WHITE);
        titre.setFont(new Font("SansSerif", Font.BOLD, 12));
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(titre);
        p.add(Box.createVerticalStrut(6));

        labelObjectif = new JLabel("  1 / 3");
        labelObjectif.setForeground(new Color(100, 220, 100));
        labelObjectif.setFont(new Font("SansSerif", Font.BOLD, 11));
        p.add(labelObjectif);
        p.add(Box.createVerticalStrut(4));

        panneauFormeObjectif = new ImagePanel();
        panneauFormeObjectif.setPreferredSize(new Dimension(90, 90));
        panneauFormeObjectif.setMaximumSize(new Dimension(90, 90));
        panneauFormeObjectif.setMinimumSize(new Dimension(90, 90));
        p.add(panneauFormeObjectif);
        p.add(Box.createVerticalStrut(4));

        labelProgression = new JLabel("  0 / 5");
        labelProgression.setForeground(Color.LIGHT_GRAY);
        labelProgression.setFont(new Font("SansSerif", Font.PLAIN, 11));
        p.add(labelProgression);

        return p;
    }

    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabIP)
     */
    private void mettreAJourAffichage() {

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                tabIP[x][y].setBackground((Image) null);
                tabIP[x][y].setFront(null);
                tabIP[x][y].setShape(null);
                tabIP[x][y].setGisement(null);
                tabIP[x][y].setEstZoneLivraison(false);

                Case c = plateau.getCases()[x][y];

                // affichage du gisement si présent
                if (c.getGisement() instanceof ItemShape) {
                    tabIP[x][y].setGisement((ItemShape) c.getGisement());
                }

                Machine m = c.getMachine();

                if (m != null) {

                    if (m instanceof ZoneLivraison) {
                        tabIP[x][y].setBackground(icoLivraison);
                        tabIP[x][y].setEstZoneLivraison(true);
                    } else if (m instanceof Tapis) {
                        switch (m.getDirection()) {
                            case North: tabIP[x][y].setBackground(icoTapisDroite); break;
                            case South: tabIP[x][y].setBackground(icoTapisBas);   break;
                            case East:  tabIP[x][y].setBackground(icoTapisBas);   break;
                            case West:  tabIP[x][y].setBackground(icoTapisGauche);break;
                            default:    tabIP[x][y].setBackground(icoTapisDroite); break;
                        }
                    } else if (m instanceof Poubelle) {
                        tabIP[x][y].setBackground(icoPoubelle);
                    } else if (m instanceof Mine) {
                        tabIP[x][y].setBackground(icoMine);
                    } else if (m instanceof Rotateur) {
                        tabIP[x][y].setBackground(icoRotateur);
                    } else if (m instanceof Decoupeur) {
                        tabIP[x][y].setBackground(icoDecoupeur);
                    } else if (m instanceof Peinture) {
                        tabIP[x][y].setBackground(icoPeinture);
                    } else if (m instanceof Empileur) {
                        tabIP[x][y].setBackground(icoEmpileur);
                    }

                    Item current = m.getCurrent();

                    if (current instanceof ItemShape) {
                        tabIP[x][y].setShape((ItemShape) current);
                    }
                    if (current instanceof ItemColor) {
                        // tabIP[x][y].setFront(); TODO
                    }

                }

            }
        }

        // mise à jour panneau objectifs
        ZoneLivraison zl = plateau.getZoneLivraison();
        if (zl != null) {
            if (zl.isPartieGagnee()) {
                labelObjectif.setText("  GAGNE !");
                labelProgression.setText("  Bravo !");
            } else {
                int idx = zl.getObjectifCourant();
                labelObjectif.setText("  " + (idx+1) + " / 3");
                labelProgression.setText("  " + zl.getQuantiteLivree(idx) + " / " + zl.getQuantiteRequise(idx));
                panneauFormeObjectif.setShape(zl.getObjectif(idx));
            }
        }

        grilleIP.repaint();
        if (panneauFormeObjectif != null) panneauFormeObjectif.repaint();

    }

    @Override
    public void update(Observable o, Object arg) {

        SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        mettreAJourAffichage();
                    }
                }); 

    }
}
