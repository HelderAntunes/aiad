package tester;

import java.net.InetSocketAddress;
import java.io.*;
import java.util.Scanner;
import java.lang.String;

import com.sun.net.httpserver.*;

public class Tester {

    private int [][] tests;
    private int numTests;
    private int currTest;
    private Handler handler;


    public static void main(String[] args) {
      /*if (args.length != 1) {
        System.out.println("Error: invalid arguments.");
        System.out.println("Use: java -jar Tester testFileName");
      }*/

      // String fileName = args[0];
      try {
          Tester tester = new Tester();
          tester.readTestInfoFile("test_werewolfs.txt");
          tester.initServer();
          tester.startTesting();
      }
      catch (Exception e) {}
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
    public int[][] readTestInfoFile(String fileName) throws Exception {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
      tests = new int[50][12];
      numTests = 0;
      currTest = 0;

        while (scanner.hasNext())
        tests[numTests++] = readATest(scanner);

      return tests;
    }

    private static int[] readATest(Scanner scanner) {
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

    private void initServer() throws Exception {
      Process proc = Runtime.getRuntime().exec("java -jar werewolfsGameTest.jar");
      handler = new Handler(tests, numTests, currTest, proc);
      HttpServer httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1",8000), 0);
      httpServer.createContext("/getTest", handler);
      httpServer.createContext("/postTest", handler);
      httpServer.createContext("/getTests", handler);

      httpServer.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
      httpServer.start();

      System.out.println("Listening at localhost:8000 ...");
    }

    private void startTesting() throws Exception {

    }
}
