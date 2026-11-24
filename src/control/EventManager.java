package control;

import java.util.PriorityQueue;

/**
 * Gestionnaire d'événements discrets (Ordonnanceur).
 * <p>
 * Cette classe centralise la gestion du temps. Elle maintient une file d'attente
 * prioritaire d'événements et une date courante. À chaque pas de temps (appel à next),
 * elle exécute tous les événements dont la date est arrivée à échéance.
 * </p>
 */
public class EventManager {
    
    /** Date courante de la simulation (temps discret). */
    private long currentDate;
    
    /** * File d'attente prioritaire ordonnant les événements par date croissante.
     * Les événements les plus anciens (date la plus petite) sortent en premier.
     */
    private PriorityQueue<Event> events;

    /**
     * Constructeur. Initialise la date à 0.
     */
    public EventManager() {
        this.currentDate = 0;
        this.events = new PriorityQueue<>();
    }

    /**
     * Planifie un nouvel événement.
     * L'événement est inséré dans la file à la position déterminée par sa date.
     * @param e L'événement à ajouter.
     */
    public void addEvent(Event e) {
        events.add(e);
    }

    /**
     * Avance le temps d'une unité et exécute les événements en attente.
     * <p>
     * Algorithme :
     * 1. Incrémente la date courante.
     * 2. Tant qu'il y a des événements dans la file ET que leur date est <= date courante :
     * a. On retire l'événement de la file.
     * b. On appelle sa méthode execute().
     * </p>
     */
    public void next() {
        currentDate++;
        while (!events.isEmpty() && events.peek().getDate() <= currentDate) {
            Event e = events.poll();
            e.execute();
        }
    }

    /**
     * Vérifie si la simulation est terminée (plus aucun événement à traiter).
     * @return true si la file d'événements est vide.
     */
    public boolean isFinished() {
        return events.isEmpty();
    }

    /**
     * Réinitialise le gestionnaire pour une nouvelle simulation.
     * Remet la date à 0 et vide la file d'événements.
     */
    public void restart() {
        this.currentDate = 0;
        this.events.clear();
    }
}