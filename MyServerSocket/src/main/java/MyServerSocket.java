import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

//Todo I can't import none of those classes

public class MyServerSocket {
    private ServerSocket server;

    public MyServerSocket(String ipAddress) throws Exception {
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
            System.out.println("\r\nMessage from " + clientAddress + ": " + data);
        }

    }

    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }

    public int getPort() {
        return this.server.getLocalPort();
    }

    public List<String[] > generateList(String filePath){
        String line = "";
        String cvsSplitBy = ",";
        List<String[]> dataSet = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] dataLine = line.split(cvsSplitBy);
                dataSet.add(dataLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataSet;
    }

    //https://www.novixys.com/blog/convert-csv-json-java/
    //TODO: I tried that but i'm having trouble with the dependencies
    private void csvToJSON(String csvFile) throws Exception {
        try (InputStream in = new FileInputStream(csvFile);) {
            Scanner csv = new Scanner(csvFile);
            ArrayList<String> fieldNames = null;
            if ( csv.hasNext() ) fieldNames = new ArrayList<String>(Collections.singleton(csv.next()));
            List<Map<String,String>> list = new ArrayList<>();
            while (csv.hasNext()) {
                List<String> x = Collections.singletonList(csv.next());
                Map<String,String> obj = new LinkedHashMap<>();
                for (int i = 0 ; i < fieldNames.size() ; i++) {
                    obj.put(fieldNames.get(i), x.get(i));
                }
                list.add(obj);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(System.out, list);
        }

    }

    public static void main(String[] args) throws Exception {


        MyServerSocket app = new MyServerSocket(args[0]);   //instantiate server
        System.out.println("\r\nRunning Server: " +
                "Host=" + app.getSocketAddress().getHostAddress() +
                " Port=" + app.getPort());

        app.listen();
    }

}
