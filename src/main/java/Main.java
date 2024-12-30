import java.io.IOException;
import server.Server;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible
    // when running tests.
    System.out.println("Logs from your program will appear here!");

    Server server = null;
    try {
      server = Server.getInstance(6379);

      server.start();

      Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    } catch (IOException e) {
      System.err.printf("Error while creating the server, error: %s\n", e.getMessage());
    }
  }
}
