package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;


/**
 * La classe ElevationProfileManager gère l'affichage et l'interaction avec le profil
 * en long d'un itinéraire.
 *
 *  @author Juan Bautista Iaconucci (342153)
 * @author Loris Verga (345661)
 */
public final class ElevationProfileManager {

    private static final int MINIMAL_STEP_LENGTH_OF_VERTICAL_LINES = 50;
    private static final int MINIMAL_STEP_LENGTH_OF_HORIZONTAL_LINES = 25;
    private static final int SIZE_OF_LABELS = 10;
    private static final int RATIO_OF_KILOMETERS_AND_METERS = 1000;
    private static final String FONT_FAMILY_OF_LABELS = "Avenir";

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty highlightedPositionProperty;

    private final BorderPane borderPane;

    private final DoubleProperty mousePositionOnProfileProperty;

    private final Pane pane;
    private final Path path;
    private final Group group;
    private final Polygon polygon;
    private final Line line;

    private final VBox vBox;
    private final Text textVbox;

    private final Insets inset = new Insets(10, 10, 20, 40);

    private ObjectProperty<Rectangle2D> blueRectangleProperty;
    private ObjectProperty<Transform> screenToWorldProperty;
    private ObjectProperty<Transform> worldToScreenProperty;

    private static final int[] POSITION_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private static final int[] ELEVATION_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

    /**
     * Le constructeur de la classe prend en argument :
     * @param elevationProfile Une propriété accessible en lecture seule, contenant
     *                         le profil à afficher. La propriété contient null dans
     *                         le cas où aucun profil n'est à afficher.
     * @param highlightedPosition Une propriété accessible en lecture seule, contenant
     *                            la position le long du profil à mettre en évidence ;
     *                            elle est de type ReadOnlyDoubleProperty et contient
     *                            NaN dans le cas où aucune position n'est à mettre en
     *                            évidence.
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile,
                                   ReadOnlyDoubleProperty highlightedPosition){
        this.elevationProfileProperty = elevationProfile;
        this.highlightedPositionProperty = highlightedPosition;


        this.mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);

        //Initialisation des nœuds :
        this.borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");

        this.pane = new Pane();
        borderPane.setCenter(pane);

        this.path = new Path();
        path.setId("grid");
        pane.getChildren().add(path);
        this.group = new Group();
        pane.getChildren().add(group);
        this.polygon = new Polygon();
        this.polygon.setId("profile");
        pane.getChildren().add(polygon);

        this.line = new Line();
        pane.getChildren().add(line);

        pane.widthProperty().addListener((p,o,n) -> redraw());
        pane.heightProperty().addListener((p,o,n) -> redraw());

        this.vBox = new VBox();
        vBox.setId("profile_data");
        borderPane.setBottom(vBox);
        textVbox = new Text();
        vBox.getChildren().add(textVbox);


        blueRectangleProperty = new SimpleObjectProperty<>();

        blueRectangleProperty.bind(Bindings.createObjectBinding(
                () -> createBlueRectangle(),
                pane.widthProperty(),
                pane.heightProperty()
        ));

        screenToWorldProperty = new SimpleObjectProperty<>();

        screenToWorldProperty.bind(Bindings.createObjectBinding(
                () -> createScreenToWorldTransformation(),
                pane.widthProperty(),
                pane.heightProperty()
        ));

        worldToScreenProperty = new SimpleObjectProperty<>();

        worldToScreenProperty.bind(Bindings.createObjectBinding(
                () -> createScreenToWorldTransformation().createInverse(),
                pane.widthProperty(),
                pane.heightProperty()
        ));

        line.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> highlightedPositionProperty.get(),
                highlightedPositionProperty));
        line.startYProperty().bind(Bindings.createDoubleBinding(
                () -> blueRectangleProperty.get().getMinY(),
                blueRectangleProperty));
        line.endYProperty().bind(Bindings.createDoubleBinding(
                () -> blueRectangleProperty.get().getMaxY(),
                blueRectangleProperty));
        line.visibleProperty().bind(highlightedPositionProperty.greaterThanOrEqualTo(0));

        borderPane.setOnMouseMoved(e-> mousePositionUpdate(e));


    }

    /**
     * La méthode mousePositionUpdate privée permet positionner la ligne de mise en évidence au bon endroit sur le graph.
     * @param e l'événement crée par la souris quand elle est déplacé.
     */
    private void mousePositionUpdate(MouseEvent e){
        double posX = e.getX();
        double posY = e.getY();
        double maxX = blueRectangleProperty.get().getMaxX();
        double minX = blueRectangleProperty.get().getMinX();
        double maxY = blueRectangleProperty.get().getMaxY();
        double minY = blueRectangleProperty.get().getMinY();
        if (posX > maxX || posX < minX || posY > maxY || posY < minY){
            mousePositionOnProfileProperty.set(Double.NaN);
        }
        else{mousePositionOnProfileProperty.set(posX);
    }}

    /**
     * La méthode redraw privée permet de dessiner la grille et le profil du graph d'élévation.
     */
    private void redraw(){
        createGrid();
        createProfilGraph();
    }

    /**
     * La méthode pane retourne le panneau contenant le dessin du profil.
     * @return un Pane JavaFX.
     */
    public Pane pane(){
        return this.borderPane;
    }


    /**
     * La méthode mousePositionOnProfileProperty retourne une propriété en lecture seule
     * contenant la position du pointeur de la souris le long du profil(en mètres, arrondie
     * à l'entier le plus proche), ou NaN si le pointeur de la souris ne se trouve pas
     * au-dessus du profil.
     * @return
     */
    public ReadOnlyDoubleProperty mousePositionProfileProperty(){
        return this.mousePositionOnProfileProperty;
    }

    private Rectangle2D createBlueRectangle(){
        double width = pane.getWidth() - (inset.getLeft() + inset.getRight());
        double height = pane.getHeight() - (inset.getTop() + inset.getBottom());

        if(width < 0 || height < 0){
            width = 0;
            height = 0;
        }
        return new Rectangle2D(inset.getLeft(), inset.getTop(), width, height);
    }

    /**
     * La méthode createScreenToWorldTransformation privée, permet de crée la transformation entre le repère du monde du graph
     * au repère centrer au coin en haut à gauche du panneau d'affichage en pixel.
     * @return la transformation qui permet le changement entre ces deux repères.
     */
    private Transform createScreenToWorldTransformation() {

        Affine screenToWorld = new Affine();

        double minX = 0;
        double minY = elevationProfileProperty.get().minElevation();
        double width = elevationProfileProperty.get().length();
        double height = elevationProfileProperty.get().maxElevation() - elevationProfileProperty.get().minElevation();

        double tx = - inset.getLeft();
        double ty = - inset.getTop();
        screenToWorld.prependTranslation( tx, ty);

        double sx = width / blueRectangleProperty.get().getWidth();
        double sy = - height / blueRectangleProperty.get().getHeight();
        screenToWorld.prependScale( sx, sy);

        tx = minX;
        ty = minY + height;
        screenToWorld.prependTranslation( tx, ty);

        return screenToWorld;
    }

    /**
     * La méthode createGrid privée, permet de créer la grille et les étiquettes sur le graph du profil.
     */
    private void createGrid(){
        path.getElements().clear();
        group.getChildren().clear();

        double pixelsBetweenVerticalLines;
        int verticalStep = 0;

        double pixelsBetweenHorizontalLines;
        int horizontalStep = 0;

        //On calcule l'espacement en pixel entre les lignes verticales pour chaque espacement dans la liste,
        //pour trouver le plus petit qui plus grand que 50 pixels s'il n'y en a pas l'espacement reste à 0.
        for(int i = 0; i < POSITION_STEPS.length && verticalStep == 0; i++) {
            Point2D point = worldToScreenProperty.get()
                    .deltaTransform(POSITION_STEPS[i], 0);
            pixelsBetweenVerticalLines = point.getX();
            if(pixelsBetweenVerticalLines > MINIMAL_STEP_LENGTH_OF_VERTICAL_LINES){
                verticalStep = POSITION_STEPS[i];
            }
        }

        //On calcule l'espacement en pixel entre les lignes horizontales pour chaque espacement dans la liste,
        //pour trouver le plus petit qui plus grand que 25 pixels, s'il n'y en a pas l'espacement reste à 0.
        for(int i = 0; i < ELEVATION_STEPS.length && horizontalStep == 0; i++) {
            Point2D point = worldToScreenProperty.get()
                    .deltaTransform(0,ELEVATION_STEPS[i]);
            pixelsBetweenHorizontalLines = Math.abs(point.getY());
            if(pixelsBetweenHorizontalLines > MINIMAL_STEP_LENGTH_OF_HORIZONTAL_LINES){
                horizontalStep = ELEVATION_STEPS[i];
            }
        }

        //Vérification qu'un espacement vertical et horizontal a été trouver.
        if(verticalStep != 0 && horizontalStep != 0) {
            double minX = 0;
            double minY = elevationProfileProperty.get().minElevation();
            double maxX = elevationProfileProperty.get().length();
            double maxY = elevationProfileProperty.get().maxElevation();

            double firstHorizontalLineY = horizontalStep * Math.ceil(minY / horizontalStep);
            double firstVerticalLineX = verticalStep * Math.ceil(minX / verticalStep);

            int numberOfVerticalLines = (int) Math.floor((maxX - firstVerticalLineX)/ verticalStep);
            int numberOfHorizontalLines = (int) Math.floor((maxY - firstHorizontalLineY)/ horizontalStep);

            //Pour chaque ligne verticale, on la place au bon endroit sur le panneau d'affichage
            // et on place l'étiquette associer.
            for (int i = 0; i <= numberOfVerticalLines; i++) {
                int valueOfPosition = i * verticalStep + (int) firstVerticalLineX;

                Point2D point2DMoveTo = worldToScreenProperty.get()
                        .transform(valueOfPosition, minY);
                PathElement moveTo = new MoveTo(point2DMoveTo.getX(), point2DMoveTo.getY());

                Point2D point2DLineTo = worldToScreenProperty.get()
                        .transform(valueOfPosition, maxY);
                PathElement lineTo = new LineTo(point2DLineTo.getX(), point2DLineTo.getY());

                Text positionLabel = new Text();

                positionLabel.getStyleClass().addAll("grid_label", "horizontal");
                positionLabel.setFont(Font.font(FONT_FAMILY_OF_LABELS, SIZE_OF_LABELS));
                positionLabel.setTextOrigin(VPos.TOP);

                positionLabel.setText(String.valueOf(valueOfPosition / RATIO_OF_KILOMETERS_AND_METERS));
                positionLabel.setX(point2DMoveTo.getX() - positionLabel.prefWidth(0) / 2);
                positionLabel.setY(point2DMoveTo.getY());

                group.getChildren().add(positionLabel);

                path.getElements().addAll(moveTo, lineTo);
            }

            //Pour chaque ligne horizontale, on la place au bon endroit sur le panneau d'affichage
            // et on place l'étiquette associer.
            for (int i = 0; i <= numberOfHorizontalLines; i++) {

                int valueOfElevation = i * horizontalStep + (int) firstHorizontalLineY;

                Point2D point2DMoveTo = worldToScreenProperty.get()
                        .transform(minX, valueOfElevation);
                PathElement moveTo = new MoveTo(point2DMoveTo.getX(), point2DMoveTo.getY());

                Point2D point2DLineTo = worldToScreenProperty.get()
                        .transform(maxX, valueOfElevation);
                PathElement lineTo = new LineTo(point2DLineTo.getX(), point2DLineTo.getY());

                Text elevationText = new Text();

                elevationText.getStyleClass().addAll("grid_label", "vertical");
                elevationText.setFont(Font.font(FONT_FAMILY_OF_LABELS, SIZE_OF_LABELS));
                elevationText.setTextOrigin(VPos.CENTER);

                elevationText.setText(String.valueOf(valueOfElevation));
                elevationText.setX(point2DMoveTo.getX() - ( elevationText.prefWidth(0) + 2));
                elevationText.setY(point2DMoveTo.getY());

                group.getChildren().add(elevationText);

                path.getElements().addAll(moveTo, lineTo);
            }
        }

        //Enfin on affiche les statistiques correspondant à l'itinéraire.
        ElevationProfile elevationProfile = elevationProfileProperty.get();
        textVbox.setText(String.format("Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m", elevationProfile.length(), elevationProfile.totalAscent(),
                elevationProfile.totalDescent(), elevationProfile.minElevation(), elevationProfile.maxElevation()));
    }


    /**
     * La méthode createProfilGraph privée, permet de créer le graph du profil de l'itinéraire.
     */
    private void createProfilGraph(){
        polygon.getPoints().clear();

        double minX = blueRectangleProperty.get().getMinX();
        double maxX = blueRectangleProperty.get().getMaxX();
        double minY = blueRectangleProperty.get().getMaxY();

        polygon.getPoints().addAll(minX,minY);
        for(double screenX = minX; screenX <= maxX; screenX++) {

            double positionX = screenToWorldProperty.get().transform(screenX, 0).getX();
            double elevationY = elevationProfileProperty.get().elevationAt(positionX);
            double screenY = worldToScreenProperty.get().transform(0, elevationY).getY();

            polygon.getPoints().addAll(screenX, screenY);
        }
        polygon.getPoints().addAll(maxX,minY);
    }
}