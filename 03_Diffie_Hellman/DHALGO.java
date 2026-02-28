import java.math.BigInteger;
import java.security.SecureRandom;

public class DHALGO {
    public static void main(String[] args) {
        // 1. Agree on public values p and g (Commonly used safe prime)
        BigInteger p = new BigInteger("23"); // In real life, use a 2048-bit prime
        BigInteger g = new BigInteger("5");

        // 2 & 3. Generate private keys a and b
        // For simplicity, we use random numbers, or you can use: new BigInteger("6");
        SecureRandom random = new SecureRandom();
        BigInteger a = new BigInteger(8, random); // Client private key
        BigInteger b = new BigInteger(8, random); // Server private key

        // 4. Client computes public value A = g^a mod p
        BigInteger A = g.modPow(a, p);

        // 5. Server computes public value B = g^b mod p
        BigInteger B = g.modPow(b, p);

        // 6. Exchange A and B (This happens over the network in reality)
        System.out.println("Client Public (A): " + A);
        System.out.println("Server Public (B): " + B);

        // 7. Client computes shared key K = B^a mod p
        BigInteger kClient = B.modPow(a, p);

        // 8. Server computes shared key K = A^b mod p
        BigInteger kServer = A.modPow(b, p);

        // 9. Verify both keys are identical
        System.out.println("Client Shared Key: " + kClient);
        System.out.println("Server Shared Key: " + kServer);
        System.out.println("Keys Match: " + kClient.equals(kServer));
    }
}