import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ExamServerGUI extends JFrame {
    private ServerSocket serverSocket;
    private Map<String, String> qpDatabase; 
    private JTextArea logArea;

    public ExamServerGUI() {
        qpDatabase = new HashMap<>();
        qpDatabase.put("10-CS101-1", "Q1: Explain DES. (10 marks)\nQ2: Explain Sockets. (5 marks)");
        qpDatabase.put("10-DS101-1", "Q1: Explain EDA. (10 marks)\nQ2: Explain AIML. (5 marks)");
        
        setTitle("Exam Server (Diffie-Hellman)");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        logArea = new JTextArea();
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        
        JButton startBtn = new JButton("Start Server");
        startBtn.addActionListener(e -> startServer());
        add(startBtn, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(8080);
                appendLog("Server started on 8080. Using DH Key Exchange.");
                while (true) {
                    Socket client = serverSocket.accept();
                    handleClient(client);
                }
            } catch (Exception ex) { appendLog("Server Error: " + ex.getMessage()); }
        }).start();
    }

    private void handleClient(Socket client) {
        new Thread(() -> {
            try (DataInputStream dis = new DataInputStream(client.getInputStream());
                 DataOutputStream dos = new DataOutputStream(client.getOutputStream())) {

                // 1. Receive Request Parameters
                String cls = dis.readUTF();
                String code = dis.readUTF();
                String sem = dis.readUTF();
                String dbKey = cls + "-" + code + "-" + sem;
                String content = qpDatabase.getOrDefault(dbKey, "Error: Paper Not Found");

                // 2. Diffie-Hellman Step 1: Generate Server KeyPair
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
                kpg.initialize(512); // Using 512 for speed; use 2048 for real security
                KeyPair serverKeyPair = kpg.generateKeyPair();
                
                // 3. Send Server Public Key to Client
                byte[] serverPubKeyEnc = serverKeyPair.getPublic().getEncoded();
                dos.writeInt(serverPubKeyEnc.length);
                dos.write(serverPubKeyEnc);
                dos.flush();

                // 4. Receive Client Public Key
                int clientKeyLen = dis.readInt();
                byte[] clientPubKeyEnc = new byte[clientKeyLen];
                dis.readFully(clientPubKeyEnc);

                // 5. Generate Shared Secret
                KeyFactory kf = KeyFactory.getInstance("DH");
                X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(clientPubKeyEnc);
                PublicKey clientPubKey = kf.generatePublic(x509Spec);

                KeyAgreement ka = KeyAgreement.getInstance("DH");
                ka.init(serverKeyPair.getPrivate());
                ka.doPhase(clientPubKey, true);
                byte[] sharedSecret = ka.generateSecret();

                // 6. Use Shared Secret to create DES Key (take first 8 bytes)
                SecretKeySpec secretKey = new SecretKeySpec(sharedSecret, 0, 8, "DES");
                
                // 7. Encrypt and Send Paper
                Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));

                dos.writeUTF("OK");
                dos.writeInt(encrypted.length);
                dos.write(encrypted);
                dos.flush();
                
                appendLog("Handshake complete. Paper sent for: " + dbKey);
            } catch (Exception ex) { appendLog("Handler Error: " + ex.getMessage()); }
        }).start();
    }

    private void appendLog(String m) { SwingUtilities.invokeLater(() -> logArea.append(m + "\n")); }
    public static void main(String[] args) { new ExamServerGUI(); }
}