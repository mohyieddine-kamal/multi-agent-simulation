package control;
import models.boids.BoidsGroup;
import simulator.BoidsSimulator;

/**
 * Événement de mise à jour ciblée pour un groupe de Boids spécifique.
 * <p>
 * Cet événement permet de gérer des fréquences de mise à jour différentes selon les groupes
 * (ex: les proies bougent à chaque tick, les prédateurs tous les 3 ticks).
 * Il transporte l'objet {@link BoidsGroup} complet contenant les règles de comportement spécifiques.
 * </p>
 */
public class GroupUpdateEvent extends Event {
    
    private BoidsSimulator simulator;
    
    /** Le groupe spécifique sur lequel appliquer la mise à jour */
    private BoidsGroup group;
    
    /** * Le délai (en pas de temps) entre deux exécutions. */
    private int delay;

    /**
     * Constructeur.
     * @param date Date de la première exécution.
     * @param simulator Le simulateur principal.
     * @param group Le groupe de boids à animer.
     * @param delay Le rythme de mise à jour (vitesse temporelle).
     */
    public GroupUpdateEvent(long date, BoidsSimulator simulator, BoidsGroup group, int delay) {
        super(date);
        this.simulator = simulator;
        this.group = group;
        this.delay = delay;
    }

    /**
     * Exécute la mise à jour du groupe et se replanifie.
     * <p>
     * 1. Appelle simulator.updateGroup(group) pour appliquer les forces.
     * 2. Calcule la prochaine date d'exécution (date actuelle + delay).
     * 3. S'ajoute à nouveau dans le gestionnaire d'événements.
     * </p>
     */
    @Override
    public void execute() {
        // 1. Mettre à jour ce groupe spécifique
        simulator.updateGroup(group); 

        // 2. Se replanifier avec le délai spécifique
        simulator.getEventManager().addEvent(
            new GroupUpdateEvent(this.getDate() + delay, simulator, group, delay)
        );
    }
}