package modele.item;

import java.io.Serializable;

public class ItemShape extends Item implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int NB_LAYERS = 3;
    public static final int NB_QUADRANTS = 4;

    private SubShape[][] layers;
    private Color[][] colors;

    private double progressInCell = 0.0;

    public enum Layer { one, two, three }

    public ItemShape(String str) {
        layers = new SubShape[NB_LAYERS][NB_QUADRANTS];
        colors = new Color[NB_LAYERS][NB_QUADRANTS];

        initialiseraVide(layers, colors);

        String[] layerStrings = str.split(":");
        for (int i = 0; i < layerStrings.length && i < NB_LAYERS; i++) {
            AnalyseLayer(layerStrings[i], i);
        }
    }

    private ItemShape(SubShape[][] layers, Color[][] colors) {
        this.layers = layers;
        this.colors = colors;
    }

    private void initialiseraVide(SubShape[][] layers, Color[][] colors) {
        for (int i = 0; i < NB_LAYERS; i++) {
            for (int j = 0; j < NB_QUADRANTS; j++) {
                layers[i][j] = SubShape.None;
                colors[i][j] = null;
            }
        }
    }

    private void AnalyseLayer(String s, int layerIndex) {
        for (int q = 0; q < NB_QUADRANTS && (q * 2 + 1) < s.length(); q++) {
            layers[layerIndex][q] = AnalyseSubShape(s.charAt(q * 2));
            colors[layerIndex][q] = AnalyseColor(s.charAt(q * 2 + 1));
        }
    }

    private SubShape AnalyseSubShape(char c) {
        switch (c) {
            case 'C': return SubShape.Carre;
            case 'O': return SubShape.Circle;
            case 'F': return SubShape.Fan;
            case 'S': return SubShape.Star;
            case '-': return SubShape.None;
            default: throw new IllegalArgumentException("Sous-forme inconnue : " + c);
        }
    }

    private Color AnalyseColor(char c) {
        switch (c) {
            case 'r': return Color.Red;
            case 'b': return Color.Blue;
            case 'g': return Color.Green;
            case 'w': return Color.White;
            case 'c': return Color.Cyan;
            case 'y': return Color.Yellow;
            case 'p': return Color.Purple;
            case '-': return null;
            default: throw new IllegalArgumentException("Couleur inconnue : " + c);
        }
    }

    private int layerIndex(Layer l) {
        switch (l) {
            case one: return 0;
            case two: return 1;
            case three: return 2;
            default: throw new IllegalArgumentException("Layer inattendu : " + l);
        }
    }

    public SubShape[] getSubShapes(Layer l) {
        return layers[layerIndex(l)].clone();
    }

    public Color[] getColors(Layer l) {
        return colors[layerIndex(l)].clone();
    }

    public int getNbActiveLayers() {
        int count = 0;
        for (int i = 0; i < NB_LAYERS; i++) {
            for (int j = 0; j < NB_QUADRANTS; j++) {
                if (layers[i][j] != SubShape.None) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private boolean layerestVide(int i) {
        for (int j = 0; j < NB_QUADRANTS; j++) {
            if (layers[i][j] != SubShape.None) {
                return false;
            }
        }
        return true;
    }

    public void rotate() {
        for (int i = 0; i < NB_LAYERS; i++) {
            SubShape[] newShapes = new SubShape[NB_QUADRANTS];
            Color[] newColors = new Color[NB_QUADRANTS];

            newShapes[0] = layers[i][3];
            newShapes[1] = layers[i][0];
            newShapes[2] = layers[i][1];
            newShapes[3] = layers[i][2];

            newColors[0] = colors[i][3];
            newColors[1] = colors[i][0];
            newColors[2] = colors[i][1];
            newColors[3] = colors[i][2];

            layers[i] = newShapes;
            colors[i] = newColors;
        }
    }

    public void stack(ItemShape ShapeSup) {
        int nbthis = getNbActiveLayers();
        int nbSup = ShapeSup.getNbActiveLayers();

        for (int i = 0; i < nbSup; i++) {
            int dest = nbthis + i;
            if (dest >= NB_LAYERS) {
                break;
            }
            layers[dest] = ShapeSup.layers[i].clone();
            colors[dest] = ShapeSup.colors[i].clone();
        }
    }

    public ItemShape Cut() {
        SubShape[][] autreL = new SubShape[NB_LAYERS][NB_QUADRANTS];
        Color[][] autreC = new Color[NB_LAYERS][NB_QUADRANTS];

        initialiseraVide(autreL, autreC);

        for (int i = 0; i < NB_LAYERS; i++) {
            for (int j = 0; j < NB_QUADRANTS; j++) {
                if (j == 0 || j == 1) {
                    autreL[i][j] = layers[i][j];
                    autreC[i][j] = colors[i][j];
                    layers[i][j] = SubShape.None;
                    colors[i][j] = null;
                }
            }
        }

        return new ItemShape(autreL, autreC);
    }

    public void Color(Color c) {
        for (int i = 0; i < NB_LAYERS; i++) {
            for (int j = 0; j < NB_QUADRANTS; j++) {
                if (layers[i][j] != SubShape.None) {
                    colors[i][j] = c;
                }
            }
        }
    }

    public double getProgressInCell() {
        return progressInCell;
    }

    public void setProgressInCell(double p) {
        this.progressInCell = Math.min(1.0, Math.max(0.0, p));
    }
}