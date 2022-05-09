package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * L'enregistrement Waypoint représente un point de passage.
 * Il possède deux attributs :
 *  - la position du point de passage dans le système de coordonnées suisse,
 *  - l'identité du nœud JaVelo le plus proche de ce point de passage.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public record Waypoint(PointCh position, int nodeIdClosestTo) {
}
