package AirNetwork;

import java.util.Objects;

public final class Route implements Cloneable{//is a directional edge in the graph...indicates that there is a service airline traveling from A to B (public)
    private Airport a;
    private Airport b;// each instance is a route from a to b
    private double distance;
    //args constructor
    public Route(Airport a, Airport b, double distance)
    {
        this.a=(Airport)a.clone();
        this.b=(Airport)b.clone();
        this.distance=distance;
    }
    //default constructor
    public Route()
    {
        a=null;
        b=null;
        distance=0;
    }
    //setters
    public void setSource(Airport a)
    {
        this.a=(Airport)a.clone();
    }
    public void setDestination(Airport b)
    {
        this.b=(Airport)b.clone();
    }
    public void setDistance(double distance)
    {
        this.distance=distance;
    }
    //getters
    public Airport getSource()
    {
        return (Airport)this.a.clone();
    }
    public Airport getDestination()
    {
        return (Airport) this.b.clone();
    }
    public double getDistance()
    {
        return this.distance;
    }
    //define hashCode and equals; because we use abjacency set of edges for graph data structure
    @Override
    public boolean equals(Object o)
    {
        if (this==o)
        {
            return true;
        }
        if(!(o instanceof Route))
        {
            return false;
        }
        return ((Route)o).a.equals(this.a) && ((Route)o).b.equals(this.b);
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(a, b);
    }
    public String toString()
    {
        return this.a.getID()+ "-"+this.b.getID()+" "+String.valueOf(Math.round(distance*100)/100.0);
    }
    @Override
    public Object clone()
    {
        return new Route ((Airport)a.clone(), (Airport)b.clone(), distance);
    }

}