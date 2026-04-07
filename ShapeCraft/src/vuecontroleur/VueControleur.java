package vuecontroleur;

import modele.item.Item;
import modele.item.ItemShape;
import modele.jeu.Jeu;
import modele.plateau.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

public class VueControleur extends JFrame implements Observer {

    private final Plateau plateau;
    private final Jeu     jeu;
    private final int     sizeX;
    private final int     sizeY;
    private static final int PX_CASE = 82;

    private Image   icoZoneLivraison;
    private Image   icoPoubelle;
    private Image   icoMine;
    private Image[] icoTapisForward = new Image[14];
    private Image[] icoTapisDroite  = new Image[14];
    private Image[] icoTapisGauche  = new Image[14];

    private final JComponent     grilleIP;
    private final ImagePanel[][] tabIP;
    private boolean sourisGauchePressed = false;

    // ------------------------------------------------------------------ init

    public VueControleur(Jeu _jeu) {
        jeu     = _jeu;
        plateau = jeu.getPlateau();
        sizeX   = Plateau.SIZE_X;
        sizeY   = Plateau.SIZE_Y;

        chargerLesIcones();

        grilleIP = new JPanel(new GridLayout(sizeY, sizeX));
        tabIP    = new ImagePanel[sizeX][sizeY];
        placerLesComposantsGraphiques();

        plateau.addObserver(this);
        mettreAJourAffichage();
    }

    private void chargerLesIcones() {
        icoZoneLivraison = charger("./data/sprites/buildings/hub.png");
        icoPoubelle      = charger("./data/sprites/buildings/trash.png");
        icoMine          = charger("./data/sprites/buildings/miner.png");
        for (int i = 0; i < 14; i++) {
            icoTapisForward[i] = charger("./data/sprites/belt/built/forward_" + i + ".png");
            icoTapisDroite[i]  = charger("./data/sprites/belt/built/right_"   + i + ".png");
            icoTapisGauche[i]  = charger("./data/sprites/belt/built/left_"    + i + ".png");
        }
    }

    private Image charger(String chemin) { return new ImageIcon(chemin).getImage(); }

    private void placerLesComposantsGraphiques() {
        setTitle("ShapeCraft");
        setSize(sizeX * PX_CASE, sizeY * PX_CASE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                ImagePanel ip = new ImagePanel();
                tabIP[x][y] = ip;
                final int fx = x, fy = y;

                ip.addMouseListener(new MouseAdapter() {
                    @Override public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            sourisGauchePressed = true;
                            jeu.press(fx, fy);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            jeu.supprimerMachine(fx, fy);
                        }
                    }
                    @Override public void mouseReleased(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) sourisGauchePressed = false;
                    }
                    @Override public void mouseEntered(MouseEvent e) {
                        if (sourisGauchePressed) jeu.slide(fx, fy);
                    }
                });

                grilleIP.add(ip);
            }
        }
        add(grilleIP);
    }

    // ------------------------------------------------------------------ affichage

    private void mettreAJourAffichage() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                ImagePanel ip = tabIP[x][y];
                ip.reset();

                Case    c = plateau.getCases()[x][y];
                Machine m = c.getMachine();

                // Gisement en fond de case
                if (c.estGisement()) {
                    ip.setImageGisement(imageGisement(c));
                }

                if (m == null) continue;

                // Machine
                if (m instanceof Tapis) {
                    Tapis t = (Tapis) m;
                    ip.setImageBackground(imageTapis(t));
                    ip.setTypeTapis(t.getTypeForme());
                    ip.setDirectionTapis(t.getD());
                    if (t.getEntree() != null) ip.setDirectionEntree(t.getEntree());
                    if (t.getCurrent() instanceof ItemShape) {
                        ItemShape is = (ItemShape) t.getCurrent();
                        ip.setShape(is);
                        ip.setProgressInCell(is.getProgressInCell());
                    }

                } else if (m instanceof Mine) {
                    ip.setImageBackground(icoMine);

                } else if (m instanceof Poubelle) {
                    ip.setImageBackground(icoPoubelle);

                } else if (m instanceof ZoneLivraison) {
                    ZoneLivraison zone = (ZoneLivraison) m;
                    ip.setImageBackground(icoZoneLivraison);
                    ip.setLabel(zone.getCompteur() + "/" + zone.getObjectif());
                    if (zone.getFormeCible() != null) ip.setShape(zone.getFormeCible());
                }

                // Item en transit (hors tapis, déjà traité ci-dessus)
                if (!(m instanceof Tapis)) {
                    Item current = m.getCurrent();
                    if (current instanceof ItemShape) ip.setShape((ItemShape) current);
                }
            }
        }
        grilleIP.repaint();
    }

    // ------------------------------------------------------------------ gisement

    private Image imageGisement(Case c) {
        if (c.getCouleurGisement() == null) return null;
        switch (c.getCouleurGisement()) {
            case Red:    return charger("./data/sprites/colors/red.png");
            case Blue:   return charger("./data/sprites/colors/blue.png");
            case Green:  return charger("./data/sprites/colors/green.png");
            case Yellow: return charger("./data/sprites/colors/yellow.png");
            case Cyan:   return charger("./data/sprites/colors/cyan.png");
            case Purple: return charger("./data/sprites/colors/purple.png");
            case White:  return charger("./data/sprites/colors/white.png");
            default:     return charger("./data/sprites/colors/uncolored.png");
        }
    }

    // ------------------------------------------------------------------ tapis

    private Image imageTapis(Tapis tapis) {
        Direction entree = tapis.getEntree();
        Direction sortie = tapis.getD();
        int       etape  = tapis.getEtapeAnimation();

        if (sortie == null) return icoTapisForward[etape];

        if (entree == null || sortie == entree.getOpposite())
            return imageTapisDroit(sortie, etape);

        // Virage droite — base : South→East
        if (entree == Direction.South && sortie == Direction.East)  return icoTapisDroite[etape];
        if (entree == Direction.West  && sortie == Direction.South) return rotation(icoTapisDroite[etape],  90);
        if (entree == Direction.North && sortie == Direction.West)  return rotation(icoTapisDroite[etape], 180);
        if (entree == Direction.East  && sortie == Direction.North) return rotation(icoTapisDroite[etape], -90);

        // Virage gauche — base : North→East
        if (entree == Direction.North && sortie == Direction.East)  return icoTapisGauche[etape];
        if (entree == Direction.East  && sortie == Direction.South) return rotation(icoTapisGauche[etape], -90);
        if (entree == Direction.South && sortie == Direction.West)  return rotation(icoTapisGauche[etape], 180);
        if (entree == Direction.West  && sortie == Direction.North) return rotation(icoTapisGauche[etape],  90);

        return imageTapisDroit(sortie, etape);
    }

    private Image imageTapisDroit(Direction sortie, int etape) {
        if (sortie == Direction.North) return icoTapisForward[etape];
        if (sortie == Direction.East)  return rotation(icoTapisForward[etape],  90);
        if (sortie == Direction.South) return rotation(icoTapisForward[etape], 180);
        if (sortie == Direction.West)  return rotation(icoTapisForward[etape], -90);
        return icoTapisForward[etape];
    }

    private Image rotation(Image src, double angleDeg) {
        int w = src.getWidth(null), h = src.getHeight(null);
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dest.createGraphics();
        g2.setTransform(AffineTransform.getRotateInstance(Math.toRadians(angleDeg), w / 2.0, h / 2.0));
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return dest;
    }

    // ------------------------------------------------------------------ Observer

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