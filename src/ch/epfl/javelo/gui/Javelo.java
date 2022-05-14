package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import java.nio.file.Path;
import java.util.function.Consumer;

import static javafx.geometry.Orientation.VERTICAL;

/**
 * La classe Javelo est la classe principale de l'application.
 *
 * @author Loris Verga (345661)
 */
public final class Javelo extends Application {

    private final String DIRECTORY_NAME = "javelo-data";
    private final String CACHE_DIRECTORY_NAME = "osm-cache";
    private final String TILE_SERVER_NAME = "tile.openstreetmap.org";
    private final int MIN_WIDTH = 800;
    private final int MIN_HEIGHT = 600;
    private final String WINDOW_NAME = "Javelo";
    private final String GPX_FILE_NAME = "javelo.gpx";





    public static void main(String[] args) {
        launch(args);
    }

    /**
     * La méthode start de Javelo se charge de construire l'interface graphique finale.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of(DIRECTORY_NAME));
        Path cacheBasePath = Path.of(CACHE_DIRECTORY_NAME);

        TileManager tileManager = new TileManager(cacheBasePath, TILE_SERVER_NAME);

        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));

        ErrorManager errorManager =  new ErrorManager();



        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(
                graph, tileManager, routeBean,errorManager );



        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);

        primaryStage.setTitle(WINDOW_NAME);

        Pane mapPane = annotatedMapManager.pane();
        SplitPane splitPane = new SplitPane(mapPane);
        splitPane.orientationProperty().set(VERTICAL);

        SplitPane.setResizableWithParent(splitPane, false);

        ReadOnlyObjectProperty elevationProfileP = routeBean.getElevationProfileProperty();


        elevationProfileP.addListener(e->{

            if (elevationProfileP.get() == null){
                if (splitPane.getItems().size() == 2){
                    splitPane.getItems().remove(1);
                }
            }
            else {
                ElevationProfileManager elevationProfileManager = new ElevationProfileManager(
                        elevationProfileP
                        , routeBean.getHighlightedPositionProperty()
                );

                Pane elevationProfilePane = elevationProfileManager.pane();
                if (splitPane.getItems().size() == 1){ //TODO SIMPLIFY
                    splitPane.getItems().add(elevationProfilePane);}
                else {splitPane.getItems().set(1, elevationProfilePane);
            }
        }});

        StackPane paneWithErrorManager = new StackPane(splitPane);
        paneWithErrorManager.getChildren().add(errorManager.pane());


        ReadOnlyObjectProperty routeP = routeBean.routeProperty();

        MenuItem menuItem = new MenuItem("Exporter GPX");
        menuItem.disableProperty().bind(Bindings.createBooleanBinding(
                () -> (routeP.get() == null), routeP));


        menuItem.setOnAction(e-> {
            Route route = (Route)routeP.get();
            ElevationProfile elevationProfile = (ElevationProfile) elevationProfileP.get();
            Document gpxDocument = GpxGenerator.createGpx(route,
                    elevationProfile);
            GpxGenerator.writeGpx(GPX_FILE_NAME, route, elevationProfile);
        });


        Menu menu = new Menu("Fichier");
        menu.getItems().add(menuItem);
        MenuBar menuBar = new MenuBar(menu);
        menuBar.setUseSystemMenuBar(true);

        BorderPane javeloPane = new BorderPane();
        javeloPane.setCenter(paneWithErrorManager);
        javeloPane.setTop(menuBar);




        primaryStage.setScene(new Scene(javeloPane));
        primaryStage.show();



    }

}
