package tester;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Handler implements HttpHandler {
    int[][]tests = new int[50][12];
    int numTests = 0;
    int currTest = 0;
    String[] winners;

    Handler(int[][] tests, int numTests, int currTest) {
        this.tests = tests;
        this.numTests = numTests;
        this.currTest = currTest;
        winners = new String[numTests];
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        switch (path) {
            case "/getTest":
                handleGetTest(httpExchange);
                break;
            case "/postTest":
                handlePostTest(httpExchange);
                break;
            default:
                break;
        }
    }

    private void handleGetTest(HttpExchange httpExchange) throws IOException {
        if (currTest >= numTests)
            writeResponse(httpExchange, "error");

        String query = getQueryOfPostRequest(httpExchange);
        Map<String,String> params = queryToMap(query);
        // String username = params.get("username");
        // String password = params.get("password");

        String response = "test " + currTest + " ";
        int[] test = tests[currTest++];
        for (int i = 0; i < test.length; i++) {
            response += test[i];
            if (i < test.length-1)
                response += " ";
        }

        writeResponse(httpExchange, response);
    }

    private void handlePostTest(HttpExchange httpExchange) throws IOException {
        String query = getQueryOfPostRequest(httpExchange);
        Map<String,String> params = queryToMap(query);
        String idTest = params.get("idTest");
        String winner = params.get("winner");
        winners[Integer.parseInt(idTest)] = winner;
        System.out.println(idTest + " -> " + winner);
        writeResponse(httpExchange, "test successfully created");
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
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}