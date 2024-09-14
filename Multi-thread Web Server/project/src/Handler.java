import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Handler implements Runnable {
    private final SocketChannel client;
    private final SelectionKey key;

    public Handler(SelectionKey key) throws IOException {
        this.key=key;
        this.client = (SocketChannel) key.channel();
        key.cancel();
        client.configureBlocking(true);
    }

    public Response process(HttpRequest httpRequest) throws IOException {
        assert httpRequest != null;
        Response response;
        File file = new File(Server.rootDir, httpRequest.getUri());
        // if file exist and not directory
        if (file.exists() && !file.isDirectory()) {
            // check modified time
            String modifiedSince = httpRequest.getHeaders().getOrDefault("If-Modified-Since", null);
            System.out.println(modifiedSince);
            System.out.println(file.lastModified());
            // if not modified
            if (modifiedSince != null && file.lastModified() == Long.parseLong(modifiedSince)) {
                response = Response.notModified(httpRequest.getVersion());
            } else if (httpRequest.getMethod() == Method.GET) {
                // if not head method
                response = Dispatch.dispatch(httpRequest);
            } else {
                // head method
                response = Response.ok(httpRequest.getVersion());
                response.setHeaders("Content-Length", String.valueOf(file.length()));
            }
            // set last modified header
            response.setHeaders("Last-Modified", String.valueOf(file.lastModified()));
        } else {
            // file not found
            response = Response.fileNotFound(httpRequest.getVersion());
        }
        return response;
    }

    @Override
    public void run() {
        try{
            ByteBuffer buffer = ByteBuffer.allocate(1000000);
            while(true){
                buffer.clear();
                // get http request
                int bytesRead = client.read(buffer);
                if (bytesRead == -1) {
                    continue;
                }
                String request = new String(buffer.array()).trim();
                if (request.isEmpty()) {
                    continue;
                }
                System.out.println(request);
                HttpRequest httpRequest = HttpRequest.parse(request);

                // check keep alive
                boolean shouldClose = request.contains("Connection: close")||request.contains("HTTP/1.0");

                // process request, get response
                Response response;
                if (httpRequest.getMethod() == null) {
                    response = Response.badRequest(HttpVersion.HTTP11);
                } else {
                    response = process(httpRequest);
                }
                // log info
                Tracker.info(String.format("%s, %s, %s, %s", client.getRemoteAddress(), System.currentTimeMillis(), httpRequest.getUri(), response.getHeaders().get("Content-Type")));
                String res=response.toString();

                buffer.clear();
                buffer.put(res.getBytes());
                buffer.flip();
                while (buffer.hasRemaining()) {
                    client.write(buffer);
                }

                // if connection is close
                if(shouldClose){
                    client.close();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
