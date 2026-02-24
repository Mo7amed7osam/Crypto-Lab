package ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class HelpPanel extends JPanel {
    public HelpPanel() {
        setLayout(new BorderLayout());

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setText(
                "Cryptography Lab - Help/About\\n\\n" +
                        "Supported ciphers:\\n" +
                        "1) Vigenere\\n" +
                        "2) One-Time Pad (OTP)\\n" +
                        "3) Rail Fence\\n" +
                        "4) Row Transposition\\n" +
                        "5) Monoalphabetic Substitution\\n" +
                        "6) Playfair\\n" +
                        "7) Hill Cipher\\n\\n" +
                        "General usage:\\n" +
                        "- Choose a tab: Encrypt, Decrypt, or Attack.\\n" +
                        "- Select a cipher and fill required key fields.\\n" +
                        "- Default text preprocessing is Letters only (A-Z).\\n" +
                        "- Use Load From File / Save Output To File for UTF-8 files.\\n" +
                        "- Encrypt File and Decrypt File directly process files.\\n\\n" +
                        "Notes:\\n" +
                        "- OTP key must match text letter count exactly.\\n" +
                        "- Monoalphabetic mapping must be 26 unique letters.\\n" +
                        "- Playfair merges I/J.\\n" +
                        "- Hill matrix key must be invertible mod 26.\\n\\n" +
                        "Attack tab:\\n" +
                        "- Monoalphabetic frequency attack returns multiple candidates.\\n" +
                        "- Hill known-plaintext attack computes key matrix when solvable.\\n" +
                        "- Vigenere helper suggests candidate keys for guessed key length.\\n"
        );

        add(new JScrollPane(area), BorderLayout.CENTER);
    }
}
