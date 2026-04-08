package modele.plateau;

import modele.item.ItemShape;

import java.util.Random;

public class Mine extends Machine {


    @Override
    public void work() {
        if (current.isEmpty() &&new Random().nextInt(4) == 0) {
            current.add(new ItemShape("CrCrCbCb"));
        }

    }

    @Override
    public void send() {
        super.send();
    }
}
