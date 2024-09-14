import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

public class Control_Manager {

    private static class Control_ManagerHolder {
        private static final Control_Manager INSTANCE = new Control_Manager();
    }

    private Control_Manager() {
    }

    public static Control_Manager getInstance() {
        return Control_ManagerHolder.INSTANCE;
    }

    public String readFileContent(String filePath) throws IOException {
        File file = new File(Server.rootDir, filePath);
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found: " + filePath);
        } catch (IOException e) {
            throw new IOException("Error reading the file: " + filePath, e);
        }

        return contentBuilder.toString();
    }

    public String encodeImageToBase64(String imagePath) throws IOException {
        File imageFile = new File(Server.rootDir, imagePath);
        BufferedImage image = ImageIO.read(imageFile);
        if (image == null) {
            throw new IOException("The file could not be opened as an image: " + imagePath);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String fileExtension = getFileExtension(imagePath);

        if (ImageIO.write(image, fileExtension, outputStream)) {
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } else {
            throw new IOException("Unsupported image format for: " + imagePath);
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        } else {
            return ""; 
        }
    }
}