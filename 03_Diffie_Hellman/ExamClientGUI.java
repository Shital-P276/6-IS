import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.interfaces.DHPublicKey;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ExamClientGUI extends JFrame {
    private JTextField cField, cdField, sField, kField;
    private JTextArea qpArea;
    private JButton reqBtn, decBtn;
    private byte[] encryptedData;
    private SecretKeySpec sharedDesKey;

    public ExamClientGUI() {
        setTitle("Exam Client (Diffie-Hellman)");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(5, 2));
        p.add(new JLabel("Class:")); cField = new JTextField("10"); p.add(cField);
        p.add(new JLabel("Course:")); cdField = new JTextField("CS101"); p.add(cdField);
        p.add(new JLabel("Sem:")); sField = new JTextField("1"); p.add(sField);
        p.add(new JLabel("DH Status:")); kField = new JTextField(); kField.setEditable(false); p.add(kField);
        
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

                // 1. Send Request
                dos.writeUTF(cField.getText());
                dos.writeUTF(cdField.getText());
                dos.writeUTF(sField.getText());
                dos.flush();

                // 2. Read Server Public Key
                int serverKeyLen = dis.readInt();
                byte[] serverPubKeyEnc = new byte[serverKeyLen];
                dis.readFully(serverPubKeyEnc);

                // 3. Generate Client KeyPair using Server's DH parameters
                KeyFactory kf = KeyFactory.getInstance("DH");
                X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(serverPubKeyEnc);
                PublicKey serverPubKey = kf.generatePublic(x509Spec);
                DHParameterSpec dhParams = ((DHPublicKey)serverPubKey).getParams();

                KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
                kpg.initialize(dhParams);
                KeyPair clientKeyPair = kpg.generateKeyPair();

                // 4. Send Client Public Key back to Server
                byte[] clientPubKeyEnc = clientKeyPair.getPublic().getEncoded();
                dos.writeInt(clientPubKeyEnc.length);
                dos.write(clientPubKeyEnc);
                dos.flush();

                // 5. Generate Shared Secret locally
                KeyAgreement ka = KeyAgreement.getInstance("DH");
                ka.init(clientKeyPair.getPrivate());
                ka.doPhase(serverPubKey, true);
                byte[] sharedSecret = ka.generateSecret();
                sharedDesKey = new SecretKeySpec(sharedSecret, 0, 8, "DES");

                // 6. Read Encrypted Paper
                String status = dis.readUTF();
                if ("OK".equals(status)) {
                    int len = dis.readInt();
                    encryptedData = new byte[len];
                    dis.readFully(encryptedData);

                    SwingUtilities.invokeLater(() -> {
                        kField.setText("Shared Secret Established");
                        qpArea.setText("Package received via DH. Ready to decrypt.");
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
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, sharedDesKey);
            byte[] result = cipher.doFinal(encryptedData);
            qpArea.setText("DECRYPTED WITH DH SECRET:\n\n" + new String(result, "UTF-8"));
        } catch (Exception ex) { qpArea.setText("Failed: " + ex.getMessage()); }
    }

    public static void main(String[] args) { new ExamClientGUI(); }
}