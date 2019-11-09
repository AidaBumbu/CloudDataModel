import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MyServerSocket {

    //Provide Workload Data Files
    private static final String ROOT_FOLDER = System.getProperty("user.dir");
    private static final String DATA_FOLDER = ROOT_FOLDER.substring(0, ROOT_FOLDER.length()-14) + "Workload Data" + File.separator ;
    private static final String DVD_TEST_FILE = DATA_FOLDER + "DVD-testing";
    private static final String DVD_TRAIN_FILE = DATA_FOLDER + "DVD-training";
    private static final String NDBENCH_TEST_FILE = DATA_FOLDER + "NDBench-testing";
    private static final String NDBENCH_TRAIN_FILE = DATA_FOLDER + "NDBench-training";
    private ServerSocket server;

    private static ObjectMapper mapper = new ObjectMapper();

    private MyServerSocket(String ipAddress) throws Exception {
        if (ipAddress != null && !ipAddress.isEmpty())
            this.server = new ServerSocket(0, 1, InetAddress.getByName(ipAddress));
        else
            this.server = new ServerSocket(0, 1, InetAddress.getLocalHost());
    }

    private void listen() throws Exception {
        String data = null;
        Socket client = this.server.accept();
        String clientAddress = client.getInetAddress().getHostAddress();
        System.out.println("\r\nNew connection from " + clientAddress);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));
        while ((data = in.readLine()) != null) {
            System.out.println("\r\nMessage from " + clientAddress + ": " + data); //For testing purpose, can be removed afterward
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(data);
            JSONObject request = (JSONObject) obj;
           // ObjectMapper map = new ObjectMapper();
            //map.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
           // map.addMixIn(RFW.class, RFWmixIn.class);
           // RFW request = mapper.readValue(data, RFW.class); //deserialize json to request
            //respond(request, client);

            RFD response = getBatch(request); //fetch batch and data for request

            DataOutputStream outToClient = new DataOutputStream(client.getOutputStream());
            //String sResponse = mapper.writeValueAsString(response); //Send the response as string
            StringBuilder builder = new StringBuilder();
            String sResponse = response.getSamplesRequested().toString();

            System.out.print(sResponse);
            outToClient.writeUTF(sResponse);
            outToClient.flush();
            //outToClient.writeObject(response);


        }
    }

    private void respond(JSONObject request, Socket client) throws Exception {

        RFD response = getBatch(request); //fetch batch and data for request

        DataOutputStream outToClient = new DataOutputStream(client.getOutputStream());
        String sResponse = mapper.writeValueAsString(response); //Send the response as string
        outToClient.writeBytes(sResponse);
    }

    private InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }

    private int getPort() {
        return this.server.getLocalPort();
    }

    private static void csvToJSON(String csvFile, String jsonFile) throws Exception {
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

    private static RFD getBatch(@NotNull JSONObject request) throws Exception{

        int batchID = ((Long) request.get("batchID")).intValue() - 1; //first batch will be batch 0
        int batchUnit = ((Long) request.get("batchUnit")).intValue();
        int batchSize = ((Long) request.get("batchSize")).intValue();
        String jsonFile = null;
        List<Workload> workloadList = new LinkedList<>();
        String metric = null;
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
        switch ((String) request.get("metric")){
            case "cpu":
                metric = "cpuutilization_Average";
                break;
            case"networkin":
                metric = "networkIn_Average";
                break;
            case"networkout":
                metric = "networkOut_Average";
            case "memory":
                metric = "memoryUtilization_Average";
        }

        List<Double> listOfMetrics = getListOfMetrics(jsonFile, metric);
        List<Double> batchMetrics = listOfMetrics.subList(batchUnit*batchID, (batchID + batchSize)*batchUnit);

        return new RFD(((Long) request.get("id")).intValue(),batchID+batchSize-1, batchMetrics);
    }

    private static List<Double> getListOfMetrics(String jsonFile, String metric) throws Exception{
        List<Double> listOfMetrics = new LinkedList<>();

        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(jsonFile))
        {
            //Read JSON file

            Object obj = jsonParser.parse(reader);
            JSONArray workloadList = (JSONArray) obj;
            for(Object o : workloadList){
                if(o instanceof JSONObject){
                    listOfMetrics.add(Double.parseDouble(((JSONObject) o).get(metric).toString()));
                }
            }
            //Iterate over array and get the metric
//            workloadList.forEach( workload -> {
//                JSONObject line = (JSONObject) workload;
//                listOfMetrics.add((Double) line.get(metric));
//            });
        }
        return listOfMetrics;
    }

    public static void main(String[] args) throws Exception {
        //transform csv files to json in same directory
        csvToJSON(DVD_TEST_FILE + ".csv", DVD_TEST_FILE + ".json");
        csvToJSON(DVD_TRAIN_FILE + ".csv", DVD_TRAIN_FILE + ".json");
        csvToJSON(NDBENCH_TEST_FILE + ".csv", NDBENCH_TEST_FILE + ".json");
        csvToJSON(NDBENCH_TRAIN_FILE + ".csv", NDBENCH_TRAIN_FILE + ".json");

        MyServerSocket app = new MyServerSocket(null);   //instantiate server
        System.out.println("\r\nRunning Server: " +
                "Host=" + app.getSocketAddress().getHostAddress() +
                " Port=" + app.getPort());
        app.listen();
    }

}
