package server;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    @Test
    void testServerConstructor() throws IOException {
        int port = 6379;
        Server instance1 = Server.getInstance(port);
        Server instance2 = Server.getInstance(port);

        assertSame(instance1, instance2, "server should be created once");
    }

    @Test
    void testPortBinding() throws IOException {
        int port = 6379;
        Server server = Server.getInstance(port);

        // assertEquals(port);
    }
}
