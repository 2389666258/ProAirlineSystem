package cs1501a3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

final public class AirlineSystem implements AirlineInterface {
  // variables
  private String [] cityNames = null;
  private Digraph G = null;
  private static final int INFINITY = Integer.MAX_VALUE;

  // constructor
  public AirlineSystem() {
  }



        /* ------------------ Main methods ------------------ */


  public boolean loadRoutes(String fileName) {

    try {
      Scanner fileScan = new Scanner(new FileInputStream(fileName));
      int v = Integer.parseInt(fileScan.nextLine());
      G = new Digraph(v);

      cityNames = new String[v];
      for(int i = 0; i < v; i++){
        cityNames[i] = fileScan.nextLine();
      }

      while(fileScan.hasNext()){
        int from = fileScan.nextInt();
        int to = fileScan.nextInt();
        int weight = fileScan.nextInt();
        double price = fileScan.nextDouble();

        G.addEdge(new WeightedDirectedEdge(from - 1, to - 1, weight, price));
        G.addEdge(new WeightedDirectedEdge(to - 1, from - 1, weight, price));

      }
      fileScan.close();
    } catch (FileNotFoundException e) {
      return false;
    }

    return true;
  }

  public Set<String> retrieveCityNames() {
    return new HashSet<>(Arrays.asList(cityNames));
  }

  public Set<Route> retrieveDirectRoutesFrom(String city) throws CityNotFoundException {
    Set<String> cities = retrieveCityNames();
    if (!cities.contains(city)) {
      throw new CityNotFoundException(city);
    }

    Set<Route> R = new HashSet<>();

    for (int i = 0; i < G.v; i++) {
      if (cityNames[i].equals(city)) {
        for (WeightedDirectedEdge e : G.adj(i)) {
          R.add(new Route(cityNames[i], cityNames[e.to()], e.weight(), e.price));
        }
        break;
      }
    }

    return R;
  }

  public Set<ArrayList<String>> fewestStopsItinerary(String sourceS, String destinationS) throws CityNotFoundException {
    int source = -1;
    int destination = -1;

    for(int i=0; i<cityNames.length; i++){
      if (Objects.equals(cityNames[i], sourceS)) source = i;
      if (Objects.equals(cityNames[i], destinationS)) destination = i;
    }

    if (source == -1 || destination == -1) {
      if (source == -1) throw new CityNotFoundException(sourceS);
      else throw new CityNotFoundException(destinationS);
    }

    Set<ArrayList<String>> pa = new HashSet<>();
    ArrayList<String> p = new ArrayList<>();

    G.bfs(source);
    if(!G.marked[destination]){
      System.out.println("There is no route from " + cityNames[source]
              + " to " + cityNames[destination]);
      return pa;
    } else {
      Stack<Integer> edge_to = new Stack<>();
      for (int i = destination; i != source; i = G.edgeTo[i]) {
        edge_to.push(i);
      }
      edge_to.push(source);

      while(!edge_to.empty())
      {
        int e = edge_to.pop();
        p.add(cityNames[e]);
      }
      pa.add(p);
    }

    return pa;
  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String sourceS, String destinationS) throws CityNotFoundException {
    int source = -1;
    int destination = -1;

    for(int i=0; i<cityNames.length; i++){
      if (Objects.equals(cityNames[i], sourceS)) source = i;
      if (Objects.equals(cityNames[i], destinationS)) destination = i;
    }

    if (source == -1 || destination == -1) {
      if (source == -1) throw new CityNotFoundException(sourceS);
      else throw new CityNotFoundException(destinationS);
    }

    Set<ArrayList<Route>> pa = new HashSet<>();
    ArrayList<Route> p = new ArrayList<>();

    if(G == null){
      System.out.println("Please import a graph first (option 1).");
      System.out.print("Please press ENTER to continue ...");
    } else {

      G.dijkstras(source, destination);
      if(!G.marked[destination]){
        System.out.println("There is no route from " + cityNames[source]
                + " to " + cityNames[destination]);
      } else {
        Stack<Integer> path = new Stack<>();
        for (int x = destination; x != source; x = G.edgeTo[x]){
          path.push(x);
        }

        int prevVertex = source;

        while(!path.empty()){
          int v = path.pop();

          // read the price of the edge from prevVertex to v
          double priceFromPrevToV = 0;
          for (WeightedDirectedEdge e : G.adj(prevVertex)){
            if (e.to() == v){
              priceFromPrevToV = e.price;
              break;
            }
          }

//          System.out.println(cityNames[prevVertex] + " to " + cityNames[v] + ": " + priceFromPrevToV);

          Route r = new Route(cityNames[prevVertex], cityNames[v], G.distTo[v] - G.distTo[prevVertex], priceFromPrevToV);
          p.add(r);
          prevVertex = v;
        }
        pa.add(p);
      }
    }

    return pa;
  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source, String transit, String destination) throws CityNotFoundException {
    Set<ArrayList<Route>> tmp;
    Set<ArrayList<Route>> pa = new HashSet<>();
    ArrayList<Route> p = new ArrayList<>();

    tmp = shortestDistanceItinerary(source, transit);
    for (ArrayList<Route> t : tmp) {
      p.addAll(t);
    }

    tmp = shortestDistanceItinerary(transit, destination);
    for (ArrayList<Route> t : tmp) {
      p.addAll(t);
    }

    pa.add(p);

    return pa;
  }

  public boolean addCity(String city) {
    cityNames = Arrays.copyOf(cityNames, cityNames.length + 1);
    cityNames[cityNames.length - 1] = city;
    return true;
  }

  public boolean addRoute(String sourceS, String destinationS, int distance, double price) throws CityNotFoundException {
    int source = -1;
    int destination = -1;

    for(int i=0; i<cityNames.length; i++){
      if (Objects.equals(cityNames[i], sourceS)) source = i;
      if (Objects.equals(cityNames[i], destinationS)) destination = i;
    }

    if (source == -1 || destination == -1) {
      if (source == -1) throw new CityNotFoundException(sourceS);
      else throw new CityNotFoundException(destinationS);
    }

    G.addEdge(new WeightedDirectedEdge(source, destination, distance, price));
    G.addEdge(new WeightedDirectedEdge(destination, source, distance, price));

    return true;
  }

  public boolean updateRoute(String sourceS, String destinationS, int distance, double price) throws CityNotFoundException {
    int source = -1;
    int destination = -1;

    for(int i=0; i<cityNames.length; i++){
      if (Objects.equals(cityNames[i], sourceS)) source = i;
      if (Objects.equals(cityNames[i], destinationS)) destination = i;
    }

    if (source == -1 || destination == -1) {
      if (source == -1) throw new CityNotFoundException(sourceS);
      else throw new CityNotFoundException(destinationS);
    }

    // read if there is an edge from source to destination
    boolean found = false;
    for (WeightedDirectedEdge e : G.adj(source)){
      if (e.to() == destination){
        e.weight = distance;
        e.price = price;
        found = true;
        break;
      }
    }

    // read if there is an edge from destination to source
    for (WeightedDirectedEdge e : G.adj(destination)){
      if (e.to() == source){
        e.weight = distance;
        e.price = price;
        found = true;
        break;
      }
    }

    return found;
  }



        /* ------------------ Other methods ------------------ */


  /**
  *  The <tt>Digraph</tt> class represents an directed graph of vertices
  *  named 0 through v-1. It supports the following operations: add an edge to
   *
  *  the graph, iterate over all of edges leaving a vertex.Self-loops are
  *  permitted.
  */
  private static class Digraph {
    private final int v;
    private int e;
    private LinkedList<WeightedDirectedEdge>[] adj;
    private boolean[] marked;  // marked[v] = is there an s-v path
    private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
    private int[] distTo;      // distTo[v] = number of edges shortest s-v path

    /**
    * Create an empty digraph with v vertices.
    */
    public Digraph(int v) {
      if (v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
      this.v = v;
      this.e = 0;
      @SuppressWarnings("unchecked")
      LinkedList<WeightedDirectedEdge>[] temp =
      (LinkedList<WeightedDirectedEdge>[]) new LinkedList[v];
      adj = temp;
      for (int i = 0; i < v; i++)
        adj[i] = new LinkedList<>();
    }

    /**
    * Add the edge e to this digraph.
    */
    public void addEdge(WeightedDirectedEdge edge) {
      int from = edge.from();
      adj[from].add(edge);
      e++;
    }


    /**
    * Return the edges leaving vertex v as an Iterable.
    * To iterate over the edges leaving vertex v, use foreach notation:
    * <tt>for (WeightedDirectedEdge e : graph.adj(v))</tt>.
    */
    public Iterable<WeightedDirectedEdge> adj(int v) {
      return adj[v];
    }

    public void bfs(int source) {
      marked = new boolean[this.v];
      distTo = new int[this.e];
      edgeTo = new int[this.v];

      Queue<Integer> q = new LinkedList<>();
      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      q.add(source);

      while (!q.isEmpty()) {
        int v = q.remove();
        for (WeightedDirectedEdge w : adj(v)) {
          if (!marked[w.to()]) {
            edgeTo[w.to()] = v;
            distTo[w.to()] = distTo[v] + 1;
            marked[w.to()] = true;
            q.add(w.to());
          }
        }
      }
    }

    public void dijkstras(int source, int destination) {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];

      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      int nMarked = 1;

      int current = source;
      while (nMarked < this.v) {
        for (WeightedDirectedEdge w : adj(current)) {
          if (distTo[current]+w.weight() < distTo[w.to()]) {
            edgeTo[w.to()] = current;
            distTo[w.to()] = distTo[current] + w.weight();
          }
        }
        //Find the vertex with minimim path distance
        //This can be done more effiently using a priority queue!
        int min = INFINITY;
        current = -1;

        for(int i=0; i<distTo.length; i++){
          if(marked[i])
            continue;
          if(distTo[i] < min){
            min = distTo[i];
            current = i;
          }
        }
   if (current == -1)
  {
    break;
  }
  else
  {
    marked[current] = true;

    nMarked++;
  }
      }
    }
  }


  /**
  *  The <tt>WeightedDirectedEdge</tt> class represents a weighted edge in an directed graph.
  */

  static class WeightedDirectedEdge {
    private final int v;
    private final int w;
    private int weight;
    private double price;

    /**
    * Create a directed edge from v to w with given weight.
    */
    public WeightedDirectedEdge(int v, int w, int weight, double price) {
      this.v = v;
      this.w = w;
      this.weight = weight;
      this.price = price;
    }

    public void setWeight(int weight) {
      this.weight = weight;
    }

    public void setPrice(double price) {
      this.price = price;
    }

    public int from(){
      return v;
    }

    public int to(){
      return w;
    }

    public int weight(){
      return weight;
    }

    public double price(){
      return price;
    }
  }
}
