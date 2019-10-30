import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
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

    //Serialize to JSON
    private void toJSON(String[] query){
        Pattern pattern = Pattern.compile(",");
        try (BufferedReader in = new BufferedReader(new FileReader(query[1]))) {
            List < Player > players = in .lines().skip(1).map(line -> {
                    String[] x = pattern.split(line);
            return new Player(Integer.parseInt(x[0]), x[1], x[2], x[3], Integer.parseInt(x[4]));
    }).collect(Collectors.toList());
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(System.out, players);
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
