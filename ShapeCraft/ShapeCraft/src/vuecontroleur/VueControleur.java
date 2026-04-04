package vuecontroleur;

import java.awt.*;
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
    private Plateau plateau; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)
    private Jeu jeu;
    private final int sizeX; // taille de la grille affichée
    private final int sizeY;
    private static final int pxCase = 82; // nombre de pixel par case
    // icones affichées dans la grille
    private Image icoRouge;
    private Image icoTapisDroite;
    private Image icoTapisGauche;
    private Image icoTapisHaut;
    private Image icoTapisBas;
    private Image icoPoubelle;
    private Image icoMine;
    private Image icoDecoupeur;
    private Image icoEmpileur;
    private Image icoPeinture;
    private Image icoRotation;
    private Image icoLivraison;
    private JComponent grilleIP;
    private JPanel boiteOutils;
    private JButton[] boutonsOutils;
    private Outil outilSelectionne = Outil.TAPIS_NORD;

    private JLabel labelObjectifNumero;
    private JLabel labelObjectifProgression;
    private ImagePanel panneauFormeObjectif;


    private JButton boutonPlayPause;
    private JButton boutonReset;

    private boolean mousePressed = false; // permet de mémoriser l'état de la souris

    private ImagePanel[][] tabIP; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône background et front, suivant ce qui est présent dans le modèle)


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

        icoRouge = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/colors/blue.png").getImage();
        icoTapisDroite = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/belt_top.png").getImage();
        icoTapisGauche = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/belt_left.png").getImage();
        icoTapisHaut = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/belt_up.png").getImage();
        icoTapisBas = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/belt_down.png").getImage();
        icoPoubelle = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/trash.png").getImage();
        icoMine = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/miner.png").getImage();
        icoDecoupeur = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/cutter.png").getImage();
        icoEmpileur = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/stacker.png").getImage();
        icoPeinture = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/painter.png").getImage();
        icoRotation = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/rotator.png").getImage();
        icoLivraison = new ImageIcon("./ShapeCraft/ShapeCraft/data/sprites/buildings/delivery.png").getImage();
    }



    private void placerLesComposantsGraphiques() {
        setTitle("ShapeCraft");
        setResizable(true);
        setSize(sizeX * pxCase, sizeX * pxCase);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre
        setLayout(new BorderLayout());


        creerBoiteOutils();
        creerPanneauObjectif();

        grilleIP = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille


        tabIP = new ImagePanel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                ImagePanel iP = new ImagePanel();

                tabIP[x][y] = iP; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )

                final int xx = x; // permet de compiler la classe anonyme ci-dessous
                final int yy = y;
                // écouteur de clics
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
        add(grilleIP);
        add(boiteOutils, BorderLayout.WEST);
        add(grilleIP, BorderLayout.CENTER);
    }

    private void creerBoiteOutils() {
        boiteOutils = new JPanel();
        boiteOutils.setLayout(new BoxLayout(boiteOutils, BoxLayout.Y_AXIS));
        boiteOutils.setPreferredSize(new Dimension(120, sizeY * pxCase));
        boiteOutils.setBackground(Color.DARK_GRAY);
        boiteOutils.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        JLabel titre = new JLabel("Outils");
        titre.setForeground(Color.WHITE);
        titre.setFont(new Font("Arial", Font.BOLD, 14));
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        boiteOutils.add(titre);
        boiteOutils.add(Box.createVerticalStrut(12));

        Outil[] tousLesOutils = Outil.values();
        boutonsOutils = new JButton[tousLesOutils.length];

        for (int i = 0; i < tousLesOutils.length; i++) {
            final Outil outil = tousLesOutils[i];

            JButton btn = new JButton(getNomOutil(outil));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(110, 38));
            btn.setFocusPainted(false);

            btn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    outilSelectionne = outil;
                    mettreAJourSurbrillanceBoutons();
                }
            });

            boutonsOutils[i] = btn;
            boiteOutils.add(btn);
            boiteOutils.add(Box.createVerticalStrut(5));
        }

        mettreAJourSurbrillanceBoutons();
    }

    private void utiliserOutil(int x, int y) {
        switch (outilSelectionne) {
            case MINE:
                jeu.placerMine(x, y);
                break;
            case TAPIS_NORD:
                jeu.placerTapis(x, y, Direction.North);
                break;
            case TAPIS_SUD:
                jeu.placerTapis(x, y, Direction.South);
                break;
            case TAPIS_EST:
                jeu.placerTapis(x, y, Direction.East);
                break;
            case TAPIS_OUEST:
                jeu.placerTapis(x, y, Direction.West);
                break;
            case POUBELLE:
                jeu.placerPoubelle(x, y);
                break;
            case SUPPRIMER:
                jeu.supprimerMachine(x, y);
                break;
        }
    }


    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabIP)
     */
    private void mettreAJourAffichage() {


        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                tabIP[x][y].setBackground((Image) null);

                tabIP[x][y].setFront(null);

                Case c = plateau.getCases()[x][y];

                Machine m = c.getMachine();

                if (m != null) {

                    if (m instanceof Tapis) {
                        tabIP[x][y].setBackground(icoTapisDroite);
                    } else if (m instanceof Poubelle) {
                        tabIP[x][y].setBackground(icoPoubelle);
                    } else if (m instanceof Mine) {
                        tabIP[x][y].setBackground(icoMine);
                    }

                    Item current = m.getCurrent();

                    if (current instanceof ItemShape) {
                        tabIP[x][y].setShape((ItemShape) current);
                    }
                    if (current instanceof ItemColor) {
                        // tabIP[x][y].setFront(); TODO : placer l'icone des couleurs approprié
                    }

                }





            }
        }
        grilleIP.repaint();


    }

    private void mettreAJourSurbrillanceBoutons() {
        Outil[] tousLesOutils = Outil.values();
        for (int i = 0; i < boutonsOutils.length; i++) {
            if (tousLesOutils[i] == outilSelectionne) {
                boutonsOutils[i].setBackground(Color.ORANGE);
            } else {
                boutonsOutils[i].setBackground(null);
            }
        }
    }

    private String getNomOutil(Outil outil) {
        switch (outil) {
            case MINE:        return "Mine";
            case TAPIS_NORD:  return "Tapis Nord";
            case TAPIS_SUD:   return "Tapis Sud";
            case TAPIS_EST:   return "Tapis Est";
            case TAPIS_OUEST: return "Tapis Ouest";
            case POUBELLE:    return "Poubelle";
            case SUPPRIMER:   return "Supprimer";
            default:          return "?";
        }
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

    private void creerPanneauObjectif() {
        JPanel panneauBas = new JPanel();
        panneauBas.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 8));
        panneauBas.setBackground(new Color(30, 30, 30));
        panneauBas.setPreferredSize(new Dimension(sizeX * pxCase, 80));

       
        boutonPlayPause = new JButton("⏸ Pause");
        boutonPlayPause.setFocusPainted(false);
        boutonPlayPause.setPreferredSize(new Dimension(100, 40));
        boutonPlayPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                jeu.togglePause();
                if (jeu.isEnPause()) {
                    boutonPlayPause.setText("▶ Play");
                } else {
                    boutonPlayPause.setText("⏸ Pause");
                }
            }
        });

        boutonReset = new JButton("↺ Reset");
        boutonReset.setFocusPainted(false);
        boutonReset.setPreferredSize(new Dimension(100, 40));
        boutonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {

                boutonPlayPause.setText("⏸ Pause");
            }
        });


        labelObjectifNumero = new JLabel("Objectif 1/3");
        labelObjectifNumero.setForeground(Color.WHITE);
        labelObjectifNumero.setFont(new Font("Arial", Font.BOLD, 13));

        labelObjectifProgression = new JLabel("0 / 5");
        labelObjectifProgression.setForeground(Color.ORANGE);
        labelObjectifProgression.setFont(new Font("Arial", Font.BOLD, 13));


        panneauFormeObjectif = new ImagePanel();
        panneauFormeObjectif.setPreferredSize(new Dimension(60, 60));
        panneauFormeObjectif.setShape(jeu.getZoneLivraison().getObjectifActuel());

        // Assemblage
        panneauBas.add(boutonPlayPause);
        panneauBas.add(boutonReset);
        panneauBas.add(Box.createHorizontalStrut(20));
        panneauBas.add(labelObjectifNumero);
        panneauBas.add(panneauFormeObjectif);
        panneauBas.add(labelObjectifProgression);

        add(panneauBas, BorderLayout.SOUTH);
    }
}