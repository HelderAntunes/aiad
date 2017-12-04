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

public class WerewolfsGameEnvTest extends jason.environment.Environment {

    private Logger logger = Logger.getLogger("WerewolfsGame.mas2j." + WerewolfsGameEnv.class.getName());

    private int MAX_TESTS = 50;
    private int[][] tests = new int[50][12];
    private int numTests = 0;

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

              try { readTestInfoFile(); }
		          catch (Exception e) {}
              initGUI();

              addPercept(Literal.parseLiteral("changeWaitTime(" + 10 + ")")); // wait time = 10 ms


              startTesting();
            }
        });

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
    public void readTestInfoFile() throws Exception {
    	File file = new File("test_werewolfs.txt");
    	Scanner scanner = new Scanner(file);

    	while (scanner.hasNext()) {
        readATest(scanner);
        numTests++;
    	}

      for (int i = 0; i < numTests; i++) {
        String literal = "createAgents(" + tests[i][0];
      	for (int j = 1; j < 12; j++) literal += "," + tests[i][j];
        literal += ")";
        logger.info(literal);
      }
    }

    private void readATest(Scanner scanner) {
      int[] agents = new int[12];
      int numTypeAgents = 4; // werewolfs, villagers, doctors, diviners

      for (int i = 0; i < numTypeAgents; i++) {
        String type = scanner.next();
        int num = scanner.nextInt();

        if (type.equals("villager_random")) agents[0] = num;
        else if (type.equals("villager_strategic")) agents[1] = num;
        else if (type.equals("villager_bdi")) agents[2] = num;
        else if (type.equals("werewolf_random")) agents[3] = num;
        else if (type.equals("werewolf_strategic")) agents[4] = num;
        else if (type.equals("werewolf_bdi")) agents[5] = num;
        else if (type.equals("diviner_random")) agents[6] = num;
        else if (type.equals("diviner_strategic")) agents[7] = num;
        else if (type.equals("diviner_bdi")) agents[8] = num;
        else if (type.equals("doctor_random")) agents[9] = num;
        else if (type.equals("doctor_strategic")) agents[10] = num;
        else if (type.equals("doctor_bdi")) agents[11] = num;
      }

      tests[numTests] = agents;
    }

    private void initGUI() {
		frame = new JFrame("The Werewolves of Millers Hollow");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH_FRAME, HEIGHT_FRAME);
        frame.setResizable(false);
        frame.setVisible(true);

        // currPanel = new InitGamePanel(this);
        // frame.getContentPane().add(currPanel);
    }

    private void startTesting() {
      int[] firstTest = tests[0]; // TODO: check size... mas não há tempo :(

      String literal = "createAgents(" + firstTest[0];
      for (int i = 1; i < firstTest.length; i++) literal += "," + firstTest[i];
      addPercept(Literal.parseLiteral(literal + ")"));
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

    public Logger getLogger() {
        return logger;
    }

    @Override
    public boolean executeAction(String agName, Structure action) {

		    if (action.getFunctor().equals("playerJoined")) {

		    } else if (action.getFunctor().equals("updateTimeDayEnv")) {

        } else if (action.getFunctor().equals("updateEventDayEnv")) {

        } else if (action.getFunctor().equals("updateEventPanelEnv")) {

        } else if (action.getFunctor().equals("playerDied")) {

        } else {
			       logger.info("executing: "+action+", but not implemented!");
			       return false;
		    }

        return true;
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}
