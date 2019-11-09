import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private static Socket socket;
    private static Scanner scanner;

    private static void start() throws Exception {
        startThread();
        ObjectMapper mapper = new ObjectMapper();
        String input;
        String howToQuery = "Please follow the required order for your request: \n" +
                "<BenchmarkType>,<WorkloadMetric>,<BatchUnit>,<BatchID>,<BatchSize>\n" +
                "BenchmarkType allowed: DVDtest, DVDtrain, NDBenchTest, NDBenchTrain\n" +
                "WorkloadMetric allowed: cpu, networkin, networkout, memory\n" +
                "Split the fields by commas only [NO SPACES]\n";
        while (true) {
            System.out.println(howToQuery);
            input = scanner.nextLine();
            String[] i = input.split(",");
            String req = null;
            if(i.length == 5){
                RFW request = new RFW(i[0],i[1],Integer.parseInt(i[2]),Integer.parseInt(i[3]),Integer.parseInt(i[4]));
                req = mapper.writeValueAsString(request); //Serialize to Json
            }

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            if (req != null){
                out.println(req);
                out.flush();
            }

        }
    }


    public static void main(String[] args) throws Exception {

        socket = new Socket ("localhost", 6789);
        scanner = new Scanner(System.in);
        System.out.println("\r\nConnected to Server: " + socket.getInetAddress());
        start();
        startThread();

    }


    //Thread to always read messages from the server and print them in the textArea
    private static void startThread() {

        new Thread (new Runnable(){ @Override
        public void run() {
            try {
                //Connect to the socket's input stream
                //ObjectInputStream inFromServer = new ObjectInputStream(socket.getInputStream());
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String clientInput;
                String data = null;

                //always read received messages and output them to client
                while ((data = inFromClient.readLine()) != null) {
                    System.out.println("\r\nMessage: " + data);

                    //JSONObject response = (JSONObject) inFromServer.readObject();
                    //System.out.println("Server response for request is\n" + response.toJSONString());
                }

            }
            catch(Exception e) { }

        }}).start();

    }
}

