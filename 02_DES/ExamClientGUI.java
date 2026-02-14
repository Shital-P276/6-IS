import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class ExamClientGUI extends JFrame {
    private JTextField cField, cdField, sField, kField;
    private JTextArea qpArea;
    private JButton reqBtn, decBtn;
    private byte[] encryptedData;
    private String receivedKey;

    public ExamClientGUI() {
        setTitle("Exam Client (DES)");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(5, 2));
        p.add(new JLabel("Class:")); cField = new JTextField("10"); p.add(cField);
        p.add(new JLabel("Course:")); cdField = new JTextField("CS101"); p.add(cdField);
        p.add(new JLabel("Sem:")); sField = new JTextField("1"); p.add(sField);
        p.add(new JLabel("Key:")); kField = new JTextField(); kField.setEditable(false); p.add(kField);
        
        reqBtn = new JButton("Request Paper");
        reqBtn.addActionListener(e -> request());
        p.add(reqBtn);
        decBtn = new JButton("Decrypt");
        decBtn.setEnabled(false);
        decBtn.addActionListener(e -> decrypt());
        p.add(decBtn);

        add(p, BorderLayout.NORTH);
        qpArea = new JTextArea();
        add(new JScrollPane(qpArea), BorderLayout.CENTER);
        setVisible(true);
    }

    private void request() {
        new Thread(() -> {
            try (Socket s = new Socket("localhost", 8080);
                 DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                 DataInputStream dis = new DataInputStream(s.getInputStream())) {

                // Send request
                dos.writeUTF(cField.getText());
                dos.writeUTF(cdField.getText());
                dos.writeUTF(sField.getText());
                dos.flush();

                // Read response in exact order
                String status = dis.readUTF();
                if ("OK".equals(status)) {
                    receivedKey = dis.readUTF();
                    int len = dis.readInt();
                    encryptedData = new byte[len];
                    dis.readFully(encryptedData);

                    SwingUtilities.invokeLater(() -> {
                        kField.setText(receivedKey);
                        qpArea.setText("Package received. Ready to decrypt.");
                        decBtn.setEnabled(true);
                    });
                }
            } catch (Exception ex) { 
                SwingUtilities.invokeLater(() -> qpArea.setText("Error: " + ex.getMessage())); 
            }
        }).start();
    }

    private void decrypt() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(receivedKey);
            SecretKey key = new SecretKeySpec(keyBytes, "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(encryptedData);
            qpArea.setText("DECRYPTED:\n\n" + new String(result, "UTF-8"));
        } catch (Exception ex) { qpArea.setText("Failed: " + ex.getMessage()); }
    }

    public static void main(String[] args) { new ExamClientGUI(); }
}
