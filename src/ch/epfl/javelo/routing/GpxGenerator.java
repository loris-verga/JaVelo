package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * La classe GpxGenerator représente un générateur d'itinéraire au format GPX.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public class GpxGenerator {
    private GpxGenerator() {}

    /**
     * La méthode createGpx permet de créer un document de type GPX.
     * @param route l'itinéraire que l'on veut convertir en GPX
     * @param profile le profile associer à l'itinéraire
     * @return le document GPX.
     */
    public static Document createGpx(Route route, ElevationProfile profile){

        //On crée la base du document GPX.
        Document doc = newDocument();

        //On crée l'élément root, un enfant de doc, et on initialise ces attributs.
        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        //On crée l'élément metadata, un enfant de root.
        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        //On crée l'élément name, un enfant de metadata,
        //qui a comme text brute "Route Javelo".
        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        //On crée l'élément rte (route), enfant de root.
        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        PointCh lastPoint = route.points().get(0);
        double currentDistance = 0.0;

        //Pour chaque point sur l'itinéraire,
        //on crée l'élément rtept (routePoint), enfant de rte,
        //qui contient les attributs lat (la latitude) et long (la longitude) du point,
        //et a comme enfant l'élément ele qui correspond à l'altitude à ce point.
        for(PointCh point: route.points()){
            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            rtept.setAttribute("lat", String.valueOf(point.lat()));
            rtept.setAttribute("lon", String.valueOf(point.lon()));

            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            currentDistance += lastPoint.distanceTo(point);
            ele.setTextContent(String.valueOf(profile.elevationAt(currentDistance)));
            lastPoint = point;

        }
        return doc;
    }

    /**
     * La méthode writeGpx permet d'écrire un document gpx sur un fichier donné.
     * @param fileName le fichier donné
     * @param route l'itinéraire que l'on veut convertir en GPX
     * @param profile le profile associer à l'itinéraire
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile profile){

        Document doc = createGpx(route, profile);

        try(FileOutputStream outputStream = new FileOutputStream(fileName);

            Writer w = new OutputStreamWriter(outputStream)){
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));


        } catch (IOException | TransformerException e) {
            throw new Error(e);
        }
    }

    /**
     * La méthode private newDocument permet de faciliter la création de Document.
     * @return un document.
     */
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
    }
}
