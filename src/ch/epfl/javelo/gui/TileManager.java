package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * La classe TileManager, publique et finale, représente un gestionnaire de tuiles OSM. Son rôle est d'obtenir
 * les tuiles depuis un serveur de tuiles et de les stocker dans un cache mémoire et dans un cache disque.
 *
 * @author Loris Verga (345661)
 */
public final class TileManager {


    private final Path path;
    private final String tileServerName;
    private final LinkedHashMap<TileId, Image> cacheMemory;


    public TileManager(Path path, String tileServerName){
        this.path = path;
        this.tileServerName = tileServerName;
        this.cacheMemory = new LinkedHashMap<>(100, 0.75F, true);
    }




    /**
     * La méthode imageForTileAt prend en argument l'identité d'une tuile et retourne son image.
     * L'image est cherchée tout d'abord dans le cache mémoire et si elle s'y trouve est retournée.
     * Si elle ne s'y trouve pas elle est cherchée dans le cache disque. Si elle s'y trouve elle
     * est retournée et placée dans le cache mémoire. Si elle ne s'y trouve pas elle est cherchée
     * sur le serveur, retournée et placée dans les mémoires.
     * @param tileId l'identité de la tuile.
     * @return l'image correspondante à l'identité de la tuile.
     */
    public Image imageForTileAt(TileId tileId) {

        //Retourne l'image si elle se trouve dans la mémoire cache.
        if (cacheMemory.containsKey(tileId)) {
            return cacheMemory.get(tileId);
        }

        //Récupération des attributs du chemin de l'image.
        String zoomLevel = Integer.toString(tileId.zoomLevel());
        String indexX = Integer.toString(tileId.indexX());
        String indexY = tileId.indexY() + ".png";

        //Création du path de l'image pour la trouver/créer dans les fichiers.
        Path pathOfImage = path.resolve(zoomLevel).
                resolve(indexX).resolve(indexY);


        //Si l'image existe dans la mémoire disque, on retourne l'image et on met à jour la mémoire cache.
        if (Files.exists(pathOfImage)) {
            try (InputStream imageStream = Files.newInputStream(pathOfImage)) {
                Image image = new Image(imageStream);
                addImageCacheMemory(image, tileId);
                return image;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            //Récupération de l'image sur le serveur :

            //Création de l'URL
            StringBuilder urlBuilder = new StringBuilder(tileServerName +"/");
            urlBuilder.append(zoomLevel).append("/").
                    append(indexX).append("/").
                    append(indexY);

            //Récupération de l'image sur le serveur.
            try {
                URL urlImage = new URL(urlBuilder.toString());
                URLConnection connection = urlImage.openConnection();
                connection.setRequestProperty("User-Agent", "Javelo");
                Files.createDirectories(pathOfImage.getParent());
                Files.createFile(pathOfImage);

                //Transfer de l'image sur la mémoire disque depuis le serveur.
                try (InputStream imageStream = connection.getInputStream();
                     FileOutputStream outPutStream = new FileOutputStream(String.valueOf(pathOfImage))) {
                    imageStream.transferTo(outPutStream);

                    //Récupération de l'image dans le disque pour la retourner.
                    try (InputStream newImageStream = Files.newInputStream(pathOfImage)) {
                        Image image = new Image(newImageStream);
                        addImageCacheMemory(image, tileId);
                        return image;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private void addImageCacheMemory(Image image, TileId tileId){
        if (cacheMemory.size()<100){
            cacheMemory.put(tileId, image);
        }
        else{
            Iterator<Map.Entry<TileId, Image>> iterator = cacheMemory.entrySet().iterator();
            TileId tileIdOfOldestAccessedElement = iterator.next().getKey();
            cacheMemory.remove(tileIdOfOldestAccessedElement);
            cacheMemory.put(tileId, image);
        }
    }


    /**
     * L'enregistrement TileId, imbriqué dans la classe TileManager, représente l'identité d'une tuile OSM.
     * Il possède trois attributs :
     * @param zoomLevel le niveau de zoom de la tuile,
     * @param indexX l'index X de la tuile,
     * @param indexY et l'index Y de la tuile.
     *
     * @author Loris Verga (345661)
     */
    public record TileId(int zoomLevel, int indexX, int indexY) {

        /**
         * Le constructeur compact de l'enregistrement TileId lève une exception si les arguments ne sont pas valides.
         *
         * @param zoomLevel le niveau de zoom de la tuile,
         * @param indexX    l'index X de la tuile,
         * @param indexY    et l'index Y de la tuile.
         */
        public TileId {
            if (!isValid(zoomLevel, indexX, indexY)) {
                throw new IllegalArgumentException();
            }
        }

        /**
         * La méthode isValid retourne vrai si et seulement si les trois arguments sont valides.
         *
         * @param zoomLevel le niveau de zoom de la tuile.
         * @param indexX    l'index X de la tuile.
         * @param indexY    l'index Y de la tuile.
         * @return vrai si les arguments sont valides.
         */
        public boolean isValid(int zoomLevel, int indexX, int indexY) {
            return zoomLevel >= 0 &&
                    indexX >= 0 &&
                    indexY >= 0;
        }
    }
}
