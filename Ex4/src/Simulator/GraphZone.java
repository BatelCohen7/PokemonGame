package Simulator;

import api.*;
import imps.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static java.lang.Thread.sleep;

public class GraphZone
{
    public static final int LEN = 25;
    public static final int SIZE_AP = 50;
    private JPanel panel;
    //sets the circle shape
    private final ImageIcon circle = new ImageIcon(this.getClass().getResource("/Simulator/circleS.png"));
    private final ImageIcon cForShow = new ImageIcon(circle.getImage().getScaledInstance(LEN, LEN, Image.SCALE_SMOOTH));
    private final ImageIcon pokPic = new ImageIcon(this.getClass().getResource("/Simulator/pokemon.png"));
    private final ImageIcon sizedPok = new ImageIcon(pokPic.getImage().getScaledInstance(SIZE_AP, SIZE_AP, Image.SCALE_SMOOTH));
    private final ImageIcon ash1 = new ImageIcon(this.getClass().getResource("/Simulator/ash1.png"));
    private ImageIcon ash1Sized;
    private HashMap<Integer, JLabel> shpeNode;      // images of circles
    private HashMap<Integer, JLabel> labelNode;     // text of nodes
    private Canvas canvas;
    private DirectedWeightedGraphAlgorithms _algorithm;
    private DirectedWeightedGraph _graph;
    private int _x, _y, _w, _h;
    private HashMap<Integer, JLabel> AgentsInGraph;
    private JLabel[] PokemonsInGraph;



    public GraphZone(DirectedWeightedGraphAlgorithms algorithm, Agents agents, int x, int y, int w, int h, JPanel panel)
    {
        double scaleW = ((double)ash1.getIconWidth()/ash1.getIconHeight()) * SIZE_AP;
        ash1Sized = new ImageIcon(ash1.getImage().getScaledInstance((int)scaleW, SIZE_AP, Image.SCALE_REPLICATE));
        shpeNode = new HashMap<>();
        labelNode = new HashMap<>();
        _algorithm = algorithm;
        _graph = algorithm.copy();
        this.panel = panel;
        PokemonsInGraph = null;
        AgentsInGraph = new HashMap<>();

        _x = x;
        _y = y;
        _w = w;
        _h = h;

        canvas = new Canvas(_x, _y, _w, _h, _algorithm);

        this.panel.add(canvas);

        // first set all agents on graph
        for (int i = 0; i < agents.size(); i++)
        {
            setAgentsOnGraph(agents.getAgent(i));
        }
        paintAllNodesEdges();       // reset all the arrows and nodes
        canvas.setVisible(true);
        canvas.repaint();
    }

    public void setAgentsOnGraph(Agent agent)
    {
        Point p = canvas.getPointFromGeo(agent.getPos());
        if (AgentsInGraph.containsKey(agent.getId()))
        {
            AgentsInGraph.get(agent.getId()).setLocation(p);
        }
        else
        {
            JLabel agentPic = new JLabel("");
            agentPic.setIcon(ash1Sized);
            agentPic.setSize(SIZE_AP, SIZE_AP);
            agentPic.setLocation(p);
            canvas.add(agentPic);
            AgentsInGraph.put(agent.getId(), agentPic);
        }
    }

    public void setPokemonsOnGraph(Pokemons pokemons)
    {
        JLabel[] oldPok = PokemonsInGraph;

        PokemonsInGraph = new JLabel[pokemons.size()];
        for (int i = 0; i < PokemonsInGraph.length; i++)
        {
            JLabel PokemonPic = new JLabel("");
            PokemonPic.setSize(SIZE_AP, SIZE_AP);
            Point p1 = canvas.getPointFromGeo(pokemons.getPokemon(i).getPos());
            PokemonPic.setIcon(sizedPok);
            PokemonPic.setLocation(p1);
            PokemonPic.setVisible(true);
            PokemonsInGraph[i] = PokemonPic;
            canvas.add(PokemonPic);
        }

        if (oldPok != null)
        {
            for (int i = 0; i < oldPok.length; i++)
            {
                oldPok[i].setVisible(false);
            }
        }

    }



    public void paintAllNodesEdges()
    {
        this.shpeNode.clear();
        this.labelNode.clear();
        canvas = new Canvas(_x, _y, _w, _h, _algorithm);    // make new one
        panel.add(canvas);

        Iterator<NodeData> itNode = _algorithm.getGraph().nodeIter();
        while (itNode.hasNext())
        {
            makeVer(itNode.next(), LEN);        // show all the nodes in the canvas
        }
        Iterator<EdgeData> itEdge = _algorithm.getGraph().edgeIter();

        while (itEdge.hasNext())
        {
            EdgeData edge = itEdge.next();
            // set each edge as arrow
            canvas.setArrow(shpeNode.get(edge.getSrc()), shpeNode.get(edge.getDest()), edge.getWeight());
        }
        // paint the settings
        canvas.repaint();
    }


    /**
     * create a visible node with text for ant new node
     * @param node
     * @param len width/height of the shape
     */
    public void makeVer(NodeData node, int len)
    {
        Point p = canvas.getPointFromGeo(node.getLocation());
        makeVer(node.getKey(), p.x, p.y, len);
    }

    /**
     * create a visible node with text for ant new node
     * @param num node id
     * @param x location x
     * @param y location y
     * @param len width of shape
     */
    public void makeVer(int num, int x, int y, int len)
    {
        String numStr = "" + num;
        // resize the circle
        ImageIcon partOf = new ImageIcon(circle.getImage().getScaledInstance(LEN, LEN, Image.SCALE_SMOOTH));

        //create the circle
        JLabel shape = new JLabel(partOf);
        shape.setBounds(x, y, len, len);

        //create the text of the node (node id)
        JLabel lbOnShape = new JLabel(numStr, SwingConstants.CENTER);
        lbOnShape.setBounds(x, y, len, len);


        // more easy to handle with names
        shape.setName(numStr);
        lbOnShape.setName(numStr);

        //set to the DS and to the visual component
        this.shpeNode.put(num, shape);
        this.labelNode.put(num, lbOnShape);
        canvas.add(lbOnShape);
        canvas.add(shape);
    }

    /**
     * gets the minimal point in axis
     * @return
     */
    public GeoLocation getStartPoint()
    {
        return canvas.get_startPoint();
    }


}