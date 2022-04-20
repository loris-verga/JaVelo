package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * L'enregistrement Waypoint représente un point de passage.
 *
 * @author Juan Bautista Iaconucci (342153)
 */
public record Waypoint(PointCh position, int nodeIdClosestTo) {
}
