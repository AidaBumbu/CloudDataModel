import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MyClientSocket {

    private Socket socket;
    private Scanner scanner;
    private MyClientSocket(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.scanner = new Scanner(System.in);
    }

   //Sends message to the server
    private void start() throws IOException {
        String input;
        while (true) {
            input = scanner.nextLine();
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            out.println(input);
            out.flush();
        }
    }
public class dataAnalysis {
        private long CPUUtilization_Average;
        private long NetworkIn_Average;
        private long NetworkOut_Average;
        private long MemoryUtilization_Average;
        private long Final_Target;
        dataAnalysis(long a, long b, long c, long d, long e){
            CPUUtilization_Average = a;
            NetworkIn_Average = b;
            NetworkOut_Average = c;
            MemoryUtilization_Average = d;
            Final_Target = e;
        }
}
    //Serialize to JSON
    private void toJSON(String[] query){
        Pattern pattern = Pattern.compile(",");
        try (BufferedReader in = new BufferedReader(new FileReader(query[1]))) {
            List<dataAnalysis> players = in .lines().skip(1).map(line -> {
                    String[] x = pattern.split(line);
            return new dataAnalysis(( Long.parseLong(x[0])), Long.parseLong(x[1]), Long.parseLong(x[2]), Long.parseLong(x[3]), Long.parseLong(x[4]));
    }).collect(Collectors.toList());
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(System.out, players);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
