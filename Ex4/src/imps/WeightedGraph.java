package imps;

import api.DirectedWeightedGraph;
import api.EdgeData;
import api.NodeData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class WeightedGraph implements DirectedWeightedGraph
{
    private HashMap<Integer, NodeData> _nodeHash;                   // hash map for nodes - key int, node is the val
    private HashMap<Integer,HashMap<Integer, EdgeData>> _edgeHash;   // hash map for edges - key(1) src, key(2) dest, val Edges
    private HashSet<EdgeData> _edgeSet;                             // Hash set for edges, more comfortable
    private final HashMap<Integer, EdgeData> emptyEdgeMap = new HashMap<>();    // empty hash for iter null return
    private int _changes;                                           // num of changes in graph


    public WeightedGraph()
    {
        this._nodeHash = new HashMap<>();
        this._edgeHash = new HashMap<>();
        this._edgeSet = new HashSet<>();
        _changes = 0;
    }


    /**
     * copy c'tor deep copy
     * @param other
     */
    public WeightedGraph(DirectedWeightedGraph other)
    {
        this();
        Iterator<NodeData> itNode = other.nodeIter();
        while (itNode.hasNext())
        {
            NodeData node = itNode.next();
            this.addNode(new Node(node));
        }

        Iterator<EdgeData> itEdge = other.edgeIter();
        while (itEdge.hasNext())
        {
            EdgeData edge = itEdge.next();
            this.connect(edge.getSrc(), edge.getDest(), edge.getWeight());
        }
        _changes = other.getMC();
    }

    @Override
    public NodeData getNode(int key)
    {
        return _nodeHash.get(key);      // gets node in O(1)
    }

    @Override
    public EdgeData getEdge(int src, int dest)
    {
        return _edgeHash.get(src).get(dest);      // gets edge in O(1)
    }

    @Override
    public void addNode(NodeData n)
    {
        _nodeHash.put(n.getKey(), n);           // add node by NodeData and put it in hash map
        _changes++;
    }

    public void addNode(int n)                  // add node by int
    {
        _nodeHash.put(n, new Node(n));
        _changes++;
    }

    /**
     * connect the nodes - creating a new edge
     * @param src - the source of the edge.
     * @param dest - the destination of the edge.
     * @param w - positive weight representing the cost (aka time, price, etc) between src-->dest.
     */
    @Override
    public void connect(int src, int dest, double w)
    {
        EdgeData edge = new Edge(src, dest, w);
        if(!_edgeHash.containsKey(src))
        {
            _edgeHash.put(src, new HashMap<>());    // if key doesn't exist then put the key
        }
        _edgeHash.get(src).put(dest, edge);
        _edgeSet.add(edge);
        _changes++;     // changes has been made
    }

    @Override
    public Iterator<NodeData> nodeIter()
    {
        return _nodeHash.values().iterator();       // return iterator of the nodes
    }

    @Override
    public Iterator<EdgeData> edgeIter()
    {
        return _edgeSet.iterator();
    }

    @Override
    public Iterator<EdgeData> edgeIter(int node_id)
    {
        if (_edgeHash.containsKey(node_id))
        {
            return _edgeHash.get(node_id).values().iterator();
        }

        return (emptyEdgeMap.values().iterator());      // return an empty iterator
    }

    @Override
    public NodeData removeNode(int key)
    {
        if (!_nodeHash.containsKey(key))
            return null;

        _edgeHash.remove(key);
        // before removing the node we must first to remove all of the edges that connected to the node
        for (int node : _nodeHash.keySet())
        {
            removeEdge(node, key);
        }
        _changes++;
        return _nodeHash.remove(key);
    }

    @Override
    public EdgeData removeEdge(int src, int dest)
    {
        //if doesn't exist return null
        if (!(_edgeHash.containsKey(src)) || !(_edgeHash.get(src).containsKey(dest)))
            return null;

        EdgeData edge = _edgeHash.get(src).remove(dest);

        // if nothig lent remove the src from keys
        if (_edgeHash.get(src).isEmpty())
        {
            _edgeHash.remove(src);
        }
        _edgeSet.remove(edge);
        _changes++;     //change has been made
        return edge;
    }

    @Override
    public int nodeSize()
    {
        return _nodeHash.size();
    }

    @Override
    public int edgeSize()
    {
        return _edgeSet.size();
    }

    @Override
    public int getMC()
    {
        return _changes;
    }
}
