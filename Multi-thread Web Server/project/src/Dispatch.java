import java.io.IOException;

public class Dispatch {
    public static Response dispatch(HttpRequest httpRequest) throws IOException {
        String data = null;

        Response httpResponse = new Response(StatusCode.OK, httpRequest.getVersion());
        
        httpResponse.setHeaders("Connection", httpRequest.getHeaders().getOrDefault("Connection", "close"));
        httpResponse.setHeaders("Content-Encoding", "null");

        String contentType = httpRequest.getHeaders().getOrDefault("Content-Type", "text/plain");

        if (contentType.equals("image/jpeg") || contentType.equals("image/png") || 
            httpRequest.getUri().endsWith(".png") || httpRequest.getUri().endsWith(".jpg")) {
            
            data = Control_Manager.getInstance().encodeImageToBase64(httpRequest.getUri());

            httpResponse.setHeaders("Content-Type", "image/*");
            httpResponse.setHeaders("Content-Length", String.valueOf(data.getBytes().length));
        } else if (contentType.equals("text/plain")) {

            data = Control_Manager.getInstance().readFileContent(httpRequest.getUri());

            httpResponse.setHeaders("Content-Type", "text/plain");
            httpResponse.setHeaders("Content-Length", String.valueOf(data.getBytes().length));
        }

        httpResponse.setBody(data);

        return httpResponse;
    }
}