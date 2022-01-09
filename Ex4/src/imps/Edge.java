package imps;

import api.EdgeData;

public class Edge implements EdgeData, Comparable<Edge>
{
    private int src;
    private int dest;
    private double weight;
    private int tag =0;
    private String info;

    /**
     * c'tor
     * @param src source node of the edge
     * @param dest destination node of an edge
     * @param weight the weight of the edge
     */
    public Edge(int src, int dest, double weight)
    {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
        info = "";
    }

    //Geters + Setters

    @Override
    public int getSrc()
    {
        return this.src;
    }

    @Override
    public int getDest()
    {
        return this.dest;
    }

    @Override
    public double getWeight()
    {
        return this.weight;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void setInfo(String s)
    {
        info = s;
    }

    @Override
    public int getTag()
    {
        return tag;
    }

    @Override
    public void setTag(int t)
    {
    this.tag = t;
    }


    @Override
    public int compareTo(Edge o)
    {
        if (this.src != o.src)
            return Integer.compare(this.src, o.src);
        return Integer.compare(this.dest, o.dest);
    }

    @Override
    public String toString()
    {
        return src + " --> " + dest + " : " + weight;
    }
}
