import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class ExamServerGUI extends JFrame {
    private ServerSocket serverSocket;
    private Map<String, String> qpDatabase;
    private JTextArea logArea;

    public ExamServerGUI() {
        // STUDENT CUSTOMIZE: Initialize simulated database with sample QPs
        qpDatabase = new HashMap<>();
        qpDatabase.put("10-CS101-1", "Question 1: What is Java? (5 marks)\nQuestion 2: Explain OOP. (10 marks)");
        qpDatabase.put("10-CS301-3", "Question 1: Socket Programming. (8 marks)\nQuestion 2: Caesar Cipher impl. (12 marks)");

        setTitle("IS Lab Exam Server - Caesar Cipher Secure QP Delivery");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        JButton startBtn = new JButton("Start Server (Port 8080)");
        startBtn.addActionListener(e -> startServer());
        add(startBtn, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(8080);
                appendLog("Server started on port 8080. Waiting for clients...");
                while (true) {
                    Socket client = serverSocket.accept();
                    appendLog("Client connected: " + client.getInetAddress());
                    handleClient(client);
                }
            } catch (Exception ex) {
                appendLog("Server error: " + ex.getMessage());
            }
        }).start();
    }

    private void handleClient(Socket client) {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 DataOutputStream dos = new DataOutputStream(client.getOutputStream())) {

                // STUDENT CUSTOMIZE: Diffie-Hellman Key Exchange
                long serverPrivate = 6;
                long serverPublic  = DHUtil.generatePublicValue(serverPrivate);
                long clientPublic  = Long.parseLong(in.readLine());
                out.println(serverPublic);
                long sharedSecret  = DHUtil.computeSharedSecret(clientPublic, serverPrivate);
                appendLog("Server Private Key : " + serverPrivate);
                appendLog("Server Public Value: " + serverPublic);
                appendLog("Client Public Value: " + clientPublic);
                appendLog("Shared Secret K    : " + sharedSecret);

                // Receive client inputs
                String cls = in.readLine();
                String courseCode = in.readLine();
                String courseName = in.readLine();
                String sem = in.readLine();

                String dbKey = cls + "-" + courseCode + "-" + sem;
                appendLog("Client request: " + dbKey);

                // Retrieve from DB
                String qpContent = qpDatabase.getOrDefault(dbKey, "ERROR: QP not found");
                if (qpContent.startsWith("ERROR")) {
                    out.println("ERROR");
                    out.println(qpContent);
                    return;
                }

                // STUDENT CUSTOMIZE: use sharedSecret as Caesar shift
                int shift = (int) sharedSecret;
                String encryptedQP = caesarEncrypt(qpContent, shift);
                appendLog("QP encrypted with shift=" + shift);

                // Send metadata
                out.println("OK");
                out.println(encryptedQP.length());
                out.println(shift);
                out.flush();

                // Send encrypted content
                dos.write(encryptedQP.getBytes("UTF-8"));
                dos.flush();
                appendLog("Encrypted QP sent to client");

            } catch (Exception ex) {
                appendLog("Client handling error: " + ex.getMessage());
            }
        }).start();
    }

    // STUDENT CUSTOMIZE: Caesar Cipher Encryption using DH shared secret as shift
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

    private void appendLog(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExamServerGUI::new);
    }
}
