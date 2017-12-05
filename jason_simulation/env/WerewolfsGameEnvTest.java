// Environment code for project WerewolfsGame.mas2j
package env;

import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;
import java.io.*;
import java.util.Scanner;
import java.lang.String;
import javax.swing.*;


import java.net.HttpURLConnection;
import java.net.URL;

public class WerewolfsGameEnvTest extends jason.environment.Environment {

    private Logger logger = Logger.getLogger("WerewolfsGame.mas2j." + WerewolfsGameEnv.class.getName());
    
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
                int[] test = readATest(response);

                String literal = "createAgents(" + test[0];
                for (int j = 1; j < test.length; j++) literal += "," + test[j];
                literal += ")";
                logger.info(literal);

                addPercept(Literal.parseLiteral("changeWaitTime(" + 10 + ")")); // wait time = 10 ms
                addPercept(Literal.parseLiteral(literal));
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

    public String sendPost(String path, String urlParameters) throws Exception {
      String url = base_url + path;
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      con.setRequestMethod("POST");
      con.setDoOutput(true);
      con.setDoInput(true);
      setPostParameters(con, urlParameters);

      int responseCode = con.getResponseCode();
      if (responseCode != 200)
        return "Error: code " + responseCode + ".";

      return readResponse(con);
    }

    private void setPostParameters(HttpURLConnection con, String postParameters) throws IOException {
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParameters);
        wr.flush();
        wr.close();
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
          try {
            sendPost("/postTest", "idTest=" + testIndex + "&winner=" + winner);
          }
          catch (Exception e) {
            logger.info(":(");
          }

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
