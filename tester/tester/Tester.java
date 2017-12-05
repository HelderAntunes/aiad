package tester;

import java.net.InetSocketAddress;
import java.io.*;
import java.util.Scanner;
import java.lang.String;

import com.sun.net.httpserver.*;

public class Tester {

  public static void main(String[] args) {
      /*if (args.length != 1) {
        System.out.println("Error: invalid arguments.");
        System.out.println("Use: java -jar Tester testFileName");
      }*/

      // String fileName = args[0];
      try {
        int [][] tests = readTestInfoFile("test_werewolfs.txt");
        for (int i = 0; i < tests.length; i++) {
            int [] test = tests[i];
            for (int j = 0; j < test.length; j++) {
                System.out.print(test[j] + " ");
            }
            System.out.println();
        }

        initServer(tests);
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
    public static int[][] readTestInfoFile(String fileName) throws Exception {
    	File file = new File(fileName);
    	Scanner scanner = new Scanner(file);
      int[][] tests = new int[50][12];
      int numTests = 0;

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

  private static void initServer(int[][] tests) throws Exception {
      Handler handler = new Handler();
      HttpServer httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1",8000), 0);
      httpServer.createContext("/getTest", handler);

      httpServer.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
      httpServer.start();
  }
}
