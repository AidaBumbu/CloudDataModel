import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ClientThread extends Thread{

    private static final String ROOT_FOLDER = System.getProperty("user.dir");
    private static final String DATA_FOLDER = ROOT_FOLDER.substring(0, ROOT_FOLDER.length()-14) + "Workload Data" + File.separator ;
    private static final String DVD_TEST_FILE = DATA_FOLDER + "DVD-testing";
    private static final String DVD_TRAIN_FILE = DATA_FOLDER + "DVD-training";
    private static final String NDBENCH_TEST_FILE = DATA_FOLDER + "NDBench-testing";
    private static final String NDBENCH_TRAIN_FILE = DATA_FOLDER + "NDBench-training";

    public int number; //client id
    public String name;
    public Socket connectionSocket; //client connection socket
    ArrayList<ClientThread> clients; //list of all clients connected to the server

    public ClientThread(int number, Socket connectionSocket, ArrayList<ClientThread> Clients, String userName) {
        this.number = number;
        this.connectionSocket = connectionSocket;
        this.clients = clients;
        this.name = userName;
    }

    private static List<Double> getListOfMetrics(String jsonFile, String metric) throws Exception{
        List<Double> listOfMetrics = new LinkedList<>();
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(jsonFile))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray workloadList = (JSONArray) obj;

            switch (metric){
                case "cpu":
                    //Iterate over array and get the metric
                    workloadList.forEach( workload -> {
                        JSONObject line = (JSONObject) workload;
                        listOfMetrics.add(((Long) line.get("cpuutilization_Average")).doubleValue());
                    });
                    break;
                case "networkin":
                    //Iterate over array and get the metric
                    workloadList.forEach( workload -> {
                        JSONObject line = (JSONObject) workload;
                        listOfMetrics.add(((Long) line.get("networkIn_Average")).doubleValue());
                    });
                    break;
                case "networkout":
                    //Iterate over array and get the metric
                    workloadList.forEach( workload -> {
                        JSONObject line = (JSONObject) workload;
                        listOfMetrics.add(((Long) line.get("networkOut_Average")).doubleValue());
                    });
                    break;
                case "memory":
                    //Iterate over array and get the metric
                    workloadList.forEach( workload -> {
                        JSONObject line = (JSONObject) workload;
                        listOfMetrics.add((Double) line.get("memoryUtilization_Average"));
                    });
                    break;
            }


        }
        return listOfMetrics;
    }

    private static RFD getBatch(@NotNull JSONObject request) throws Exception{

        int batchID = ((Long) request.get("batchID")).intValue() - 1; //first batch will be batch 0
        int batchUnit = ((Long) request.get("batchUnit")).intValue();
        int batchSize = ((Long) request.get("batchSize")).intValue();
        String jsonFile = null;
        List<Workload> workloadList = new LinkedList<>();

        switch ((String) request.get("benchmark")) {
            case "dvdtest":
                jsonFile = DVD_TEST_FILE + ".json";
                break;
            case "dvdtrain":
                jsonFile = DVD_TRAIN_FILE + ".json";
                break;
            case "ndbenchtest":
                jsonFile = NDBENCH_TEST_FILE + ".json";
                break;
            case "ndbenchtrain":
                jsonFile = NDBENCH_TRAIN_FILE + ".json";
                break;
        }
        List<Double> listOfMetrics = getListOfMetrics(jsonFile, ((String) request.get("metric")).toLowerCase());
        List<Double> batchMetrics = listOfMetrics.subList(batchUnit*batchID, (batchID + batchSize)*batchUnit);

        return new RFD(((Long) request.get("id")).intValue(),batchID+batchSize-1, batchMetrics);
    }

    public void run() {
        try {

            //create a buffer reader and connect it to the client's connection socket
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            String clientInput;
            DataOutputStream outToClient;
            String data = null;

            while ((data = inFromClient.readLine()) != null) {
                System.out.println("\r\nMessage: " + data); //For testing purpose, can be removed afterward
                JSONParser jsonParser = new JSONParser();
                Object obj = jsonParser.parse(data);
                JSONObject request = (JSONObject) obj;
                ObjectMapper mapper = new ObjectMapper();
                RFD response = getBatch(request); //fetch batch and data for request
                String sResponse = mapper.writeValueAsString(response); //Send json response to client

                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    outToClient.writeBytes(sResponse);

            }
        }
        catch(Exception ex) {
        }
    }
}