package models.boids;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Représente un agent de type Proie (Boid classique).
 * <p>
 * Implémente les trois règles fondamentales de Craig Reynolds :
 * <ul>
 * <li><b>Séparation :</b> Éviter les collisions avec les voisins proches.</li>
 * <li><b>Alignement :</b> Aller dans la même direction que le groupe.</li>
 * <li><b>Cohésion :</b> Rester proche du centre du groupe.</li>
 * </ul>
 * Ajoute une règle de survie : <b>Fuite (Flee)</b> face aux prédateurs.
 * </p>
 */
public class PreyBoid extends AbstractBoids {

    /**
     * Constructeur d'une proie (couleur bleue par défaut).
     */
    public PreyBoid(int x, int y) {
        super(x, y, Color.BLUE);
    }

    @Override
    public AbstractBoids copy() {
        return new PreyBoid((int) position.x, (int) position.y);
    }

    @Override
    protected void initializeParams(){
        this.maxSpeed = 8.0; // Rapide pour pouvoir fuir
        this.maxForce = 0.5; // Maniable
        this.perceptionRadius = 50; // Vision de groupe
        this.separationRadius = 30; // Espace personnel
    }

    /**
     * Calcule et accumule toutes les forces de comportement.
     * Sépare les voisins en deux listes (amis/ennemis) pour appliquer les bonnes règles.
     */
    @Override
    public void flock(ArrayList<AbstractBoids> allBoids, double sepW, double aliW, double cohW){
        ArrayList<AbstractBoids> preys = new ArrayList<>();
        ArrayList<AbstractBoids> predators = new ArrayList<>();
        
        // Tri des voisins
        for (AbstractBoids boid : allBoids) {
            if (boid instanceof PredatorBoid) {
                predators.add(boid);
            } else if (boid instanceof PreyBoid) {
                preys.add(boid);
            }
        }

        // Calcul des forces unitaires
        Point2D.Double separation = separate(preys);
        Point2D.Double alignment = align(preys);
        Point2D.Double cohesion = cohere(preys);
        Point2D.Double flee = fleeFromPredators(predators);

        // Pondération des forces
        separation.x *= sepW; separation.y *= sepW;
        alignment.x *= aliW;  alignment.y *= aliW;
        cohesion.x *= cohW;   cohesion.y *= cohW;
        
        // La survie est prioritaire (Poids x3.0)
        flee.x *= 3.0;
        flee.y *= 3.0;
        
        // Application à l'accélération
        applyForce(separation);
        applyForce(alignment);
        applyForce(cohesion);
        applyForce(flee);
    }
    
    /**
     * Comportement de fuite face aux prédateurs.
     * La force de répulsion est inversement proportionnelle au carré de la distance.
     * (Plus le prédateur est proche, plus la panique est forte).
     * @param predators Liste des prédateurs perçus
     * @return Vecteur de fuite
     */
    public Point2D.Double fleeFromPredators(ArrayList<AbstractBoids> predators) {
        Point2D.Double steer = new Point2D.Double(0, 0);
        double dangerRadius = 100;  // Rayon de détection du danger (plus grand que la cohésion)
        double dangerRadiusSq = dangerRadius * dangerRadius;
        int count = 0;
        
        for(AbstractBoids predator : predators) {
            double distanceSq = this.getPosition().distanceSq(predator.getPosition());

            if (distanceSq < dangerRadiusSq ) {
                count++;
                Point2D.Double desired = new Point2D.Double(
                    this.getPosition().x - predator.getPosition().x,
                    this.getPosition().y - predator.getPosition().y);
                
                // Pondération inverse : 1 / distance^2
                // Crée une force explosive quand la distance est faible
                desired.x /= distanceSq;
                desired.y /= distanceSq;

                steer.x += desired.x;
                steer.y += desired.y;
            }     
        }
        
        if (count > 0) {
            steer.x /= count;
            steer.y /= count;
        }
        
        return this.calculateSteer(steer, maxSpeed, maxForce);
    }

    /**
     * Règle de Séparation : Éviter le chevauchement avec les voisins proches.
     */
    private Point2D.Double separate(ArrayList<AbstractBoids> boids) {
        Point2D.Double steer = new Point2D.Double(0, 0);
        int count = 0;
        
        for (AbstractBoids other : boids) {
            if (other == this) continue;
            double dSq = position.distanceSq(other.position);
            
            if (dSq > 0 && dSq < this.separationRadiusSq) {
                double d = Math.sqrt(dSq);
                Point2D.Double diff = new Point2D.Double(
                    position.x - other.position.x,
                    position.y - other.position.y
                );
                diff.x /= d; diff.y /= d; // Normalisation
                diff.x /= d; diff.y /= d; // Pondération par distance (plus c'est proche, plus on repousse)
                
                steer.x += diff.x; steer.y += diff.y;
                count++;
            }
        }
        if (count > 0) {
            steer.x /= count; steer.y /= count;
        }
        return calculateSteer(steer, maxSpeed, maxForce);
    }

    /**
     * Règle d'Alignement : Adopter la vitesse moyenne des voisins.
     */
    private Point2D.Double align(ArrayList<AbstractBoids> boids) {
        Point2D.Double sum = new Point2D.Double(0, 0);
        int count = 0;
        
        for (AbstractBoids other : boids) {
            if (other == this) continue;
            double dSq = position.distanceSq(other.position);
            
            if (dSq > 0 && dSq < perceptionRadiusSq) {
                sum.x += other.velocity.x;
                sum.y += other.velocity.y;
                count++;
            }
        }
        if (count > 0) {
            sum.x /= count; sum.y /= count;
        }
        return this.calculateSteer(sum, maxSpeed, maxForce);
    }

    /**
     * Règle de Cohésion : Se diriger vers le centre de masse des voisins.
     */
    private Point2D.Double cohere(ArrayList<AbstractBoids> boids) {
        Point2D.Double sum = new Point2D.Double(0, 0);
        int count = 0;
        
        for (AbstractBoids other : boids) {
            if (other == this) continue;
            double dSq = position.distanceSq(other.position);
            
            if (dSq > 0 && dSq < perceptionRadiusSq) {
                sum.x += other.position.x;
                sum.y += other.position.y;
                count++;
            }
        }
        if (count > 0) {
            sum.x /= count; sum.y /= count;
            return seek(sum);
        }
        return new Point2D.Double(0, 0);
    }
}