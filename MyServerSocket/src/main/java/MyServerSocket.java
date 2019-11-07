import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import commProto.Communicate;

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

    private MyServerSocket(String ipAddress) throws Exception {
        if (ipAddress != null && !ipAddress.isEmpty())
            this.server = new ServerSocket(0, 1, InetAddress.getByName(ipAddress));
        else
            this.server = new ServerSocket(0, 1, InetAddress.getLocalHost());
    }

    private RFW splitRFW(Communicate.batch_request RFW_R){
        RFW batch = new RFW(RFW_R.getBenchType(),RFW_R.getWrkLdMetric(), RFW_R.getBatchUnit(),RFW_R.getBatchID(),RFW_R.getBatchSize());
        return batch;
    }


    private RFD processBatch (List<Workload> wkld, RFW batch){
        /*
        * read values stored in batch
        * perform required tasks in the list
        * tasks:
        * - create the batches based on batch unit
        * - counter for batch numbers
        * etc etc
        * make sure batch unit > batch size
        * also make sure you never exceed list size.
        * */

        int bUnit = batch.getBatchUnit();
        int bSize = batch.getBatchSize();
        List<List<Double>> batchAns = new LinkedList<>();
        List<Double> singleAns = new LinkedList<>();
        int sizeCounter = batch.getBatchID()-1;
        while(sizeCounter < bSize){
            for(int downCount = (sizeCounter+1)*bUnit; downCount > bUnit*sizeCounter; downCount--){ // set downCount to top value of
                singleAns.add(wkld.get((sizeCounter+1)*bUnit-downCount).benchGet(batch.getBenchType()));                           // add elements to linkedlist from start of list.
            }
            batchAns.add(singleAns);
            singleAns.clear();
            sizeCounter++;
        }                       // the list of batches has been made and can be returned. sizeCounter is how many batches exist.
        RFD ans = new RFD(batch.getID(),sizeCounter+1,batchAns);
        return ans;
    }



    private void listen() throws Exception {
        String data = null;
        Socket client = this.server.accept();
        String clientAddress = client.getInetAddress().getHostAddress();
        System.out.println("\r\nNew connection from " + clientAddress);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));
        Communicate.batch_request RFW_Request = Communicate.batch_request.parseFrom(client.getInputStream());

        RFW newBatch = splitRFW(RFW_Request);
        List<Workload> currentList;
        switch (newBatch.getBenchType()){
            case 1:
                currentList = DVDTesting;
                break;
            case 2:
                currentList = DVDTraining;
                break;
            case 3:
                currentList = NDBenchTesting;
                break;
            default:
                currentList = NDBenchTraining;
                break;
        }
        RFD ansServer = processBatch(currentList,newBatch);

        String reply = ansServer.toString();
        //@Aida please try typing the sending function here.


        
//        Communicate.batch_ans.Builder answer = Communicate.batch_ans.newBuilder();
//        answer.setLastBatchID(ansServer.getLastBatchID());
//        answer.setRFWID(ansServer.getRFWid());
//        answer.setSamplesRequested(ansServer.getSamplesRequested().toString());
//        Communicate.batch_ans rep = answer.build();
//        StreamObserver<Communicate.batch_ans> resp;

        while ((data = in.readLine()) != null) {
            System.out.println("\r\nMessage from " + clientAddress + ": " + data);
        }
        //Serialize to byte[].
        // Send serialized byte[] to client via gRPC.


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
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(System.out, workloads);
            return workloads;
        }
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
