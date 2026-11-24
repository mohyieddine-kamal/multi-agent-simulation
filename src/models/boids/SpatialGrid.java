package models.boids;

import java.util.ArrayList;

/**
 * Structure de données pour optimiser la recherche des voisins.
 * <p>
 * Le problème naïf de la détection des voisins est en O(N^2) (chaque boid vérifie tous les autres).
 * Cette grille divise l'espace en cellules de taille fixe (resolution).
 * Pour trouver ses voisins, un boid ne regarde que dans sa cellule et les 8 adjacentes.
 * Cela réduit la complexité moyenne à O(N * k), où k est le nombre moyen de voisins.
 * </p>
 */
public class SpatialGrid {
    private int cols;
    private int rows;
    private double resolution;
    private ArrayList<AbstractBoids>[][] grid;

    /**
     * Initialise la grille spatiale.
     * @param width Largeur de la zone de simulation
     * @param height Hauteur de la zone de simulation
     * @param resolution Taille d'une cellule (doit être >= au rayon de perception max)
     */
    @SuppressWarnings("unchecked")
    public SpatialGrid(int width, int height, double resolution) {
        this.resolution = resolution;
        // On ajoute +1 pour gérer les débordements sur les bords
        this.cols = (int) (width / resolution) + 1;
        this.rows = (int) (height / resolution) + 1;

        // Création de la matrice de listes
        grid = new ArrayList[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j] = new ArrayList<>();
            }
        }
    }

    /**
     * Vide toutes les cellules de la grille.
     * Doit être appelé au début de chaque pas de temps.
     */
    public void clear() {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].clear();
            }
        }
    }

    /**
     * Place un boid dans la cellule correspondant à sa position actuelle.
     * @param b Le boid à ajouter
     */
    public void addBoid(AbstractBoids b) {
        int col = (int) (b.getPosition().x / resolution);
        int row = (int) (b.getPosition().y / resolution);

        // Vérification des bornes pour éviter les exceptions
        if (col >= 0 && col < cols && row >= 0 && row < rows) {
            grid[col][row].add(b);
        }
    }

    /**
     * Récupère les voisins potentiels d'un boid.
     * Cherche dans la cellule du boid et les 8 cellules voisines (Moore Neighborhood).
     * 
     * @param b Le boid pour lequel on cherche des voisins
     * @return Une liste brute de boids proches (la distance exacte doit encore être vérifiée)
     */
    public ArrayList<AbstractBoids> getPotentialNeighbors(AbstractBoids b) {
        ArrayList<AbstractBoids> neighbors = new ArrayList<>();
        int col = (int) (b.getPosition().x / resolution);
        int row = (int) (b.getPosition().y / resolution);

        // Parcours des cellules voisines (x-1 à x+1, y-1 à y+1)
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int neighborCol = col + i;
                int neighborRow = row + j;

                // Vérification des limites de la grille
                if (neighborCol >= 0 && neighborCol < cols && 
                    neighborRow >= 0 && neighborRow < rows) {
                    neighbors.addAll(grid[neighborCol][neighborRow]);
                }
            }
        }
        return neighbors;
    }
}