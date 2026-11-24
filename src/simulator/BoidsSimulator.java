package simulator;

import models.boids.*;
import control.*;
import gui.GUISimulator;
import gui.Oval;
import gui.Simulable;
import java.awt.Color;
import java.util.ArrayList;

/**
 * Contrôleur principal de la simulation des Boids.
 * <p>
 * Cette classe implémente l'interface {@link Simulable} pour être pilotée par l'interface graphique[cite: 34, 46].
 * Elle orchestre les interactions entre :
 * <ul>
 * <li>L'interface graphique )</li>
 * <li>Le gestionnaire d'événements (temps discret)]</li>
 * <li>Les groupes de boids </li>
 * <li>La grille spatiale (optimisation des voisinages)</li>
 * </ul>
 * </p>
 */
public class BoidsSimulator implements Simulable {
    private ArrayList<BoidsGroup> groups;
    // Instantané de la population initiale (prise au moment de addGroup),
    // utilisé par restart() pour relancer la simulation depuis son état de
    // départ plutôt que de la laisser vide.
    private ArrayList<BoidsGroup> initialGroups = new ArrayList<>();
    // Delai de mise a jour recurrente enregistre pour chaque groupe via
    // scheduleGroupUpdate() (-1 si aucun, cas des groupes pilotes par le
    // BoidsUpdateEvent global de createUpdateEvent()). Parallele a initialGroups.
    private ArrayList<Integer> initialGroupDelays = new ArrayList<>();
    // Vrai si createUpdateEvent() a ete utilise (mise a jour globale de tous
    // les groupes a chaque pas), pour pouvoir la replanifier apres un restart().
    private boolean globalUpdateScheduled = false;
    private GUISimulator gui;
    private EventManager eventManager;
    private SpatialGrid spatialGrid;
    private int width, height;

    /**
     * Constructeur du simulateur.
     * Initialise la grille spatiale et lie le simulateur à l'interface graphique.
     *
     * @param gui     L'instance de l'interface graphique
     * @param manager Le gestionnaire d'événements
     */
    public BoidsSimulator(GUISimulator gui, EventManager manager) {
        this.gui = gui;
        this.eventManager = manager;
        this.width = gui.getPanelWidth();
        this.height = gui.getPanelHeight();
        
        this.groups = new ArrayList<>();
        // Résolution de 120px égale au rayon de perception des Prédateurs
        this.spatialGrid = new SpatialGrid(width, height, 120);
        
        gui.setSimulable(this);
    }


    public void addGroup(BoidsGroup group) {
        this.groups.add(group);
        // Garde une copie indépendante de la population de départ, pour que
        // restart() puisse la restaurer au lieu de vider le groupe.
        this.initialGroups.add(group.copy());
        this.initialGroupDelays.add(-1);
    }

    /**
     * Enregistre une mise à jour récurrente pour un groupe déjà ajouté via
     * {@link #addGroup}, au rythme indiqué (en pas de temps). À utiliser à la
     * place de la création manuelle d'un {@link GroupUpdateEvent} par
     * l'appelant, pour que le simulateur garde la trace de cette
     * planification et puisse la reproduire après un restart().
     *
     * @param group le groupe concerné (déjà ajouté via addGroup)
     * @param delay le nombre de pas de temps entre deux mises à jour
     */
    public void scheduleGroupUpdate(BoidsGroup group, int delay) {
        int idx = this.groups.indexOf(group);
        if (idx >= 0) {
            this.initialGroupDelays.set(idx, delay);
        }
        this.eventManager.addEvent(new GroupUpdateEvent(0, this, group, delay));
    }

    /**
     * Exécute la logique de mise à jour pour TOUS les groupes inscrits.
     * Généralement appelé par un {@link BoidsUpdateEvent} global.
     */
    public void updateAllGroups() {
        // 1. Mise à jour de la grille avec tous les agents
        refreshSpatialGrid();

        // 2. Calcul des comportements pour chaque groupe
        for (BoidsGroup group : groups) {
            processGroupLogic(group);
        }
        
        // 3. Suppression des agents morts
        removeDeadBoids();
    }

    /**
     * Exécute la logique de mise à jour pour un groupe SPÉCIFIQUE.
     * Utilisé par les événements différenciés {@link GroupUpdateEvent}.
     * <p>
     * <b>Note importante :</b> Même si on ne met à jour qu'un seul groupe, on doit
     * rafraîchir la grille spatiale avec TOUS les boids de la simulation pour que
     * les interactions inter-groupes (chasse/fuite) fonctionnent.
     * </p>
     *
     * @param group Le groupe spécifique à mettre à jour
     */
    public void updateGroup(BoidsGroup group) {
        refreshSpatialGrid();
        processGroupLogic(group);
        removeDeadBoids();
    }

    /**
     * Vide et remplit la grille spatiale avec tous les boids vivants de tous les groupes.
     * Cette étape est cruciale pour que la méthode {@code getPotentialNeighbors} retourne
     * des résultats complets.
     */
    private void refreshSpatialGrid() {
        spatialGrid.clear();
        for (BoidsGroup g : groups) {
            for (AbstractBoids b : g.getBoidsList()) {
                if (b.isAlive()) {
                    spatialGrid.addBoid(b);
                }
            }
        }
    }

    /**
     * Applique les règles de comportement (Flocking et Interactions) aux membres d'un groupe.
     * <p>
     * Cette méthode gère :
     * <ol>
     * <li>La détection des voisins via la grille spatiale.</li>
     * <li>La logique de prédation (si un prédateur touche une proie).</li>
     * <li>L'application des forces de Reynolds (Cohésion, Alignement, Séparation).</li>
     * <li>Le déplacement physique (mise à jour position/vitesse).</li>
     * <li>La gestion des bordures (rebond).</li>
     * </ol>
     * </p>
     *
     * @param group Le groupe à traiter
     */
    private void processGroupLogic(BoidsGroup group) {
        double s = group.getSepW();
        double a = group.getAliW();
        double c = group.getCohW();

        // Distance de contact pour manger une proie (au carré pour optimisation)
        double killRadiusSq = 15 * 15;

        for (AbstractBoids b : group.getBoidsList()) {
            if (!b.isAlive()) continue;

            // Récupération des voisins (inclut proies ET prédateurs grâce à refreshSpatialGrid)
            ArrayList<AbstractBoids> neighbors = spatialGrid.getPotentialNeighbors(b);
            
            //  Elimination des proies
            if (b instanceof PredatorBoid) {
                for (AbstractBoids neighbor : neighbors) {
                    // Si le voisin est une proie vivante et qu'on est assez proche
                    if (neighbor instanceof PreyBoid && neighbor.isAlive()) {
                        if (b.getPosition().distanceSq(neighbor.getPosition()) < killRadiusSq) {
                            neighbor.setAlive(false); // La proie est éliminée
                        }
                    }
                }
            }
            
            // Application du Flocking 
            b.flock(neighbors, s, a, c);
            
            // Mise à jour physique
            b.update();
            
            // Gestion des murs 
            b.boundaries(50, width, height);
        }
    }
    
    /**
     * Nettoie les listes en supprimant définitivement les boids marqués comme morts.
     */
    private void removeDeadBoids() {
        for (BoidsGroup group : groups) {
            group.getBoidsList().removeIf(b -> !b.isAlive());
        }
    }

    // --- Interface Simulable ---

    /**
     * Avance la simulation d'un pas de temps.
     * Délègue la gestion du temps à l'{@link EventManager}[cite: 311].
     */
    @Override
    public void next() {
        eventManager.next();
        dessinerBoids();
    }

    /**
     * Réinitialise la simulation à son état initial.
     * <p>
     * Recrée chaque groupe à partir de l'instantané pris lors de
     * {@link #addGroup}, et replanifie les mêmes mises à jour récurrentes
     * (par groupe via {@link #scheduleGroupUpdate}, ou globale via
     * {@link #createUpdateEvent}) qu'au lancement. Sans ça, vider
     * simplement {@code groups} laisserait la simulation définitivement
     * vide : plus aucun événement ne serait planifié pour la repeupler.
     * </p>
     */
    @Override
    public void restart() {
        eventManager.restart();
        groups.clear();
        spatialGrid.clear();

        for (int i = 0; i < initialGroups.size(); i++) {
            BoidsGroup fresh = initialGroups.get(i).copy();
            groups.add(fresh);
            int delay = initialGroupDelays.get(i);
            if (delay > 0) {
                eventManager.addEvent(new GroupUpdateEvent(0, this, fresh, delay));
            }
        }
        if (globalUpdateScheduled) {
            eventManager.addEvent(new BoidsUpdateEvent(0, this));
        }

        dessinerBoids();
    }
    
    /**
     * Dessine tous les boids vivants sur l'interface graphique.
     * Utilise des {@link Oval} de tailles et couleurs différentes selon le type.
     */
    public void dessinerBoids() {
        gui.reset();
        for (BoidsGroup group : groups) {
            for (AbstractBoids b : group.getBoidsList()) {
                if (!b.isAlive()) continue;
                
                Color c = (b.getColor() != null) ? b.getColor() : Color.BLACK;
                int size = (b instanceof PredatorBoid) ? 14 : 8;
                gui.addGraphicalElement(new Oval((int)b.getPosition().x, (int)b.getPosition().y, c, c, size));
            }
        }
    }
    
    /**
     * Méthode de compatibilité appelée par {@link BoidsUpdateEvent}.
     */
    public void updateBoids() {
        updateAllGroups(); 
    }
    
    /**
     * Initialise un événement de mise à jour globale (t=0).
     */
    public void createUpdateEvent() {
        this.globalUpdateScheduled = true;
        eventManager.addEvent(new BoidsUpdateEvent(0, this));
    }
    
    public EventManager getEventManager() { return eventManager; }
}