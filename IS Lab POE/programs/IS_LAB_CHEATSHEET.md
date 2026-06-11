# IS Lab - All 5 Experiments Cheat Sheet

---

## What stays the same in every experiment
- The entire GUI template (fields, buttons, layout)
- Socket connect / send / receive flow
- DB lookup on server
- File download on client
- Only `// STUDENT CUSTOMIZE` sections change

---

## Expt 1 — Caesar Cipher (already in template)

### What to remember
Shift each letter by key value. Decrypt = shift by (26 - key).

### caesarEncrypt (Server & Client)
```java
private String caesarEncrypt(String text, int shift) {
    StringBuilder result = new StringBuilder();
    for (char c : text.toCharArray()) {
        if (Character.isLetter(c)) {
            char base = Character.isUpperCase(c) ? 'A' : 'a';
            result.append((char) ((c - base + shift) % 26 + base));
        } else {
            result.append(c);
        }
    }
    return result.toString();
}
```

### caesarDecrypt (Client only)
```java
private String caesarDecrypt(String text, int shift) {
    return caesarEncrypt(text, 26 - (shift % 26));
}
```

---

## Expt 1 — Transposition Cipher (alternative)

### transposeEncrypt (Server)
```java
private String transposeEncrypt(String text, int cols) {
    while (text.length() % cols != 0) text += "_";
    int rows = text.length() / cols;
    StringBuilder result = new StringBuilder();
    for (int c = 0; c < cols; c++)
        for (int r = 0; r < rows; r++)
            result.append(text.charAt(r * cols + c));
    return result.toString();
}
```

### transposeDecrypt (Client)
```java
private String transposeDecrypt(String text, int cols) {
    int rows = text.length() / cols;
    char[][] grid = new char[rows][cols];
    int idx = 0;
    for (int c = 0; c < cols; c++)
        for (int r = 0; r < rows; r++)
            grid[r][c] = text.charAt(idx++);
    StringBuilder result = new StringBuilder();
    for (int r = 0; r < rows; r++)
        for (int c = 0; c < cols; c++)
            result.append(grid[r][c]);
    return result.toString().replace("_", "");
}
```

---

## Expt 2 — DES / AES

### DESUtil.java (new file)
```java
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.util.Base64;

public class DESUtil {
    private static final String SECRET_KEY = "12345678"; // 8 chars for DES

    private static SecretKey getKey() throws Exception {
        DESKeySpec keySpec = new DESKeySpec(SECRET_KEY.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        return keyFactory.generateSecret(keySpec);
    }

    public static String encrypt(String data) throws Exception {
        SecretKey key = getKey();
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKey key = getKey();
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
    }
}
```

### AESUtil.java (alternative — change 2 things from DES)
```java
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String SECRET_KEY = "1234567890123456"; // 16 chars for AES

    public static String encrypt(String data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
    }
}
```

### Server STUDENT CUSTOMIZE
```java
// replace caesarEncrypt method:
private String desEncrypt(String text) {
    try { return DESUtil.encrypt(text); }
    catch (Exception e) { return "0"; }
}
// and in handleClient, replace caesarEncrypt call:
String encryptedQP = desEncrypt(qpContent);
```

### Client STUDENT CUSTOMIZE
```java
// replace caesarDecrypt method:
private String desDecrypt(String text) {
    try { return DESUtil.decrypt(text); }
    catch (Exception e) { return "ERROR: " + e.getMessage(); }
}
// and in decryptAndDownload, replace caesarDecrypt call:
String decrypted = desDecrypt(encryptedQP);
```

---

## Expt 3 — Diffie-Hellman Key Exchange

### DHUtil.java (new file)
```java
public class DHUtil {
    public static final long P = 23;
    public static final long Q = 5;

    public static long generatePublicValue(long privateKey) {
        return (long)(Math.pow(Q, privateKey) % P);
    }

    public static long computeSharedSecret(long publicValue, long privateKey) {
        return (long)(Math.pow(publicValue, privateKey) % P);
    }
}
```

### Server STUDENT CUSTOMIZE (top of handleClient, before reading inputs)
```java
long serverPrivate = 6;
long serverPublic  = DHUtil.generatePublicValue(serverPrivate);
long clientPublic  = Long.parseLong(in.readLine());
out.println(serverPublic);
long sharedSecret  = DHUtil.computeSharedSecret(clientPublic, serverPrivate);
appendLog("Shared Secret K: " + sharedSecret);
// use sharedSecret as shift:
int shift = (int) sharedSecret;
```

### Client STUDENT CUSTOMIZE (top of requestQP, before sending inputs)
```java
long clientPrivate = 15;
long clientPublic  = DHUtil.generatePublicValue(clientPrivate);
out.println(clientPublic);   // must be println not print!
long serverPublic  = Long.parseLong(in.readLine());
long sharedSecret  = DHUtil.computeSharedSecret(serverPublic, clientPrivate);
qpArea.setText("Shared Secret K: " + sharedSecret + "\n\n");
```

### Expected output
Both sides print: **Shared Secret K: 4**

---

## Expt 4 — Hash Functions (SHA-256 / MD5)

### HashUtil.java (new file)
```java
import java.security.MessageDigest;
import java.util.Base64;

public class HashUtil {
    public static String sha256(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(md.digest(data.getBytes()));
    }

    public static String md5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return Base64.getEncoder().encodeToString(md.digest(data.getBytes()));
    }
}
```

### Server STUDENT CUSTOMIZE (in handleClient, before encrypting)
```java
String hash = HashUtil.sha256(qpContent);
appendLog("Hash: " + hash);
// add to send block:
out.println("OK");
out.println(hash);           // send hash before length
out.println(encryptedQP.length());
out.println(shift);
```

### Client STUDENT CUSTOMIZE
```java
// new field at top of class:
private String receivedHash;

// in requestQP, after reading "OK":
receivedHash = in.readLine();   // read hash before length

// in decryptAndDownload, after decrypting:
String computedHash = HashUtil.sha256(decrypted);
if (receivedHash.equals(computedHash)) {
    qpArea.setText("✓ HASH MATCH - QP Authentic!\n\n" + decrypted);
} else {
    qpArea.setText("✗ HASH MISMATCH - QP Tampered!");
}
```

---

## Expt 5 — Digital Signature (RSA)

### RSAUtil.java (new file)
```java
import java.security.*;
import java.util.Base64;

public class RSAUtil {
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        return keyGen.generateKeyPair();
    }

    public static String sign(String data, PrivateKey privateKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(data.getBytes());
        return Base64.getEncoder().encodeToString(sig.sign());
    }

    public static boolean verify(String data, String signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data.getBytes());
        return sig.verify(Base64.getDecoder().decode(signature));
    }
}
```

### Server STUDENT CUSTOMIZE
```java
// new field + constructor:
private KeyPair keyPair;
keyPair = RSAUtil.generateKeyPair();

// in handleClient, before encrypting:
String signature    = RSAUtil.sign(qpContent, keyPair.getPrivate());
String publicKeyStr = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
appendLog("QP signed with RSA private key");

// in send block:
out.println("OK");
out.println(signature);       // send signature
out.println(publicKeyStr);    // send public key
out.println(encryptedQP.length());
out.println(shift);
```

### Client STUDENT CUSTOMIZE
```java
// new fields at top of class:
private String receivedSignature;
private String receivedPublicKey;

// in requestQP, after reading "OK":
receivedSignature = in.readLine();
receivedPublicKey = in.readLine();

// in decryptAndDownload, after decrypting:
byte[] keyBytes   = Base64.getDecoder().decode(receivedPublicKey);
PublicKey pubKey  = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
boolean valid     = RSAUtil.verify(decrypted, receivedSignature, pubKey);
if (valid) {
    qpArea.setText("✓ SIGNATURE VALID - QP Authentic!\n\n" + decrypted);
} else {
    qpArea.setText("✗ SIGNATURE INVALID - QP Tampered!");
}
```

---

## Quick Reference — What util class does each experiment need?

| Expt | Util File       | Key Line to Remember                        |
|------|----------------|---------------------------------------------|
| 1    | none           | `(c - base + shift) % 26 + base`            |
| 2    | DESUtil.java   | `DESUtil.encrypt(text)` / `.decrypt(text)`  |
| 3    | DHUtil.java    | `generatePublicValue(private)` → exchange → `computeSharedSecret(otherPublic, private)` |
| 4    | HashUtil.java  | `HashUtil.sha256(text)` → compare           |
| 5    | RSAUtil.java   | `RSAUtil.sign(text, privateKey)` → `RSAUtil.verify(text, sig, publicKey)` |

## Quick Reference — What extra lines does each experiment send?

| Expt | Extra lines sent by server (before length) |
|------|--------------------------------------------|
| 1    | none (just shift)                          |
| 2    | none (just 0 as placeholder)               |
| 3    | serverPublic (DH exchange at very top)     |
| 4    | hash                                       |
| 5    | signature, publicKeyStr                    |
