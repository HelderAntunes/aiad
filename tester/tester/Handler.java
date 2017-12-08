package tester;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Handler implements HttpHandler {
    AtomicInteger currTest;
    AtomicInteger testsDone;
    Process currProc;
    private ArrayList<Test> tests;

    Handler(ArrayList<Test> tests, Process currProc) {
        this.tests = tests;
        this.currTest = new AtomicInteger(0);
        this.testsDone = new AtomicInteger(0);
        this.currProc = currProc;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        try {
            switch (path) {
                case "/getTest":
                    handleGetTest(httpExchange);
                    break;
                case "/postTest":
                    handlePostTest(httpExchange);
                    break;
                case "/getTests":
                    handleGetTests(httpExchange);
                    break;
                default:
                    break;
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    private void handleGetTests(HttpExchange httpExchange) throws IOException {
        String response = "[";
        for (int i = 0; i < tests.size();) {
            int id = tests.get(i).id;
            int[] configuration = tests.get(i).agents;
            int villagers_wins = 0;
            int werewolfs_wins = 0;
            for (; i < tests.size(); i++) {
                if (tests.get(i).id != id) {
                    break;
                }
                String winner = formatStrings(tests.get(i).winner);
                if (winner.equals("Werewolfs win!"))
                    werewolfs_wins++;
                else
                    villagers_wins++;
            }
            String testResult = "{\n";
            testResult += "id: " + id + ",\n";
            testResult += "configuration: {\n";
            testResult += "villager_random: " + configuration[0] + ",\n";
            testResult += "villager_strategic: " + configuration[1] + ",\n";
            testResult += "villager_bdi: " + configuration[2] + ",\n";
            testResult += "villager_random: " + configuration[3] + ",\n";
            testResult += "villager_strategic: " + configuration[4] + ",\n";
            testResult += "villager_bdi: " + configuration[5] + ",\n";
            testResult += "villager_random: " + configuration[6] + ",\n";
            testResult += "villager_strategic: " + configuration[7] + ",\n";
            testResult += "villager_bdi: " + configuration[8] + ",\n";
            testResult += "villager_random: " + configuration[9] + ",\n";
            testResult += "villager_strategic: " + configuration[10] + ",\n";
            testResult += "villager_bdi: " + configuration[11] + ",\n";
            testResult += "},\n";
            testResult += "results: {\n";
            testResult += "villager_wins: " + villagers_wins + ",\n";
            testResult += "werewolfs_wins: " + werewolfs_wins + "\n";
            testResult += "},\n},\n";

            response += testResult;
        }
        response += "]";

        writeResponse(httpExchange, response);
    }

    private void handleGetTest(HttpExchange httpExchange) throws IOException {
        String response = "test " + currTest.get() + " ";
        Test test = tests.get(currTest.get());
        currTest.addAndGet(1);

        for (int i = 0; i < test.agents.length; i++) {
            response += test.agents[i];
            if (i < test.agents.length-1)
                response += " ";
        }

        writeResponse(httpExchange, response);
    }

    private synchronized void handlePostTest(HttpExchange httpExchange) throws Exception {
        String query = getQueryOfPostRequest(httpExchange);
        Map<String,String> params = queryToMap(query);

        String idTest = params.get("idTest");
        tests.get(Integer.parseInt(idTest)).winner = params.get("winner");

        writeResponse(httpExchange, "test successfully created");
        if (testsDone.addAndGet(1) == tests.size()) {
            System.out.println("\nTests done. Results:");
            for (int i = 0; i < tests.size(); i++) {
                System.out.println("Game " + i + " winner: " + tests.get(i).winner);
            }
            System.out.println("\nOpen statistics.html for more details.");
            currProc.destroy();
        }
        else {
            currProc.destroy();
            currProc = Runtime.getRuntime().exec("java -jar werewolfsGameTest.jar");
        }
    }

    private String getQueryOfPostRequest(HttpExchange httpExchange) throws IOException {
        InputStream in = httpExchange.getRequestBody();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte buf[] = new byte[4096];

        for (int n = in.read(buf); n > 0; n = in.read(buf))
            out.write(buf, 0, n);
        return new String(out.toByteArray());
    }

    private void writeResponse(HttpExchange httpExchange, String response) throws IOException {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "application/json");

        httpExchange.sendResponseHeaders(200, response.length());

        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    /**
     * returns the url parameters in a map
     * @param query
     * @return map
     */
    private static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    private String formatStrings(String text) {
        if (text.length() <= 2)
            return text;
        return text.substring(1, text.length()-1);
    }
}