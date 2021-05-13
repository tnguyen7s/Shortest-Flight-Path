package AirNetwork;

import java.util.Objects;

public final class Airport implements Cloneable{ //each instance of airport is a node/vertex in the graph (public-considered)
    private String id; //IATA code - primary key, used for equal and hashcode
    private String name;
    private String country;
    private double longitude;
    private double latitude;
    //args constructor
    public Airport(String id, String name, String country, double latitude, double longitude)
    {
        this.id=id;
        this.name=name;
        this.country=country;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    //default constructor
    public Airport()
    {
        id="tmp";
        name="N/A";
        country="N/A";
        longitude=0;
        latitude=0;
    }
    //setters
    public void setID(String id)
    {
        this.id=id;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public void setCountry(String country)
    {
        this.country=country;
    }
    public void setLongitude(double longitude)
    {
        this.longitude=longitude;
    }
    public void setLatitude(double latitude)
    {
        this.latitude=latitude;
    }
    //getters
    public String getID()
    {
        return this.id;
    }
    public String getName()
    {
        return this.name;
    }
    public String getCountry()
    {
        return this.country;
    }
    public double getLongitude()
    {
        return longitude;
    }
    public double getLatitude()
    {
        return latitude;
    }
    //Override equals and hashCode
    @Override
    public boolean equals(Object o)
    {
        if (this==o)//same reference
        {
            return true;
        }
        if (!(o instanceof Airport))//check if received object is an instance of Airport before casting it and check its content
        {
            return false;
        }
        return ((Airport)o).id.equals(this.id);
    }
    @Override
    public int hashCode(){
        return Objects.hash(this.id);//same id=>same aiport=> same hashcode
    }
    @Override
    public String toString()
    {
        return this.name;
    }
    @Override
    public Object clone()
    {
        return new Airport(this.id, this.name, this.country, this.latitude, this.longitude);
    }

}