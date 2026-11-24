package models.boids;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Classe abstraite représentant un agent générique (Boid).
 * <p>
 * Cette classe gère la physique de base (position, vitesse, accélération)
 * et les comportements de pilotage (steering behaviors) communs à tous les agents.
 * Les règles spécifiques de comportement de groupe sont déléguées aux sous-classes.
 * </p>
 */
public abstract class AbstractBoids {
    protected Point2D.Double position;
    protected Point2D.Double velocity;
    protected Point2D.Double acceleration;
    protected Color color;
    protected boolean isAlive = true;
    
    // Paramètres de comportement
    protected double maxForce; 
    protected double maxSpeed;
    protected double perceptionRadius;
    protected double separationRadius;
    
    // Optimisation (Squared values)
    protected double perceptionRadiusSq;
    protected double separationRadiusSq;

    public AbstractBoids(int x, int y, Color color) {
        this.position = new Point2D.Double(x, y);
        initializeParams();
        updateRadiusSquares();
        this.velocity = new Point2D.Double(
            Math.random() * maxSpeed - maxSpeed/2, 
            Math.random() * maxSpeed - maxSpeed/2
        );
        this.acceleration = new Point2D.Double(0, 0);
        this.color=color;
        
    }
    /**
     * Initialise les paramètres spécifiques à l'espèce du Boid (Vitesse max, Rayons, etc.).
     */
    protected abstract void initializeParams();
    /**
     * Pour des raisons d'optimisation , on calcule le carré de ces rayons
     */
    protected void updateRadiusSquares() {
        this.perceptionRadiusSq = perceptionRadius * perceptionRadius;
        this.separationRadiusSq = separationRadius * separationRadius;
    }

    /**
     * Applique les règles de comportement de groupe (Flocking).
     * Calcule et applique les forces de Séparation, Alignement et Cohésion.
     * * @param nearbyBoids Liste des voisins perçus par l'agent
     * @param sepW Poids de la séparation
     * @param aliW Poids de l'alignement
     * @param cohW Poids de la cohésion
     */
    public abstract void flock(ArrayList<AbstractBoids> nearbyBoids, double sepW, double aliW, double cohW);

    /**
     * Crée un nouveau boid du même type, à la position actuelle de celui-ci.
     * Utilisé pour prendre un instantané de l'état initial d'un groupe
     * (voir {@link BoidsGroup#copy()}), afin qu'un restart de la simulation
     * puisse repartir de cet état plutôt que de vider le groupe.
     */
    public abstract AbstractBoids copy();


    
    // --- Getters / Setters et Méthodes Physiques ---
    
    public Point2D.Double getPosition() { return position; }
    public void setPosition(Point2D.Double position) { this.position = position; }
    public Point2D.Double getVelocity() { return velocity; }
    public void setVelocity(Point2D.Double velocity) { this.velocity = velocity; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public Point2D.Double getAcceleration() { return acceleration; }
    public void setAcceleration(Point2D.Double acceleration) { this.acceleration = acceleration;}
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { this.isAlive = alive; }

    // Méthodes physiques
    protected Point2D.Double seek(Point2D.Double target) {
        Point2D.Double desired = new Point2D.Double(
            target.x - position.x, 
            target.y - position.y
        );
        return calculateSteer(desired, maxSpeed, maxForce);
    }

    /**
     * Calcule le vecteur de pilotage (Steering) selon la formule de Reynolds :
     * Steering = Desired - Velocity
     * * @param desired Le vecteur vitesse désiré (direction idéale)
     * @param maxSpeed La vitesse maximale autorisée pour ce mouvement
     * @param maxForce La force maximale (maniabilité) autorisée
     * @return Le vecteur force limité à maxForce
     */
    protected Point2D.Double calculateSteer(Point2D.Double desired, double maxSpeed, double maxForce) {
        double mag = Math.sqrt(desired.x * desired.x + desired.y * desired.y);
        
        if (mag > 0) {
            desired.x = (desired.x / mag) * maxSpeed;
            desired.y = (desired.y / mag) * maxSpeed;
        } else {
            return new Point2D.Double(0, 0);
        }
        
        Point2D.Double steer = new Point2D.Double(
            desired.x - velocity.x,
            desired.y - velocity.y
        );
        
        double steerMag = Math.sqrt(steer.x * steer.x + steer.y * steer.y);
        if (steerMag > maxForce) {
            steer.x = (steer.x / steerMag) * maxForce;
            steer.y = (steer.y / steerMag) * maxForce;
        }
        return steer;
    }
    
    public void applyForce(Point2D.Double force) {
        acceleration.x += force.x;
        acceleration.y += force.y;
    }

    /**
     * Met à jour l'état physique de l'agent pour un pas de temps.
     * 1. Met à jour la vitesse selon l'accélération.
     * 2. Limite la vitesse au maximum autorisé.
     * 3. Met à jour la position selon la vitesse.
     * 4. Réinitialise l'accélération pour le prochain tour.
     */    
    public void update() {
        velocity.x += acceleration.x;
        velocity.y += acceleration.y;
        
        double speed = Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);
        if (speed > maxSpeed) {
            velocity.x = (velocity.x / speed) * maxSpeed;
            velocity.y = (velocity.y / speed) * maxSpeed;
        }
        
        position.x += velocity.x;
        position.y += velocity.y;
        
        acceleration.x = 0;
        acceleration.y = 0;
    }

    /**
     * Gestion des bordures de l'écran (Mode "Steering").
     * Applique une force répulsive importante si l'agent approche trop près du bord.
     * * @param margin La marge en pixels déclenchant la force de retour
     * @param width Largeur de la zone
     * @param height Hauteur de la zone
     */
    public void boundaries(double margin, int width, int height) {
        Point2D.Double desired = null;
        double maForce = this.maxForce * 5; 

        if (position.x < margin) desired = new Point2D.Double(maxSpeed, velocity.y);
        else if (position.x > width - margin) desired = new Point2D.Double(-maxSpeed, velocity.y);

        if (position.y < margin) desired = (desired == null) ? new Point2D.Double(velocity.x, maxSpeed) : new Point2D.Double(desired.x, maxSpeed);
        else if (position.y > height - margin) desired = (desired == null) ? new Point2D.Double(velocity.x, -maxSpeed) : new Point2D.Double(desired.x, -maxSpeed);

        if (desired != null) {
            Point2D.Double steer = new Point2D.Double(desired.x - velocity.x, desired.y - velocity.y);
            double mag = Math.sqrt(steer.x * steer.x + steer.y * steer.y);
            if (mag > maForce) {
                steer.x = (steer.x / mag) * maForce;
                steer.y = (steer.y / mag) * maForce;
            }
            applyForce(steer);
        }
    }
}