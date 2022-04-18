package cs1501a3;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Scanner;
import java.io.IOException;

final public class AirlineTest {

  private AirlineInterface airline;
  private Scanner scan;
  /**
  * Test client.
  */
  public static void main(String[] args) throws IOException {
    new AirlineTest();
  }

  public AirlineTest(){
    airline = new AirlineSystem();
    scan = new Scanner(System.in);
    while(true){
      try{
        switch (menu()) {
          case 1 -> readGraph();
          case 2 -> printGraph();
          case 3 -> shortestHops();
          case 4 -> shortestDistance();
          case 5 -> shortestDistanceWithTransit();
          case 6 -> addCity();
          case 7 -> addRoute();
          case 8 -> updateRoute();
          case 9 -> System.exit(0);
          default -> System.out.println("Incorrect option.");
        }
      } catch (CityNotFoundException e){
        System.out.println("City not found. Please choose from the list " +
        "and check spelling: " + e.getMessage());
      }
      catch (NullPointerException e){
        System.out.println("Null pointer exception " + e.getMessage());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    }
  }

  private int menu(){
    int choice = -1;
    System.out.println("*********************************");
    System.out.println("Welcome to Fifteen O'One Airlines!");
    System.out.println("1. Read routes from a file.");
    System.out.println("2. Display all routes.");
    System.out.println("3. Compute shortest path(s) based on number of stops.");
    System.out.println("4. Compute shortest path(s) based on distance.");
    System.out.println("5. Compute shortest path(s) based on distance " +
    "with a specified transit.");
    System.out.println("6. Add a city.");
    System.out.println("7. Add a route.");
    System.out.println("8. Update a route.");
    System.out.println("9. Exit.");
    System.out.println("*********************************");
    while(true){
      System.out.print("Please choose a menu option (1-9): ");
      try{
        choice = Integer.parseInt(scan.nextLine());
        break;
      } catch(Exception e){
        System.out.println("Invalid input: " + e.getMessage());
      }
    }
    return choice;
  }


  /* --- 1. Read routes from a file. --- */

  private void readGraph() throws FileNotFoundException {
    System.out.println("Please enter filename:");
    String fileName;
//    fileName = scan.nextLine();
    fileName = "D:\\jetbrains\\Pro\\JavaDemo\\src\\cs1501a3\\a4data1.txt";
    if(airline.loadRoutes(fileName)){
      System.out.println("Data imported successfully.");
    } else {
      System.out.println("Error importing data from " + fileName);
    }
  }


  /* 2. Display all routes. */

  private void printGraph() throws CityNotFoundException {
    for(String city: airline.retrieveCityNames()){
      System.out.print(city + ": ");
      for (Route r : airline.retrieveDirectRoutesFrom(city)) {
        System.out.print(r.destination + "(" + r.distance + " miles, $"
        + r.price + ") ");
      }
      System.out.println();
    }
  }


  /* 3. Compute shortest path(s) based on number of stops. */

  private void shortestHops() throws CityNotFoundException {
    for(String city : airline.retrieveCityNames()){
      System.out.println(city);
    }
    System.out.print("Please enter source city: ");
    String source = scan.nextLine();
    System.out.print("Please enter destination city: ");
    String destination = scan.nextLine();
    Set<ArrayList<String>> shortestSet =
    airline.fewestStopsItinerary(source, destination);
    System.out.println("Found " + shortestSet.size() + " path(s):");
    for(ArrayList<String> shortest : shortestSet){
      System.out.print("The shortest path from " + source +
      " to " + destination + " has " +
      (shortest.size()-2) + " stop(s): ");
      for(String r : shortest){
        System.out.print(r + " ");
      }
      System.out.println();
    }
  }


  /* 4. Compute shortest path(s) based on distance. */

  private void shortestDistance() throws CityNotFoundException {
    for(String city : airline.retrieveCityNames()){
      System.out.println(city);
    }
    System.out.print("Please enter source city: ");
    String source = scan.nextLine();
    System.out.print("Please enter destination city: ");
    String destination = scan.nextLine();
    Set<ArrayList<Route>> shortestSet =
    airline.shortestDistanceItinerary(source, destination);
    System.out.println("Found " + shortestSet.size() + " path(s):");
    for(ArrayList<Route> shortest : shortestSet){
      int totalDistance = 0;
      for(Route r : shortest){
        totalDistance += r.distance;
      }
      System.out.print("The shortest path from " + source +
      " to " + destination + " has " +
      totalDistance + " miles: ");
      System.out.print(source);
      for(Route r : shortest){
        System.out.print(" " + r.distance + " " + r.destination);
      }
      System.out.println();
    }
  }


  /* 5. Compute shortest path(s) based on distance with a specified transit. */

  private void shortestDistanceWithTransit() throws CityNotFoundException {
    for(String city : airline.retrieveCityNames()){
      System.out.println(city);
    }
    System.out.print("Please enter source city: ");
    String source = scan.nextLine();
    System.out.print("Please enter destination city: ");
    String destination = scan.nextLine();
    System.out.print("Please enter transit city: ");
    String transit = scan.nextLine();
    Set<ArrayList<Route>> shortestSet =
    airline.shortestDistanceItinerary(source, transit, destination);
    System.out.println("Found " + shortestSet.size() + " path(s):");
    for(ArrayList<Route> shortest : shortestSet){
      int totalDistance = 0;
      for(Route r : shortest){
        totalDistance += r.distance;
      }
      System.out.print("The shortest path from " + source +
      " to " + destination + " passing by " + transit + " has " +
      totalDistance + " miles: ");
      System.out.print(source);
      for(Route r : shortest){
        System.out.print(" " + r.distance + " " + r.destination);
      }
      System.out.println();
    }
  }


  /* 6. Add a city */

  private void addCity() {
    System.out.print("Please enter city name: ");
    String city = scan.nextLine();
    if(!airline.addCity(city)){
      System.out.println("Failed to add city.");
    } else {
      System.out.println("City successfully added.");
    }
  }


  /* 7. Add a route */

  private void addRoute() throws CityNotFoundException {
    for(String city : airline.retrieveCityNames()){
      System.out.println(city);
    }
    System.out.print("Please enter source city: ");
    String source = scan.nextLine();
    System.out.print("Please enter destination city: ");
    String destination = scan.nextLine();
    int distance = 0;
    while(true){
      try{
        System.out.print("Please enter distance: ");
        distance = Integer.parseInt(scan.nextLine());
        break;
      } catch(Exception e){
        System.out.println("Invalid input: " + e.getMessage());
      }
    }
    double price = 0;
    while(true){
      try{
        System.out.print("Please enter price: ");
        price = Double.parseDouble(scan.nextLine());
        break;
      } catch(Exception e){
        System.out.println("Invalid input: " + e.getMessage());
      }
    }

    if(!airline.addRoute(source, destination, distance, price)){
      System.out.println("Failed to add route.");
    } else {
      System.out.println("Route successfully added.");
    }
  }


  /* 8. Update a route */

  private void updateRoute() throws CityNotFoundException {
    for(String city : airline.retrieveCityNames()){
      System.out.println(city);
    }
    System.out.print("Please enter source city: ");
    String source = scan.nextLine();
    System.out.print("Please enter destination city: ");
    String destination = scan.nextLine();
    int distance = 0;
    while(true){
      try{
        System.out.print("Please enter distance: ");
        distance = Integer.parseInt(scan.nextLine());
        break;
      } catch(Exception e){
        System.out.println("Invalid input: " + e.getMessage());
      }
    }
    double price = 0;
    while(true){
      try{
        System.out.print("Please enter price: ");
        price = Double.parseDouble(scan.nextLine());
        break;
      } catch(Exception e){
        System.out.println("Invalid input: " + e.getMessage());
      }
    }

    if(!airline.updateRoute(source, destination, distance, price)){
      System.out.println("Failed to update route.");
    } else {
      System.out.println("Route successfully updated.");
    }
  }
}
