package simulator;
import gui.GUISimulator;
import gui.Rectangle;
import gui.Simulable;
import gui.Text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

/**Classe abstraite GrilleSimulator
 * qui définit les attributs principaux d'un automate cellulaire 
 * et es méthodes principales (initialisation, dessin, état suivant)
 * hérite de Simulable donc doit définir les fonctions next() et restart()
*/
public abstract class GrilleSimulator implements Simulable{
    /** L'interface graphique associée */
    private GUISimulator gui;

    /** Le nombre possible de couleurs d'une case*/
    protected int nbr_color;

    /** Nombre de lignes de la grille */
    protected int nbr_lin;
    /** Nombre de colonnes de la grille */
    protected int nbr_col;
    /** Largeur d'une case */
    protected int taille_case;

    /** Grille des états courants */
    protected int[] grille;
    


    /**Crée un automate cellulaire
     * @param gui l'interface graphique associée, dans laquelle se fera le
     * dessin et qui enverra les messages via les méthodes héritées de
     * Simulable.
     * @param taille_case largeur d'une case de la grille
     * le nombre de lignes et le nombre de colones de la grille sont définis
     *  en fonction de la taille de la case demandée 
     * et de la largeur de la fenêtre graphique
     * @param nbr_color nombre de couleurs possible d'une case
     */
    public GrilleSimulator(GUISimulator gui,int taille_case, int nbr_color){
        if(nbr_color <= 0){
            throw new IllegalArgumentException("Le nombre de couleurs doit être supérieur ou égal à 1 !");
        }
        if(taille_case <= 0){
            throw new IllegalArgumentException("La taille d'une case doit être supérieure ou égale à 1 !");
        }
        this.gui = gui;
        gui.setSimulable(this); // association a la gui!
        int height = gui.getHeight() - 140; // hauteur de la fenêtre où dessiner la grille
        int width = gui.getWidth() - 40; // largeur de la fenêtre où dessiner la grille

        
        this.nbr_lin = height / taille_case;
        this.nbr_col = width / taille_case;
        this.taille_case = taille_case;
        this.nbr_color = nbr_color;

        grille = new int[nbr_col*nbr_lin];
        init_array();
    }

    /**Crée un automate cellulaire
     * @param gui l'interface graphique associée, dans laquelle se fera le
     * dessin et qui enverra les messages via les méthodes héritées de
     * Simulable.
     * @param nbr_lin nombre de lignes de la grille
     * @param nbr_col nombre de colonnes de la grille
     * la taille d'une case de la grille est en fonction
     * de nbr_lin et nbr_col et de la taille de la fenêtre graphique 
     * @param nbr_color nombre de couleurs possible d'une case
     */
    public GrilleSimulator(GUISimulator gui,int nbr_lin,int nbr_col, int nbr_color){
        if(nbr_color <= 0){
            throw new IllegalArgumentException("Le nombre de couleurs doit être supérieur ou égal à 1 !");
        }
        if(nbr_lin <= 0 || nbr_col <= 0){
            throw new IllegalArgumentException("Le nombre de lignes et de colonnes doit être supérieure ou égale à 1 !");
        }
        this.gui = gui;
        gui.setSimulable(this);	// association a la gui!
        int height = gui.getHeight() - 140; // hauteur de la fenêtre où dessiner la grille
        int width = gui.getWidth() - 40; // largeur de la fenêtre où dessiner la grille

        this.nbr_lin = nbr_lin;
        this.nbr_col = nbr_col;
        int size_n = height/nbr_lin;
        int size_m = width/nbr_col;
        this.taille_case = size_n<size_m? size_n : size_m;
        this.nbr_color = nbr_color;

        
        grille = new int[nbr_col*nbr_lin];
        init_array();
    }

    /** Initialise les couleurs des cases de la grille de départ  
     * de manière aléatoire
    */
    protected void init_array(){
        Random randomNumber = new Random();
        for(int k=0;k<nbr_lin*nbr_col;k++){
            grille[k] = randomNumber.nextInt(nbr_color);
        }
    }

    /** Calcule l'état suivant de la case i*/
    protected abstract void etat_suiv_case(int i,int j,int imoins1,int iplus1,int jmoins1,int jplus1);



    @Override
    public abstract void next();

    @Override
    public void restart() {
        // réinitialise grille avec des couleurs aléatoires
        Random randomNumber = new Random();
        for(int k=0;k<nbr_lin*nbr_col;k++){
            grille[k] = randomNumber.nextInt(nbr_color);
        }
        draw();
    }

    //** Dessine la grille */
    protected void draw() {
        int bordure = 20;
        gui.reset(); // clear window
        for(int i=0;i<nbr_lin;i++){
            for(int j=0;j<nbr_col;j++){
                int x = grille[i*nbr_col+j];
                int c = 255 - x*255/nbr_color;
                Color fillColor = new Color(c,c,c);
                gui.addGraphicalElement(new Rectangle(j*this.taille_case+bordure,i*this.taille_case+bordure,Color.BLACK,fillColor,this.taille_case));
            }
        }
    }
}