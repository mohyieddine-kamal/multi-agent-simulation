package models.boids;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Représente un agent de type Prédateur dans la simulation.
 * <p>
 * Contrairement aux boids classiques (Proies), le prédateur :
 * <ul>
 * <li>N'obéit pas aux règles de cohésion et d'alignement de groupe.</li>
 * <li>Possède un rayon de vision plus large pour repérer ses cibles.</li>
 * <li>Est légèrement plus lent que les proies (nécessite de l'anticipation pour attraper).</li>
 * <li>Chasse activement la proie la plus proche.</li>
 * </ul>
 * </p>
 */
public class PredatorBoid extends AbstractBoids {

    /**
     * Constructeur d'un prédateur.
     * @param x Position initiale en X
     * @param y Position initiale en Y
     */
    public PredatorBoid(int x, int y) {
        super(x, y, Color.RED); // Les prédateurs sont toujours rouges
    }

    @Override
    public AbstractBoids copy() {
        return new PredatorBoid((int) position.x, (int) position.y);
    }

    /**
     * Initialise les paramètres physiques spécifiques au prédateur.
     * Définit un agent "lourd" (maxForce faible), lent (maxSpeed faible)
     * mais avec une excellente vision (perceptionRadius élevé).
     */
    @Override
    protected void initializeParams() {
        this.maxForce = 0.25; // Manœuvrabilité réduite par rapport aux proies
        this.maxSpeed = 3.5;  // Plus lent que les proies (6.0 vs 3.5)
        this.perceptionRadius = 120;  // Vision très large pour repérer les proies de loin
        this.separationRadius = 40;   // Espace personnel
    }

    /**
     * Génère un vecteur de déplacement aléatoire (Errance).
     * Utilisé lorsque le prédateur ne détecte aucune proie dans son champ de vision.
     * @return Un vecteur de force faible et aléatoire.
     */
    private Point2D.Double wander() {
        double wanderStrength = 0.1;
        return new Point2D.Double(
            (Math.random() - 0.5) * wanderStrength,
            (Math.random() - 0.5) * wanderStrength
        );
    }

    /**
     * Comportement de Poursuite (Pursuit).
     * <p>
     * Cette méthode implémente une poursuite intelligente : au lieu de viser la position
     * actuelle de la proie (ce qui ferait courir le prédateur derrière elle), 
     * elle vise une position future estimée basée sur la vitesse de la proie.
     * </p>
     * * @param preys Liste des proies perçues
     * @return Le vecteur de force de pilotage vers la cible (ou errance si vide)
     */
    private Point2D.Double pursuePrey(ArrayList<AbstractBoids> preys){
        // 1. Trouver la proie la plus proche
        AbstractBoids closestPrey = null;
        double minDistSq = Double.MAX_VALUE;
        
        for (AbstractBoids prey : preys) {
            double dSq = position.distanceSq(prey.getPosition());
            // On vérifie si la proie est dans le rayon de perception
            if (dSq < minDistSq && dSq < perceptionRadiusSq) {
                minDistSq = dSq;
                closestPrey = prey;
            }
        }
        
        // 2. Calculer la trajectoire d'interception
        if (closestPrey != null) {
            // Prédiction : On vise là où la proie sera dans le futur
            Point2D.Double futurePos = new Point2D.Double(
                closestPrey.getPosition().x + closestPrey.getVelocity().x * 3,
                closestPrey.getPosition().y + closestPrey.getVelocity().y * 3
            );
            return seek(futurePos);
        }
        
        // 3. Si aucune proie visible, on cherche au hasard
        return wander();
    }

    /**
     * Calcule la force de séparation uniquement par rapport aux autres prédateurs.
     * Cela évite que les prédateurs ne se chevauchent pendant la chasse.
     * * @param predators Liste des autres prédateurs
     * @return Vecteur de force répulsive
     */
    private Point2D.Double separateFromPredators(ArrayList<AbstractBoids> predators) {
        Point2D.Double steer = new Point2D.Double(0, 0);
        int count = 0;
        
        for (AbstractBoids other : predators) {
            if (other == this) continue;
            
            double dSq = position.distanceSq(other.getPosition());
            
            if (dSq > 0 && dSq < separationRadiusSq) {
                double d = Math.sqrt(dSq);
                
                Point2D.Double diff = new Point2D.Double(
                    position.x - other.getPosition().x,
                    position.y - other.getPosition().y
                );
                
                // Normalisation et pondération par l'inverse de la distance
                diff.x /= d; diff.y /= d; // Normalize
                diff.x /= d; diff.y /= d; // Weight by distance
                
                steer.x += diff.x;
                steer.y += diff.y;
                count++;
            }
        }
        
        if (count > 0) {
            steer.x /= count;
            steer.y /= count;
        }
        return this.calculateSteer(steer, maxSpeed, maxForce);
    }

    /**
     * Surcharge de la méthode principale de comportement.
     * Orchestre la décision du prédateur à chaque pas de temps.
     * * @param allboids Liste de tous les voisins (Proies et Prédateurs mélangés)
     * @param sepW
     * @param aliW
     * @param cohW
     */
    @Override
    public void flock(ArrayList<AbstractBoids> allboids, double sepW, double aliW, double cohW){
        ArrayList<AbstractBoids> preys = new ArrayList<>();
        ArrayList<AbstractBoids> predators = new ArrayList<>();
        
        // Tri des voisins par type
        for (AbstractBoids boid : allboids) {
            if (boid instanceof PredatorBoid) {
                predators.add(boid);
            } else if (boid instanceof PreyBoid) {
                preys.add(boid);
            }
        }
        
        // Calcul des comportements
        Point2D.Double pursuit = pursuePrey(preys);
        Point2D.Double separation = separateFromPredators(predators);
        
        // Application des poids 
        pursuit.x *= 2.5;
        pursuit.y *= 2.5;
        
        separation.x *= 1.5;  // Éviter les collisions avec les collègues est secondaire
        separation.y *= 1.5;
        
        // Application des forces à l'accélération
        applyForce(pursuit);
        applyForce(separation);
    }
}
