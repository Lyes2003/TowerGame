import java.awt.*;
import java.util.List;
/**
 * Classe représentant une tour de feu.
 * La tour enflamme les ennemis proches, leur infligeant des dégâts sur la durée.
 * @author Chikh Lyes (CHIL68350302) , Ghanem Mohammed Mehdi (GHAM83330409)
 */
public class TourDeFeu extends Tour{
    public static final int PERIODE_RECHARGE_TIC = 15;
    public static final Tuile TOUR_BAS = new Tuile( Color.red, Color.lightGray, BitMap.MUR );
    public static final Tuile TOUR_HAUT = new Tuile( Color.red, Color.lightGray, BitMap.TOUR_DESSUS );



    private static final int NB_CARACTERISTIQUE = 2;

    private static final int DUREE = 1;
    private static final int DISTANCE = 0;


    private static final int [] PRIX_AUGMENTATION_DISTANCE = { 40, 80};
    private static final int [] DISTANCE_MAX_TIR_PIXEL = { 20, 30, 50};
    
    private static final int [] DUREE_ENFLAMME  = { 3, 5, 7 };
    private static final int [] PRIX_AUGMENTATION_DUREE  = { 20, 30};

    {
        caracteristiques = new Caracteristique[NB_CARACTERISTIQUE];

        caracteristiques[ DUREE ] =
                new Caracteristique( "dur", DUREE_ENFLAMME, PRIX_AUGMENTATION_DUREE, Constantes.POSITION_CARACTERISTIQUE_Y  );
        caracteristiques[ DISTANCE ] =
                new Caracteristique( "dis", DISTANCE_MAX_TIR_PIXEL, PRIX_AUGMENTATION_DISTANCE, Constantes.POSITION_CARACTERISTIQUE_Y + 2 );
    }

    private int recharge_tic = 0;

    /**
     * Constructeur.
     * @param position Position de la tour sur la grille.
     */
    public TourDeFeu( PositionTuile position ) {
        super( position );
    }

    /**
     * Applique l'effet de feu au premier ennemi non enflammé dans le rayon.
     * @param ennemis Liste des ennemis à cibler.
     * @return Argent gagné.
     */
    private int tirer(List<Ennemi> ennemis) {
        int argentGagne = 0;

        for (int i = 0; i < ennemis.size(); i++) {
            Ennemi ennemi = ennemis.get(i);

            if (ennemi.getPositionPixel().distance(position.positionPixel()) <= caracteristiques[DISTANCE].getValeur()
                    && !ennemi.estEnflamme()) {

                ennemi.appliquerEffetFeu(caracteristiques[DUREE].getValeur());

                if (ennemi.reduireVie(0)) { // Vérification sans réduire de points de vie supplémentaires
                    argentGagne += ennemi.getValeurArgent();
                    ennemis.remove(i); 
                }
                break; 
            }
        }

        return argentGagne;
    }


    /**
     * Animer la tour, appelée à chaque tic du jeu.
     * @param ennemis Liste des ennemis.
     * @return Argent gagné.
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
