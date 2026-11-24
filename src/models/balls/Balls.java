package models.balls;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Représente la "logique" d'un ensemble de balles (agents).
 * Cette classe stocke les positions des balles et gère leur
 * déplacement (translate) et leur réinitialisation (reInit),
 * séparant ainsi les données de la simulation graphique.
 */

public class Balls {
    // Tableau des positions actuelles des balles
    private final Point[] balls;
    // Tableau des vitesses actuelles
    private final Point[] velocityBalls;
    // Tableau des vitesses initiales
    private final Point[] initialVelocities;

    // Liste pour sauvegarder les positions initiales (pour la méthode reInit)
    private final Point[] initialBalls;
    private int NBalls;
    private int diameter = 20;

    /**
     * Constructeur de la classe Balls.
     * Initialise les tableaux et définit les positions initiales des balles.
     * @param N : Nombre de balles
     * @param dimX : Largeur de l'interface
     * @param dimY : Hauteur de l'interface
     */
    public Balls(int N, int dimX,int dimY) {
        this.NBalls = N;
        this.balls = new Point[N];
        this.initialBalls = new Point[N];
        this.initialVelocities = new Point[N];
        this.velocityBalls = new Point[N];
        Random rand = new Random();
        //Génération aléatoire des points
        for (int i =0; i < N; i++) {
            balls[i] = new Point(rand.nextInt(dimX - 40 - 2*diameter) + diameter, rand.nextInt(dimY - 100 - 2*diameter) + diameter);
            initialBalls[i] = (Point) balls[i].clone();
            initialVelocities[i] = new Point(rand.nextInt(20) - 10, rand.nextInt(20) - 10);
            velocityBalls[i] = (Point) initialVelocities[i].clone();
        }

    }

    /**
     * Déplace (translate) toutes les balles.

     */
    public void translate() {
        for (int i = 0; i < NBalls; i++) {
            Point p = balls[i];
            Point v = velocityBalls[i];
            p.translate(v.x, v.y);
        }
    }

    /**
     * Réinitialise toutes les balles à leurs positions initiales.
     */
    public void reInit() {
        for (int i = 0; i < NBalls; i++) {
            balls[i] = (Point) initialBalls[i].clone();
            velocityBalls[i] = (Point) initialVelocities[i].clone();
        }
    }

    /**
     * Retourne une représentation textuelle (String) de la liste des balles.
     * (Redéfinit la méthode standard de la classe Object).
     *
     * @return Une chaîne de caractères formatée (ex: "[java.awt.Point[x=10,y=30], ...]")
     */
    @Override
    public String toString() {
        return balls.toString();
    }

    /**
     * "Getter" (accesseur) qui permet aux autres classes (comme BallsSimulator)
     * de lire les positions des balles pour les dessiner.
     *
     * @return La liste de Points (Point) des balles.
     */
    public Point[] getBalls() {
        return this.balls;
    }
    public Point[] getVelocty() {
        return this.velocityBalls;
    }
    public int getNBalls(){
        return this.NBalls;
    }
    public int getDiameter(){
        return this.diameter;
    }

}
