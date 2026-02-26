package ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import ciphers.Cipher;
import ciphers.CipherFactory;
import ciphers.KeyParams;
import utils.FileUtils;
import utils.TextUtils;

public class CryptoOperationPanel extends JPanel {
    private final boolean encryptMode;

    private final JTextArea inputArea = new JTextArea(10, 45);
    private final JTextArea outputArea = new JTextArea(10, 45);

    private final JTextField inputPathField = new JTextField(30);
    private final JTextField outputPathField = new JTextField(30);

    private final CipherConfigPanel configPanel;

    public CryptoOperationPanel(boolean encryptMode) {
        this.encryptMode = encryptMode;
        this.configPanel = new CipherConfigPanel();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        add(configPanel, gbc);

        JPanel textPanel = createTextPanel();
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(textPanel, gbc);

        JPanel controls = createControlPanel();
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(controls, gbc);
    }

    private JPanel createTextPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Text Mode"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        panel.add(new JLabel("Input"), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel("Output"), gbc);

        outputArea.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1;
        panel.add(new JScrollPane(inputArea), gbc);

        gbc.gridx = 1;
        panel.add(new JScrollPane(outputArea), gbc);

        JButton processBtn = new JButton(encryptMode ? "Encrypt" : "Decrypt");
        processBtn.addActionListener(e -> processTextAction(encryptMode));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(processBtn, gbc);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("File Mode (UTF-8)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Input File:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(inputPathField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JButton browseInput = new JButton("Browse");
        browseInput.addActionListener(e -> chooseFile(inputPathField, true));
        panel.add(browseInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Output File:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(outputPathField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JButton browseOutput = new JButton("Browse");
        browseOutput.addActionListener(e -> chooseFile(outputPathField, false));
        panel.add(browseOutput, gbc);

        JPanel buttonsRow = new JPanel();
        JButton loadBtn = new JButton("Load From File");
        loadBtn.addActionListener(e -> loadFromFile());

        JButton saveBtn = new JButton("Save Output To File");
        saveBtn.addActionListener(e -> saveOutputToFile());

        JButton encryptFileBtn = new JButton("Encrypt File");
        encryptFileBtn.addActionListener(e -> processFileAction(true));

        JButton decryptFileBtn = new JButton("Decrypt File");
        decryptFileBtn.addActionListener(e -> processFileAction(false));

        buttonsRow.add(loadBtn);
        buttonsRow.add(saveBtn);
        buttonsRow.add(encryptFileBtn);
        buttonsRow.add(decryptFileBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        panel.add(buttonsRow, gbc);

        return panel;
    }

    private void chooseFile(JTextField targetField, boolean openMode) {
        JFileChooser chooser = new JFileChooser();
        int result = openMode ? chooser.showOpenDialog(this) : chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            targetField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void loadFromFile() {
        try {
            if (inputPathField.getText().trim().isEmpty()) {
                chooseFile(inputPathField, true);
            }
            String path = inputPathField.getText().trim();
            if (path.isEmpty()) {
                return;
            }
            inputArea.setText(FileUtils.readUtf8(path));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void saveOutputToFile() {
        try {
            if (outputPathField.getText().trim().isEmpty()) {
                chooseFile(outputPathField, false);
            }
            String path = outputPathField.getText().trim();
            if (path.isEmpty()) {
                return;
            }
            FileUtils.writeUtf8(path, outputArea.getText());
            JOptionPane.showMessageDialog(this, "Output saved.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void processTextAction(boolean encrypt) {
        try {
            outputArea.setText(processText(inputArea.getText(), encrypt));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void processFileAction(boolean encrypt) {
        try {
            if (inputPathField.getText().trim().isEmpty()) {
                chooseFile(inputPathField, true);
            }
            if (outputPathField.getText().trim().isEmpty()) {
                chooseFile(outputPathField, false);
            }
            String inputPath = inputPathField.getText().trim();
            String outputPath = outputPathField.getText().trim();
            if (inputPath.isEmpty() || outputPath.isEmpty()) {
                return;
            }

            String source = FileUtils.readUtf8(inputPath);
            String result = processText(source, encrypt);
            FileUtils.writeUtf8(outputPath, result);
            outputArea.setText(result);
            JOptionPane.showMessageDialog(this, (encrypt ? "Encryption" : "Decryption") + " file completed.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private String processText(String source, boolean encrypt) {
        String normalized = TextUtils.normalize(source, configPanel.isLettersOnlyEnabled());
        KeyParams params = configPanel.buildKeyParams();
        Cipher cipher = CipherFactory.getCipher(configPanel.getSelectedCipherType());
        return encrypt ? cipher.encrypt(normalized, params) : cipher.decrypt(normalized, params);
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
