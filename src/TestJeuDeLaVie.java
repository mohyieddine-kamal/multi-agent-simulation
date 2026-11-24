import gui.GUISimulator;
import gui.Rectangle;
import gui.Simulable;
import gui.Text;
import simulator.GrilleSimulator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class TestJeuDeLaVie{
    public static void main(String[] args) {
        // crée la fenêtre graphique dans laquelle dessiner
        GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);
        // crée la grille, en l'associant à la fenêtre graphique précédente
        GrilleJeuDeLaVie grille = new GrilleJeuDeLaVie(gui,20);
    }
}


/** Classe implémentant la grille du jeu de la vie de Conway
 * qui hérite de GrilleSimulator donc doit implémenter etat_suiv_case()
 */
class GrilleJeuDeLaVie extends GrilleSimulator{

    /** Créer une grille du jeu de la vie
     * @param gui l'interface graphique associée
     * @param taille_case largeur d'une case de la grille
    */
    public GrilleJeuDeLaVie(GUISimulator gui,int taille_case){
        super(gui,taille_case,2);
        super.draw();
    }

    /** Créer une grille du jeu de la vie
     * @param gui l'interface graphique associée
     * @param nbr_lin nombre de lignes de la grille
     * @param nbr_col nombre de colonnes de la grille
     */
    public GrilleJeuDeLaVie(GUISimulator gui,int nbr_lin,int nbr_col){
        super(gui,nbr_lin,nbr_col,2);
        super.draw();
    }

    @Override
    protected void etat_suiv_case(int i,int j,int imoins1,int iplus1,int jmoins1,int jplus1){
        int sum = super.grille[imoins1*super.nbr_col + jmoins1] % 2;
        sum += super.grille[imoins1*super.nbr_col + j] % 2;
        sum += super.grille[imoins1*super.nbr_col + jplus1] % 2;
        sum += super.grille[i*super.nbr_col + jmoins1] % 2;
        sum += super.grille[i*super.nbr_col + jplus1] % 2;
        sum += super.grille[iplus1*super.nbr_col + jmoins1] % 2;
        sum += super.grille[iplus1*super.nbr_col + j] % 2;
        sum += super.grille[iplus1*super.nbr_col + jplus1] % 2;

        int k = i*super.nbr_col+j;
        if(super.grille[k]==0){
            if(sum==3){super.grille[k] = 2;}
        }
        else{
            if(sum!=2 && sum!=3){super.grille[k] = 3;}
        }
    }

    @Override
    public void next() {
        // calcule l'état suivant de chaque case
        // Si case morte devient vivante alors grille[case] = 2
        // Si case vivante devient morte alors grille[case] = 3
        for(int i = 0; i < super.nbr_lin; i++){
            for(int j=0; j < nbr_col; j++){
                int imoins1 = (i==0)? nbr_lin-1 : i-1;
                int iplus1 = (i==nbr_lin-1)? 0 : i+1;
                int jmoins1 = (j==0)? nbr_col-1 : j-1;
                int jplus1 = (j==nbr_col-1)? 0 : j+1;

                etat_suiv_case(i,j,imoins1,iplus1,jmoins1,jplus1);
            }
        }
        // met à jour la grille
        for(int k = 0; k < super.nbr_lin*super.nbr_col; k++){
            if(grille[k]>1){
                grille[k] = grille[k] == 2 ? 1 : 0;
            }
        }
        // dessine grille dans la fenêtre graphique
        super.draw();
    }

}
