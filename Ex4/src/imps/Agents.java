package imps;

import ex4_java_client.StudentCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * class to get all the agens in the game
 */
public class Agents
{
    private HashMap<Integer, Agent> _agents;

    public Agents()
    {
        _agents = new HashMap<>();
    }

    public Agents(String str)
    {
        _agents = new HashMap<>();
        update(str);

    }

    public int size()
    {
        return _agents.size();
    }

    public void update(String str)
    {
        if (str == null)
            return;
        int len = str.split("\"Agent\"").length - 1;
        for (int i = 0; i < len; i++)
        {
            _agents.remove(i);
            _agents.put(i, new Agent(str, i));

        }

    }

    // get specific agent from agents Hash Map
    public Agent getAgent(int id)
    {
        if (_agents.containsKey(id))
            return _agents.get(id);

        return null;
    }

//    public static void main(String[] args)
//    {
//        String str = "{\"Agents\":[{\"Agent\":{\"id\":0,\"value\":0.0,\"src\":0,\"dest\":-1,\"speed\":1.0,\"pos\":\"35.18753053591606,32.10378225882353,0.0\"}}]}";
//        System.out.println(Arrays.toString(str.split("[{]")));
//
//        Agents agents = new Agents(str);
//    }

}

