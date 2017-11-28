// Environment code for project WerewolfsGame.mas2j
package env;

import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;
import java.io.*;
import java.util.Scanner;
import java.lang.String;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class WerewolfsGameEnv extends jason.environment.Environment {

    private Logger logger = Logger.getLogger("WerewolfsGame.mas2j." + WerewolfsGameEnv.class.getName());
    public static int WIDTH_FRAME = 800;
    public static int HEIGHT_FRAME = 600;

    JFrame frame;
	  JPanel currPanel;

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initGUI();
            }
        });

    }
    private void initGUI() {
        //Create and set up the window.
		frame = new JFrame("The Werewolves of Millers Hollow");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		currPanel = new InitGamePanel(this);
        frame.getContentPane().add(currPanel);

        //Size and display the window.
        frame.setSize(WIDTH_FRAME, HEIGHT_FRAME);
        frame.setVisible(true);
    }

	public JPanel getCurrPanel() {
		return currPanel;
	}

	public void setCurrPanel(JPanel newPanel) {
		currPanel = newPanel;
	}

    public JFrame getFrame() {
    	return frame;
    }

	/**
	 * Agent initials:
	 * RV, SV, BV: Random, Strategic, BDI villager
	 * RW, SW, BW: Random, Strategic, BDI werewolf
	 * RDi, SDi, BDi: Random, Strategic, BDI diviner
	 * RDo, SDo, BDo: Random, Strategic, BDI doctor
	 *
	 * agents array = [RV, SV, BV, RW, SW, BW, RDi, SDi, BDi, RDo, SDo, BDo]
	 *
	 */
	public void readOptionFile () throws Exception {
		File file = new File("werewolf_options.txt");
		Scanner scanner = new Scanner(file);
		int[] agents = new int[12];

		while (scanner.hasNext()) {
			String type = scanner.next();
			int num = scanner.nextInt();
			if (type.equals("villager_random")) agents[0] = num;
			else if (type.equals("werewolf_random")) agents[3] = num;
			else if (type.equals("werewolf_bdi")) agents[5] = num;
			else if (type.equals("diviner_random")) agents[6] = num;
			else if (type.equals("doctor_random")) agents[9] = num;
		}

		String literal = "createAgents(" + agents[0];
		for (int i = 1; i < agents.length; i++) literal += "," + agents[i];
		addPercept(Literal.parseLiteral(literal + ")"));
	}

    @Override
    public boolean executeAction(String agName, Structure action) {
		if (action.getFunctor().equals("Something")) {
			return true;
		}
		else if (action.getFunctor().equals("playerJoined")) {
			if (currPanel instanceof MidGamePanel)
				((MidGamePanel)currPanel).playerJoined(action.getTerm(0).toString(), action.getTerm(1).toString());
			return true;
		}
		else {
			logger.info("executing: "+action+", but not implemented!");
			return false;
		}
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}
