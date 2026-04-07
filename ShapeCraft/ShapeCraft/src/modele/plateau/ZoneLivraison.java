package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

public class ZoneLivraison extends Machine {

    private ItemShape objectifActuel;  // la forme attendue
    private int quantiteRecue;         // combien on en a reçu
    private int quantiteObjectif;      // combien il en faut
    private boolean objectifAtteint;   // true quand l'objectif est rempli

    public ZoneLivraison(ItemShape _objectif, int _quantite) {
        objectifActuel   = _objectif;
        quantiteObjectif = _quantite;
        quantiteRecue    = 0;
        objectifAtteint  = false;
    }

    /**
     * La zone de livraison reçoit un item et vérifie s'il correspond
     * à l'objectif. Si oui, on incrémente le compteur.
     */
    @Override
    public void work() {
        if (current.size() > 0) {
            Item item = current.getFirst();

            // On vérifie que c'est bien une ItemShape
            if (item instanceof ItemShape) {
                quantiteRecue++;

                // Vérifie si l'objectif est atteint
                if (quantiteRecue >= quantiteObjectif) {
                    objectifAtteint = true;
                }
            }

            // Dans tous les cas on consomme l'item (la zone l'absorbe)
            current.removeFirst();
        }
    }

    /**
     * La zone de livraison n'envoie rien vers une autre machine.
     */
    @Override
    public void send() {
        // rien à envoyer
    }

    // --- Getters pour la vue ---

    public ItemShape getObjectifActuel() {
        return objectifActuel;
    }

    public int getQuantiteRecue() {
        return quantiteRecue;
    }

    public int getQuantiteObjectif() {
        return quantiteObjectif;
    }

    public boolean isObjectifAtteint() {
        return objectifAtteint;
    }

    /**
     * Passe à l'objectif suivant.
     * Appelé par Jeu quand l'objectif courant est atteint.
     */
    public void nouveauObjectif(ItemShape _objectif, int _quantite) {
        objectifActuel   = _objectif;
        quantiteObjectif = _quantite;
        quantiteRecue    = 0;
        objectifAtteint  = false;
    }
}