package modele.plateau;

import modele.item.Color;
import modele.item.Item;
import modele.item.SubShape;

import java.io.Serializable;

public class Case implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Plateau plateau;
    protected Machine machine;
    protected Item gisement;

    private Color couleurGisement = null;
    private SubShape formeGisement = null;

    public Case(Plateau plateau) {
        this.plateau = plateau;
    }

    public void setMachine(Machine m) {
        machine = m;
        if (m != null) {
            m.setCase(this);
        }
    }

    public Machine getMachine() {
        return machine;
    }

    public void removeMachine() {
        this.machine = null;
    }

    public Item getGisement() {
        return gisement;
    }

    public void setGisement(Item gisement) {
        this.gisement = gisement;
    }

    public Color getCouleurGisement() {
        return couleurGisement;
    }

    public void setCouleurGisement(Color couleurGisement) {
        this.couleurGisement = couleurGisement;
    }

    public SubShape getFormeGisement() {
        return formeGisement;
    }

    public void setFormeGisement(SubShape formeGisement) {
        this.formeGisement = formeGisement;
    }

    public boolean estGisement() {
        return couleurGisement != null && formeGisement != null;
    }
}