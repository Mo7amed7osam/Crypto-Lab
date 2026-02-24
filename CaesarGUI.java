import javax.swing.*;
import java.awt.*;

public class CaesarGUI extends JFrame {

    private JTextField inputField;
    private JTextField shiftField;
    private JTextArea resultArea;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton attackButton;
    private JButton clearButton;

    public CaesarGUI() {

        setTitle("Caesar Cipher");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        JPanel topPanel = new JPanel(new GridLayout(3,2,10,10));

        inputField = new JTextField();
        shiftField = new JTextField();

        topPanel.add(new JLabel("Enter Text:"));
        topPanel.add(inputField);
        topPanel.add(new JLabel("Shift Value:"));
        topPanel.add(shiftField);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");
        attackButton = new JButton("Attack");
        clearButton = new JButton("Clear");

        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(attackButton);
        buttonPanel.add(clearButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        encryptButton.addActionListener(e -> process(true));
        decryptButton.addActionListener(e -> process(false));
        attackButton.addActionListener(e -> bruteForceAttack());

        clearButton.addActionListener(e -> {
            inputField.setText("");
            shiftField.setText("");
            resultArea.setText("");
        });
    }

    private void process(boolean isEncrypt) {
        try {
            String text = inputField.getText();
            int shift = Integer.parseInt(shiftField.getText());

            String result;

            if (isEncrypt) {
                result = CaesarCipher.encrypt(text, shift);
            } else {
                result = CaesarCipher.decrypt(text, shift);
            }

            resultArea.setText(result);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for shift!");
        }
    }

    private void bruteForceAttack() {

        String cipherText = inputField.getText();
        StringBuilder results = new StringBuilder();

        for (int shift = 0; shift < 26; shift++) {

            String decrypted = CaesarCipher.decrypt(cipherText, shift);

            results.append("Shift ")
                    .append(shift)
                    .append(": ")
                    .append(decrypted)
                    .append("\n");
        }

        resultArea.setText(results.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CaesarGUI().setVisible(true);
        });
    }
}
