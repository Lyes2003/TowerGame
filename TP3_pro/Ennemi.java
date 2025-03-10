import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Classe représentant un ennemi dans le jeu.
 * Les ennemis peuvent être enflammés ou gelés, 
 * ce qui affecte leur vitesse ou leur points de vie.
 */
public class Ennemi {
	
    private static final int TICS_PAR_DEGAT = 5; 
    public static final int TYPE_LAPIN = 1;
    public static final int TYPE_TORTUE = 2;
    public static final int TYPE_BOSS = 3;
    
    private Chemin chemin;
    private int noSegment;
    private double distance;

    private double vitesse;
    private int pointVie;
    private int pointVieMax;

    private Tuile image;
    private int valeurArgent;
    
    private boolean enflamme = false;
    private int dureeFeuRestante = 0;
    private int compteurTicsFeu = 0;
    
    private boolean gele = false;
    private double vitesseorigine;
 
    //j'ai ajouter un attribue pour le constructeur ennemi pour savoir quelle type est l'ennemi
    private final int typeEnnemi ; 

    

    public Ennemi( Chemin chemin, double vitesse, int pointVie, int valeurArgent, Tuile image, int typreEnnemi ) {
		this.chemin = chemin;
        this.vitesse = vitesse;
        this.vitesseorigine = vitesse;
        this.pointVie = pointVie;
        this.pointVieMax = pointVie;
        this.valeurArgent = valeurArgent;
        this.image = image;
        this.distance = 0.0;
        this.noSegment = 0;
        this.typeEnnemi = typreEnnemi;

    }

    public Ennemi( Ennemi original ) {
		chemin = original.chemin;
        noSegment = original.noSegment;
        distance = original.distance;
        vitesse = original.vitesse;
        pointVie = original.pointVie;
        pointVieMax = original.pointVieMax;
        this.valeurArgent = original.valeurArgent;
        image = original.image;
        this.typeEnnemi = original.typeEnnemi;
    }

    public static int comparer( Ennemi e1, Ennemi e2 ) {
        return Integer.compare( e1.distanceChateau(), e2.distanceChateau() );
    }
    
    /**
     * Applique l'effet de feu à l'ennemi, diminuant progressivement ses points de vie.
     * @param duree Durée de l'effet en tics.
     */
    public void appliquerEffetFeu(int duree) {
        enflamme = true;
        dureeFeuRestante = duree;
        compteurTicsFeu = 0; 
    }
    
    /**
     * Applique l'effet de gel à l'ennemi.
     * @param reductionPourcentage Pourcentage de réduction de vitesse.
     */
    public void appliquerEffetGele(double reductionPourcentage) {
        if (!gele) { // Appliquer l'effet seulement si l'ennemi n'est pas déjà gelé
            gele = true;
            vitesseorigine = vitesse; // Sauvegarder la vitesse actuelle
            vitesse = vitesseorigine * (1 - (reductionPourcentage / 100.0)); // Réduire la vitesse
        }
    }
    
    /**
     * Diminue la durée de l'effet de feu et inflige 1 point de dégâts .
     */
    public void diminuerEffetFeu() {
        if (enflamme) {
            compteurTicsFeu++;
            if (compteurTicsFeu >= TICS_PAR_DEGAT) {
                // Infliger un dégât tous les TICS_PAR_DEGAT
                reduireVie(1);
                compteurTicsFeu = 0;
                dureeFeuRestante--;
            }

            if (dureeFeuRestante <= 0) {
                enflamme = false; 
            }
        }
    }
    
    /**
     * Supprime l'effet de gel en restaurant la vitesse d'origine.
     */
    public void diminuerEffetGele() {
        if (gele) { // Restaurer uniquement si l'ennemi est gelé
            gele = false;
            vitesse = vitesseorigine; // Restaurer la vitesse d'origine
        }
    }
    
    /**
     * Vérifie si l'ennemi est actuellement gelé.
     * @return true si gelé, sinon false.
     */
    public boolean estGele() {
    	return gele;
    }
    
    /**
     * Vérifie si l'ennemi est actuellement enflammé.
     * @return true si enflammé, sinon false.
     */
    public boolean estEnflamme() {
        return enflamme;
    }

    public boolean reduireVie( int dommage ) {
        pointVie -= dommage;
        return pointVie <= 0;
    }

    public boolean aAtteintChateau() {
        return chemin.nombreSegment() <= noSegment;
    }

    public void avancer() {
        if( noSegment < chemin.nombreSegment() ) {
            distance += vitesse;
            int longueur = chemin.getSegment( noSegment ).longueur();
            if( longueur < distance ) {
                distance -= longueur;
                ++ noSegment;
            }
        }
    }

    public int distanceChateau() {
        return chemin.getLongueur() - ( (int)distance );
    }

    public PositionPixel getPositionPixel() {
        return chemin.calculerPosition( noSegment, distance );
    }

    
    public void afficher(Graphics2D g2, AffineTransform affineTransform) {
        if (noSegment < chemin.nombreSegment()) {
            AffineTransform pCurseur = (AffineTransform) affineTransform.clone();
            PositionPixel position = getPositionPixel();
            pCurseur.translate(position.x(), position.y());
            
            Tuile imageAfficher;
            if (enflamme && gele) {
                imageAfficher = getFeuEtGeleTuile(); 
            } else if (enflamme) {
                imageAfficher = getEnflammeTuile(); 
            } else if (gele) {
                imageAfficher = getGeleTuile(); 
            } else {
                imageAfficher = image;
            }
           
            
            g2.drawImage(imageAfficher, pCurseur, null);
            
            pCurseur.translate(0, -2);
            int rPV = (pointVie * Constantes.FACTEUR_PV) / Math.max(1, pointVieMax); 
            rPV = Math.max(0, Math.min(rPV, Constantes.PV_ENNEMI.length - 1)); 
            g2.drawImage(Constantes.PV_ENNEMI[rPV], pCurseur, null);
        }
    }
    
    public void setImage(Tuile image) {
    	 this.image = image;
    }
    public Tuile getImage() {
    	return image;
    }

    public int getValeurArgent() {
        return valeurArgent;
    }
    
    /**
     * Récupère l'image de l'ennemi enflammé selon son type.
     * @return Tuile correspondante.
     */
    private Tuile getEnflammeTuile() {
        switch (typeEnnemi) {
            case NiveauTD_1.TYPE_LAPIN:
                return Constantes.E_LAPIN_EN_FLAMME;
            case NiveauTD_1.TYPE_TORTUE:
                return Constantes.E_TORTUE_EN_FLAMME;
            case NiveauTD_1.TYPE_BOSS:
                return Constantes.E_BOSS_EN_FLAMME;
            default:
                return image;
        }
    }
    
    /**
     * Récupère l'image de l'ennemi gelé selon son type.
     * @return Tuile correspondante.
     */

    private Tuile getGeleTuile() {
    	switch(typeEnnemi) {
    	 case NiveauTD_1.TYPE_LAPIN:
    		return Constantes.E_LAPIN_GLACE;
    	 case NiveauTD_1.TYPE_TORTUE:
    	    return Constantes.E_TORTUE_GLACE;
       	 case NiveauTD_1.TYPE_BOSS:
    		return Constantes.E_BOSS_GLACE;
    	 default:
    		 return image;
    	}
    }
    
    /**
     * Récupère l'image de l'ennemi à la fois enflammé et gelé (violet).
     * @return Tuile correspondante.
     */
    private Tuile getFeuEtGeleTuile() {
    	switch(typeEnnemi) {
    	case NiveauTD_1.TYPE_LAPIN:
    		return Constantes.E_LAPIN_EN_FLAMME_ET_EN_GLACE;
    	case NiveauTD_1.TYPE_TORTUE:
    		return Constantes.E_TORTUE_EN_FLAMME_ET_EN_GLACE;
    	case NiveauTD_1.TYPE_BOSS:
    		return Constantes.E_BOSS_EN_FLAMME_ET_EN_GLACE;
    	default :
    		return image;
    	}
    }
}
