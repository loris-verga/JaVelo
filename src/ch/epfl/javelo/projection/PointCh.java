package ch.epfl.javelo.projection;


import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * La classe record PointCh représente un point dans le système de coordonnées suisse.
 *
 * @author Loris Verga (345661)
 */
public record PointCh(double e, double n) {

    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }


    /**
     * La méthode squaredDistanceTo retourne le carré de la distance en mètre séparant le récepteur (this)
     * de l'argument that.
     *
     * @param that autre point
     * @return le carré de la distance en mètre (double)
     */
    public double squaredDistanceTo(PointCh that) {
        double uX = this.e() - that.e();
        double uY = this.n() - that.n();
        return Math2.squaredNorm(uX, uY);
    }

    /**
     * La méthode distanceTo retourne la distance en mètres séparant le récepteur (this) de l'argument that.
     *
     * @param that un autre point
     * @return la distance entre les deux points en mètre (double)
     */
    public double distanceTo(PointCh that) {
        double uX = this.e() - that.e();
        double uY = this.n() - that.n();

        return Math2.norm(uX, uY);
    }

    /**
     * La méthode lon retourne la longitude du point dans le système WGS84 en radiant.
     *
     * @return la longitude dans le système WGS84 en radiant (double).
     */
    public double lon() {
        return Ch1903.lon(this.e(), this.n());
    }

    /**
     * La méthode lat retourne la latitude du point dans le système WGS84 en radiant.
     *
     * @return la latitude dans le système WGS84 en radiant (double).
     */
    public double lat() {
        return Ch1903.lat(this.e(), this.n());
    }

}
