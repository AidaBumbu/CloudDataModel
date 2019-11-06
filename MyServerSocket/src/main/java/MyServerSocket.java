import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
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
    private static final String DVD_TEST_FILE = DATA_FOLDER + "DVD-testing.csv";
    private static final String DVD_TRAIN_FILE = DATA_FOLDER + "DVD-training.csv";
    private static final String NDBENCH_TEST_FILE = DATA_FOLDER + "NDBench-testing.csv";
    private static final String NDBENCH_TRAIN_FILE = DATA_FOLDER + "NDBench-training.csv";
    private ServerSocket server;

    private static List<Workload> DVDTesting = new LinkedList<>();
    private static List<Workload> DVDTraining = new LinkedList<>();
    private static List<Workload> NDBenchTesting = new LinkedList<>();
    private static List<Workload> NDBenchTraining = new LinkedList<>();

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
            RFW request = JSONtoRequest(data); //deserialize json to request
            RFD response = getBatch(request); //fetch batch and data for request
            String sResponse = responseToJSON(response); //Serialize the response
            //Need to send to client the response

        }
    }

    private InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }

    private int getPort() {
        return this.server.getLocalPort();
    }

    private static List<Workload> csvToJSON(String csvFile) throws Exception {
        Pattern pattern = Pattern.compile(",");
        try (BufferedReader in = new BufferedReader(new FileReader(csvFile))) {
            List<Workload> workloads = in.lines().skip(1).map(line -> {
                String[] x = pattern.split(line);
                return new Workload(Integer.parseInt(x[0]), Integer.parseInt(x[1]), Integer.parseInt(x[2]),
                        Double.parseDouble(x[3]), Double.parseDouble(x[4]));
            }).collect(Collectors.toList());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(System.out, workloads);
            return workloads;
        }
    }

    //Deserialize request from client
    private RFW JSONtoRequest(String json) throws Exception{
        return mapper.readValue(json, RFW.class);
    }

    //Serialize response before sending to client
    private String responseToJSON(RFD response) throws Exception{
        return mapper.writeValueAsString(response);
    }

    private static RFD getBatch(RFW request){

        //TODO Elie can you look into getting only one attribute from the json file?
        //Here should be the logic of the program used to return a RFD (response)
        return null; //added this only to not have any error

    }

    public static void main(String[] args) throws Exception {

        DVDTesting = csvToJSON(DVD_TEST_FILE);
        DVDTraining = csvToJSON(DVD_TRAIN_FILE);
        NDBenchTesting = csvToJSON(NDBENCH_TEST_FILE);
        NDBenchTraining = csvToJSON(NDBENCH_TRAIN_FILE);

        MyServerSocket app = new MyServerSocket(null);   //instantiate server
        System.out.println("\r\nRunning Server: " +
                "Host=" + app.getSocketAddress().getHostAddress() +
                " Port=" + app.getPort());
        app.listen();
    }

}
