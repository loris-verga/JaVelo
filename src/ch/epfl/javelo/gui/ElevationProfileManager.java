package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

/**
 * La classe ElevationProfileManager gère l'affichage et l'interaction avec le profil
 * en long d'un itinéraire.
 */
public final class ElevationProfileManager {

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty highlightedPositionProperty;

    private final BorderPane borderPane;

    private final DoubleProperty mousePositionOnProfileProperty;

    //Le panneau sert à organiser ses enfants.
    private final Pane pane;
    //Le chemin représente la grille.
    private final Path path;
    //le groupe contient les étiquettes textuelles de la grille.
    private final Group group;
    //Le polygone représente le graphe du profil.
    private final Polygon polygon;
    //La ligne représente la position en évidence.
    private final Line line;


    private final VBox vBox;
    private final Text textVbox;

    private final Insets inset = new Insets(10, 10, 20, 40); //TODO utiliser pour part 2

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

        this.vBox = new VBox();
        vBox.setId("profile_data");
        borderPane.setBottom(vBox);
        textVbox = new Text();
        vBox.getChildren().add(textVbox);


        screenToWorldProperty = new SimpleObjectProperty<>();
        worldToScreenProperty = new SimpleObjectProperty<>();

        blueRectangleProperty = new SimpleObjectProperty<>();

        pane.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redraw);
        });

        line.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> highlightedPositionProperty.get(), highlightedPositionProperty));

        line.startYProperty().bind(Bindings.createDoubleBinding(
                () -> blueRectangleProperty.get().getMinY()));
        line.endYProperty().bind(Bindings.createDoubleBinding(
                () -> blueRectangleProperty.get().getMaxY()));
        line.visibleProperty().bind(highlightedPositionProperty.greaterThanOrEqualTo(0));



    }

    private void redraw(){
        if (pane.getHeight() != 0 && pane.getWidth() != 0) {
            createBlueRectangleProperty();
            try {
                createTransformationsProperty();
            } catch (NonInvertibleTransformException e) {
                e.printStackTrace();
            }
            createGrid();
        }
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
        return this.mousePositionOnProfileProperty; //TODO check que la la valeur soit arrondie à l'entier le plus proche.
    }

    private void createTransformationsProperty() throws NonInvertibleTransformException {

        Affine screenToWorld = new Affine();

        double tx = - inset.getLeft();
        double ty = - inset.getTop();
        screenToWorld.prependTranslation( tx, ty);

        double p1 = borderPane.getWidth() ;
        double p2 = borderPane.getHeight();

        tx = blueRectangleProperty.get().getWidth()
                / (borderPane.getWidth() - (inset.getLeft() + inset.getRight()));
        ty = - blueRectangleProperty.get().getHeight()
                /(borderPane.getHeight() - (inset.getTop() + inset.getBottom()));
        screenToWorld.prependScale( tx, ty);

        tx = blueRectangleProperty.get().getMinX();
        ty = blueRectangleProperty.get().getMaxY();
        screenToWorld.prependTranslation( tx, ty);

        screenToWorldProperty.set(screenToWorld);
        worldToScreenProperty.set(screenToWorld.createInverse());
    }

    private void createBlueRectangleProperty(){

        double minX = 0;
        double minY = elevationProfileProperty.get().minElevation();
        double width = elevationProfileProperty.get().length();
        double height = elevationProfileProperty.get().maxElevation() - elevationProfileProperty.get().minElevation();
        blueRectangleProperty.set(new Rectangle2D(minX, minY,width, height));
    }

    private void createGrid(){
        path.getElements().clear();
        group.getChildren().clear();

        double pixelsBetweenVerticalLines;
        int verticalStep = 0;

        double pixelsBetweenHorizontalLines;
        int horizontalStep = 0;

        for(int i = 0; i < POSITION_STEPS.length; i++) {
            Point2D point = worldToScreenProperty.get()
                    .deltaTransform(
                            new Point2D(POSITION_STEPS[i], 0));
            pixelsBetweenVerticalLines = point.getX();
            if(pixelsBetweenVerticalLines > 50){
                verticalStep = POSITION_STEPS[i];
                break;
            }
        }

        for(int i = 0; i < ELEVATION_STEPS.length; i++) {
            Point2D point = worldToScreenProperty.get()
                                .deltaTransform(
                                    new Point2D(0,ELEVATION_STEPS[i]));
            pixelsBetweenHorizontalLines = Math.abs(point.getY());
            if(pixelsBetweenHorizontalLines > 25){
                horizontalStep = ELEVATION_STEPS[i];
                break;
            }
        }
        if(verticalStep!=0 && horizontalStep!=0) {
            int numberOfVerticalLines = (int) blueRectangleProperty.get().getWidth() / verticalStep;
            int numberOfHorizontalLines = (int) blueRectangleProperty.get().getHeight() / horizontalStep;


            for (int i = 0; i <= numberOfVerticalLines; i++) {
                int valueOfPosition = i * verticalStep;

                Point2D point2DMoveTo = worldToScreenProperty.get()
                        .transform(valueOfPosition, blueRectangleProperty.get().getMinY());
                PathElement moveTo = new MoveTo(point2DMoveTo.getX(), point2DMoveTo.getY());

                Point2D point2DLineTo = worldToScreenProperty.get()
                        .transform(valueOfPosition, blueRectangleProperty.get().getMaxY());
                PathElement lineTo = new LineTo(point2DLineTo.getX(), point2DLineTo.getY());

                Text positionText = new Text();
                positionText.setTextOrigin(VPos.TOP);
                positionText.setText(String.valueOf(valueOfPosition / 1000));
                positionText.setX(point2DMoveTo.getX() - positionText.prefWidth(0) / 2);
                positionText.setY(point2DMoveTo.getY());

                positionText.getStyleClass().addAll("grid_label", "vertical");
                positionText.setFont(Font.font("Avenir", 10));
                group.getChildren().add(positionText);

                path.getElements().addAll(moveTo, lineTo);
            }

            double firstHorizontalLineY = blueRectangleProperty.get().getMinY() - blueRectangleProperty.get().getMinY() % horizontalStep + horizontalStep;
            for (int i = 0; i <= numberOfHorizontalLines; i++) {
                int valueOfElevation = i * horizontalStep + (int) firstHorizontalLineY;

                Point2D point2DMoveTo = worldToScreenProperty.get()
                        .transform(blueRectangleProperty.get().getMinX(), valueOfElevation);
                PathElement moveTo = new MoveTo(point2DMoveTo.getX(), point2DMoveTo.getY());

                Point2D point2DLineTo = worldToScreenProperty.get()
                        .transform(blueRectangleProperty.get().getMaxX(), valueOfElevation);
                PathElement lineTo = new LineTo(point2DLineTo.getX(), point2DLineTo.getY());

                Text positionText = new Text();
                positionText.setTextOrigin(VPos.CENTER);
                positionText.setText(String.valueOf(valueOfElevation));
                positionText.setX(point2DMoveTo.getX() - (positionText.prefWidth(0) + 2));
                positionText.setY(point2DMoveTo.getY());

                positionText.getStyleClass().addAll("grid_label", "horizontal");
                positionText.setFont(Font.font("Avenir", 10));
                group.getChildren().add(positionText);

                path.getElements().addAll(moveTo, lineTo);
            }
        }
    }

    public double mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty.get();
    }


}
