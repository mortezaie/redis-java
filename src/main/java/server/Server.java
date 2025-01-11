package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Server {
    private static Server INSTANCE;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private boolean running = false;

    private Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        threadPool = Executors.newCachedThreadPool();
        System.out.printf("Server created on port: %d\n", port);
    }

    public static Server getInstance(int port) throws IOException {
        if (INSTANCE == null) {
            synchronized (Server.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Server(port);
                }
            }
        }

        return INSTANCE;
    }

    public void start() {
        if (running) {
            return;
        }

        running = true;

        threadPool.execute(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    System.out.println("Received new connection");

                    threadPool.execute(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (!running) {
                        System.out.println("Server stopped");
                    } else {
                        System.out.printf("Error accepting connection: %s\n", e.getMessage());
                    }
                }
            }
        });
    }

    private void handleClient(Socket clientSocket) {
        try {
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            byte[] buffer = new byte[1024];
            int readBytes;
            while ((readBytes = input.read(buffer)) != -1) {
                output.write("+PONG\r\n".getBytes());
            }
        } catch (IOException e) {
            System.err.printf("Error while processing client, error: %s\n", e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.printf("Error while closing client, error: %s\n", e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
            threadPool.shutdown();

            System.out.println("Server stopped successfully");
        } catch (IOException e) {
            System.err.printf("Error while stopping the server, error: %s\n", e.getMessage());
        }
    }
}
