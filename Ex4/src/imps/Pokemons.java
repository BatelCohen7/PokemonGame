package imps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * class to get all the pokemons in the game
 */
public class Pokemons {
    private HashMap<Integer,Pokemon> pokemons;
    public Pokemons() { pokemons = new HashMap<>(); }

    public Pokemons(String str)
    {
        pokemons = new HashMap<>();
        update(str);
    }

    public int size()
    {
        return pokemons.size();
    }

    public void update (String str)
    {
        pokemons = new HashMap<>();
        int len = str.split("\"Pokemon\"").length - 1;
        for (int i = 0; i < len ; i++)
        {
            pokemons.put(i, new Pokemon(str, i));
        }
    }
    // allocate pokemon to specific agent
    public void allocateAllPokemons(Agents agents)
    {
        for( int i: pokemons.keySet() )
        {
            pokemons.get(i).allocateToAgent(agents);
        }
    }

    // get specific pokemon from pokemons Hash Map
    public Pokemon getPokemon(int i)
    {
        if (pokemons.containsKey(i))
            return pokemons.get(i);
        return null;
    }

//    public static void main(String[] args)
//    {
//        //String str =
//        // System.out.println(Arrays.toString(str.split("[{]")));
//    }
}
