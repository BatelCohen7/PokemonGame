package imps;

import api.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOError;
import java.io.IOException;


public class Agent {
    private int id,src,dest;
    private double value,speed;
    private GeoLocation pos;
    private Pokemon pokemon;

    // @param: id -> this id means which agent to take from agents
    public Agent(String agentsINFO, int id)
    {
        // load agent details from json
        JsonObject jsonObject;
        jsonObject = new JsonObject();
        JsonParser jp = new JsonParser();
        jsonObject = (JsonObject) jp.parse(agentsINFO);

        JsonArray agentsArray = (JsonArray) jsonObject.get("Agents");
        JsonObject agent = (JsonObject) agentsArray.get(id);
        JsonObject details = agent.getAsJsonObject("Agent");
        this.id = details.get("id").getAsInt();
        this.src = details.get("src").getAsInt();
        this.dest = details.get("dest").getAsInt();
        this.value = details.get("value").getAsDouble();
        this.speed = details.get("speed").getAsDouble();
        this.pos = new GeoLocationImp(details.get("pos").getAsString());
        pokemon = null;
    }


    //##################### GETTERS AND SETTERS ######################

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSrc() { return src; }
    public void setSrc(int src) { this.src = src; }
    public int getDest() { return dest; }
    public void setDest(int dest) { this.dest = dest; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public GeoLocation getPos() { return pos; }
    public void setPos(GeoLocation pos) { this.pos = pos; }
    public void setPokemon(Pokemon pokemon)
    {
        this.pokemon = pokemon;
    }
    public Pokemon getPokemon() {
        return pokemon;
    }

    // ##############################################################


    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", src=" + src +
                ", dest=" + dest +
                ", value=" + value +
                ", speed=" + speed +
                ", pos=" + pos +
                '}';
    }

}
