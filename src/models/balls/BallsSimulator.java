package models.balls;
import gui.GUISimulator;
import gui.Simulable;

import java.awt.Color;
import java.awt.Point;

import gui.Oval;

import java.util.List;

/**
 * Classe "Contrôleur" pour la simulation des balles.
 * Elle fait le lien entre l'interface graphique (GUISimulator) et
 * la logique des données (Balls).
 * Elle implémente Simulable pour répondre aux événements des boutons
 * de l'interface.
 */
public class BallsSimulator implements Simulable {
    private final Balls balls;
    private final GUISimulator gui;


    /**
     * Constructeur du simulateur de balles.
     *
     * @param gui L'instance de GUISimulator dans laquelle dessiner.
     */
    public BallsSimulator(GUISimulator gui) {
        this.gui = gui;
        this.balls = new Balls(50, gui.getWidth(), gui.getHeight());
        draw();
    }

    /**
     * Méthode exécutée à chaque "pas" de simulation.
     * Appelée par le bouton "Suivant" ou en boucle par "Lecture"
     */
    @Override
    public void next() {
        balls.translate();
        move();
        draw();
    }

    /**
     * Méthode exécutée lors d'un clic sur le bouton "Début".
     */
    @Override
    public void restart() {
        balls.reInit();
        draw();

    }

    /**
     * Méthode privée pour dessiner l'état actuel de la simulation.
     * (Implémentation de la Question 3)
     */
    private void draw() {
        gui.reset();
        Point[] points = balls.getBalls();
        for (Point p : points) {
            gui.addGraphicalElement(
                    new Oval(p.x, p.y, Color.WHITE, Color.WHITE, 20)
            );
        }
    }

    /**
     * Méthode privée pour gérer la physique (collision avec les bords).
     * (Implémentation de la Question 4)
     */
    private void move() {
        int width = gui.getWidth() - 40;
        int height = gui.getHeight() - 100;
        int diameter = balls.getDiameter();

        for (int i = 0; i < balls.getNBalls(); i++) {
            Point p = balls.getBalls()[i];
            Point v = balls.getVelocty()[i];
            if (p.x + diameter > width || p.x - diameter< 0) {
                v.x = -v.x ;
            }

            if (p.y + diameter > height || p.y - diameter< 0) {
                v.y = -v.y;
            }
        }
    }

}
