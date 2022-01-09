package imps;
import api.GeoLocation;

import java.util.Objects;

public class GeoLocationImp implements GeoLocation
{
    private double _x, _y, _z;
    private boolean _isValid;

    /**
     * copy c'tor
     * @param other
     */
    public GeoLocationImp(GeoLocation other)
    {
        this(other.x(), other.y(), other.z());
    }

    /**
     * c'tor will make 0,0,0
     */
    public GeoLocationImp()
    {
        this(0,0,0);
    }

    /**
     * c'tor will create point by x,y alone
     * @param x
     * @param y
     */
    public GeoLocationImp(double x, double y)
    {
        this(x,y,0);
    }

    /**
     * c'tor will create 3d point by coordinates
     * @param x
     * @param y
     * @param z
     */
    public GeoLocationImp(double x, double y, double z)
    {
        _x = x;
        _y = y;
        _z = z;
        _isValid = true;
    }

    /**
     * c'tor by string of points - splited by ','
     * @param pos
     */
    public GeoLocationImp(String pos)
    {
        this();

        String[] geoPlace = pos.split(",");
        try
        {
            _x = Double.parseDouble(geoPlace[0]);
            _y = Double.parseDouble(geoPlace[1]);
            _z = Double.parseDouble(geoPlace[2]);
        }
        catch (IndexOutOfBoundsException ex)
        {
            _isValid = true;
        }
        catch (Exception ex)
        {
            _isValid = false;
        }
    }

    //Getters

    @Override
    public double x()
    {
        return _x;
    }

    @Override
    public double y()
    {
        return _y;
    }

    @Override
    public double z()
    {
        return _z;
    }

    /**
     * computes the distane of 3d point
     * @param g - other point in 3d
     * @return
     */
    @Override
    public double distance(GeoLocation g)
    {
        double distX = Math.pow(g.x() - this.x(), 2.0);
        double distY = Math.pow(g.y() - this.y(), 2.0);
        double distZ = Math.pow(g.z() - this.z(), 2.0);

        return Math.sqrt(distX + distY + distZ);
    }

    @Override
    public String toString()
    {
        return "(" + _x + "," + _y + "," + _z + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || (!(o instanceof GeoLocation) && !(o instanceof GeoLocationImp)))
            return false;
        GeoLocation that = (GeoLocation) o;
        return (Double.compare(that.x(), _x) == 0 && Double.compare(that.y(), _y) == 0 && Double.compare(that.z(), _z) == 0);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(_x, _y, _z);
    }
}
