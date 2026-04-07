package vuecontroleur;

import modele.item.ItemShape;
import modele.item.SubShape;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private Image imgBackground;
    private Image imgFront;
    private ItemShape shape;
    private ItemShape gisement;       // gisement affiché en fond si pas de machine
    private boolean estZoneLivraison; // fond vert pour la zone de livraison


    public void setShape(ItemShape _shape) {
        shape = _shape;
    }

    public void setBackground(Image _imgBackground) {
        imgBackground = _imgBackground;
    }

    public void setFront(Image _imgFront) {
        imgFront = _imgFront;
    }

    public void setGisement(ItemShape g) {
        gisement = g;
    }

    public void setEstZoneLivraison(boolean b) {
        estZoneLivraison = b;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final int bordure= 1;
        final int xBack = bordure;
        final int yBack = bordure;
        final int widthBack = getWidth() - bordure*2;
        final int heigthBack = getHeight() - bordure*2;

        final int subPartWidth = widthBack / 4;
        final int subPartHeigth = heigthBack / 4;

        final int xFront = bordure + subPartWidth;
        final int yFront = bordure + subPartHeigth;
        final int widthFront = subPartWidth*2;
        final int heigthFront = subPartHeigth*2;

        // fond vert pour la zone de livraison
        if (estZoneLivraison) {
            g.setColor(new Color(30, 100, 30));
            g.fillRect(xBack, yBack, widthBack, heigthBack);
        }

        // cadre
        if (estZoneLivraison) {
            g.setColor(new Color(60, 200, 60));
        } else {
            g.setColor(Color.BLACK);
        }
        g.drawRoundRect(bordure, bordure, widthBack, heigthBack, bordure, bordure);

        if (imgBackground != null) {
            g.drawImage(imgBackground, xBack, yBack, widthBack, heigthBack, this);
        }

        if (imgFront != null) {
            g.drawImage(imgFront, xFront, yFront, widthFront, heigthFront, this);
        }

        // gisement affiché en petit si pas de machine posée
        if (gisement != null && imgBackground == null) {
            int mg = subPartWidth;
            dessinerShape(g, gisement, xBack + mg, yBack + mg, widthBack - mg*2, heigthBack - mg*2);
        }

        if (shape != null) {
            // TODO autres layers
            dessinerShape(g, shape, xFront, yFront, widthFront, heigthFront);
        }

    }

    private void dessinerShape(Graphics g, ItemShape s, int x, int y, int w, int h) {
        SubShape[] tabS = s.getSubShapes(ItemShape.Layer.one);
        modele.item.Color[] tabC = s.getColors(ItemShape.Layer.one);

        for (int i = 0; i < 4; i++) {
            SubShape ss = tabS[i];
            if (ss != SubShape.None) {
                // couleur
                if (tabC[i] != null) {
                    switch (tabC[i]) {
                        case Red:    g.setColor(new Color(220, 50, 50));  break;
                        case Green:  g.setColor(new Color(50, 200, 80));  break;
                        case Blue:   g.setColor(new Color(60, 100, 220)); break;
                        case Yellow: g.setColor(new Color(230, 210, 40)); break;
                        case Purple: g.setColor(new Color(160, 60, 200)); break;
                        case Cyan:   g.setColor(new Color(40, 200, 220)); break;
                        case White:  g.setColor(Color.WHITE);             break;
                        default:     g.setColor(Color.GRAY);              break;
                    }
                } else {
                    g.setColor(Color.GRAY);
                }

                // position du quadrant : 0=haut-droit, 1=bas-droit, 2=bas-gauche, 3=haut-gauche
                int qx = x + (w / 2) * ((i >> 1) ^ 1);
                int qy = y + (h / 2) * ((i & 1) ^ ((i >> 1) & 1));
                int qw = w / 2;
                int qh = h / 2;

                switch (ss) {
                    case Carre:
                        g.fillRect(qx, qy, qw, qh);
                        break;
                    case Circle:
                        g.fillOval(qx, qy, qw, qh);
                        break;
                    case Fan:
                        int[] anglesDepart = {0, 270, 180, 90};
                        g.fillArc(qx - qw/2, qy - qh/2, qw*2, qh*2, anglesDepart[i], 90);
                        break;
                    case Star:
                        int cx = qx + qw/2;
                        int cy = qy + qh/2;
                        int r = qw/2;
                        int rInt = r/2;
                        int n = 4;
                        int[] xp = new int[n*2];
                        int[] yp = new int[n*2];
                        for (int j = 0; j < n*2; j++) {
                            double angle = Math.PI / n * j - Math.PI / 2;
                            int rayon = (j % 2 == 0) ? r : rInt;
                            xp[j] = cx + (int)(rayon * Math.cos(angle));
                            yp[j] = cy + (int)(rayon * Math.sin(angle));
                        }
                        g.fillPolygon(xp, yp, n*2);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
