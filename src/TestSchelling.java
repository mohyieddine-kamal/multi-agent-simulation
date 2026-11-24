import gui.GUISimulator;
import gui.Rectangle;
import gui.Simulable;
import gui.Text;
import simulator.GrilleSimulator;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Collections;
import java.util.Random;

public class TestSchelling{
    public static void main(String[] args) {
        // crée la fenêtre graphique dans laquelle dessiner
        GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);
        // crée la grille, en l'associant à la fenêtre graphique précédente
        GrilleSchelling grille = new GrilleSchelling(gui,20,3,5);
    }
}


/** Classe implémentant la grille du modèle de Schelling
 * qui hérite de GrilleSimulator donc doit implémenter etat_suiv_case()
 */
class GrilleSchelling extends GrilleSimulator{

    /** Constante K qui définit le nombre max pour une case de voisins de populations différentes */
    private int k;

    /** File des indices des logements vacants */
    private Queue<Integer> file_logements_vacants;
    /** Nombre de logements vacants dans la grille */
    private int nbr_logVacants;

    /** Tableau d'une permutation des indices pour faire des tirages sans remise */
    private int[] tab_indice;

    /** Grille auxiliaire pour récupérer le résultat */
    private int[] grille_aux;

    /** Créer une grille du modèle de Schelling
     * @param gui l'interface graphique associée
     * @param taille_case largeur d'une case de la grille
     * @param nbr_population nombre de populations du modèle de Schelling
     * @param k constante K du modèle de Schelling
    */
    public GrilleSchelling(GUISimulator gui,int taille_case,int nbr_population,int k){
        super(gui,taille_case,nbr_population+1); //+1 car il y a les logements vacants
        this.nbr_logVacants = (super.nbr_col * super.nbr_lin * 20)/100;
        this.k = k;
        init_elem_aux();
        super.draw();
    }

    /** Créer une grille du modèle de Schelling
     * @param gui l'interface graphique associée
     * @param taille_case largeur d'une case de la grille
     * @param nbr_population nombre de populations du modèle de Schelling
     * @param k constante K du modèle de Schelling
     * @param pourcentageLogementsVacants pourcentage de logements vacants
    */
    public GrilleSchelling(GUISimulator gui,int taille_case,int nbr_population,int k,int pourcentageLogementsVacants){
        super(gui,taille_case,nbr_population+1); //+1 car il y a les logements vacants
        if(pourcentageLogementsVacants<0 || pourcentageLogementsVacants >= 100){
            throw new IllegalArgumentException("Le pourcentage de logements vacants doit être compris entre 1 et 99 !");
        }
        this.nbr_logVacants = (super.nbr_col * super.nbr_lin * pourcentageLogementsVacants) / 100;
        this.k = k;
        init_elem_aux();
        super.draw();
    }

    /** Créer une grille du modèle de Schelling
     * @param gui l'interface graphique associée
     * @param nbr_lin nombre de lignes de la grille
     * @param nbr_col nombre de colonnes de la grille
     * @param nbr_population nombre de populations du modèle de Schelling
     * @param k constante K du modèle de Schelling
     * @param pourcentageLogementsVacants pourcentage de logements vacants
    */
    public GrilleSchelling(GUISimulator gui,int nbr_lin,int nbr_col,int nbr_population,int k,int pourcentageLogementsVacants){
        super(gui,nbr_lin,nbr_col,nbr_population+1); //+1 car il y a les logements vacants
        if(pourcentageLogementsVacants<0 || pourcentageLogementsVacants >= 100){
            throw new IllegalArgumentException("Le pourcentage de logements vacants doit être compris entre 1 et 99 !");
        }
        this.nbr_logVacants = (super.nbr_col * super.nbr_lin * pourcentageLogementsVacants) / 100;
        this.k = k;
        init_elem_aux();
        super.draw();
    }

    @Override
    protected void init_array(){
        Random randomNumber = new Random();
        int n = super.nbr_lin*super.nbr_col;
        for(int k=0;k<n;k++){
            super.grille[k] =randomNumber.nextInt(super.nbr_color-1)+1;
        }
    }

    /** Initialisation des éléments de GrilleSchelling qui ne sont pas dans GrilleSimulator */
    private void init_elem_aux(){
        if(this.nbr_logVacants == 0){
            this.nbr_logVacants = 1;
        }
        init_tab();
        init_logements_vacants();
    }

    /** Initialise le tableau d'indices de 0 à nbr_lin*nbr_col et la grille_aux avec que des 0*/
    private void init_tab(){
        int n = super.nbr_lin*super.nbr_col;
        this.tab_indice = new int[n];
        this.grille_aux = new int[n];
        for(int i=0;i<n;i++){
            this.grille_aux[i] = 0;
            this.tab_indice[i] = i;
        }
    }

    /** Echanger deux élements d'un tableau */
    private void swap(int[] tab,int i,int j){
        int val = tab[i];
        tab[i] = tab[j];
        tab[j] = val;
    }

    /** Initialise les indices des logements vacants de départ
     * effectue des tirages sans remise avec le tableau d'indices
     */
    private void init_logements_vacants(){
        this.file_logements_vacants = new LinkedList<Integer>();
        Random randomNumber = new Random();
        int n = super.nbr_lin*super.nbr_col;
        for(int k=0;k<this.nbr_logVacants;k++){
            int x = randomNumber.nextInt(n-k);
            int i = this.tab_indice[x];
            this.file_logements_vacants.add(i);
            super.grille[i] = 0;
            swap(this.tab_indice,x,n-k-1);
        }
    }

    @Override
    protected void etat_suiv_case(int i,int j,int imoins1,int iplus1,int jmoins1,int jplus1){
        int n = i*super.nbr_col+j;
        int c = super.grille[n];
        if(c == 0){
            return;
        }
        int sum = 0;
        if(super.grille[imoins1*super.nbr_col + jmoins1] != c){sum++;} // case (i-1,j-1)
        if(super.grille[imoins1*super.nbr_col + j] != c){sum++;} // case (i-1,j)
        if(super.grille[imoins1*super.nbr_col + jplus1] != c){sum++;} // case (i-1,j+1)
        if(super.grille[i*super.nbr_col + jmoins1] != c){sum++;} // case (i,j-1)
        if(super.grille[i*super.nbr_col + jplus1] != c){sum++;} // case (i,j+1)
        if(super.grille[iplus1*super.nbr_col + jmoins1] != c){sum++;} // case (i+1,j-1)
        if(super.grille[iplus1*super.nbr_col + j] != c){sum++;} // case (i+1,j)
        if(super.grille[iplus1*super.nbr_col + jplus1] != c){sum++;} // case (i+1,j+1)

        if(sum >= this.k){
            int indice_logVacants = this.file_logements_vacants.remove();
            this.file_logements_vacants.add(n);
            this.grille_aux[indice_logVacants] = c;
            this.grille_aux[n] = 0;
        }
        else{
            this.grille_aux[n] = c;
        }
    }

    @Override
    public void next() {
        Random randomNumber = new Random();
        int n = nbr_col*nbr_lin;
        for(int k=0;k<n;k++){
            int x = randomNumber.nextInt(n-k);
            int indice = this.tab_indice[x];
            swap(this.tab_indice,x,n-k-1);
            int j = indice%super.nbr_col;
            int i = (indice-j)/nbr_col;

            int imoins1 = (i==0)? nbr_lin-1 : i-1;
            int iplus1 = (i==nbr_lin-1)? 0 : i+1;
            int jmoins1 = (j==0)? nbr_col-1 : j-1;
            int jplus1 = (j==nbr_col-1)? 0 : j+1;

            etat_suiv_case(i,j,imoins1,iplus1,jmoins1,jplus1);
        }
        for(int k = 0; k < n; k++){
            super.grille[k] = this.grille_aux[k];
        }
        super.draw();
    }


    @Override
    public void restart() {
        Random randomNumber = new Random();
        int n = super.nbr_lin*super.nbr_col;
        for(int k=0;k<n;k++){
            this.grille_aux[k] = 0;
            super.grille[k] = randomNumber.nextInt(super.nbr_color-1)+1;
        }
        this.file_logements_vacants = new LinkedList<Integer>();
        for(int k=0;k<nbr_logVacants;k++){
            int x = randomNumber.nextInt(n-k);
            int i = this.tab_indice[x];
            file_logements_vacants.add(i);
            super.grille[i] = 0;
            swap(this.tab_indice,x,n-k-1);
        }
        super.draw();
    }
}