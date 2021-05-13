package AirNetwork;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;



public abstract class Data {
    /*
    Underlying data structure: Hashmap: key: an instance of Airport ---------> value: a hashSet of routes which have the key as the source airport
    Ex: There are routes from   A to B,C,D      |key: A ------------> value {A->B, A->C, A->D}
                                B to C,D,E      |key: B ------------> value {B->C, B->D, B->E}
    */
    protected HashMap<Airport, HashSet<Route>> routes = new HashMap<>(); 
    private final String sqlCoString="jdbc:mysql://localhost:3306/project_cs300?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private char[] password;
    private String userName;
    private final double earthRadius = 6371; //unit: km
    //constructor
    protected Data(String u, char[] pw)
    {
        this.userName=u;
        this.password=pw;
        //using try-with-resource, when connecting and requesting info have done, the connection and statement are closed
        try(
            Connection connection = DriverManager.getConnection(sqlCoString, userName, String.valueOf(password));
            Statement stmt = connection.createStatement();
        )
        {
            //create a query to mysql to retrieve the data and store them in the hashmap
            String sqlQuery = new StringBuilder().append(" WITH firstJoinTable(source_iata, source_name, source_country, source_latitude, source_longitude, dest_iata)")
            .append( "  AS (SELECT airports.IATA, airports.name, airports.country, airports.latitude, airports.longitude, routes.destination_airport")
            .append( " FROM routes JOIN airports ON airports.IATA=routes.source_airport WHERE airports.IATA IS NOT NULL)")
            .append(" SELECT firstJoinTable.source_iata, firstJoinTable.source_name, firstJoinTable.source_country, firstJoinTable.source_latitude, firstJoinTable.source_longitude,")
            .append(" airports.IATA AS dest_iata, airports.name AS dest_name, airports.country AS dest_country, airports.latitude AS dest_latitude, airports.longitude AS dest_longitude")
            .append(" FROM firstJoinTable JOIN airports ON firstJoinTable.dest_iata=airports.IATA WHERE airports.IATA IS NOT NULL;").toString();

            ResultSet rSet = stmt.executeQuery(sqlQuery);//execute the query

            while (rSet.next())
            {
                Airport source = new Airport(rSet.getString("source_iata"), rSet.getString("source_name"), rSet.getString("source_country"), rSet.getDouble("source_latitude"), rSet.getDouble("source_longitude"));
                Airport dest =  new Airport(rSet.getString("dest_iata"), rSet.getString("dest_name"), rSet.getString("dest_country"), rSet.getDouble("dest_latitude"), rSet.getDouble("dest_longitude"));
                routes.putIfAbsent(source, new HashSet<>());
                routes.putIfAbsent(dest, new HashSet<>());
                double distance=calculateDistance(deg2rad(source.getLatitude()),deg2rad(source.getLongitude()), deg2rad(dest.getLatitude()), deg2rad(dest.getLongitude()));
                routes.get(source).add(new Route(source, dest, distance));

            }
        }
        catch(SQLException e )//catch exception if any
        {
            System.out.println("*Fail while loading database to the app");
        }
    }
    //calculate the greate-circle distance between two airports using HARVERSINE FORMULA
    protected double calculateDistance(double source_latitude, double source_longitude, double dest_latitude, double dest_longitude)
    {
        return 2*earthRadius*Math.asin(Math.sqrt(hav(source_latitude, dest_latitude)+Math.cos(source_latitude)*Math.cos(dest_latitude)*hav(source_longitude, dest_longitude)));
        
    }
    private double hav(double x1, double x2)
    {
        return Math.sin((x2-x1)/2)*Math.sin((x2-x1)/2);
    }
    //need to convert the data from degree to radius before applying Harversine formula
    protected double deg2rad(double degree)
    {
        return (degree/180)*Math.PI;
    }
    //clear password;
    public final void clearPw()
    {
        for (int i=0; i<password.length; i++) password[i]=' '; 
    }
    //get airport info from sql database
    public String getAirportInfo(String id)
    {
        StringBuilder info= new StringBuilder();
        try (Connection connection = DriverManager.getConnection(sqlCoString, userName, String.valueOf(password));
        Statement stmt = connection.createStatement())
        {
            String query="SELECT * FROM airports WHERE IATA='"+id+"';";
            ResultSet rSet=stmt.executeQuery(query);
            if (rSet.next())
            {
                info.append("Name: ").append(rSet.getString("name")).append("\n");
                info.append("Country: ").append(rSet.getString("country")).append("\n");
                rSet.getString("IATA");
                info.append("Latitue: ").append(rSet.getDouble("latitude")).append("\n");
                info.append("Longitude: ").append(rSet.getDouble("longitude")).append("\n");
            }
            
        }
        catch(SQLException e)
        {
            System.out.println("*Fail while reading data from mysql.");
        }
        return info.toString();
    }
    protected void addAirportToDbs(String id, String name, String country, Double latitude, Double longitude)
    {
        String query = "INSERT INTO airports (name, country, IATA, latitude, longitude) VALUES ( ?, ?, ?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(sqlCoString, userName, String.valueOf(password));
        PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, name);
            stmt.setString(2, country);
            stmt.setString(3, id);
            stmt.setDouble(4, latitude);
            stmt.setDouble(5, longitude);
            stmt.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("*"+e.getMessage());
        }
    }
    protected void deleteAirportFromDbs(String id)
    {
        String query1 = "DELETE FROM airports WHERE IATA='"+ id+"';";
        String query2 = "DELETE FROM routes WHERE source_airport='"+id+"' OR destination_airport='"+id+"';";
        try(Connection connection = DriverManager.getConnection(sqlCoString, userName, String.valueOf(password));
        PreparedStatement stmt1 = connection.prepareStatement(query1);
        PreparedStatement stmt2 =  connection.prepareStatement(query2))
        {
            stmt1.executeUpdate();
            stmt2.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("*Fail while deleting from database.");
        }
    }
    
    protected void addRouteToDbs(String sourceId, String destId)
    {
        String query ="INSERT INTO routes(source_airport, destination_airport) VALUES (?,?)";
        try(Connection connection = DriverManager.getConnection(sqlCoString, userName, String.valueOf(password));
        PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, sourceId);
            stmt.setString(2, destId);
            stmt.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("*Fail while adding new route to database");
        }
      
    }
    protected void removeRouteFromDbs(String sourceId, String destId)
    {
        String query ="DELETE FROM routes WHERE source_airport=? AND destination_airport=?";
        try(Connection connection = DriverManager.getConnection(sqlCoString, userName, String.valueOf(password));
        PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1,sourceId);
            stmt.setString(2, destId);
            stmt.executeUpdate();
        }
        catch(SQLException e)
        {
            System.out.println("Fail while removing the route from the database.");
        }

    }
}
