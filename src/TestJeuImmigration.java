import gui.GUISimulator;
import gui.Rectangle;
import gui.Simulable;
import gui.Text;
import simulator.GrilleSimulator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class TestJeuImmigration{
    public static void main(String[] args) {
        // crée la fenêtre graphique dans laquelle dessiner
        GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);
        // crée la grille, en l'associant à la fenêtre graphique précédente
        GrilleImmigration grille = new GrilleImmigration(gui,20,3);
    }
}

/** Classe implémentant la grille du jeu de l'immigration
 * qui hérite de GrilleSimulator donc doit implémenter etat_suiv_case()
 */
class GrilleImmigration extends GrilleSimulator{

    /** tableau circulaire pour calculer l'état suivant*/
    private int[] tab_aux;
    private int i_debut = 0;
    private int i_fin = 0;
    private int capacite;

    /** tableau récupérant la première ligne de la grille */
    private int[] ligne_1;

    /** Créer une grille du jeu de l'immigration
     * @param gui l'interface graphique associée
     * @param taille_case largeur d'une case de la grille
     * @param nbr_color nombre de populations du jeu de l'immigration
    */
    public GrilleImmigration(GUISimulator gui,int taille_case,int nbr_color){
        super(gui,taille_case,nbr_color);
        init_tableaux_auxiliaires();
        super.draw();
    }

    /** Créer une grille du jeu de l'immigration
     * @param gui l'interface graphique associée
     * @param nbr_lin nombre de lignes de la grille
     * @param nbr_col nombre de colonnes de la grille
     * @param nbr_color nombre de populations du jeu de l'immigration
    */
    public GrilleImmigration(GUISimulator gui,int nbr_lin,int nbr_col,int nbr_color){
        super(gui,nbr_lin,nbr_col,nbr_color);
        init_tableaux_auxiliaires();
        super.draw();
    }

    void init_tableaux_auxiliaires(){
        capacite = super.nbr_col * 2;
        tab_aux = new int[capacite];
        ligne_1 = new int[super.nbr_col];
    }

    void ajouter_element_tab_aux(int x){
        tab_aux[i_fin] = x;
        i_fin = (i_fin + 1) % capacite;
    }

    @Override
    protected void etat_suiv_case(int i,int j,int imoins1,int iplus1,int jmoins1,int jplus1){
        int n = i*super.nbr_col+j;
        int etat_k = super.grille[n];
        int etat_kplus1 = (etat_k+1)%super.nbr_color;
        int sum = 0;
        if(tab_aux[(i_debut + jmoins1) % capacite] == etat_kplus1){sum++;} //case (i-1,j-1)
        if(tab_aux[(i_debut + j) % capacite] == etat_kplus1){sum++;} // case (i-1,j)
        if(tab_aux[(i_debut + jplus1) % capacite] == etat_kplus1){sum++;} // case (i-1,j)
        if(tab_aux[(i_debut + super.nbr_col + jmoins1) % capacite] == etat_kplus1){sum++;} // case (i,j-1)
        if(tab_aux[(i_debut + super.nbr_col + jplus1) % capacite] == etat_kplus1){sum++;} // case (i,j+1)

        if(iplus1 == 0){
            if(ligne_1[jmoins1] == etat_kplus1){sum++;} // case (i+1,j-1)
            if(ligne_1[j] == etat_kplus1){sum++;} // case (i+1,j)
            if(ligne_1[jplus1] == etat_kplus1){sum++;} // case (i+1,j+1)
        }
        else{
            if(super.grille[iplus1*super.nbr_col + jmoins1] == etat_kplus1){sum++;} // case (i+1,j-1)
            if(super.grille[iplus1*super.nbr_col + j] == etat_kplus1){sum++;} // case (i+1,j)
            if(super.grille[iplus1*super.nbr_col + jplus1] == etat_kplus1){sum++;} // case (i+1,j+1)
        }
        

        if(sum>=3){
            super.grille[n] = etat_kplus1;
        }
    }

    @Override
    public void next(){
        int n = super.nbr_col * super.nbr_lin;

        // recopie de la première ligne de la grille dans ligne_1[]
        for(int k = 0; k < super.nbr_col; k++){
            ligne_1[k] = super.grille[k];
        }

        // recopie de la dernière ligne de la grille dans tab_aux
        for(int k = super.nbr_col *(super.nbr_lin-1);k<n;k++){
            ajouter_element_tab_aux(super.grille[k]);
        }

        // calcule l'état suivant de chaque case et met le résultat dans grille
        for(int i = 0; i < super.nbr_lin; i++){
            // recopie de la ligne i en écrasant les valeurs de la ligne i-2
            for(int k = super.nbr_col*i; k < super.nbr_col*(i+1);k++){
                ajouter_element_tab_aux(super.grille[k%n]);
            }
            this.i_debut = this.i_fin;

            for(int j=0; j < nbr_col; j++){
                int imoins1 = (i==0)? nbr_lin-1 : i-1;
                int iplus1 = (i==nbr_lin-1)? 0 : i+1;
                int jmoins1 = (j==0)? nbr_col-1 : j-1;
                int jplus1 = (j==nbr_col-1)? 0 : j+1;

                etat_suiv_case(i,j,imoins1,iplus1,jmoins1,jplus1);
            }
        }
        super.draw();
    }
}