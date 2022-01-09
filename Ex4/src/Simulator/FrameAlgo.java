package Simulator;

import api.DirectedWeightedGraphAlgorithms;
import imps.Agents;
import imps.Pokemons;

import javax.swing.*;

import java.awt.*;

import static java.awt.Frame.MAXIMIZED_BOTH;

public class FrameAlgo {
    public static int iter = 0;
    private DirectedWeightedGraphAlgorithms _algorithm;
    public GraphZone graphZone;
    public JFrame frame = new JFrame("GraphAlgorithms");
    private JPanel panel;
    private int mode;
    public static final int NONE = 0;


    public FrameAlgo(DirectedWeightedGraphAlgorithms algorithm, Agents agents)
    {
        mode = NONE;
        _algorithm = algorithm;
        frame.setExtendedState(MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        panel = new JPanel();
        panel.setLayout(null);

        frame.add(panel);

        JLabel label = new JLabel("GraphAlgorithms", SwingConstants.CENTER);
        panel.add(label);
        graphZone = new GraphZone(_algorithm, agents, 120, 70, frame.getWidth() - 200, frame.getHeight()-200, panel);
        ImageIcon backGIM = new ImageIcon(this.getClass().getResource("/Simulator/pokenomframe.png"));
        backGIM = new ImageIcon(backGIM.getImage().getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH));
        JLabel back = new JLabel(backGIM);
        back.setBounds(0,0, frame.getWidth(), frame.getHeight());
        back.setVisible(true);
        panel.add(back);
        //frame.pack();
        frame.repaint();

    }

    public void setAgentsAndPokes(Agents agents, Pokemons pokemons)
    {
        for (int i = 0; i < agents.size(); i++)
        {
            graphZone.setAgentsOnGraph(agents.getAgent(i));
        }
        graphZone.setPokemonsOnGraph(pokemons);
    }

}