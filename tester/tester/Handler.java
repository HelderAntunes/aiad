package tester;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Handler implements HttpHandler {
    int[][]tests = new int[50][12];
    AtomicInteger numTests;
    AtomicInteger currTest;
    AtomicInteger testsDone;
    String[] winners;
    Process currProc;

    Handler(int[][] tests, int numTests, int currTest, Process currProc) {
        this.tests = tests;
        this.numTests = new AtomicInteger(numTests);
        this.currTest = new AtomicInteger(0);
        this.testsDone = new AtomicInteger(0);
        this.winners = new String[numTests];
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
        catch (Exception e) {}

    }

    private void handleGetTests(HttpExchange httpExchange) throws IOException {
        String response = "test " + currTest.get() + " ";
        int[] test = tests[currTest.get()];
        currTest.addAndGet(1);
        for (int i = 0; i < test.length; i++) {
            response += test[i];
            if (i < test.length-1)
                response += " ";
        }

        writeResponse(httpExchange, response);
    }

    private void handleGetTest(HttpExchange httpExchange) throws IOException {
        if (currTest.get() >= numTests.get())
            writeResponse(httpExchange, "error");

        String response = "test " + currTest.get() + " ";
        int[] test = tests[currTest.get()];
        currTest.addAndGet(1);
        for (int i = 0; i < test.length; i++) {
            response += test[i];
            if (i < test.length-1)
                response += " ";
        }

        writeResponse(httpExchange, response);
    }

    private synchronized void handlePostTest(HttpExchange httpExchange) throws Exception {
        String query = getQueryOfPostRequest(httpExchange);
        Map<String,String> params = queryToMap(query);

        String idTest = params.get("idTest");
        String winner = params.get("winner");
        winners[Integer.parseInt(idTest)] = winner;

        writeResponse(httpExchange, "test successfully created");
        if (testsDone.addAndGet(1) == numTests.get()) {
            for (int i = 0; i < winners.length; i++) {
                System.out.println("Game " + i + " winner: " + winners[i]);
            }
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
}