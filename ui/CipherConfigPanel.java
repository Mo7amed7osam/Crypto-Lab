package ui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.SecureRandom;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import ciphers.CipherType;
import ciphers.KeyParams;
import utils.MathUtils;
import utils.TextUtils;

public class CipherConfigPanel extends JPanel {
    private final Supplier<String> inputTextSupplier;
    private final Component dialogParent;
    private final SecureRandom random = new SecureRandom();

    private final JComboBox<CipherType> cipherCombo;
    private final JCheckBox lettersOnlyCheck;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private final JSpinner caesarShiftSpinner = new JSpinner(new SpinnerNumberModel(3, 0, 25, 1));
    private final JTextField vigenereKeyField = new JTextField(18);
    private final JTextField otpKeyField = new JTextField(18);
    private final JSpinner railSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 20, 1));
    private final JTextField rowOrderField = new JTextField("3 1 4 2", 18);
    private final JTextField playfairKeyField = new JTextField(18);
    private final JTextField hillMatrixField = new JTextField("3 3; 2 5", 18);
    private final JTextField aesKeyField = new JTextField("Thats my Kung Fu", 18);

    public CipherConfigPanel(Supplier<String> inputTextSupplier, Component dialogParent) {
        this.inputTextSupplier = inputTextSupplier;
        this.dialogParent = dialogParent;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cipherCombo = new JComboBox<CipherType>(CipherType.values());
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
        cardPanel.add(createAesPanel(), CipherType.AES.name());
        cardPanel.add(createCaesarPanel(), CipherType.CAESAR.name());
        cardPanel.add(createVigenerePanel(), CipherType.VIGENERE.name());
        cardPanel.add(createOtpPanel(), CipherType.OTP.name());
        cardPanel.add(createRailPanel(), CipherType.RAIL_FENCE.name());
        cardPanel.add(createRowPanel(), CipherType.ROW_TRANSPOSITION.name());
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

    private JPanel createAesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("AES Key (16 chars):"), gbc);
        gbc.gridx = 1;
        panel.add(aesKeyField, gbc);
        return panel;
    }

    private JPanel createCaesarPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("Shift:"), gbc);
        gbc.gridx = 1;
        panel.add(caesarShiftSpinner, gbc);
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

    private JPanel createVigenerePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        panel.add(new JLabel("Key:"), gbc);
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

        gbc.gridx = 2;
        gbc.weightx = 0;
        JButton genBtn = new JButton("Generate Random Key");
        genBtn.addActionListener(e -> generateOtpKey());
        panel.add(genBtn, gbc);
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
        panel.add(new JLabel("Order (ex: 3 1 4 2):"), gbc);
        gbc.gridx = 1;
        panel.add(rowOrderField, gbc);
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
        if (type == CipherType.AES) {
            lettersOnlyCheck.setSelected(false);
            lettersOnlyCheck.setEnabled(false);
        } else {
            lettersOnlyCheck.setEnabled(true);
        }
    }

    private void generateOtpKey() {
        String input = inputTextSupplier == null ? "" : inputTextSupplier.get();
        int len = TextUtils.lettersOnly(input).length();
        if (len == 0) {
            JOptionPane.showMessageDialog(dialogParent,
                    "Write input text first to generate OTP key.",
                    "OTP",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder key = new StringBuilder();
        for (int i = 0; i < len; i++) {
            key.append((char) ('A' + random.nextInt(26)));
        }
        otpKeyField.setText(key.toString());
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
            case AES:
                params.put("key", aesKeyField.getText());
                break;
            case CAESAR:
                params.put("shift", (Integer) caesarShiftSpinner.getValue());
                break;
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
                params.put("order", rowOrderField.getText().trim());
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
