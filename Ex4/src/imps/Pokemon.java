package imps;

import api.GeoLocation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ex4_java_client.StudentCode;

import java.util.HashMap;
import java.util.Map;

// struct to pokemon
public class Pokemon
{
    private double value;
    private int type;
    private GeoLocation pos;
    private int[] onNodes;
    private static int id = 0;
    private int currId;
    private int alloc;
    private double timeBest;

    // @param: index -> this index means which pokemon to take from pokemons
    public Pokemon(String pokemonsINFO ,int index)
    {
        // load pokemon details from json
        JsonObject jsonObject;
        jsonObject = new JsonObject();
        JsonParser jp = new JsonParser();
        jsonObject = (JsonObject) jp.parse(pokemonsINFO);

        JsonArray agentsArray = (JsonArray) jsonObject.get("Pokemons");
        JsonObject agent = (JsonObject) agentsArray.get(index);
        JsonObject details = agent.getAsJsonObject("Pokemon");
        this.value = details.get("value").getAsDouble();
        this.type = details.get("type").getAsInt();
        this.pos = new GeoLocationImp(details.get("pos").getAsString());
        onNodes = null;
        currId = id++;
        alloc = -1;
        timeBest = Double.MAX_VALUE;
    }

    // allocate pokemon to specific agent
    public void allocateToAgent(Agents agents)
    {
        if (onNodes == null)
        {
            onNodes = StudentCode.algo.getNodesOfLocation(pos, type);
        }

        double minTime = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < agents.size(); i++)
        {
            Agent agent = agents.getAgent(i);
            if (onNodes == null)
            {
                continue;
            }
            double currW = StudentCode.algo.shortestPathDist(agent.getSrc(), onNodes[0]);
            if (onNodes.length == 2 && currW >= 0)
            {
                currW += StudentCode.algo.shortestPathDist(onNodes[0], onNodes[1]);
            }

            if (currW >= 0)
                currW /= agent.getSpeed();

            if (currW >= 0 && currW < minTime && agent.getPokemon() == null)
            {
                index = i;
                minTime = currW;
            }
        }

        if (index < 0)
            return;

        Agent agent = agents.getAgent(index);
        Pokemon agentPoke = agent.getPokemon();

        if (agentPoke == null)
            agent.setPokemon(this);

        alloc = index;
        timeBest = minTime;

    }

    // ################ GETTERS AND SETTERS ##################
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    public GeoLocation getPos() { return pos; }
    public void setPos(GeoLocation pos) { this.pos = pos; }

    public int getId()
    {
        return currId;
    }

    public void setAlloc(int alloc) {
        this.alloc = alloc;
    }
    public double getTimeBest() {
        return timeBest;
    }

    public void setTimeBest(double timeBest) {
        this.timeBest = timeBest;
    }

    public int getAlloc() {
        return alloc;
    }

    public int[] getOnNodes() {
        return onNodes;
    }

    public void setOnNodes(int[] onNodes) {
        this.onNodes = onNodes;
    }
// #######################################################
}
