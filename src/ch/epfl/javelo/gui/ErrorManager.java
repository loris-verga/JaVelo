package ch.epfl.javelo.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * La classe ErrorManager gère l'affichage de messages d'erreur.
 *
 * @author Juan B Iaconucci (342153)
 */
public final class ErrorManager {
    private static final Duration DURATION_OF_FADE_TRANSITION = Duration.seconds(0.2);
    private static final Duration DURATION_OF_PAUSE_TRANSITION = Duration.seconds(2.0);
    private static final double VALUE_OF_TRANSPARENT_OPACITY = 0;
    private static final double VALUE_OF_OPAQUE_OPACITY = 0.8;

    private VBox pane;
    private SequentialTransition animation;

    /**
     * Le constructeur de ErrorManager initialise juste son panneau d'affichage
     * et l'animation utiliser pour afficher les erreurs.
     */
    ErrorManager(){
        pane = new VBox();
        pane.getStylesheets().add("error.css");
        pane.setMouseTransparent(true);

        animation = createAnimation();
    }

    /**
     * La méthode pane retourne le panneau où sont affiché les erreurs.
     * @return le panneau où sont affiché les erreurs.
     */
    public Pane pane(){return pane;}

    /**
     * La méthode displayError permet d'afficher à l'écran le message d'erreur donné en argument.
     * @param errorMessage le message qu'on l'on veut afficher a l'écran.
     */
    public void displayError(String errorMessage){

        if(animation.getStatus() == Animation.Status.RUNNING){
            animation.stop();
        }

        pane.getChildren().clear();
        pane.getChildren().add(new Text(errorMessage));

        java.awt.Toolkit.getDefaultToolkit().beep();
        animation.play();
    }

    /**
     * La méthode createAnimation privée, permet de créer l'animation que l'on utilise pour afficher les messages d'erreurs.
     * @return l'animation du panneau.
     */
    private SequentialTransition createAnimation(){

        FadeTransition ft1 = new FadeTransition(DURATION_OF_FADE_TRANSITION, pane);
        ft1.setFromValue(VALUE_OF_TRANSPARENT_OPACITY);
        ft1.setToValue(VALUE_OF_OPAQUE_OPACITY);

        PauseTransition pt = new PauseTransition(DURATION_OF_PAUSE_TRANSITION);

        FadeTransition ft2 = new FadeTransition(DURATION_OF_FADE_TRANSITION, pane);
        ft2.setFromValue(VALUE_OF_OPAQUE_OPACITY);
        ft2.setToValue(VALUE_OF_TRANSPARENT_OPACITY);

        return new SequentialTransition(ft1, pt, ft2);
    }
}
