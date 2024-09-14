import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Tracker {
    private static final Tracker INSTANCE;

    static {
        try {
            INSTANCE = new Tracker();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final BufferedWriter bw;

    private Tracker() throws IOException {
        bw = new BufferedWriter(new FileWriter("./history.log", true));
    }

    public static void info(String msg) throws IOException {
        INSTANCE.bw.write(msg);
        INSTANCE.bw.newLine();
        INSTANCE.bw.flush();
    }

    public static void close() throws IOException {
        INSTANCE.bw.flush();
        INSTANCE.bw.close();
    }
}