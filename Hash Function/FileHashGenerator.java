import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class FileHashGenerator {
    public static String getSHA256Hash(String filePath) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream fis = new FileInputStream(filePath)) {
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;
            // Read file data and update digest
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        // Get hash bytes
        byte[] bytes = digest.digest();
        // Convert to hex format
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        String sourceHash = getSHA256Hash("C:/Users/User/Desktop./B44IS/file.txt");
        String destHash = getSHA256Hash("C:/Users/User/Desktop/B44IS/file1.txt");

        System.out.println("Source: " + sourceHash);
        System.out.println("Dest:   " + destHash);
        System.out.println("Match:  " + sourceHash.equals(destHash));
    }
}
