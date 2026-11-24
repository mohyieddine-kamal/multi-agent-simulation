import gui.GUISimulator;
import models.balls.BallsSimulator;

import java.awt.*;


public class TestBallsSimulator {
    public static void main(String[] args) {
        GUISimulator gui = new GUISimulator(500, 500, Color.BLACK);
        gui.setSimulable(new BallsSimulator(gui));
    }
    
}