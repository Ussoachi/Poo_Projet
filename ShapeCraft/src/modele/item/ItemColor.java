package modele.item;

public class ItemColor extends Item {
    private Color color;
    public ItemColor(Color color){
        this.color = color;
    }
    public Color getColor(){
        return color;
    }

    public void transform(Color add) { // faire varier la couleur suivant la couleur ajoutée
        if (add == null){
            return; // Si vide on prend la nouvelle
        }
        color = add;//on remplace l'ancienne couleur par la nouvelle
    }

    @Override
    public String toString() {
        return "ItemColor{" + color + "}";
    }
}
