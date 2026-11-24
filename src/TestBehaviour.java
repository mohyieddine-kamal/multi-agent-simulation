import gui.GUISimulator;
import simulator.BoidsSimulator;
import control.EventManager;
import control.GroupUpdateEvent;
import models.boids.*;

import java.awt.Color;

public class TestBehaviour{
    
    public static void main(String[] args) {
        GUISimulator gui = new GUISimulator(800, 600, Color.WHITE);
        EventManager manager = new EventManager();
        BoidsSimulator simulator = new BoidsSimulator(gui, manager);
        
        // 1. Haut-Gauche : POISSONS (Classique)
        BoidsGroup fish = new BoidsGroup("Poissons", 2.0, 1.0, 1.0);
        for(int i=0; i<40; i++) fish.addBoid(new PreyBoid(200, 200));
        
        // 2. Haut-Droite : MOUSTIQUES (Cohésion forte, pas d'alignement)
        BoidsGroup insects = new BoidsGroup("Moustiques", 1.5, 0.1, 2);
        for(int i=0; i<40; i++) insects.addBoid(new PreyBoid(800, 200));

        // 3. Bas-Gauche : GAZ (Séparation seule)
        BoidsGroup gas = new BoidsGroup("Gaz", 4.0, 0.0, 0.0);
        for(int i=0; i<40; i++) gas.addBoid(new PreyBoid(200, 600));


        // Ajout au simulateur ce que vous voulez ou tous à la fois
        simulator.addGroup(fish);
        // simulator.addGroup(insects);
        // simulator.addGroup(gas);
        

        // Lancement
        simulator.createUpdateEvent();
        
        
    }
    }
