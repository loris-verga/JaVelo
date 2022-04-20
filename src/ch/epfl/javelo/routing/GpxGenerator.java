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
import java.util.function.DoubleUnaryOperator;


/**
 * La classe GpxGenerator représente un générateur d'itinéraire au format GPX.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public class GpxGenerator {
    private GpxGenerator() {}

    //todo not sure if profil here is an elevationProfil or a DoubbleUnaryOperator..
    public static Document createGpx(Route route, ElevationProfile profile){

        Document doc = newDocument();

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

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");


        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        PointCh lastPoint = route.points().get(0);
        double currentDistance = 0.0;
        for(PointCh point: route.points()){
            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            rtept.setAttribute("lat", String.valueOf(point.lat()));
            rtept.setAttribute("lon", String.valueOf(point.lon()));

            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            currentDistance += lastPoint.distanceTo(point);
            ele.setTextContent(String.valueOf(profile.elevationAt(currentDistance)));
            //todo I think you have to put this as child
            lastPoint = point;

        }
        return doc;
    }

    public static void writeGpx(String fileName, Route route, ElevationProfile profil){

        Document doc = createGpx(route, profil);

        try(FileOutputStream outputStream = new FileOutputStream(fileName);

            Writer w = new OutputStreamWriter(outputStream);){
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));


        } catch (FileNotFoundException e) {
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        } catch (TransformerConfigurationException e) {
            throw new Error(e);
        } catch (TransformerException e) {
            throw new Error(e);
        }
    }

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
