package models.boids;

import java.util.ArrayList;

public class BoidsGroup {
    
    private ArrayList<AbstractBoids> boidsList;
    private String name; // Ex: "Proies", "Prédateurs"
    
    // Les règles spécifiques à ce groupe
    private double sepW; // Poids Séparation
    private double aliW; // Poids Alignement
    private double cohW; // Poids Cohésion

    /**
     * Constructeur
     * @param name Nom du groupe
     * @param sepW Poids séparation
     * @param aliW Poids alignement
     * @param cohW Poids cohésion
     */
    public BoidsGroup(String name, double sepW, double aliW, double cohW) {
        this.name = name;
        this.boidsList = new ArrayList<>();
        this.sepW = sepW;
        this.aliW = aliW;
        this.cohW = cohW;
    }

    public void addBoid(AbstractBoids b) {
        this.boidsList.add(b);
    }

    public ArrayList<AbstractBoids> getBoidsList() {
        return boidsList;
    }

    public double getSepW() { return sepW; }
    public double getAliW() { return aliW; }
    public double getCohW() { return cohW; }

    /**
     * Clone ce groupe : même nom, mêmes poids, et une copie indépendante de
     * chaque boid à sa position actuelle. Utilisé pour prendre un instantané
     * de l'état initial, que le simulateur peut restaurer plus tard au lieu
     * de simplement vider le groupe (voir {@code BoidsSimulator.restart()}).
     */
    public BoidsGroup copy() {
        BoidsGroup clone = new BoidsGroup(this.name, this.sepW, this.aliW, this.cohW);
        for (AbstractBoids b : this.boidsList) {
            clone.addBoid(b.copy());
        }
        return clone;
    }
}