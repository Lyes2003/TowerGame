import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

public abstract class Tour {
    protected PositionTuile position;
    protected Caracteristique [] caracteristiques;
    

    public Tour( PositionTuile position ) {
        this.position = position;
    }

    public abstract int animer( List< Ennemi > ennemis );

    public void inscrireEvenements( GestionSouris gestionSouris ) {
        for( int i = 0; i < caracteristiques.length; ++ i ) {
            caracteristiques[ i ].inscrireEvenement( gestionSouris );
        }
    }
    
    public void desinscrireEvenements( GestionSouris gestionSouris ) {
        for( int i = 0; i < caracteristiques.length; ++ i ) {
            caracteristiques[ i ].desinscrireEvenement( gestionSouris );
        }
    }

    public void afficherControl( Graphics2D g2, Jeu jeu ) {
        AffineTransform pCug = (AffineTransform) jeu.origine_pixel.clone();
        pCug.translate( getPositionPixel().x(), getPositionPixel().y() );
        g2.drawImage( Constantes.SOUSLIGNE_TOUR, pCug, null );

        for( int i = 0; i < caracteristiques.length; ++ i ) {
            caracteristiques[ i ].afficherControl( g2, jeu );
        }
    }
    
 // Méthodes abstraites pour fournir les tuiles
    protected abstract Tuile getTuileBas();
    protected abstract Tuile getTuileHaut();

    public void afficherTour( Graphics2D g2, AffineTransform origine_pixel ) {
        AffineTransform pCurseur = (AffineTransform) origine_pixel.clone();
        PositionPixel pos = getPositionTuile().positionPixel();
        
      
        pCurseur.translate( pos.x(), pos.y() );


        g2.drawImage( getTuileBas(), pCurseur, null );
        pCurseur.translate( 0, - Constantes.TAILLE_TUILE );
        g2.drawImage( getTuileHaut(), pCurseur, null );
        
        
    }

    public PositionTuile getPositionTuile() {
        return position;
    }

    public PositionPixel getPositionPixel() {
        return position.positionPixel();
    }
}
