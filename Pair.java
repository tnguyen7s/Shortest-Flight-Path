package AirNetwork;

//Pair class's instance stores the airport and cost, particularly used for shortestPath algorithm
public final class Pair implements Comparable<Pair>{
    public Airport airport;
    public double cost;// in this program, this field can be the cost from the airport's predecessor or  from the origin to the airport
    public Pair(Airport airport, double cost)
    {
        this.airport=airport;
        this.cost=cost;
    } 
    @Override
    public int compareTo(Pair o)
    {
        if (this.cost>o.cost)
        {
            return 1;
        }
        else if (this.cost<o.cost)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

}
