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

import java.net.HttpURLConnection;
import java.net.URL;

public class WerewolfsGameEnvTest extends jason.environment.Environment {

    private Logger logger = Logger.getLogger("WerewolfsGame.mas2j." + WerewolfsGameEnv.class.getName());

    public static int WIDTH_FRAME = 800;
    public static int HEIGHT_FRAME = 600;
    JFrame frame;
    JPanel currPanel;

    private String base_url = "http://localhost:8000";

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
        logger.info("fdsfsdf");

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              try {
                  logger.info("fdsfsdf");
                String response = sendGet("/getTest", "void");
                logger.info(response);

                String literal = readTestInfoFile();
                addPercept(Literal.parseLiteral("changeWaitTime(" + 10 + ")")); // wait time = 10 ms
                addPercept(Literal.parseLiteral(literal));
                initGUI();
              }
		          catch (Exception e) {}

            }
        });

    }

    public String sendGet(String path, String urlParameters) throws Exception {
        String url = base_url + path + "?" + urlParameters;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode != 200)
            return "Error: code " + responseCode + ".";

        return readResponse(con);
    }

    private String readResponse(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
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
    public String readTestInfoFile() throws Exception {
    	File file = new File("test_werewolfs.txt");
    	Scanner scanner = new Scanner(file);
      int[] test = readATest(scanner);

      String literal = "createAgents(" + test[0];
    	for (int j = 1; j < test.length; j++) literal += "," + test[j];
      literal += ")";
      logger.info(literal);

      return literal;
    }

    private int[] readATest(Scanner scanner) {
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

      return agents;
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

        } else if (action.getFunctor().equals("gameFinished")) {
          String winner = action.getTerm(0).toString();
          logger.info("THE WINNNNER IS: " + winner);
          //addPercept(Literal.parseLiteral("restart"));
          // TODO: add to file the result
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
