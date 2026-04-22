package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import ciphers.Aes128KeyExpansion;
import utils.FileUtils;

public class AesKeyExpansionPanel extends JPanel {
    private static final String FORMAT_TEXT = "TEXT (16 chars)";
    private static final String FORMAT_HEX = "HEX (32 hex chars)";
    private static final String FORMAT_BINARY = "BINARY (128 bits)";

    private final JComboBox<String> formatBox = new JComboBox<>(new String[]{FORMAT_TEXT, FORMAT_HEX, FORMAT_BINARY});
    private final JTextArea keyInputArea = new JTextArea(4, 50);
    private final JTextArea outputArea = new JTextArea(24, 55);

    public AesKeyExpansionPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new TitledBorder("AES-128 Key Expansion"));

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JLabel hint = new JLabel("Enter the AES key manually or load it from a file, then generate the expanded words.");
        controlsPanel.add(hint);

        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.add(new JLabel("Input format:"));
        formatPanel.add(formatBox);
        controlsPanel.add(formatPanel);

        JPanel keyPanel = new JPanel(new BorderLayout(4, 4));
        keyPanel.setBorder(BorderFactory.createTitledBorder("Key input"));
        keyInputArea.setLineWrap(true);
        keyInputArea.setWrapStyleWord(true);
        keyPanel.add(new JScrollPane(keyInputArea), BorderLayout.CENTER);
        controlsPanel.add(keyPanel);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadBtn = new JButton("Load Key From File");
        loadBtn.addActionListener(e -> loadKeyFromFile());

        JButton generateBtn = new JButton("Generate Words");
        generateBtn.addActionListener(e -> generateExpansion());

        JButton clearBtn = new JButton("Clear All");
        clearBtn.addActionListener(e -> {
            keyInputArea.setText("");
            outputArea.setText("");
        });

        JButton exportBtn = new JButton("Export Output To File");
        exportBtn.addActionListener(e -> exportOutputToFile());

        buttonRow.add(loadBtn);
        buttonRow.add(generateBtn);
        buttonRow.add(exportBtn);
        buttonRow.add(clearBtn);
        controlsPanel.add(buttonRow);

        outputArea.setFont(keyInputArea.getFont());
        outputArea.setEditable(false);
        outputArea.setLineWrap(false);
        outputArea.setText("");

        add(controlsPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    private void loadKeyFromFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            String text = FileUtils.readUtf8(chooser.getSelectedFile().getAbsolutePath());
            keyInputArea.setText(removeTrailingNewLines(text));
        } catch (IOException ex) {
            showError("Could not load file: " + ex.getMessage());
        }
    }

    private void generateExpansion() {
        try {
            int[] keyBytes = convertKeyToBytes(keyInputArea.getText(), getSelectedFormat());
            outputArea.setText(Aes128KeyExpansion.expandKeyBytes(keyBytes));
            outputArea.setCaretPosition(0);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void exportOutputToFile() {
        if (outputArea.getText().isBlank()) {
            showError("Generate the expanded key first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            FileUtils.writeUtf8(chooser.getSelectedFile().getAbsolutePath(), outputArea.getText());
        } catch (IOException ex) {
            showError("Could not save file: " + ex.getMessage());
        }
    }

    private String getSelectedFormat() {
        Object selected = formatBox.getSelectedItem();
        return selected == null ? FORMAT_TEXT : selected.toString();
    }

    private int[] convertKeyToBytes(String input, String format) {
        if (FORMAT_TEXT.equals(format)) {
            return convertTextToBytes(input);
        }
        if (FORMAT_HEX.equals(format)) {
            return convertHexToBytes(input);
        }
        return convertBinaryToBytes(input);
    }

    private int[] convertTextToBytes(String text) {
        if (text == null) {
            throw new IllegalArgumentException("TEXT input must be exactly 16 characters.");
        }

        if (text.length() != 16) {
            throw new IllegalArgumentException("TEXT input must be exactly 16 characters.");
        }

        int[] bytes = new int[16];
        for (int i = 0; i < 16; i++) {
            char ch = text.charAt(i);
            if (ch > 255) {
                throw new IllegalArgumentException("TEXT input must use simple 1-byte characters only.");
            }
            bytes[i] = ch;
        }
        return bytes;
    }

    private int[] convertHexToBytes(String text) {
        String hex = removeAllSpaces(text);
        if (hex.length() != 32) {
            throw new IllegalArgumentException("HEX input must be exactly 32 hex characters.");
        }

        for (int i = 0; i < hex.length(); i++) {
            if (!isHexCharacter(hex.charAt(i))) {
                throw new IllegalArgumentException("HEX input contains invalid characters.");
            }
        }

        int[] bytes = new int[16];
        for (int i = 0; i < 16; i++) {
            int start = i * 2;
            bytes[i] = Integer.parseInt(hex.substring(start, start + 2), 16);
        }
        return bytes;
    }

    private int[] convertBinaryToBytes(String text) {
        String binary = removeAllSpaces(text);
        if (binary.length() != 128) {
            throw new IllegalArgumentException("BINARY input must be exactly 128 bits.");
        }

        for (int i = 0; i < binary.length(); i++) {
            char ch = binary.charAt(i);
            if (ch != '0' && ch != '1') {
                throw new IllegalArgumentException("BINARY input must contain only 0 and 1.");
            }
        }

        int[] bytes = new int[16];
        for (int i = 0; i < 16; i++) {
            int start = i * 8;
            bytes[i] = Integer.parseInt(binary.substring(start, start + 8), 2);
        }
        return bytes;
    }

    private String removeAllSpaces(String text) {
        if (text == null) {
            return "";
        }

        StringBuilder clean = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!Character.isWhitespace(ch)) {
                clean.append(ch);
            }
        }
        return clean.toString();
    }

    private String removeTrailingNewLines(String text) {
        while (text.endsWith("\n") || text.endsWith("\r")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    private boolean isHexCharacter(char ch) {
        return (ch >= '0' && ch <= '9')
                || (ch >= 'a' && ch <= 'f')
                || (ch >= 'A' && ch <= 'F');
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "AES Key Expansion", JOptionPane.ERROR_MESSAGE);
    }
}