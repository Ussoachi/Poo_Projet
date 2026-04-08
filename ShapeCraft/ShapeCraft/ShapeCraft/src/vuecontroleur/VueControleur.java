package vuecontroleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

import modele.item.Item;
import modele.item.ItemShape;
import modele.jeu.Jeu;
import modele.plateau.*;

public class VueControleur extends JFrame implements Observer {

    private Plateau plateau;
    private Controleur controleur;

    private final int sizeX;
    private final int sizeY;
    private static final int pxCase = 82;

    private Image[] icoTapisForward;
    private Image[] icoTapisDroite;
    private Image[] icoTapisGauche;

    private Image icoMine, icoCoupeur, icoRotateur, icoPoubelle, icoZone;

    private ImagePanel[][] tabIP;

    public VueControleur(Jeu jeu, Controleur controleur) {
        this.controleur = controleur;
        this.plateau = jeu.getPlateau();

        sizeX = Plateau.SIZE_X;
        sizeY = Plateau.SIZE_Y;

        chargerImages();
        initUI();

        plateau.addObserver(this);
        update(null, null);
    }


    private void chargerImages() {
        icoMine     = new ImageIcon("./ShapeCraft/data/sprites/buildings/miner.png").getImage();
        icoCoupeur  = new ImageIcon("./ShapeCraft/data/sprites/buildings/cutter.png").getImage();
        icoRotateur = new ImageIcon("./ShapeCraft/data/sprites/buildings/rotater.png").getImage();
        icoPoubelle = new ImageIcon("./ShapeCraft/data/sprites/buildings/trash.png").getImage();
        icoZone     = new ImageIcon("./ShapeCraft/data/sprites/buildings/hub.png").getImage();

        icoTapisForward = new Image[14];
        icoTapisDroite  = new Image[14];
        icoTapisGauche  = new Image[14];

        for (int i = 0; i < 14; i++) {
            icoTapisForward[i] = new ImageIcon("./ShapeCraft/data/sprites/belt/built/forward_" + i + ".png").getImage();
            icoTapisDroite[i]  = new ImageIcon("./ShapeCraft/data/sprites/belt/built/right_"   + i + ".png").getImage();
            icoTapisGauche[i]  = new ImageIcon("./ShapeCraft/data/sprites/belt/built/left_"    + i + ".png").getImage();
        }
    }


    private void initUI() {
        setTitle("ShapeCraft");
        setSize(sizeX * pxCase, sizeY * pxCase);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JToolBar bar = new JToolBar();

        JButton btnTapis    = new JButton("Tapis");
        JButton btnMine     = new JButton("Mine");
        JButton btnCoupeur  = new JButton("Coupeur");
        JButton btnRotateur = new JButton("Rotateur");
        JButton btnPoubelle = new JButton("Poubelle");

        btnTapis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controleur.setOutil(Controleur.Outil.TAPIS);
            }
        });
        btnMine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controleur.setOutil(Controleur.Outil.MINE);
            }
        });
        btnCoupeur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controleur.setOutil(Controleur.Outil.COUPEUR);
            }
        });
        btnRotateur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controleur.setOutil(Controleur.Outil.ROTATEUR);
            }
        });
        btnPoubelle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controleur.setOutil(Controleur.Outil.POUBELLE);
            }
        });

        bar.add(btnTapis);
        bar.add(btnMine);
        bar.add(btnCoupeur);
        bar.add(btnRotateur);
        bar.add(btnPoubelle);

        add(bar, BorderLayout.NORTH);

        JPanel grille = new JPanel(new GridLayout(sizeY, sizeX));
        tabIP = new ImagePanel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {

                ImagePanel ip = new ImagePanel();
                tabIP[x][y] = ip;

                final int xx = x;
                final int yy = y;

                ip.addMouseListener(new MouseAdapter() {

                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            controleur.setLeftPressed(true);
                            controleur.press(xx, yy);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            if (e.isShiftDown()) {
                                controleur.rotateMachine(xx, yy);
                            } else {
                                controleur.supprimerMachine(xx, yy);
                            }
                        }
                    }

                    public void mouseReleased(MouseEvent e) {
                        controleur.setLeftPressed(false);
                    }

                    public void mouseEntered(MouseEvent e) {
                        if (controleur.isLeftPressed()) {
                            controleur.slide(xx, yy);
                        }
                    }
                });

                grille.add(ip);
            }
        }

        add(grille, BorderLayout.CENTER);
    }

    private Image rotate(Image img, double angle) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.toRadians(angle), w / 2.0, h / 2.0);
        g.setTransform(at);
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return out;
    }

    private Image getMachineImage(Image base, Direction d) {
        if (d == Direction.North) return base;
        if (d == Direction.East)  return rotate(base,  90);
        if (d == Direction.South) return rotate(base, 180);
        if (d == Direction.West)  return rotate(base, -90);
        return base;
    }

    private Image getTapisImage(Tapis t, int e) {
        Direction in  = t.getEntree();
        Direction out = t.getD();

        if (out == null) return icoTapisForward[e];

        if (in == null || out == in.getOpposite()) {
            return getStraight(out, e);
        }

        if (in == Direction.South && out == Direction.East)  return icoTapisDroite[e];
        if (in == Direction.West  && out == Direction.South) return rotate(icoTapisDroite[e],  90);
        if (in == Direction.North && out == Direction.West)  return rotate(icoTapisDroite[e], 180);
        if (in == Direction.East  && out == Direction.North) return rotate(icoTapisDroite[e], -90);

        if (in == Direction.North && out == Direction.East)  return icoTapisGauche[e];
        if (in == Direction.East  && out == Direction.South) return rotate(icoTapisGauche[e], -90);
        if (in == Direction.South && out == Direction.West)  return rotate(icoTapisGauche[e], 180);
        if (in == Direction.West  && out == Direction.North) return rotate(icoTapisGauche[e],  90);

        return getStraight(out, e);
    }

    private Image getStraight(Direction d, int e) {
        if (d == Direction.North) return icoTapisForward[e];
        if (d == Direction.East)  return rotate(icoTapisForward[e],  90);
        if (d == Direction.South) return rotate(icoTapisForward[e], 180);
        if (d == Direction.West)  return rotate(icoTapisForward[e], -90);
        return icoTapisForward[e];
    }


    @Override
    public void update(Observable o, Object arg) {

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                ImagePanel ip = tabIP[x][y];
                ip.reset();

                Machine m = plateau.getCases()[x][y].getMachine();
                if (m == null) continue;

                if (m instanceof Mine) {
                    ip.setImageBackground(getMachineImage(icoMine, m.getD()));
                } else if (m instanceof Coupeur) {
                    ip.setImageBackground(getMachineImage(icoCoupeur, m.getD()));
                } else if (m instanceof Rotateur) {
                    ip.setImageBackground(getMachineImage(icoRotateur, m.getD()));
                } else if (m instanceof Poubelle) {
                    ip.setImageBackground(getMachineImage(icoPoubelle, m.getD()));
                } else if (m instanceof ZoneLivraison) {
                    ZoneLivraison zone = (ZoneLivraison) m;
                    ip.setImageBackground(icoZone);
                    ip.setLabel(zone.getCompteur() + "/" + zone.getObjectif());
                    if (zone.getFormeCible() != null) {
                        ip.setShape(zone.getFormeCible());
                    }
                } else if (m instanceof Tapis) {
                    Tapis t = (Tapis) m;
                    ip.setImageBackground(getTapisImage(t, t.getEtapeAnimation()));
                }

                Item it = m.getCurrent();
                if (it instanceof ItemShape) {
                    ItemShape is = (ItemShape) it;
                    ip.setShape(is);
                    ip.setProgressInCell(is.getProgressInCell());
                }
            }
        }

        repaint();
    }
}
