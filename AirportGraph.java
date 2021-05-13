package AirNetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.plaf.DimensionUIResource;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public final class AirportGraph extends Data{ //inherits from Data class ---- Communicate with Data class to load data and access data for application
    /*construct airportGraph => construct Data 
    => instante of Data using sql-connection-string to connect to sql and load data
    airportGraph can access the data through its parent Data class
    */
   
    public AirportGraph(String userName, char[] password)
    {
        super(userName, password);
    }
    /*
    check if the network contains an airport
    args: airport IATA (String)
    return type: boolean (true=found, false=not found)
    if found, return info of the airport
    */
    public boolean containsNode(String id, StringBuilder s)
    {
        /*
        Because Airport class uses only id for equals and hashcode
        the method creates a temporary Airport containing the argumented id
        this tmp Airport should have the same hashcode as the one that we are searching for in the network if exists
        */
        Airport tmp = new Airport();
        tmp.setID(id);
        if (!routes.containsKey(tmp))
        {
            s.append("No such airport found in the current netwrork");
            return false;
        }
        s.append(getAirportInfo(id));
        return true;
    }
    /*
    add a new airport to the network and to database
    args: info of the airport 
    return type: true: successfully adding ; false: already in the network 
    */
    public boolean addNode(String id, String name, String country, double latitude, double longitude)
    {
        Airport newAirport = new Airport(id, name, country, latitude, longitude);
        if (routes.containsKey(newAirport))
        {
            return false;
        }
        routes.put(newAirport, new HashSet<>());
        addAirportToDbs(id, name, country, latitude, longitude);
        return true;
    }
    /*
    remove node from graph and database
    args: IATA code 
    return type: boolean (true: successfully removed, false: not in the graph)
    */
    public boolean removeNode(String id)
    {
        //Create a tmp airport mimic the airport that we are looking for as the equals and hashcode use IATA 
        Airport tmp = new Airport();
        tmp.setID(id);
        //Check if the airport is in the network, if not return false to indicate the airport does not exist in the network
        if (!routes.containsKey(tmp))
        {
            return false;
        }
        //delete all routes that have this node (tmp) as source airport 
        routes.remove(tmp);
        //remove all routes that have this node (tmp) as destination airport
        for (Airport source : routes.keySet())
        {
            Route removed = null;
            for (Route r:routes.get(source))
            {
                if (r.getDestination().equals(tmp))
                {
                    removed=r;
                    break;
                }
            }
            if (removed!=null)
            {
                routes.get(source).remove(removed);//O(1)
            }
        }
        deleteAirportFromDbs(id);

        return true;
    }
    /*
    add a new route to the network
    args: IATAs of two airports
    return type: boolean (true: successfully added, false: already in the network)
    */
    public boolean addEdge(String sourceId, String sourceName, String sourceCountry, double sourceLatitude, double sourceLongitude, String destId, String destName, String destCountry, double destLatitude, double destLongitude)
    {
        Airport source = new Airport(sourceId, sourceName, sourceCountry, sourceLatitude, sourceLongitude);
        Airport dest = new Airport(destId, destName, destCountry, destLatitude, destLongitude);
        Route newRoute = new Route(source, dest,super.calculateDistance(sourceLatitude, sourceLongitude, destLatitude, destLongitude));
        //check if the route is in the network
        if (routes.containsKey(source))
        {
            if (routes.get(source).contains(newRoute))
            {  
                return false;
            }
        }
        routes.putIfAbsent(source, new HashSet<>());
        routes.putIfAbsent(dest, new HashSet<>());
        routes.get(source).add(newRoute);
        super.addRouteToDbs(sourceId, destId);
        super.addAirportToDbs(sourceId, sourceName, sourceCountry, sourceLatitude, sourceLongitude);
        super.addAirportToDbs(destId, destName, destCountry, destLatitude, destLongitude);
        return true;
    }
    /*
    check if the network contains a route
    args: IATAs of the two airports
    return type: boolean (true: exist in the graph; false: not exist in the graph)
    */
    public boolean containsEdge(String sourceId, String destId)
    {
        Airport source = new Airport();
        source.setID(sourceId);
        Airport dest = new Airport();
        dest.setID(destId);
        Route route = new Route();
        route.setSource(source);
        route.setDestination(dest);
        if (routes.containsKey(source))
        {
            if (routes.get(source).contains(route))
            {
                return true;
            }
        }
        return false;
    }
    public boolean removeEdge(String sourceId, String destId)
    {
        if (!containsEdge(sourceId, destId))
        {
            return false;
        }
        Airport source = new Airport();
        source.setID(sourceId);
        Airport dest = new Airport();
        dest.setID(destId);
        Route route = new Route();
        route.setSource(source);
        route.setDestination(dest);
        routes.get(source).remove(route);
        super.removeRouteFromDbs(sourceId, destId);
        return true;
    }
    public double distanceBetweenTwoAirports(String sourceId, String destId)
    {
        if (!containsEdge(sourceId, destId))
        {
            return 0;
        }
        Airport source = new Airport();
        source.setID(sourceId);
        for (Route r: routes.get(source))
        {
            if (r.getDestination().getID().equals(destId))
            {
                return r.getDistance();
            }
        }
        return 0;
   }
   /*private List<Airport> getSuccessors(Airport source)
   {
       ArrayList<Airport> returnedL = new ArrayList<>();
       for (Route r:routes.get(source))
       {
           returnedL.add(r.getDestination());
       }
       return returnedL;
   }*/
   
   /*
   + method functionality: is to find a combination of routes from source to destination that has the min number of in-between airports 
     (using breath-first-search and treating the graph as unweighted graph)
   + args: the IATA code for the two airports (starting and ending ones)
   + return: an optimal solution with min number of routes. Because there are multiple combinations that have the min number of routes, only return the combination that has the shortest total distance 
            all airports involved in the solution will store in sol Stack to return back to the caller end
            also return the total distance (double)
   
   public double shortestPathAsUnweightedG(String sourceId, String destId, Stack<Airport> sol)
   {
       //Create mimic airport objects that have the specified IATA codes to identify the two airports
       Airport source = new Airport();
       source.setID(sourceId);
       Airport dest = new Airport();
       dest.setID(destId);
       //get the actual source and destination objects
       HashSet<Route> rs =routes.get(source);
       Iterator<Route> iterator = rs.iterator();
       source=iterator.next().getSource();
       rs=routes.get(dest);
       iterator = rs.iterator();
       dest = iterator.next().getSource();
       //Create a pending queue for  breadth first search
       Queue<Airport> pending = new LinkedList<>();
       pending.offer(source);
       //Create a hashSet of visited airport => look-up O(1)
       HashSet<Airport> visited = new HashSet<>();
       //Create a hashmap to store the predecessors 
       HashMap<Airport, HashSet<Airport>> predecessors = new HashMap<>();
       Airport tmp;
       //flag to indicate whther the destination airport has been found or not
       boolean found = false;
       //start traversing using breath-first-search
       while (!pending.isEmpty())
        {
            tmp=pending.poll();
           //check if the node is unvisited
           if (visited.contains(tmp))
           {
               continue;
           }
           //visit the node if unvisited
           visited.add(tmp);
           //add its successors to the pending
           for (Airport successor:getSuccessors(tmp))
           {
               //store predeccessors
               if (predecessors.containsKey(tmp))
               {
                   if (!predecessors.get(tmp).contains(successor))
                   {
                        predecessors.putIfAbsent(successor, new HashSet<>());
                        predecessors.get(successor).add(tmp);
                   }
               }
               else
               {
                     predecessors.putIfAbsent(successor, new HashSet<>());
                    predecessors.get(successor).add(tmp);
               }
               
               //if already found the destination airport dont need to add more nodes to the pending queue to visit
               //but still continue  the loop until the pending queue is empty to store all possible predecessors of the dest node
               if (!visited.contains(successor) && !found)
               {
                   pending.add(successor);
               }
               if (successor.equals(dest))
               {
                   found = true;
               }
               
           }   

       }
       Airport current =dest;
       while (!current.equals(source))
       {
           System.out.println(current.getName()+"<-");
           current=predecessors.get(current).iterator().next();
       }
       Stack<Airport> tempStack = new Stack<>();
       double[] distances = new double[2];//at [0] stores the calculated distance, [1] stores the current minimum distance
       distances[0]=0;
       distances[1]=Double.MAX_VALUE;
      //find out in those solutions, which have the minimum number of routes, has the shortest distance (using backtrack)
       
        shortestDistanceForMinRoutes(predecessors, sol, tempStack, dest, source, distances);
        return distances[1];
   }*/
   /*
   private void shortestDistanceForMinRoutes(HashMap<Airport, HashSet<Airport>> predecessors, Stack<Airport> sol, Stack<Airport> tmp, Airport current, Airport source, double[] distances) 
   {
        tmp.push(current);
        if (current.equals(source))
        {
            if (distances[0]<distances[1])
            {
                distances[1]=distances[0];
                sol.clear();
                sol.addAll(tmp);
            }
            return;
        }
        HashSet<Airport> p=predecessors.get(current);
        for (Airport predecessor: p)
        {
            double d = getDistance(current, predecessor);      
            if (distances[0]+d<distances[1])
            {
                distances[0]=distances[0]+d;
                shortestDistanceForMinRoutes(predecessors, sol, tmp, predecessor, source, distances);
                tmp.pop();
                distances[0]-=d;
            }
        }
   }*/
   public Set<Route> minSpanningTree(String originId, String country)
   {
       //Create a subgraph of airports that are located in the specified country
       HashMap<Airport, HashSet<Route>> subG = new HashMap<>();
       //create a mimic airport has the source id
       Airport origin = new Airport();
       origin.setID(originId);
       origin=routes.get(origin).iterator().next().getSource();
       if (!routes.containsKey(origin))
       {
           return null;
       }
       //find all edges of the origin and add to the hashmap
       subG.put(origin, new HashSet<>());
       for (Route e:routes.get(origin))
       {
           if (e.getDestination().getCountry().equals(country))
           {
               subG.get(origin).add(e);
           }
       }
       if (subG.get(origin).size()==0)
       {
           return null;
       }
       //find all airports and their routes where sources and destinations are located in the specified country, add them to the graph
       for (Airport source:routes.keySet())
       {
           if (source.getCountry().equals(country))
           {
               subG.put(source, new HashSet<>());
               for (Route r : routes.get(source))
               {
                   if (r.getDestination().getCountry().equals(country))
                   {
                       subG.get(source).add(r);
                   }
               }
           }
       }

       //start the process of finding spanning tree for the graph
       HashSet<Route> tree = new HashSet<>();//store all the routes that are in the spanning tree
       HashSet<Airport> included = new HashSet<>();//to keep track which airports have been included in the spanning tree
       included.add(origin);
       HashSet<Airport> excluded = new HashSet<>();//to store those have not been added to the tree yet
       excluded.addAll(subG.keySet());
       excluded.remove(origin);
       
       while (!excluded.isEmpty())//work until all airports are included in the tree
       {
           //each iteration: find a route that has the least distance 
           //within a collection of routes that have the sources in the included set and destinations in the excluded set
           //so that we can bring a new airport into the spanning tree
           double minD = Double.MAX_VALUE;
           Route minR =null; 
           for (Airport source:included)
           {
               for (Route r:routes.get(source))
               {
                   if (excluded.contains(r.getDestination()) && r.getDistance()<minD)
                   {
                       minR = r;
                       minD=r.getDistance();
                   }
               }
           }
           tree.add(minR);//add the new least distance route to the tree
           included.add(minR.getDestination());
           excluded.remove(minR.getDestination());
       }
       visualizeGraphUsingJung(subG.keySet(), tree);
       //Return a deep copy
       HashSet<Route> returnedTree = new HashSet<>();
       for (Route r:tree)
       { 
           returnedTree.add((Route)r.clone());
       }
       return returnedTree;
   }
   private void visualizeGraphUsingJung(Collection<Airport> vertices, Collection<Route> edges)
   {
       //Create graph using Jung library 
        Graph<Airport, Route> g = new SparseMultigraph<Airport, Route>();
       for (Airport a:vertices)
       {
           g.addVertex(a);
       }
       for (Route r:edges)
       {
           g.addEdge(r, r.getSource(), r.getDestination());
       }
       //Create Layout and BasicVisualizationServer from Jung library to display the graph
       Layout<Airport, Route> layout = new CircleLayout<>(g);
       layout.setSize(new DimensionUIResource(1300, 700));
       BasicVisualizationServer<Airport, Route> vv = new BasicVisualizationServer<>(layout);
       vv.setPreferredSize(new DimensionUIResource(1550, 850));
       vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
       vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<>());
       vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
        /*Transformer<Integer,Pa> vertexPaint = new Transformer<Integer,Paint>() {            
           public Paint transform(Integer i) {                
               return Color.GREEN;            
            }        
        }; */
       
       //Create a frame 
       JFrame frame = new JFrame("Minimum spanning tree");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.getContentPane().add(vv);
       frame.pack();
       frame.setVisible(true);
   }
  
   private HashMap<Airport, Airport> predecessors = new HashMap<>();//to store predecessors of vertices when implementing Dijsktra's algorithm
   private HashMap<Airport, Double> costsFromTheOrigin = new HashMap<>();//to store minimum cost to reach to each airport when implementing Dijskstra's algorithm 
    //Execute the Dijkstra's algorithm to find the shortest path from the source airport to all other airports 
   public boolean executeShortestPath(String sourceId)
   {
       //clear the results from previous execution
       predecessors.clear();
       costsFromTheOrigin.clear();
       //Create mimic objects for source and destination airports
       Airport source = new Airport();
       source.setID(sourceId);
       //obtains the actual object
       if (routes.containsKey(source))
       {
            source = routes.get(source).iterator().next().getSource();
       }
       else
       {
           return false;//invalid source airport id code
       }
       //find the shortest path from the source airport (origin) to all other airports 
       //A hashmap to store predecessors of airports 
       predecessors.put(source, null);
       //A hashmap to store the costs to reach to each of the airports
       for (Airport a:routes.keySet())
       {
           costsFromTheOrigin.put(a, Double.POSITIVE_INFINITY); //hasn't know the shortest distance yet, thus assign each vertex to infinity
       }
       costsFromTheOrigin.put(source, 0.0);//the shortest distance from source to the source is simply 0
       //A hashset to store the airports that have been already explored by the argorithm
       HashSet<Airport> visited = new HashSet<>();
       //A priority queue allows to pop the airport that have the smallest upperbound cost (the smallest cost from source to the airport)
       PriorityQueue<Pair> q = new PriorityQueue<>(); //O(logn) to retrieve the shortest one
       q.add(new Pair(source, 0));//all the airports' costs are infinitive except the source (cost=0), the first choice is always the source
       //explore all the nodes in the network to improve the shortest paths from the origin to all other airports using Dijisktra algorithm
       while (!q.isEmpty())
       {
           Pair exploredNode =q.poll();
           //only explore the vertex if it has not been explored yet
           if (visited.contains(exploredNode.airport))
           {
               continue;
           }
           for (Pair successors:getSuccessorsWithDistance(exploredNode.airport))
           {
               double newCost =exploredNode.cost+successors.cost;
               if (newCost<costsFromTheOrigin.get(successors.airport))
               {
                   predecessors.put(successors.airport, exploredNode.airport);
                   costsFromTheOrigin.put(successors.airport, newCost);
                   q.add(new Pair(successors.airport, newCost));
               }
            }
            visited.add(exploredNode.airport);
       }
       return true;
   }
    //getSuccessors to get pairs storing successors and their edge costs (from the source to the successors)
    private List<Pair> getSuccessorsWithDistance(Airport source)
    {
        ArrayList<Pair> returnedL = new ArrayList<>();
        for (Route r:routes.get(source))
        {
            returnedL.add(new Pair(r.getDestination(), r.getDistance()));
        }
        return returnedL;
    }
    public Stack<Route> getShortestPath(String sourceId, String destId)
    {
            //stack of route to return, the topmost is route coming off from the source
            Stack<Route> rs = new Stack<>(); 

            Airport dest = new Airport();
            dest.setID(destId);
            if (routes.containsKey(dest))
            {
                dest = routes.get(dest).iterator().next().getSource();
            }
            else
            {
                return rs;
            }
            Airport source = new Airport();
            source.setID(sourceId);
            source = routes.get(source).iterator().next().getSource();
            
            //store airports used to pass to visualizeGraphUsingJung
            ArrayList<Airport> airports = new ArrayList<>();
            airports.add(dest);
            
            Airport current = dest;

            while (!current.equals(source))
            {
                Airport p = predecessors.get(current);
                airports.add(p);
                rs.push(new Route(p, current, calculateDistance(deg2rad(p.getLatitude()), deg2rad(p.getLongitude()), deg2rad(current.getLatitude()), deg2rad(current.getLongitude()))));
                current = p;
            } 
            System.out.println("Shortest total distance to go from "+source.getName()+" to "+dest.getName()+" is "+costsFromTheOrigin.get(dest));
            visualizeGraphUsingJung(airports,rs);
            return rs;
    }
}
