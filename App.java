package AirNetwork;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.lang.StringBuilder;

public class App{
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        //receive password and username to get access database and use the graph API
        char[] password = {'P', 'A', 'S', 'S', 'W', 'O', 'R', 'D'};
        AirportGraph graph = new AirportGraph("root", password);
        /*ask for operation code and execute the requested operation
        until users want to quit
        */
        printOpCodes();
        System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////////");
        int opCode;
        System.out.print("Please enter an operation code or QUIT(0): ");
        opCode = scanner.nextInt();
        while (opCode!=0)
        {
            //use opCode to know which kind of operation and execute the request
            switch(opCode){  
                case 1: addAirport(graph);break;
                case 2: removeAirport(graph);break;
                case 3: addRoute(graph);break;
                case 4: removeRoute(graph); break;
                case 5: containsAirport(graph);break;
                case 6:containsRoute(graph);break;
                case 7: minSpanningTree(graph);break;
                case 8: shortestDistance(graph); break;
                case 9: distance(graph);break;
                default:System.out.println("Invalid Code");break;
            }
            //ask for another operation code to continue or quit
            System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////////");
            System.out.print("Please enter an operation code or QUIT(0): ");
            opCode = scanner.nextInt();
        }
        graph.clearPw();

      
    }
    //opcode 1
    public static void addAirport(AirportGraph graph)
    {
        System.out.print("Enter the airport's IATA code: ");
        String IATA = scanner.next();
        scanner.nextLine();
        System.out.print("Enter the airport's name: ");
        String name = scanner.nextLine();
        System.out.print("Enter the country in which it is located: ");
        String country=scanner.nextLine();
        System.out.print("Enter the airport's latitude: ");
        double latitude = scanner.nextDouble();
        System.out.print("Enter the airport's longitude: ");
        double longitude = scanner.nextDouble();
        if (graph.addNode(IATA, name, country, latitude, longitude))
        {
            System.out.println("Successfully added!");
        }
        else
        {
            System.out.println("Airport already in the system");
        }
    }
    //opcode 2
    public static void removeAirport(AirportGraph graph)
    {
        System.out.print("Enter the airport IATA code: ");
        String id = scanner.next();
        if(graph.removeNode(id))
        {
            System.out.println("Successfully removed!");
        }
        else
        {
            System.out.println("Airport is not in the current network");
        }
    } 
    //opCode 3
    public static void addRoute(AirportGraph graph)
    {
        System.out.print("Enter source airport IATA: ");
        String sourceId = scanner.next();
        scanner.nextLine();
        System.out.print("Enter source airport's name: ");
        String sourceName = scanner.nextLine();
        System.out.print("Enter the country in which it is located: ");
        String sourceCountry=scanner.nextLine();
        System.out.print("Enter source airport's latitude: ");
        double sourceLatitude = scanner.nextDouble();
        System.out.print("Enter source airport's longitude: ");
        double sourceLongitude = scanner.nextDouble();
        System.out.print("Enter destination airport IATA: ");
        String destId=scanner.next();
        scanner.nextLine();
        System.out.print("Enter destination airport's name: ");
        String destName = scanner.nextLine();
        System.out.print("Enter the country in which it is located: ");
        String destCountry=scanner.nextLine();
        System.out.print("Enter destination airport's latitude: ");
        double destLatitude = scanner.nextDouble();
        System.out.print("Enter destination airport's longitude: ");
        double destLongitude = scanner.nextDouble();
        if (graph.addEdge(sourceId, sourceName, sourceCountry, sourceLatitude, sourceLongitude, destId, destName, destCountry, destLatitude, destLongitude))
        {
            System.out.println("Successfully added!");
        }
        else
        {
            System.out.println("The edge is already in the graph!");
        }
    }
    //opcode 4
    public static void removeRoute(AirportGraph graph)
    {
        System.out.print("Enter source airport's IATA: ");
        String sourceId=scanner.next();
        System.out.print("Enter destination airport's IATA: ");
        String destId=scanner.next();
        if (graph.removeEdge(sourceId, destId))
        {
            System.out.println("Successfully removed the route.");
        }
        else
        {
            System.out.println("Route does not exist in the network");
        }
    }
    //opCode 5
    public static void containsAirport(AirportGraph graph)
    {
        System.out.print("Enter the airport IATA code: ");
        String id = scanner.next();
        StringBuilder s=new StringBuilder();
        graph.containsNode(id, s);
        System.out.println(s.toString());
    }
    //opCode 6
    public static void containsRoute(AirportGraph graph)
    {
        System.out.print("Enter source airport's IATA: ");
        String sourceId = scanner.next();
        System.out.print("Enter destination airport's IATA: ");
        String destId = scanner.next();
        if (graph.containsEdge(sourceId, destId))
        {
            System.out.println("The route from "+ sourceId + " to "+destId+" does exist in the network.");
        }
        else
        {
            System.out.println("The route from "+ sourceId + " to "+destId+" does not exist in the network.");
        }
    }
    //opcode 7
    public static void minSpanningTree(AirportGraph graph)
    {
        //get the origin of the spanning tree
        System.out.print("Enter the source airport's IATA code: ");
        String sourceId = scanner.next();
        scanner.nextLine();
        //limit the spanning tree to include only airports of a country
        System.out.print("Enter the country's name: ");
        String country = scanner.nextLine();
        //call the method minSpanningTree from the AirportGraph class; this method will create a minimum spanning tree and display it using Jung library
        Set<Route> tree = graph.minSpanningTree(sourceId, country);
        if (tree==null)
        {
            System.out.println("The source airport's IATA code is invalid or the source doesn't have any route connecting to the country's airports");
        }
        else
        {
            for (Route r:tree)
            {
                System.out.println(r);
            }
        }
    }
    /*//opCode 7
    public static void minNumRoutes(AirportGraph graph)
    {
        System.out.print("Enter source airport's IATA:");
        String idSource = scanner.next();
        System.out.print("Enter destination's IATA: ");
        String idDestination = scanner.next();
        Stack <Airport> optimalSolution = new Stack<>(); 
        double distance = graph.shortestPathAsUnweightedG(idSource, idDestination, optimalSolution);
        while (!optimalSolution.isEmpty())
        {
            
            System.out.print(optimalSolution.pop().getName()+"->");
        }
        System.out.println("END.");
        System.out.println("Total distance: " + String.valueOf(distance));
    }*/
    //opCode 8
    public static void shortestDistance(AirportGraph graph)
    {
        System.out.print("Enter the source's airport IATA: ");
        String sourceId = scanner.next();
        if (!graph.executeShortestPath(sourceId))
        {
            System.out.println("Invalid source's airport IATA");
            return;
        }
        System.out.print("Enter IATA of the airport to which there is a shortest path from the source airport: ");
        String destId = scanner.next();
        Stack<Route> path;
        while (destId.charAt(0)!='0')
        {
            path=graph.getShortestPath(sourceId, destId);
            if (path.isEmpty())
            {
                System.out.print("Destination IATA is invalid");
            }
            while (!path.isEmpty())
            {
                System.out.println(path.pop());
            }
            System.out.print("Enter IATA of another airport or 0 for quit: ");
            destId=scanner.next();
        }
        
        
    }

    //opcode 9
    public static void distance(AirportGraph graph)
    {
        System.out.print("Enter source airport's IATA: ");
        String sourceId = scanner.next();
        System.out.print("Enter destination airport's IATA: ");
        String destId = scanner.next();
        double d;
        if ((d=graph.distanceBetweenTwoAirports(sourceId, destId))==0){
            System.out.println("The route does not exist in the network.");
        }   
        else
        {
            System.out.println("Distance: "+String.valueOf(d));
        }
    }
    public static void printOpCodes()
    {
        System.out.println("Flight network applications operation codes: ");
        System.out.println("****Code #0: QUIT");
        System.out.println("****Code #1: add new airport into the network (update the database as well)");
        System.out.println("****Code #2: remove an airport from the network (update the database as well)");
        System.out.println("****Code #3: add a new route into the network (update the database as well)");
        System.out.println("****Code #4: remove a route from the network (update the database as well)");
        System.out.println("****Code #5: Check if the current network contains a specified airport");
        System.out.println("****Code #6: Check if the current network contains a route");
        System.out.println("****Code #7: Minimum spanning tree connecting all airports within a country");
        System.out.println("****Code #8: Find the shortest distance to travel from one airport to another airport");
        System.out.println("****Code #9: Get the distance between two airports");
        
    }
}
