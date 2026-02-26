package ui;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import ciphers.CipherType;
import ciphers.KeyParams;
import utils.MathUtils;

public class CipherConfigPanel extends JPanel {
    private final JComboBox<CipherType> cipherCombo;
    private final JCheckBox lettersOnlyCheck;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private final JSpinner caesarShiftSpinner = new JSpinner(new SpinnerNumberModel(3, 0, 25, 1));
    private final JTextField playfairKeyField = new JTextField(18);
    private final JTextField hillMatrixField = new JTextField("3 3; 2 5", 18);

    public CipherConfigPanel() {
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
        cardPanel.add(createCaesarPanel(), CipherType.CAESAR.name());
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
            case CAESAR:
                params.put("shift", (Integer) caesarShiftSpinner.getValue());
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
