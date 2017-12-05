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
    private int testIndex;

    private String base_url = "http://localhost:8000";

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              try {
                String response = sendGet("/getTest", "null");
                logger.info("fsefsdfsdfsdfsdf");
                logger.info(response);

                // test 1 8 0 0 2 0 0 1 0 0 1 0 0
                int[] test = readATest(response);

                String literal = "createAgents(" + test[0];
                for (int j = 1; j < test.length; j++) literal += "," + test[j];
                literal += ")";
                logger.info(literal);

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

    private int[] readATest(String response) {
      Scanner scanner = new Scanner(response);
      int[] agents = new int[12];
      scanner.next();
      testIndex = scanner.nextInt();

      for (int i = 0; i < 12; i++) {
        int num = scanner.nextInt();
        agents[i] = num;
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
