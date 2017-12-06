package tester;

import java.net.InetSocketAddress;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.String;

import com.sun.net.httpserver.*;

public class Tester {

    private int currID = 0;
    private Handler handler;
    private ArrayList<Test> tests = new ArrayList<Test>();

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
    public void readTestInfoFile(String fileName) throws Exception {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNext())
            readATest(scanner);
    }

    private void readATest(Scanner scanner) {
      int[] agents = new int[12];
      int numSubTests = scanner.nextInt();

      for (int i = 0; i < 12; i++) {
        String type = scanner.next();
        int num = scanner.nextInt();

        if (type.equals("vr")) agents[0] = num;
        else if (type.equals("vs")) agents[1] = num;
        else if (type.equals("vb")) agents[2] = num;
        else if (type.equals("wr")) agents[3] = num;
        else if (type.equals("ws")) agents[4] = num;
        else if (type.equals("wb")) agents[5] = num;
        else if (type.equals("dir")) agents[6] = num;
        else if (type.equals("dis")) agents[7] = num;
        else if (type.equals("dib")) agents[8] = num;
        else if (type.equals("dor")) agents[9] = num;
        else if (type.equals("dos")) agents[10] = num;
        else if (type.equals("dob")) agents[11] = num;
      }

      for (int i = 0; i < numSubTests; i++) {
          tests.add(new Test(currID, agents.clone()));
      }

      currID++;
    }

    private void initServer() throws Exception {
      Process proc = Runtime.getRuntime().exec("java -jar werewolfsGameTest.jar");

      handler = new Handler(tests, proc);
      HttpServer httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1",8000), 0);
      httpServer.createContext("/getTest", handler);
      httpServer.createContext("/postTest", handler);
      httpServer.createContext("/getTests", handler);

      httpServer.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
      httpServer.start();

      System.out.println("Listening at localhost:8000 ...");
    }
}
