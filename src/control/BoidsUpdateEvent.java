package control;

import simulator.BoidsSimulator;

/**
 * Événement de mise à jour globale des boids.
 * <p>
 * Cet événement déclenche la mise à jour de TOUS les boids du simulateur en une seule fois.
 * Il est utilisé pour les simulations simples où tous les agents partagent la même horloge.
 * </p>
 */
public class BoidsUpdateEvent extends Event {
    private BoidsSimulator simulator;
    
    /**
     * Constructeur.
     * @param date Date d'exécution de l'événement.
     * @param simulator Le simulateur contenant les boids à mettre à jour.
     */
    public BoidsUpdateEvent(long date, BoidsSimulator simulator) {
        super(date);
        this.simulator = simulator;
    }
    
    /**
     * Exécute la mise à jour et assure la perpétuité de la simulation.
     * 1. Appelle simulator.updateBoids() pour calculer les mouvements.
     * 2. Crée un nouvel événement identique pour le pas de temps suivant (date + 1).
     */
    @Override
    public void execute() {
        // Mettre à jour tous les boids
        simulator.updateBoids();
        
        // Se reposter pour le prochain pas de temps (t + 1)
        simulator.getEventManager().addEvent(
            new BoidsUpdateEvent(this.getDate() + 1, simulator)
        );
    }
    
    @Override
    public String toString() {
        return "BoidsUpdateEvent[date=" + getDate() + "]";
    }
}