package ui;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import attacks.AttackResult;
import attacks.HillAttack;
import attacks.MonoalphabeticAttack;
import attacks.VigenereAttackHelper;
import utils.MathUtils;

public class AttackPanel extends JPanel {
    private static final String ATTACK_MONO = "Monoalphabetic Frequency Attack";
    private static final String ATTACK_HILL = "Hill Known-Plaintext Attack";
    private static final String ATTACK_VIG = "Vigenere Key-Length Helper";

    private final JComboBox<String> attackSelector = new JComboBox<>(new String[]{
            ATTACK_MONO, ATTACK_HILL, ATTACK_VIG
    });

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final JTextArea monoCipherArea = new JTextArea(6, 50);
    private final JSpinner monoCandidatesSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

    private final JTextArea hillPlainArea = new JTextArea(5, 45);
    private final JTextArea hillCipherArea = new JTextArea(5, 45);
    private final JSpinner hillSizeSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 6, 1));

    private final JTextArea vigCipherArea = new JTextArea(6, 50);
    private final JSpinner vigKeyLenSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
    private final JSpinner vigCandidatesSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Candidate", "Score/Status", "Preview"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public AttackPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel top = new JPanel(new GridBagLayout());
        top.setBorder(new TitledBorder("Attack Selection"));
        GridBagConstraints tbc = new GridBagConstraints();
        tbc.insets = new Insets(4, 4, 4, 4);
        tbc.gridx = 0;
        top.add(new JLabel("Attack Type:"), tbc);
        tbc.gridx = 1;
        tbc.weightx = 1;
        tbc.fill = GridBagConstraints.HORIZONTAL;
        top.add(attackSelector, tbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        add(top, gbc);

        cardPanel.add(createMonoPanel(), ATTACK_MONO);
        cardPanel.add(createHillPanel(), ATTACK_HILL);
        cardPanel.add(createVigenerePanel(), ATTACK_VIG);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.5;
        add(cardPanel, gbc);

        JPanel actions = new JPanel();
        JButton runBtn = new JButton("Run Attack");
        runBtn.addActionListener(e -> runAttack());
        JButton clearBtn = new JButton("Clear Results");
        clearBtn.addActionListener(e -> tableModel.setRowCount(0));
        actions.add(runBtn);
        actions.add(clearBtn);

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(actions, gbc);

        JTable resultTable = new JTable(tableModel);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        JScrollPane resultPane = new JScrollPane(resultTable);
        resultPane.setBorder(new TitledBorder("Attack Results"));

        gbc.gridy = 3;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        add(resultPane, gbc);

        attackSelector.addActionListener(e -> switchCard());
        switchCard();
    }

    private JPanel createMonoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Monoalphabetic Frequency Analysis"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        panel.add(new JLabel("Ciphertext:"), gbc);

        gbc.gridy = 1;
        gbc.weighty = 1;
        panel.add(new JScrollPane(monoCipherArea), gbc);

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel row = new JPanel();
        row.add(new JLabel("Max candidates:"));
        row.add(monoCandidatesSpinner);
        panel.add(row, gbc);

        return panel;
    }

    private JPanel createHillPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Hill Known-Plaintext Attack"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        panel.add(new JLabel("Known Plaintext Chunk:"), gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.45;
        panel.add(new JScrollPane(hillPlainArea), gbc);

        gbc.gridy = 2;
        gbc.weighty = 0;
        panel.add(new JLabel("Matching Ciphertext Chunk:"), gbc);

        gbc.gridy = 3;
        gbc.weighty = 0.45;
        panel.add(new JScrollPane(hillCipherArea), gbc);

        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel row = new JPanel();
        row.add(new JLabel("Matrix size N:"));
        row.add(hillSizeSpinner);
        panel.add(row, gbc);

        return panel;
    }

    private JPanel createVigenerePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Vigenere Crib/Key Helper"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        panel.add(new JLabel("Ciphertext:"), gbc);

        gbc.gridy = 1;
        gbc.weighty = 1;
        panel.add(new JScrollPane(vigCipherArea), gbc);

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel row = new JPanel();
        row.add(new JLabel("Guessed key length:"));
        row.add(vigKeyLenSpinner);
        row.add(new JLabel("Max candidates:"));
        row.add(vigCandidatesSpinner);
        panel.add(row, gbc);

        return panel;
    }

    private void switchCard() {
        String selected = (String) attackSelector.getSelectedItem();
        cardLayout.show(cardPanel, selected);
    }

    private void runAttack() {
        tableModel.setRowCount(0);
        String selected = (String) attackSelector.getSelectedItem();
        try {
            if (ATTACK_MONO.equals(selected)) {
                runMonoAttack();
            } else if (ATTACK_HILL.equals(selected)) {
                runHillAttack();
            } else if (ATTACK_VIG.equals(selected)) {
                runVigenereHelper();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Attack Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runMonoAttack() {
        MonoalphabeticAttack attack = new MonoalphabeticAttack();
        int max = (Integer) monoCandidatesSpinner.getValue();
        List<AttackResult> results = attack.run(monoCipherArea.getText(), max);
        for (AttackResult r : results) {
            tableModel.addRow(new Object[]{r.getCandidate(), r.getScore(), r.getPreview()});
        }
    }

    private void runHillAttack() {
        HillAttack attack = new HillAttack();
        int n = (Integer) hillSizeSpinner.getValue();
        HillAttack.HillAttackResult result = attack.knownPlaintextAttack(
                hillPlainArea.getText(),
                hillCipherArea.getText(),
                n
        );

        if (result.isSuccess()) {
            tableModel.addRow(new Object[]{
                    MathUtils.matrixToString(result.getKeyMatrix()),
                    "SUCCESS",
                    result.getMessage()
            });
        } else {
            tableModel.addRow(new Object[]{"N/A", "FAILED", result.getMessage()});
        }
    }

    private void runVigenereHelper() {
        VigenereAttackHelper helper = new VigenereAttackHelper();
        int keyLen = (Integer) vigKeyLenSpinner.getValue();
        int max = (Integer) vigCandidatesSpinner.getValue();
        List<AttackResult> results = helper.suggestKeys(vigCipherArea.getText(), keyLen, max);
        for (AttackResult r : results) {
            tableModel.addRow(new Object[]{r.getCandidate(), r.getScore(), r.getPreview()});
        }
    }
}
