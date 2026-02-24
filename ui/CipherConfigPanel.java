package ui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import ciphers.CipherType;
import ciphers.KeyParams;
import utils.FileUtils;
import utils.MathUtils;
import utils.TextUtils;

public class CipherConfigPanel extends JPanel {
    private final JComboBox<CipherType> cipherCombo;
    private final JCheckBox lettersOnlyCheck;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private final JTextField vigenereKeyField = new JTextField(18);
    private final JTextField otpKeyField = new JTextField(18);
    private final JSpinner railSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 100, 1));
    private final JTextField rowOrderField = new JTextField("3 1 4 2", 18);
    private final JTextField monoMapField = new JTextField(26);
    private final JTextField playfairKeyField = new JTextField(18);
    private final JTextField hillMatrixField = new JTextField("3 3; 2 5", 18);

    private final Supplier<String> inputTextSupplier;
    private final Component dialogParent;

    public CipherConfigPanel(Supplier<String> inputTextSupplier, Component dialogParent) {
        this.inputTextSupplier = inputTextSupplier;
        this.dialogParent = dialogParent;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cipherCombo = new JComboBox<>(CipherType.values());
        lettersOnlyCheck = new JCheckBox("Letters only (A-Z)", true);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Cipher:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        add(cipherCombo, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        add(lettersOnlyCheck, gbc);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(createVigenerePanel(), CipherType.VIGENERE.name());
        cardPanel.add(createOtpPanel(), CipherType.OTP.name());
        cardPanel.add(createRailPanel(), CipherType.RAIL_FENCE.name());
        cardPanel.add(createRowPanel(), CipherType.ROW_TRANSPOSITION.name());
        cardPanel.add(createMonoPanel(), CipherType.MONOALPHABETIC.name());
        cardPanel.add(createPlayfairPanel(), CipherType.PLAYFAIR.name());
        cardPanel.add(createHillPanel(), CipherType.HILL.name());

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        add(cardPanel, gbc);

        cipherCombo.addActionListener(e -> showSelectedCard());
        showSelectedCard();
    }

    private JPanel createVigenerePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("Keyword:"), gbc);
        gbc.gridx = 1;
        panel.add(vigenereKeyField, gbc);
        return panel;
    }

    private JPanel createOtpPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("OTP Key Text:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(otpKeyField, gbc);

        JButton genBtn = new JButton("Generate OTP Key");
        genBtn.addActionListener(e -> generateOtpKey());
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(genBtn, gbc);

        JButton saveBtn = new JButton("Save OTP Key");
        saveBtn.addActionListener(e -> saveOtpKey());
        gbc.gridx = 3;
        panel.add(saveBtn, gbc);
        return panel;
    }

    private JPanel createRailPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("Rails:"), gbc);
        gbc.gridx = 1;
        panel.add(railSpinner, gbc);
        return panel;
    }

    private JPanel createRowPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("Numeric Key Order:"), gbc);
        gbc.gridx = 1;
        panel.add(rowOrderField, gbc);
        return panel;
    }

    private JPanel createMonoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("26-Letter Mapping:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(monoMapField, gbc);

        JButton genBtn = new JButton("Random Mapping");
        genBtn.addActionListener(e -> monoMapField.setText(TextUtils.generateRandomSubstitutionMap()));
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(genBtn, gbc);
        return panel;
    }

    private JPanel createPlayfairPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("Keyword (I/J merged):"), gbc);
        gbc.gridx = 1;
        panel.add(playfairKeyField, gbc);
        return panel;
    }

    private JPanel createHillPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("Matrix Key (rows ';')"), gbc);
        gbc.gridx = 1;
        panel.add(hillMatrixField, gbc);
        return panel;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        return gbc;
    }

    private void showSelectedCard() {
        CipherType type = getSelectedCipherType();
        cardLayout.show(cardPanel, type.name());
    }

    private void generateOtpKey() {
        String text = inputTextSupplier == null ? "" : inputTextSupplier.get();
        int lettersCount = TextUtils.lettersOnly(text).length();
        if (lettersCount == 0) {
            JOptionPane.showMessageDialog(dialogParent,
                    "Input text has no letters. Enter text first to generate OTP key.",
                    "OTP Generation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        otpKeyField.setText(TextUtils.generateRandomLetters(lettersCount));
    }

    private void saveOtpKey() {
        String key = TextUtils.lettersOnly(otpKeyField.getText());
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(dialogParent,
                    "OTP key is empty.",
                    "Save OTP Key",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(dialogParent) == JFileChooser.APPROVE_OPTION) {
            try {
                FileUtils.writeUtf8(chooser.getSelectedFile().getAbsolutePath(), key);
                JOptionPane.showMessageDialog(dialogParent, "OTP key saved.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogParent, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public CipherType getSelectedCipherType() {
        return (CipherType) cipherCombo.getSelectedItem();
    }

    public boolean isLettersOnlyEnabled() {
        return lettersOnlyCheck.isSelected();
    }

    public KeyParams buildKeyParams() {
        CipherType type = getSelectedCipherType();
        KeyParams params = new KeyParams();
        switch (type) {
            case VIGENERE:
                params.put("key", vigenereKeyField.getText().trim());
                break;
            case OTP:
                params.put("key", otpKeyField.getText().trim());
                break;
            case RAIL_FENCE:
                params.put("rails", (Integer) railSpinner.getValue());
                break;
            case ROW_TRANSPOSITION:
                params.put("order", TextUtils.parseNumericKeyOrder(rowOrderField.getText()));
                break;
            case MONOALPHABETIC:
                params.put("map", monoMapField.getText().trim().toUpperCase());
                break;
            case PLAYFAIR:
                params.put("key", playfairKeyField.getText().trim());
                break;
            case HILL:
                int[][] matrix = MathUtils.parseSquareMatrix(hillMatrixField.getText());
                MathUtils.inverseMatrixMod(matrix, 26);
                params.put("matrix", matrix);
                break;
            default:
                throw new IllegalArgumentException("Unsupported cipher type: " + type);
        }
        return params;
    }
}
