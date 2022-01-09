package ex4_java_client; /**
 * @author AchiyaZigi
 * A trivial example for starting the server and running all needed commands
 */
import ex4_java_client.*;
import Simulator.*;
import api.*;
import com.google.gson.Gson;
import imps.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

import static java.lang.Math.random;
import static java.lang.Thread.sleep;

public class StudentCode
{
    public static boolean isRuning = true;
    public static Agents agents = new Agents();
    public static Pokemons pokemons = new Pokemons();
    public static GraphAlgorithms algo = new GraphAlgorithms();
    public static FrameAlgo frameAlgo;

    /**
     * use with ip='127.0.0.1' , port=6666
     * to start a new connection to the game server
     */
    public static void main(String[] args)
    {
        Gson gson = new Gson();  //load new gson
        Client client = new Client();
        try {
            client.startConnection("127.0.0.1", 6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(client.getInfo());

        String graphStr = client.getGraph();

        algo.loadFromStr(graphStr); // Convert from string to json
        System.out.println(graphStr);

        int lenNodes = algo.getGraph().nodeSize() + 1;
        int rand = 0;
        Random random = new Random();
        do
        {
            rand = random.nextInt(lenNodes);
        }
        while(client.addAgent("{\"id\":" + rand + "}").equals("true"));

        String agentsStr = client.getAgents();
        agents = new Agents(agentsStr); // list of agens
        String pokemonsStr = client.getPokemons(); //list of pokemons
        String isRunningStr = client.isRunning(); // check if the game is still running - True \ false
        frameAlgo = new FrameAlgo(algo, agents);

        client.start(); //start game
        isRuning = true;



        /**
         * in this method we create thread for each agent in tha game,
         * we make this to run several agents simultaneously
         * and perform the same functions for each agent simultaneously without overloading the computer
         */
        ArrayList<Thread> agentsThread = new ArrayList<>();

        for (int i = 0; i < agents.size(); i++)
        {
            Thread agentTh = new Thread(new AgentRunToPokemon(i, client));
            agentTh.setDaemon(true);
            agentTh.start();
            agentsThread.add(0, agentTh);
        }

        int i = 0;
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        while (client.isRunning().equals("true"))
        {
            /**
             *  synchronization method that only one thread can access the resource at a given point in time.
             *  Java provides a way of creating threads and synchronizing their tasks using synchronized blocks.
             */
            synchronized (client)
            {
                client.move();
                agentsStr = client.getAgents();
                StudentCode.agents.update(agentsStr);
                StudentCode.pokemons = new Pokemons(client.getPokemons());
                StudentCode.pokemons.allocateAllPokemons(StudentCode.agents);
                StudentCode.frameAlgo.setAgentsAndPokes(StudentCode.agents, StudentCode.pokemons);

            }

            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isRuning = false;
    }

}

/**
 * thread for move function
 * the main function of the project - run --> start the game
 */
class RunMoves implements Runnable
{
    private Client _client;
    public RunMoves(Client client)
    {
        _client = client;
    }
    @Override
    public void run()
    {
        while (StudentCode.isRuning)
        {
            synchronized (_client)
            {
                _client.move();
            }

            try {
                sleep(111);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * thread for AgentRunToPokemon function
 * In this method we connect an agent to the Pokemon closest to him
 */
class AgentRunToPokemon implements Runnable
{
    private int _id;
    private final Client _client;
    public AgentRunToPokemon(int id, Client client)
    {
        _id = id;
        _client = client;
    }
    @Override
    public void run()
    {
        boolean first = true;
        while (StudentCode.isRuning)  // isRuning = true
        {
            Agent agent = null;
            Pokemon pokemon = null;
            agent = StudentCode.agents.getAgent(_id);  // get agent
            if (agent == null)
                continue;

            pokemon = agent.getPokemon();   // getPokemon
            if (pokemon == null)
                continue;


            int[] onNodes = pokemon.getOnNodes(); // find the nodes that the Pokemon are between
            int src =  agent.getSrc();
            int dest = agent.getDest();
            if (src != onNodes[0])
            {
                // find the shortestPath to the nodes that the Pokemon are between
                List<NodeData> nodes = StudentCode.algo.shortestPath(src, onNodes[0]);
                if (first && dest == -1)
                {
                    int index = onNodes[0];
                    if (nodes.size() > 0)
                    {
                        index = (src == nodes.get(0).getKey()) ? 1 : 0;
                        index = nodes.get(index).getKey();
                    }

                    // that method synchronized the next moves of the agents
                    synchronized (_client)
                    {
                        _client.chooseNextEdge("{\"agent_id\":" + _id + ", \"next_node_id\": " + index + "}");

                        try {
                            _client.wait(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        first = false;
                    }
                }
                else
                {
                    first = true;
                }
            }
            else if (first && onNodes.length == 2 && dest == -1)
            {
                synchronized (_client)
                {
                    _client.chooseNextEdge("{\"agent_id\":" + _id + ", \"next_node_id\": " + onNodes[1] + "}");
                    try {
                        _client.wait(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                first = true;
            }
        }
    }
}