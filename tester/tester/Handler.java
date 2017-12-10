package tester;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
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
        writeResponse(httpExchange, getTests());
    }

    private String getTests() {
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
            int total = villagers_wins + werewolfs_wins;
            String testResult = "{\n";
            testResult += "id: " + id + ",\n";
            testResult += "configuration: {\n";
            testResult += "villager_random: " + configuration[0] + ",\n";
            testResult += "villager_strategic: " + configuration[1] + ",\n";
            testResult += "villager_bdi: " + configuration[2] + ",\n";
            testResult += "werewolf_random: " + configuration[3] + ",\n";
            testResult += "werewolf_strategic: " + configuration[4] + ",\n";
            testResult += "werewolf_bdi: " + configuration[5] + ",\n";
            testResult += "diviner_random: " + configuration[6] + ",\n";
            testResult += "diviner_strategic: " + configuration[7] + ",\n";
            testResult += "diviner_bdi: " + configuration[8] + ",\n";
            testResult += "doctor_random: " + configuration[9] + ",\n";
            testResult += "doctor_strategic: " + configuration[10] + ",\n";
            testResult += "doctor_bdi: " + configuration[11] + ",\n";
            testResult += "},\n";
            testResult += "results: {\n";
            testResult += "villager_wins: " + villagers_wins + ",\n";
            testResult += "werewolf_wins: " + werewolfs_wins + ",\n";
            testResult += "total: " + total + "\n";
            testResult += "},\n},\n";

            response += testResult;
        }
        response += "]";
        return response;
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
            createStatistics();
        }
        else {
            currProc.destroy();
            currProc = Runtime.getRuntime().exec("java -jar werewolfsGameTest.jar");
        }
    }

    private void createStatistics() throws IOException{
        String htmlpage = "<!DOCTYPE html><html lang=\"en\"><head> <meta charset=\"UTF-8\"> <title>Statistics</title> <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\"> <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script> <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script> <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script> <script src=\"https://ajax.googleapis.com/ajax/libs/angularjs/1.6.4/angular.min.js\"></script> <style>body{background-color: #111111; color: white;}.jumbotron{background-color: salmon; padding: 20px;}.result{border-radius: 30px; margin: 10px 20px; background-color: #232323; color: white; padding: 10px;}.result table, .result th, .statistics, footer{text-align: center;}.result tr td:first-of-type, .result tfoot td:nth-child(4){font-weight: bold;}.result tr td:first-of-type{font-weight: bold;}.result table img{max-height: 100%; display: block}footer{background-color: #232323; margin-top: 20px; padding: 10px;}</style></head><body ng-app=\"myApp\" ng-controller=\"myCtrl\"> <div class=\"container-fluid\"> <header class=\"jumbotron\"> <h1>Statistics</h1> <h2>Werewolf's Game</h2> </header> </div><div> <div class=\"row result\" ng-repeat=\"test in tests\"> <div class=\"col-sm-7\"> <h3>Test #{{test.id}}</h3> <table class=\"table table-condensed\"> <tr> <th></th> <th>Random</th> <th>Strategic</th> <th>Beliefs</th> <th>Total</th> </tr><tr> <td>Werewolf</td><td>{{test.configuration.werewolf_random}}</td><td>{{test.configuration.werewolf_strategic}}</td><td>{{test.configuration.werewolf_bdi}}</td><td>{{test.configuration.werewolf_bdi + test.configuration.werewolf_random + test.configuration.werewolf_strategic}}</td></tr><tr> <td>Villager</td><td>{{test.configuration.villager_random}}</td><td>{{test.configuration.villager_strategic}}</td><td>{{test.configuration.villager_bdi}}</td><td>{{test.configuration.villager_bdi + test.configuration.villager_random + test.configuration.villager_strategic}}</td></tr><tr> <td>Doctor</td><td>{{test.configuration.doctor_random}}</td><td>{{test.configuration.doctor_strategic}}</td><td>{{test.configuration.doctor_bdi}}</td><td>{{test.configuration.doctor_bdi + test.configuration.doctor_random + test.configuration.doctor_strategic}}</td></tr><tr> <td>Diviner</td><td>{{test.configuration.diviner_random}}</td><td>{{test.configuration.diviner_strategic}}</td><td>{{test.configuration.diviner_bdi}}</td><td>{{test.configuration.diviner_bdi + test.configuration.diviner_strategic + test.configuration.diviner_random}}</td></tr><tfoot> <td></td><td></td><td></td><td>Total</td><td>{{test.configuration.werewolf_bdi + test.configuration.werewolf_random + test.configuration.werewolf_strategic + test.configuration.villager_bdi + test.configuration.villager_random + test.configuration.villager_strategic + test.configuration.doctor_bdi + test.configuration.doctor_random + test.configuration.doctor_strategic + test.configuration.diviner_bdi + test.configuration.diviner_strategic + test.configuration.diviner_random}}</td></tr></table> </div><div class=\"col-sm-5 statistics\"> <div class=\"col-sm-12\"> <h4>Total Games:{{test.results.total}}</h4> </div><div class=\"col-sm-6\"> <img src=\"assets/werewolf.png\"> </div><div class=\"col-sm-6\"> <img src=\"assets/villager.png\"> </div><div class=\"col-sm-6\"> Werewolfs Wins:{{test.results.werewolf_wins}}({{test.results.werewolf_wins/test.results.total*100 | number:0}}%) </div><div class=\"col-sm-6\"> Villager Wins:{{test.results.villager_wins}}({{test.results.villager_wins/test.results.total*100 | number:0}}%) </div><div class=\"col-sm-12\"> <div class=\"progress\"> <div class=\"progress-bar progress-bar-danger progress-bar-striped\" role=\"progressbar\" ng-attr-aria-valuenow=\"100 - statistics.total_percent\" aria-valuemin=\"0\" aria-valuemax=\"100\" ng-style=\"{'width': test.results.werewolf_wins/test.results.total*100 + '%'}\"></div><div class=\"progress-bar progress-bar-warning progress-bar-striped\" role=\"progressbar\" ng-attr-aria-valuenow=\"statistics.total_percent\" aria-valuemin=\"0\" aria-valuemax=\"100\" ng-style=\"{'width': test.results.villager_wins/test.results.total*100 + '%'}\"></div></div></div></div></div></div><footer> <h3>AIAD @ 2017/2018</h3> </footer></body> <script>var app=angular.module('myApp', []); app.controller('myCtrl', function ($scope, $http){";
        String codeJS = "$scope.tests = " + getTests() + ";";
        String rest = "console.log($scope.tests);}); </script></html>";
        htmlpage = htmlpage + codeJS + rest;

        BufferedWriter output = null;
        try {
            File file = new File("statistics.html");
            output = new BufferedWriter(new FileWriter(file));
            output.write(htmlpage);
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( output != null ) {
                output.close();
            }
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