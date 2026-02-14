import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.crypto.*;
import java.util.Base64;

public class ExamServerGUI extends JFrame {
    private ServerSocket serverSocket;
    private Map<String, String> qpDatabase; 
    private JTextArea logArea;

    public ExamServerGUI() {
        qpDatabase = new HashMap<>();
        qpDatabase.put("10-CS101-1", "Q1: Explain DES. (10 marks)\nQ2: Explain Sockets. (5 marks)");
        
        setTitle("Exam Server (DES)");
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
                appendLog("Server started on 8080. Press Request on Client.");
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

                // Read client strings
                String cls = dis.readUTF();
                String code = dis.readUTF();
                String sem = dis.readUTF();

                String dbKey = cls + "-" + code + "-" + sem;
                String content = qpDatabase.getOrDefault(dbKey, "Error: Paper Not Found");

                // DES Logic
                KeyGenerator keyGen = KeyGenerator.getInstance("DES");
                SecretKey secretKey = keyGen.generateKey();
                Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                
                byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));
                String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

                // Send everything through DataOutputStream for synchronization
                dos.writeUTF("OK");
                dos.writeUTF(encodedKey);
                dos.writeInt(encrypted.length);
                dos.write(encrypted);
                dos.flush();
                
                appendLog("Sent: " + dbKey);
            } catch (Exception ex) { appendLog("Handler Error: " + ex.getMessage()); }
        }).start();
    }

    private void appendLog(String m) { SwingUtilities.invokeLater(() -> logArea.append(m + "\n")); }
    public static void main(String[] args) { new ExamServerGUI(); }
}
