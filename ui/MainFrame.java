package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Cryptography Lab - Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 760);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Encrypt", new CryptoOperationPanel(true));
        tabs.addTab("Decrypt", new CryptoOperationPanel(false));
        tabs.addTab("Attack", new AttackPanel());
        tabs.addTab("AES Key Expansion", new AesKeyExpansionPanel());

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }
}
