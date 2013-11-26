import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 31.08.13
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(10000);

            Socket client = server.accept();

            InputStreamReader streamReader = new InputStreamReader(client.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
