package vuecontroleur;

import modele.item.ItemShape;
import modele.item.SubShape;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private Image imgBackground;
    private Image imgFront;
    private ItemShape shape;

    private String label;
    private float progress = 0f;

    public void setShape(ItemShape _shape) {
        shape = _shape;
    }

    public void setImageBackground(Image _imgBackground) {
        imgBackground = _imgBackground;
    }

    public void setLabel(String _label) {
        label = _label;
    }

    public void setProgressInCell(double p) { // ✅ accepte double
        progress = (float) p;
    }

    public void reset() {
        imgBackground = null;
        imgFront = null;
        shape = null;
        label = null;
        progress = 0f;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final int bordure = 1;
        final int xBack = bordure;
        final int yBack = bordure;
        final int widthBack = getWidth() - bordure * 2;
        final int heightBack = getHeight() - bordure * 2;

        final int subPartWidth = widthBack / 4;
        final int subPartHeight = heightBack / 4;

        final int xFront = bordure + subPartWidth;
        final int yFront = bordure + subPartHeight;
        final int widthFront = subPartWidth * 2;
        final int heightFront = subPartHeight * 2;

        g.drawRoundRect(bordure, bordure, widthBack, heightBack, bordure, bordure);

        if (imgBackground != null) {
            g.drawImage(imgBackground, xBack, yBack, widthBack, heightBack, this);
        }

        if (imgFront != null) {
            g.drawImage(imgFront, xFront, yFront, widthFront, heightFront, this);
        }

        if (shape != null) {

            SubShape[] tabS = shape.getSubShapes(ItemShape.Layer.one);
            modele.item.Color[] tabC = shape.getColors(ItemShape.Layer.one);

            for (int i = 0; i < 4; i++) {

                SubShape ss = tabS[i];

                if (ss != SubShape.None) {

                    switch (tabC[i]) {
                        case Red: g.setColor(Color.RED); break;
                        case White: g.setColor(Color.WHITE); break;
                        case Green: g.setColor(Color.GREEN); break;
                        case Blue: g.setColor(Color.BLUE); break;
                        case Yellow: g.setColor(Color.YELLOW); break;
                        case Purple: g.setColor(new Color(128, 0, 128)); break;
                        case Cyan: g.setColor(Color.CYAN); break;
                    }

                    int xQ = xFront + (widthFront / 2) * ((i >> 1) ^ 1);
                    int yQ = yFront + (heightFront / 2) * ((i & 1) ^ ((i >> 1) & 1));
                    int wQ = widthFront / 2;
                    int hQ = heightFront / 2;

                    switch (ss) {
                        case Carre:
                            g.fillRect(xQ, yQ, wQ, hQ);
                            break;
                        case Circle:
                            g.fillOval(xQ, yQ, wQ, hQ);
                            break;
                        case Fan:
                            int[] xFan = {xQ, xQ + wQ, xQ};
                            int[] yFan = {yQ, yQ + hQ, yQ + hQ};
                            g.fillPolygon(xFan, yFan, 3);
                            break;
                        case Star:
                            int[] xStar = {xQ + wQ/2, xQ + wQ, xQ + wQ/2, xQ};
                            int[] yStar = {yQ, yQ + hQ/2, yQ + hQ, yQ + hQ/2};
                            g.fillPolygon(xStar, yStar, 4);
                            break;
                    }
                }
            }
        }

        if (label != null) {
            g.setColor(Color.BLACK);
            g.drawString(label, xBack + 5, yBack + 15);
        }

        if (progress > 0) {
            g.setColor(Color.GREEN);
            int barWidth = (int) (widthBack * progress);
            g.fillRect(xBack, yBack + heightBack - 5, barWidth, 5);
        }
    }
}