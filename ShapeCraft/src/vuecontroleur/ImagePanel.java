package vuecontroleur;

import modele.item.ItemShape;
import modele.item.SubShape;
import modele.plateau.Direction;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {

    private Image     imgBackground;
    private Image     imgGisement;
    private String    typeTapis      = "top";
    private Direction directionTapis  = Direction.North;
    private Direction directionEntree = Direction.South;

    private ItemShape shape;
    private double    progressInCell = 0.0;

    private ItemShape ghostShape;
    private int       ghostDrawX;
    private int       ghostDrawY;

    private String label;

    public ImagePanel() { setPreferredSize(new Dimension(82, 82)); }

    public void setImageBackground(Image img)   { imgBackground  = img; }
    public void setImageGisement(Image img)     { imgGisement    = img; }
    public void setTypeTapis(String t)          { typeTapis      = t;   }
    public void setDirectionTapis(Direction d)  { directionTapis  = d;  }
    public void setDirectionEntree(Direction d) { directionEntree = d;  }
    public void setShape(ItemShape s)           { shape          = s;   }
    public void setProgressInCell(double p)     { progressInCell = Math.max(0, Math.min(1, p)); }
    public void setLabel(String l)              { label          = l;   }

    public void setGhostAt(ItemShape s, int x, int y) { ghostShape = s; ghostDrawX = x; ghostDrawY = y; }
    public void clearGhost()                          { ghostShape = null; }

    public void reset() {
        imgBackground = null; imgGisement = null;
        shape = null; ghostShape = null; label = null;
        typeTapis = "top"; progressInCell = 0.0;
    }

    // ------------------------------------------------------------------ peinture

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int total = Math.min(getWidth(), getHeight());
        final int half  = total / 2;
        final int sF    = (total / 4) * 2;   // taille de la forme (50% de la case)

        // 0. Gisement
        if (imgGisement != null)
            g.drawImage(imgGisement, 1, 1, getWidth() - 2, getHeight() - 2, this);

        // 1. Machine
        if (imgBackground != null)
            g.drawImage(imgBackground, 1, 1, getWidth() - 2, getHeight() - 2, this);

        // 2. Ghost (débordement de la case voisine)
        if (ghostShape != null)
            drawShape(g, ghostShape, ghostDrawX, ghostDrawY, sF, sF);

        // 3. Forme principale animée
        if (shape != null) {
            int drawX, drawY;

            if (typeTapis.equals("top")) {
                // Tapis droit : la forme se déplace en ligne droite
                int offset = (int)((progressInCell - 0.5) * total);
                drawX = half - sF / 2;
                drawY = half - sF / 2;
                switch (directionTapis) {
                    case North: drawY = half - offset - sF / 2; break;
                    case South: drawY = half + offset - sF / 2; break;
                    case East:  drawX = half + offset - sF / 2; break;
                    case West:  drawX = half - offset - sF / 2; break;
                }
            } else {
                // Virage : interpolation linéaire entre entrée et sortie
                int ax = half, ay = half, cx = half, cy = half;
                switch (directionEntree) {
                    case North: ax = half;  ay = 0;     break;
                    case South: ax = half;  ay = total; break;
                    case East:  ax = total; ay = half;  break;
                    case West:  ax = 0;     ay = half;  break;
                }
                switch (directionTapis) {
                    case North: cx = half;  cy = 0;     break;
                    case South: cx = half;  cy = total; break;
                    case East:  cx = total; cy = half;  break;
                    case West:  cx = 0;     cy = half;  break;
                }
                drawX = (int)(ax + progressInCell * (cx - ax)) - sF / 2;
                drawY = (int)(ay + progressInCell * (cy - ay)) - sF / 2;
            }

            drawShape(g, shape, drawX, drawY, sF, sF);
        }

        // 4. Cadre
        g.setColor(new Color(150, 150, 150, 80));
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // 5. Label (compteur ZoneLivraison)
        if (label != null) {
            g.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g.getFontMetrics();
            int lx = getWidth()  - fm.stringWidth(label);
            int ly = getHeight() - 6;
            g.setColor(new Color(255, 255, 255, 180));
            g.fillRoundRect(lx - 3, ly - fm.getAscent(), fm.stringWidth(label) + 6, fm.getHeight(), 4, 4);
            g.setColor(Color.BLACK);
            g.drawString(label, lx, ly);
        }
    }

    // ------------------------------------------------------------------ dessin d'une forme

    private void drawShape(Graphics g, ItemShape shape, int drawX, int drawY, int wF, int hF) {
        // On dessine les 3 layers, du bas vers le haut (layer one en dernier = au-dessus)
        for (ItemShape.Layer layer : new ItemShape.Layer[]{ItemShape.Layer.three, ItemShape.Layer.two, ItemShape.Layer.one}) {
            SubShape[]          tabS = shape.getSubShapes(layer);
            modele.item.Color[] tabC = shape.getColors(layer);

            int qW = wF / 2, qH = hF / 2;

            for (int i = 0; i < 4; i++) {
                SubShape ss = tabS[i];
                if (ss == SubShape.None || tabC[i] == null) continue;

                // Couleur du quadrant
                g.setColor(couleurAWT(tabC[i]));

                // Position du quadrant dans la forme
                int qX = drawX + (i == 0 || i == 1 ? qW : 0);
                int qY = drawY + (i == 1 || i == 2 ? qH : 0);

                switch (ss) {
                    case Carre:
                        g.fillRect(qX, qY, qW, qH);
                        g.setColor(Color.BLACK);
                        g.drawRect(qX, qY, qW, qH);
                        break;

                    case Circle:
                        int startAngle = (i == 0 ? 0 : i == 1 ? 270 : i == 2 ? 180 : 90);
                        g.fillArc(drawX, drawY, wF, hF, startAngle, 90);
                        g.setColor(Color.BLACK);
                        g.drawArc(drawX, drawY, wF, hF, startAngle, 90);
                        // Rayons vers le centre
                        int cX = drawX + wF / 2, cY = drawY + hF / 2;
                        if      (i == 0) { g.drawLine(cX, cY, cX + qW, cY); g.drawLine(cX, cY, cX, cY - qH); }
                        else if (i == 1) { g.drawLine(cX, cY, cX + qW, cY); g.drawLine(cX, cY, cX, cY + qH); }
                        else if (i == 2) { g.drawLine(cX, cY, cX - qW, cY); g.drawLine(cX, cY, cX, cY + qH); }
                        else             { g.drawLine(cX, cY, cX - qW, cY); g.drawLine(cX, cY, cX, cY - qH); }
                        break;

                    case Fan:
                        int fanAngle = (i == 0 ? 0 : i == 1 ? 270 : i == 2 ? 180 : 90);
                        g.fillArc(qX - qW, qY - qH, wF, hF, fanAngle, 90);
                        g.setColor(Color.BLACK);
                        g.drawArc(qX - qW, qY - qH, wF, hF, fanAngle, 90);
                        break;

                    case Star:
                        int[] px = new int[3], py = new int[3];
                        int mx = qX + qW / 2, my = qY + qH / 2;
                        switch (i) {
                            case 0: px[0]=qX;      py[0]=qY;      px[1]=qX+qW;   py[1]=qY;      px[2]=mx;    py[2]=my; break;
                            case 1: px[0]=qX+qW;   py[0]=qY;      px[1]=qX+qW;   py[1]=qY+qH;   px[2]=mx;    py[2]=my; break;
                            case 2: px[0]=qX+qW;   py[0]=qY+qH;   px[1]=qX;      py[1]=qY+qH;   px[2]=mx;    py[2]=my; break;
                            case 3: px[0]=qX;      py[0]=qY+qH;   px[1]=qX;      py[1]=qY;       px[2]=mx;    py[2]=my; break;
                        }
                        g.fillPolygon(px, py, 3);
                        g.setColor(Color.BLACK);
                        g.drawPolygon(px, py, 3);
                        break;
                }
            }
        }
    }

    // ------------------------------------------------------------------ couleurs

    private Color couleurAWT(modele.item.Color c) {
        switch (c) {
            case Red:    return Color.RED;
            case White:  return Color.WHITE;
            case Blue:   return Color.BLUE;
            case Green:  return Color.GREEN;
            case Yellow: return Color.YELLOW;
            case Cyan:   return Color.CYAN;
            case Purple: return new Color(128, 0, 128);
            default:     return Color.LIGHT_GRAY;
        }
    }
}