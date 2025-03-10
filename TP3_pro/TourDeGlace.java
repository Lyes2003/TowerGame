import java.awt.*;
import java.util.List;
/**
 * Classe représentant une tour de glace.
 * La tour ralentit les ennemis proches en réduisant leur vitesse.
 * @author Chikh Lyes (CHIL68350302) , Ghanem Mohammed Mehdi (GHAM83330409)
 */
public class TourDeGlace extends Tour{
    public static final int PERIODE_RECHARGE_TIC = 1;
    public static final Tuile TOUR_BAS = new Tuile( Color.blue, Color.lightGray, BitMap.MUR );
    public static final Tuile TOUR_HAUT = new Tuile( Color.blue, Color.lightGray, BitMap.TOUR_DESSUS );


    private static final int NB_CARACTERISTIQUE = 2;

    private static final int REDUCTION = 0; // Réduction de vitesse
    private static final int DISTANCE = 1; // Distance d'action

    private static final int[] REDUCTION_VITESSE = {20, 40}; // Réduction en pourcentage
    private static final int[] PRIX_AUGMENTATION_REDUCTION = {60}; // Coût d'amélioration

    private static final int[] DISTANCE_MAX_TIR_PIXEL = {24, 32, 40}; // Rayon d'action
    private static final int[] PRIX_AUGMENTATION_DISTANCE = {40, 60}; // Coût d'amélioration

    private int recharge_tic = 0;

    {
        caracteristiques = new Caracteristique[NB_CARACTERISTIQUE];

        caracteristiques[REDUCTION] =
                new Caracteristique("reduc", REDUCTION_VITESSE, PRIX_AUGMENTATION_REDUCTION, Constantes.POSITION_CARACTERISTIQUE_Y);
        caracteristiques[DISTANCE] =
                new Caracteristique("ray", DISTANCE_MAX_TIR_PIXEL, PRIX_AUGMENTATION_DISTANCE, Constantes.POSITION_CARACTERISTIQUE_Y + 2);
    }

    /**
     * Constructeur.
     * @param position Position de la tour sur la grille.
     */
    public TourDeGlace( PositionTuile position ) {
        super( position );
    }

    /**
     * Applique l'effet de gel aux ennemis dans le rayon d'action.
     * @param ennemis Liste des ennemis à cibler.
     * @return Argent gagné (toujours 0 pour cette tour).
     */
    private int tirer( java.util.List< Ennemi > ennemis ) {
        int argentGagne = 0;
        int j = 0;
        
        
        for (Ennemi ennemi : ennemis) {
        	try {
            if (ennemi.getPositionPixel().distance(position.positionPixel()) <= caracteristiques[DISTANCE].getValeur()) {
                if (!ennemi.estGele()) {
                	int duree = 1; // Durée toujours 1 tic
                    double reductionPourcentage = caracteristiques[REDUCTION].getValeur(); // Réduction en %
                    ennemi.appliquerEffetGele(reductionPourcentage);
                }
            }
        }catch( IndexOutOfBoundsException e ) {
            // l'ennemi choisi a deja atteint le chateau, donc ne peut tirer sur l'ennemi.
        }
    }
    return argentGagne;
}

        

    /**
     * Animer la tour, appelée à chaque tic du jeu.
     * @param ennemis Liste des ennemis.
     * @return Argent gagné (toujours 0 pour cette tour).
     */
    @Override
    public int animer( List< Ennemi > ennemis ) {
        int argentGagne = 0;
        boolean pretTirer = false;

        ++ recharge_tic;

        if( PERIODE_RECHARGE_TIC <= recharge_tic) {
            pretTirer = true;
            recharge_tic = 0;
        }

        if( pretTirer ) {
            argentGagne = tirer( ennemis );
        }

        return argentGagne;
    }

    /**
     * Récupère l'apparence de la base de la tour.
     * @return Tuile de la base.
     */
	@Override
	protected Tuile getTuileBas() {
		return TOUR_BAS;
	}

	/**
	 * Récupère l'apparence du sommet de la tour.
	 * @return Tuile du sommet.
	 */
	@Override
	protected Tuile getTuileHaut() {
		return TOUR_HAUT;
	}
}
