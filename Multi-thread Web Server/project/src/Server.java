import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    // set root directory
    public final static File rootDir = new File("C:\\");
    // Implement http1.1 using non-blocking sockets
    private final ServerSocketChannel serverSocket;
    // Multiplexing
    private final Selector selector;
    private final ExecutorService pool;

    public Server(int port, int poolSize) throws IOException {
        // init multiplexing and socket, register event
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000, 16);
        server.start();
        server.stop();
    }

    public void start() throws IOException {
        while (true) {
            // check multiplexing
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            // iterate the prepared sockets
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    // connect
                    SocketChannel client = serverSocket.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("Connected to: " + client);
                }else if (key.isReadable()) {
                    System.out.println(key);
                    // Multi-threading socket processing
                    pool.execute(new Handler(key));
                }
            }
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
        pool.shutdown();
    }
}
