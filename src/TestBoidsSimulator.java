import gui.GUISimulator;
import simulator.BoidsSimulator;
import models.boids.*;
import control.*;
import java.awt.Color;

public class TestBoidsSimulator {
    public static void main(String[] args) {
        GUISimulator gui = new GUISimulator(800, 600, Color.WHITE);
        EventManager manager = new EventManager();
        
        // Création du simulateur
        BoidsSimulator simulator = new BoidsSimulator(gui, manager);

        // 1. Création du GROUPE PROIES
        // (Nom, Sep, Ali, Coh)
        BoidsGroup preyGroup = new BoidsGroup("Poissons", 2.5, 1.0, 1.5);
        
        // Remplissage du groupe
        for(int i=0; i<50; i++) {
            preyGroup.addBoid(new PreyBoid(400 + (int)(Math.random()*100), 300 + (int)(Math.random()*100)));
        }
        
        // 2. Création du GROUPE PRÉDATEURS
        // (Nom, Sep, Ali, Coh)
        BoidsGroup predatorGroup = new BoidsGroup("Requins", 5.0, 0.2, 0.0);
        
        predatorGroup.addBoid(new PredatorBoid(100, 100));
        predatorGroup.addBoid(new PredatorBoid(700, 500));
        predatorGroup.addBoid(new PredatorBoid(100, 500));

        // 3. Ajout des groupes au simulateur (pour l'affichage et la grille)
        simulator.addGroup(preyGroup);
        simulator.addGroup(predatorGroup);

        // --- INITIALISATION DES ÉVÉNEMENTS ---

        // Les proies sont mises à jour à chaque pas de temps (Délai = 1)
        simulator.scheduleGroupUpdate(preyGroup, 1);

        // Les prédateurs sont plus lents/saccadés (Délai = 3)
        simulator.scheduleGroupUpdate(predatorGroup, 3);

    }
}