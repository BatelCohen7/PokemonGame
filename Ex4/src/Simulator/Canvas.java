package Simulator;

import api.*;
import imps.*;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Line2D;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

public class Canvas extends JComponent
{
    private Line2D line = new Line2D.Double();
    private Polygon tri = new Polygon();
    private HashMap<Integer, JLabel> nodes;
    private HashMap<Integer, HashMap<Integer, Double>> edges;
    private static int LEN = GraphZone.LEN;
    private DirectedWeightedGraphAlgorithms _algorithm;
    private GeoLocation _ratioAxis;
    private GeoLocation _startPoint;
    private static final double arwHead = 6.0;
    private static final double radiusCut = Math.sqrt(Math.pow(LEN/2.0, 2) - Math.pow(arwHead, 2));
    private HashSet<String> specialEdges;


    /**
     * c'tor for the Canvas - Canvas is the area that has all the nodes and edges
     * @param x x location
     * @param y y location
     * @param w width
     * @param h height
     * @param algorithm the algorithm object to perform algorithms
     */
    public Canvas(int x, int y, int w, int h, DirectedWeightedGraphAlgorithms algorithm)
    {
        setBounds(x + LEN / 2, y + LEN / 2, w - LEN, h - LEN);
        System.out.println();
        nodes = new HashMap<>();
        edges = new HashMap<>();
        _algorithm = algorithm;
        setRatioPoins();                // sets the points and seting the ratio according to the 3d locations
        specialEdges = new HashSet<>();
    }

    /**
     * set the path according to the nodes order, only the path will be shown
     * @param nodes orderd nodes
     */
    public void setPath(List<NodeData> nodes)
    {
        specialEdges.clear();
        for (int i = 0; i < nodes.size() - 1; i++)
        {
            specialEdges.add(nodes.get(i) + "," + nodes.get(i + 1));     // set the edge for each 2 nodes in list
        }
        repaint(); // draw the graph again
    }

    public void removePath()
    {
        specialEdges.clear();
    }

    //get the point that starts by the original axis
    public GeoLocation get_startPoint()
    {
        return new GeoLocationImp(_startPoint);
    }

    /**
     * get the first and last position on the original graph
     * @return
     */
    private GeoLocation[] getRangeNodes()
    {
        double[][] range = new double[][] {{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE},
                {Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE}};

        Iterator<NodeData> itNode = _algorithm.getGraph().nodeIter();
        while (itNode.hasNext())
        {
            GeoLocation geo = itNode.next().getLocation();
            range[0][0] = Math.min(range[0][0], geo.x());
            range[0][1] = Math.min(range[0][1], geo.y());
            range[0][2] = Math.min(range[0][2], geo.z());

            range[1][0] = Math.max(range[1][0], geo.x());
            range[1][1] = Math.max(range[1][1], geo.y());
            range[1][2] = Math.max(range[1][2], geo.z());
        }

        return new GeoLocation[]{new GeoLocationImp(range[0][0], range[0][1], range[0][2]),
                                    new GeoLocationImp(range[1][0], range[1][1], range[1][2])};
    }

    /**
     * set the ratio by the canvas size and start ending points
     */
    public void setRatioPoins()
    {
        GeoLocation[] minMaxPoints = getRangeNodes();
        double dx = (minMaxPoints[1].x() - minMaxPoints[0].x()) / (getWidth() - 2*LEN);
        double dy = (minMaxPoints[1].y() - minMaxPoints[0].y()) / (getHeight() - 2 *LEN);
        _ratioAxis = new GeoLocationImp(dx, dy, 0);
        _startPoint = minMaxPoints[0];
    }

    /**
     * gets the location on GUI by a 3d location
     * @param location
     * @return
     */
    public Point getPointFromGeo(GeoLocation location)
    {
        double x = location.x(), y = location.y();
        //computes based on how far from start axis points
        Point p = new Point((int)((x - _startPoint.x()) / _ratioAxis.x()), (int)((y-_startPoint.y()) / _ratioAxis.y()));
        p.setLocation(p.x, getHeight() - p.y - 2*LEN);
        return p;
    }

    /**
     * gets the 3d location from position on the GUI
     * @param p
     * @return
     */
    public GeoLocation getGeoFromPoint(Point p)
    {
        double x = p.x * _ratioAxis.x() + _startPoint.x();
        double y = (getHeight() - p.y - 2*LEN) * _ratioAxis.y() + _startPoint.y();

        return new GeoLocationImp(x, y, 0);
    }

    /**
     * set a line according to two points - used for arrow body
     * @param p1
     * @param p2
     */
    public void setLine(Point p1, Point p2)
    {
        line = new Line2D.Double(p1, p2);
    }

    /**
     * set a triangle by 3 points - used for arrow head
     * @param p1
     * @param p2
     * @param p3
     */
    public void setTri(Point p1, Point p2, Point p3)
    {
        tri = new Polygon();
        tri.addPoint(p1.x, p1.y);
        tri.addPoint(p2.x, p2.y);
        tri.addPoint(p3.x, p3.y);
    }

    /**
     * set arrow between 2 nodes (as their labels)
     * @param node1
     * @param node2
     * @param w weight of edge
     */
    public void setArrow(JLabel node1, JLabel node2, double w)
    {
        try
        {
            int src = Integer.parseInt(node1.getName());        // gets the src node id
            int dest = Integer.parseInt(node2.getName());       // gets the src node id
            nodes.put(src, node1);                              // puts the src and dest in the nodes hash
            nodes.put(dest, node2);
            if (!edges.containsKey(src))
            {
                edges.put(src, new HashMap<>());
            }
            edges.get(src).put(dest, w);
        }
        catch (Exception ex) {}     // if had some troubles with parsing

    }

    /**
     * gets the point that far from a given point by some length and degree of beta
     * @param point
     * @param beta
     * @param dist
     * @return
     */
    public Point getNearRadPoint(Point point, double beta, double dist)
    {
        Point p = new Point(point);
        p.translate((int)(Math.cos(beta)*dist), (int)(Math.sin(beta)*dist));
        return p;
    }

    /**
     * if the nodes has 2 sided edges, then print then seperatly that the weight can be seen
     * @param src
     * @param dest
     * @param canvas
     */
    public void printDoubeEdge(int src, int dest, Graphics2D canvas)
    {
        JLabel node1 = nodes.get(src);
        JLabel node2 = nodes.get(dest);
        // getting both of their centers
        Point center1 = node1.getLocation();
        int r = LEN / 2;
        center1.translate(r, r);
        Point center2 = node2.getLocation();
        center2.translate(r, r);

        Point original1 = new Point(center1);
        Point original2 = new Point(center2);


        double angle = Math.atan2(center2.y - center1.y, center2.x - center1.x);
        double beta = angle + Math.toRadians(90);       //  print 90 deg more then the curr deg
        center1.translate((int)(Math.cos(beta)*arwHead), (int)(Math.sin(beta)*arwHead));
        center2.translate((int)(Math.cos(beta)*arwHead), (int)(Math.sin(beta)*arwHead));
        canvas.setColor(specialEdges.contains(src + "," + dest) ? Color.BLUE : Color.BLACK);
        // if there is no path to be printed, or if it's indeed part of path then print it
        setPartsOfArrow(canvas, radiusCut, center2, center1, edges.get(dest).get(src));
        //drawString(canvas, center1, center2, true, edges.get(dest).get(src));


        beta = angle - Math.toRadians(90);  // print 90 deg less then the original deg
        center1 = new Point(original1);
        center2 = new Point(original2);
        center1.translate((int)(Math.cos(beta)*arwHead), (int)(Math.sin(beta)*arwHead));
        center2.translate((int)(Math.cos(beta)*arwHead), (int)(Math.sin(beta)*arwHead));
        canvas.setColor(specialEdges.contains(dest + "," + src) ? Color.BLUE : Color.BLACK);
        // if there is no path to be printed, or if it's indeed part of path then print it
        setPartsOfArrow(canvas, radiusCut, center1, center2, edges.get(dest).get(src));
        //drawString(canvas, center1, center2, true, edges.get(dest).get(src));
    }

    /**
     * print single arrow
     * @param src
     * @param dest
     * @param canvas
     */
    public void printArrow(int src, int dest, Graphics2D canvas)
    {
        JLabel node1 = nodes.get(src);
        JLabel node2 = nodes.get(dest);
        Point center1 = node1.getLocation();
        int r = LEN / 2;
        center1.translate(r, r);                // the center is half of the square
        Point center2 = node2.getLocation();
        center2.translate(r, r);

        canvas.setColor(specialEdges.contains(src + "," + dest) ? Color.BLUE : Color.BLACK);
        // if there is no path to be printed, or if it's indeed part of path then print it
        setPartsOfArrow(canvas, r, center1, center2, edges.get(src).get(dest));
        //drawString(canvas, center1, center2, true, edges.get(src).get(dest));

    }

    /**
     * by given 2 points set weight as string on canvas from some side(Up or Down), in the middle
     * @param canvas
     * @param center1
     * @param center2
     * @param isUp
     * @param w
     */
    public void drawString(Graphics2D canvas, Point center1, Point center2, boolean isUp, double w)
    {
        String num = (float) Math.round(w * 100) / 100 + ""; // get only 2 digits after point

        // settings of the string that to be printed
        AttributedString attr = new AttributedString(num);
        attr.addAttribute(TextAttribute.BACKGROUND, Color.white);
        attr.addAttribute(TextAttribute.SIZE, 8);

        // copmute where it must be
        double alpha = Math.atan2(center2.y - center1.y, center2.x - center1.x);    // get the degree
        alpha += (isUp ? Math.toDegrees(90) : Math.toDegrees(-90));                 // if its up add 90 deg, else sub 90 deg
        Point p1 = getNearRadPoint(center1, alpha, arwHead);                        // get the first new point with some dost from it
        Point p2 = getNearRadPoint(center2, alpha, arwHead);                        //same as above
        canvas.drawString(attr.getIterator() , (p1.x + p2.x - num.length() * 4)/2, (p1.y + p2.y)/2);
        // set it in the middle of those 2 points
    }

    /**
     * by given radius of the shape, 2 points and weight, the method sets to the drawing area the body of the arrow,
     * the arrow head and its weight
     * @param canvas
     * @param r
     * @param center1
     * @param center2
     * @param w
     */
    public void setPartsOfArrow(Graphics2D canvas, double r, Point center1, Point center2, double w)
    {
        double dist = center1.distance(center2);
        double angle = Math.atan2(center2.y - center1.y, center2.x - center1.x);

        Point p4 = new Point(center1);
        p4.translate((int)((dist-r)*Math.cos(angle)), (int)((dist-r)*Math.sin(angle)));
        // get the point from the start point on the same line with dist of (dist - r)

        dist = center1.distance(p4) - 10; // sets the length to set the next points
        double deg = Math.abs(Math.toDegrees(Math.asin(arwHead/dist)));
        double alpha = Math.toDegrees(Math.atan2(p4.y - center1.y, p4.x - center1.x));
        double beta = Math.toRadians(alpha + deg);
        Point pTri1 = new Point(center1);
        pTri1.translate((int)(Math.cos(beta)*dist), (int)(Math.sin(beta)*dist));    // set the upper point of triangle
        beta = Math.toRadians(alpha - deg);
        Point pTri2 = new Point(center1);
        pTri2.translate((int)(Math.cos(beta)*dist), (int)(Math.sin(beta)*dist));    // set the lower side of the triangle

        setLine(center1, center2);      // set the body
        setTri(p4, pTri1, pTri2);       // sets the arrow head
        canvas.draw(line);              // draw the body
        canvas.fill(tri);               // draw the head

    }


    /**
     * every time that nedds to repainted the function is called
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        // a 2D graph
        Graphics2D canvas = (Graphics2D) g;
        canvas.setColor(Color.BLACK);
        canvas.setStroke(new BasicStroke(2)); //make the arrow more bold

        HashSet<String> edgesStr = new HashSet<>();
        for (int src: edges.keySet())
        {
            for (int dest: edges.get(src).keySet())
            {
                String currStr = src + "," + dest;
                if (edges.containsKey(dest) && edges.get(dest).containsKey(src))
                {
                    // complete double check
                    if (!edgesStr.contains(currStr))
                    {
                        printDoubeEdge(src, dest, canvas);  // if the opposite also exists than print as double
                        edgesStr.add(dest + "," + src);     // add the curr edge as string
                    }
                }
                else
                {
                    printArrow(src, dest, canvas);      // in any case print as single arrow
                }

                edgesStr.add(currStr);

            }
        }
        removePath();   // after printing set path to default

    }
}
