import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Server {

    private static final String ROOT_FOLDER = System.getProperty("user.dir");
    private static final String DATA_FOLDER = ROOT_FOLDER.substring(0, ROOT_FOLDER.length()-14) + "Workload Data" + File.separator ;
    private static final String DVD_TEST_FILE = DATA_FOLDER + "DVD-testing";
    private static final String DVD_TRAIN_FILE = DATA_FOLDER + "DVD-training";
    private static final String NDBENCH_TEST_FILE = DATA_FOLDER + "NDBench-testing";
    private static final String NDBENCH_TRAIN_FILE = DATA_FOLDER + "NDBench-training";

    public static ArrayList<ClientThread> Clients = new ArrayList<>();
    static int clientCount = 0;
    static String clientName="";

    private static void csvToJSON(String csvFile, String jsonFile) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File newFile = new File(jsonFile);
        Pattern pattern = Pattern.compile(",");
        try (BufferedReader in = new BufferedReader(new FileReader(csvFile))) {
            List<Workload> workloads = in.lines().skip(1).map(line -> {
                String[] x = pattern.split(line);
                return new Workload(Integer.parseInt(x[0]), Integer.parseInt(x[1]), Integer.parseInt(x[2]),
                        Double.parseDouble(x[3]), Double.parseDouble(x[4]));
            }).collect(Collectors.toList());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(newFile, workloads); // writes JSON files
        }
    }

    public static void main(String[] args) throws Exception {

        //transform csv files to json in same directory
        csvToJSON(DVD_TEST_FILE + ".csv", DVD_TEST_FILE + ".json");
        csvToJSON(DVD_TRAIN_FILE + ".csv", DVD_TRAIN_FILE + ".json");
        csvToJSON(NDBENCH_TEST_FILE + ".csv", NDBENCH_TEST_FILE + ".json");
        csvToJSON(NDBENCH_TRAIN_FILE + ".csv", NDBENCH_TRAIN_FILE + ".json");

        //create the welcoming server's socket
        ServerSocket welcomeSocket = new ServerSocket(6789);
        //thread to always listen for new connections from clients
        new Thread (new Runnable(){ @Override
        public void run() {
            Socket connectionSocket;
            DataOutputStream outToClient;
            while (!welcomeSocket.isClosed()) {
                try {
                    //when a new client connect, accept this connection and assign it to a new connection socket
                    connectionSocket = welcomeSocket.accept();
                    System.out.println("Connection from: " + connectionSocket.getInetAddress().getHostAddress());
                    //create a new output stream and send the message "You are connected" to the client
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    outToClient.writeBytes("{\"Connected\":\"Yes\"}");
                    clientCount++;
                    //add the new client to the client's array
                    Clients.add(new ClientThread(clientCount, connectionSocket, Clients, clientName));
                    //start the new client's thread
                    Clients.get(Clients.size() - 1).start();
                }
                catch (Exception ex) { }
            }
        }}).start();


    }


}