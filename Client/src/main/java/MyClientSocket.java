
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;

import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class MyClientSocket {

    private Socket socket;
    private Scanner scanner;

    private static ObjectMapper mapper = new ObjectMapper();

    private MyClientSocket(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.scanner = new Scanner(System.in);
    }

   //Sends message to the server
    private void start() throws Exception {
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

            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            if (req != null){
                out.println(req);
                out.flush();
            }

            ObjectInputStream inFromServer = new ObjectInputStream(this.socket.getInputStream());
            JSONObject response = (JSONObject) inFromServer.readObject();
            System.out.println("Server response for request is\n" + response.toJSONString());

        }
    }



    public static void main(String[] args) throws Exception {

        //Connect to server by entering IPaddress and PortNumber of Server
        MyClientSocket client = new MyClientSocket(
                InetAddress.getByName(args[0]),
                Integer.parseInt(args[1]));

        System.out.println("\r\nConnected to Server: " + client.socket.getInetAddress());
        client.start();
    }

}
