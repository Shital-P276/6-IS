import java.security.MessageDigest;
import java.util.*;

public class HashUtil {
    public static String sha256(String data) throws Exception{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(md.digest(data.getBytes()));
    }    

    public static String md5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return Base64.getEncoder().encodeToString(md.digest(data.getBytes()));
    }
}
