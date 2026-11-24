package control;

/**
 * Classe abstraite représentant un événement discret dans la simulation.
 * <p>
 * Chaque événement possède une date d'exécution. Cette classe implémente l'interface
 * {@link Comparable} pour permettre au {@link EventManager} de trier automatiquement
 * les événements par ordre chronologique dans sa file de priorité.
 * </p>
 */
public abstract class Event implements Comparable<Event> {
    
    /** La date (temps discret) à laquelle l'événement doit être exécuté. */
    private long date;

    /**
     * Constructeur de base.
     * @param date La date d'exécution (doit être >= date courante du manager).
     */
    public Event(long date) {
        this.date = date;
    }

    /**
     * Récupère la date prévue pour cet événement.
     * @return La date sous forme d'entier long.
     */
    public long getDate() {
        return date;
    }

    /**
     * Action concrète à réaliser lorsque l'événement survient.
     * Doit être implémentée par les sous-classes (ex: déplacer des boids).
     */
    public abstract void execute();

    /**
     * Comparaison pour le tri dans la PriorityQueue.
     * <p>
     * Un événement est considéré "plus petit" (donc prioritaire) s'il a une date antérieure.
     * </p>
     * @param other L'autre événement à comparer.
     * @return un entier négatif, zéro, ou positif si la date de cet événement 
     * est respectivement antérieure, égale ou postérieure à l'autre.
     */
    @Override
    public int compareTo(Event other) {
        return Long.compare(this.date, other.getDate());
    }
}