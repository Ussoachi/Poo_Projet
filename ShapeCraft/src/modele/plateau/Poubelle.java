package modele.plateau;

import modele.item.Item;

public class Poubelle extends Machine {
    @Override
    public void send() {

    }


    protected boolean ReceiveItem(Item item) {
        return true;
    }
}
