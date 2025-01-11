package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

class ServerTest {
    int port = 6379;

    @Test
    void testServerConstructor() throws IOException {
        Server instance1 = Server.getInstance(port);
        Server instance2 = Server.getInstance(port);

        assertSame(instance1, instance2, "server should be created once");
    }

    @Nested
    class ClientConnectionTest {
        private static Server server;

        @BeforeAll
        static void setupServer() throws Exception {
            server = Server.getInstance(6379);
            server.start();
        }

        @AfterAll
        static void stopServer() {
            server.stop();
        }

        @Test
        void testClientConnection() throws Exception {
            ExecutorService clientExecutor = Executors.newSingleThreadExecutor();

            clientExecutor.submit(() -> {
                try {
                    Socket clientSocket = new Socket("localhost", 6379);
                    OutputStream output = clientSocket.getOutputStream();
                    InputStream input = clientSocket.getInputStream();

                    output.write("*1\r\n$4\r\nPING\r\n".getBytes());
                    byte[] response = input.readAllBytes();

                    assertEquals(response.toString(), "+PONG\r\n");
                } catch (Exception e) {
                    fail("Client connection failed, error: " + e.getMessage());
                }
            });

            clientExecutor.shutdown();
        }
    }
}
