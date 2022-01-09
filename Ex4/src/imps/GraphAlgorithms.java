package imps;

import api.*;
import com.google.gson.Gson;
import ex4_java_client.StudentCode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class Realized a Directed (positive) Weighted Graph Theory Algorithms including:
 * 0. init(graph);
 * 1. getGraph();
 * 2. copy;
 * 3. double shortestPathDist(int src, int dest);
 * 4. List<NodeData> shortestPath(int src, int dest);
 * 5. save(file); // JSON file
 * 6. load(file); // JSON file
 * 7. loadFromStr(String strJson);
 * 8. HashMap<Integer, Integer> dijkstra(int src, int dest);
 * 9. allPairsShoretestPath();
 * 10. setPairShortest(int src, int dest);
 * 11. getNodesOfLocation(GeoLocation p, int type);
 * 12. setWeightPair(int src, int dest, double weight);
 * 13. setRoutePair(int src, int dest, List<NodeData> path);
 * 14. getNodesOfLocation(GeoLocation p, int type);
 * 15. equalsDEps(double a, double b);
 * 16. isOnLine(GeoLocation a, GeoLocation b, GeoLocation p);
 */

public class GraphAlgorithms implements DirectedWeightedGraphAlgorithms
{
    private DirectedWeightedGraph _g;
    private HashMap<Integer, HashMap<Integer, List<NodeData>>> _bestRoutes;
    private HashMap<Integer, HashMap<Integer, Double>> _bestWeigths;
    static final double EPS = 0.00000001;


    public void GraphAlgorithms(String strJson)
    {
        _bestRoutes = new HashMap<>();
        _bestWeigths = new HashMap<>();
        loadFromStr(strJson);
    }

    /**
     * Inits the graph on which this set of algorithms operates on.
     * @param g
     */
    @Override
    public void init(DirectedWeightedGraph g)
    {
        _g = g;
        _bestRoutes = new HashMap<>();
        _bestWeigths = new HashMap<>();
        allPairsShoretestPath();
    }

    @Override
    public DirectedWeightedGraph getGraph()
    {
        return _g;
    }

    @Override
    public DirectedWeightedGraph copy()
    {
        if (_g == null)
            return null;
        return new WeightedGraph(_g);
    }

    // return the distance of the shortest path
    @Override
    public double shortestPathDist(int src, int dest)
    {
        if (!_bestWeigths.containsKey(src) || !_bestWeigths.get(src).containsKey(dest))
            return -1;

        return (_bestWeigths.get(src).get(dest));
    }

    @Override
    public List<NodeData> shortestPath(int src, int dest)
    {
        if (!_bestRoutes.containsKey(src) || !_bestRoutes.get(src).containsKey(dest))
            return new ArrayList<>();

        return (_bestRoutes.get(src).get(dest));
    }

    // save nodes and edge lists to json file
    @Override
    public boolean save(String file)
    {
        Gson gson = new Gson();
        try
        {
            ArrayList<Map<String, Object>> nodes = new ArrayList<>();
            Iterator<NodeData> itNodes = _g.nodeIter();
            while (itNodes.hasNext())
            {
                NodeData node = itNodes.next();
                Map<String, Object> mapNode = new HashMap<>();
                mapNode.put("id", node.getKey());
                mapNode.put("pos", node.getLocation().toString());
                nodes.add(mapNode);
            }

            ArrayList<Map<String, Object>> edges = new ArrayList<>();
            Iterator<EdgeData> itEdges = _g.edgeIter();
            while (itEdges.hasNext())
            {
                EdgeData edge = itEdges.next();
                Map<String, Object> mapEdge = new HashMap<>();
                mapEdge.put("src", edge.getSrc());
                mapEdge.put("w", edge.getWeight());
                mapEdge.put("dest", edge.getDest());
                edges.add(mapEdge);
            }

            Map<String, Object> contentToJson = new HashMap<>();
            contentToJson.put("Nodes", nodes);
            contentToJson.put("Edges", edges);
            String jsonData = gson.toJson(contentToJson);

            Files.writeString(Paths.get(file), jsonData);

        }
        catch (Exception ex)
        {
            System.err.println(ex.toString());
            return false;
        }
        return true;
    }

    // load string file
    @Override
    public boolean load(String file)
    {
        _g = new WeightedGraph();
        Gson gson = new Gson();
        try
        {
            String strJson = Files.readString(Paths.get(file));
            Map<String, Object> jsonMap = gson.fromJson(strJson, Map.class);
            if (jsonMap.containsKey("Nodes"))
            {
                ArrayList<Map<String,Object>> arrNodes = (ArrayList)jsonMap.get("Nodes");
                for (Map<String, Object> nodeMap: arrNodes)
                {
                    double id = (double)nodeMap.get("id");
                    _g.addNode(new Node((int)id, (String)nodeMap.get("pos")));
                }
            }
            if (jsonMap.containsKey("Edges"))
            {
                ArrayList<Map<String,Object>> arrEdges = (ArrayList)jsonMap.get("Edges");
                for (Map<String, Object> edgeMap: arrEdges)
                {
                    double src = (double)edgeMap.get("src");
                    double dest = (double)edgeMap.get("dest");
                    double w = (double)edgeMap.get("w");
                    _g.connect((int)src, (int)dest, w);
                }
            }
        }
        catch (Exception ex)
        {
            System.err.println(ex.toString());
            return false;
        }
        allPairsShoretestPath();
        return true;
    }

    // load json file from String strJson
    public boolean loadFromStr(String strJson)
    {
        _g = new WeightedGraph();
        Gson gson = new Gson();
        try
        {
            Map<String, Object> jsonMap = gson.fromJson(strJson, Map.class);
            if (jsonMap.containsKey("Nodes"))
            {
                ArrayList<Map<String,Object>> arrNodes = (ArrayList)jsonMap.get("Nodes");
                for (Map<String, Object> nodeMap: arrNodes)
                {
                    double id = (double)nodeMap.get("id");
                    _g.addNode(new Node((int)id, (String)nodeMap.get("pos")));
                }
            }
            if (jsonMap.containsKey("Edges"))
            {
                ArrayList<Map<String,Object>> arrEdges = (ArrayList)jsonMap.get("Edges");
                for (Map<String, Object> edgeMap: arrEdges)
                {
                    double src = (double)edgeMap.get("src");
                    double dest = (double)edgeMap.get("dest");
                    double w = (double)edgeMap.get("w");
                    _g.connect((int)src, (int)dest, w);
                }
            }
        }
        catch (Exception ex)
        {
            System.err.println(ex.toString());
            return false;
        }
        allPairsShoretestPath();
        return true;
    }

    // dijkstra algorithm to find the Shoretest Path
    private HashMap<Integer, Integer> dijkstra(int src, int dest)
    {
        HashMap<Integer, Double> dist = new HashMap<>();
        HashMap<Integer, Integer> prev = new HashMap<>();
        HashSet<Integer> Q = new HashSet<>();
        prev.put(src, -1);


        Iterator<NodeData> itNode = _g.nodeIter();
        while (itNode.hasNext())
        {
            int v = itNode.next().getKey();
            NodeData node = _g.getNode(v);
            if (v != src)
                dist.put(v, -1.0);

            Q.add(v);
        }
        dist.put(src, 0.0);

        while (!Q.isEmpty())
        {
            int u = -1;
            double maxD = Double.MAX_VALUE;
            Iterator<Integer> it = Q.iterator();
            while (it.hasNext())
            {
                int node = it.next();
                if (0 <= dist.get(node) && dist.get(node) < maxD)
                {
                    u = node;
                    maxD = dist.get(node);
                }
            }

            Q.remove(u);

            if (u == dest)
                return prev;

            Iterator<EdgeData> itEdge = _g.edgeIter(u);
            while (itEdge.hasNext())
            {
                EdgeData edge = itEdge.next();
                int v = edge.getDest();
                if (dist.get(u) >= 0 && Q.contains(v))
                {
                    double alt = dist.get(u) + edge.getWeight();
                    if (dist.get(v) < 0 || alt < dist.get(v))
                    {
                        dist.put(v, alt);
                        prev.put(v, u);
                    }
                }
            }
        }
        return null;
    }

    // find the shortest path between tow nodes
    private void allPairsShoretestPath()
    {
        _bestRoutes = new HashMap<>();
        _bestWeigths = new HashMap<>();

        HashSet<Integer> nodes = new HashSet<>();
        Iterator<NodeData> itNode = _g.nodeIter();
        while(itNode.hasNext())
        {
            nodes.add(itNode.next().getKey());
        }
        for (int src: nodes)
        {
            for (int dest: nodes)
            {
                setPairShortest(src, dest);
            }
        }
    }

    // set to the shortest path weight and path to the maps for O(1) use
    private void setPairShortest(int src, int dest)
    {

        if (src == dest)
        {
            setWeightPair(src, dest, 0);
            setRoutePair(src, dest, new ArrayList<>());
            return;
        }

        HashMap<Integer, Integer> prevMap = dijkstra(src, dest);
        if (prevMap == null || !prevMap.containsKey(dest))
            return;

        setWeightPair(src, dest, _g.getNode(dest).getWeight());
        List<NodeData> path = new ArrayList<>();
        int u = dest;
        while (prevMap.containsKey(u))
        {
            path.add(0, _g.getNode(u));
            u = prevMap.get(u);
        }
        setRoutePair(src, dest, path);
    }

    // find the best weight of rotes for two nodes
    private void setWeightPair(int src, int dest, double weight)
    {
        if (!_bestWeigths.containsKey(src))
        {
            _bestWeigths.put(src, new HashMap<>());
        }
        _bestWeigths.get(src).put(dest, weight);
    }

    // find the best rotes for two nodes
    private void setRoutePair(int src, int dest, List<NodeData> path)
    {
        if (!_bestRoutes.containsKey(src))
        {
            _bestRoutes.put(src, new HashMap<>());
        }
        _bestRoutes.get(src).put(dest, path);
    }

    //Find the closest nodes to the location
    public int[] getNodesOfLocation(GeoLocation p, int type)
    {
        List<Integer> nodes = new ArrayList<>();
        Iterator<NodeData> itNodes = _g.nodeIter();
        while (itNodes.hasNext())
        {
            NodeData node = itNodes.next();
            if (node.getLocation().equals(p))
            {
                return new int[]{node.getKey()};
            }
            nodes.add(node.getKey());
        }

        for (int src: nodes)
        {
            GeoLocation pSrc = _g.getNode(src).getLocation();
            Iterator<EdgeData> itEdge = _g.edgeIter(src);
            while (itEdge.hasNext())
            {
                int dest = itEdge.next().getDest();
                if (type > 0 && src > dest || type < 0 && src < dest)
                    continue;

                GeoLocation pDest = _g.getNode(dest).getLocation();
                if (isOnLine(pSrc, pDest, p))
                {
                    return new int[] {src, dest};
                }

            }
        }
        return null;
    }

    static boolean equalsDEps(double a, double b)
    {
        return (Math.abs(a - b) <= EPS);
    }

    public static boolean isOnLine(GeoLocation a, GeoLocation b, GeoLocation p)
    {
        double apDist = a.distance(p);
        double pbDist = b.distance(p);
        double originalDist = a.distance(b);

        return equalsDEps(apDist + pbDist, originalDist);
    }

//    public static void main(String[] args) {
//
//        GraphAlgorithms algo = new GraphAlgorithms();
//        System.out.println(algo.load("data/A0.json"));
//        //algo.dijkstra(0, 2);
//        int[] arr = algo.getNodesOfLocation(new GeoLocationImp("35.197656770719604,32.10191878639921,0.0"), -1);
//        System.out.println(Arrays.toString(arr));
//        System.out.println(algo.shortestPathDist(0, 9) + algo.shortestPathDist(9, 8));
//        System.out.println(algo.shortestPathDist(0, 8) + algo.shortestPathDist(8, 9));
//        System.out.println(algo.shortestPath(1, 4));
//        System.out.println(algo.shortestPath(8, 4));
//
//    }
}
