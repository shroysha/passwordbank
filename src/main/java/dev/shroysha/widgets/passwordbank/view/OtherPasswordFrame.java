package dev.shroysha.widgets.passwordbank.view;

import dev.shroysha.widgets.passwordbank.controller.PasswordDecryptor;
import dev.shroysha.widgets.passwordbank.model.Password;
import dev.shroysha.widgets.passwordbank.model.WebsitePassword;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;


public class OtherPasswordFrame extends JFrame {

    private final ArrayList<Password> passwords;
    private int passCursor = 0;
    private JPanel passwordPanel;
    private JPanel contentPane;
    private JButton nextButton, backButton;
    private JButton editButton;
    private JButton removeButton;
    private Password current;
    private JButton sortButton;

    public OtherPasswordFrame() {
        super("Your Passwords");

        passwords = PasswordDecryptor.getOtherPasswords();

        init();
    }

    public static void main(String[] args) {
        OtherPasswordFrame frame = new OtherPasswordFrame();
        frame.setVisible(true);
    }

    private void init() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        try {
            passwordPanel = createPasswordPanel(password(passCursor));
        } catch (IndexOutOfBoundsException ex) {
            passwordPanel = createPasswordPanel(null);
        }

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 0, 10));

        nextButton = new JButton("Next");
        nextButton.addActionListener(ae -> {
            passCursor++;
            reassignPasswordPanel(password(passCursor));
        });

        backButton = new JButton("Back");
        backButton.setRolloverEnabled(true);
        backButton.addActionListener(ae -> {
            passCursor--;
            reassignPasswordPanel(password(passCursor));
        });

        contentPane.add(backButton, BorderLayout.WEST);
        contentPane.add(nextButton, BorderLayout.EAST);
        contentPane.add(passwordPanel, BorderLayout.CENTER);
        contentPane.add(createActionsPanel(), BorderLayout.SOUTH);

        this.add(contentPane, BorderLayout.CENTER);

        finishUp();
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(ae -> {
            NewPasswordFrame frame = new NewPasswordFrame(OtherPasswordFrame.this);
            frame.setVisible(true);
        });

        editButton = new JButton("Edit");
        editButton.addActionListener(ae -> {
            NewPasswordFrame frame = new NewPasswordFrame(password(passCursor), OtherPasswordFrame.this);
            frame.setVisible(true);
        });

        removeButton = new JButton("Remove");
        removeButton.addActionListener(ae -> {
            passwords.remove(current);
            if (passCursor == passwords.size())
                passCursor--;

            if (passCursor == -1) {
                passCursor = 0;
                reassignPasswordPanel(null);
            } else {
                reassignPasswordPanel(password(passCursor));
            }


        });

        sortButton = new JButton("Sort");
        sortButton.addActionListener(ae -> sort());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(removeButton);
        panel.add(sortButton);

        panel.setBorder(new CompoundBorder(new EmptyBorder(10, 0, 10, 0), new EtchedBorder()));

        return panel;
    }

    private JPanel createPasswordPanel(Password password) {

        if (password == null) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(10, 10, 10, 10)));

            JLabel label = new JLabel("No passwords yet");
            label.setHorizontalTextPosition(SwingConstants.CENTER);
            panel.add(label, BorderLayout.CENTER);
            return panel;
        }

        current = password;

        JPanel passPanel = new JPanel(new BorderLayout());

        final JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(layout);
        panel.setBorder(new CompoundBorder(new CompoundBorder(new EmptyBorder(0, 10, 0, 10), new EtchedBorder()), new EmptyBorder(10, 10, 10, 10)));

        JLabel typeLabel = new JLabel(password.getType());
        System.out.println(password.getType());
        JLabel usernameLabel = new JLabel(password.getUsername());
        System.out.println(password.getUsername());
        JLabel passwordLabel = new JLabel(password.getPassword());
        System.out.println(password.getPassword());

        panel.add(typeLabel, Box.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10));
        panel.add(usernameLabel, Box.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10));
        panel.add(passwordLabel, Box.LEFT_ALIGNMENT);

        if (password instanceof WebsitePassword) {
            final WebsitePassword wp = (WebsitePassword) password;
            JLabel label;
            if (wp.getWebsiteIcon() != null) {
                ImageIcon icon = new ImageIcon(wp.getWebsiteIcon());
                label = new JLabel(wp.getWebsite(), icon, JLabel.CENTER);
                label.setVerticalTextPosition(JLabel.BOTTOM);
                label.setHorizontalTextPosition(JLabel.CENTER);
            } else
                label = new JLabel(wp.getWebsite() + "\n" + "No icon");

            passPanel.add(panel, BorderLayout.WEST);
            passPanel.add(label, BorderLayout.EAST);
        } else {
            passPanel.add(panel, BorderLayout.CENTER);
        }

        return passPanel;
    }

    private void reassignPasswordPanel(Password password) {
        contentPane.remove(passwordPanel);
        passwordPanel = createPasswordPanel(password);
        contentPane.add(passwordPanel, BorderLayout.CENTER);
        contentPane.revalidate();
        finishUp();
    }

    private void finishUp() {
        if (passCursor >= passwords.size() - 1)
            nextButton.setEnabled(false);
        else
            nextButton.setEnabled(true);

        if (passCursor <= 0)
            backButton.setEnabled(false);
        else
            backButton.setEnabled(true);

        if (passwords.isEmpty()) {
            editButton.setEnabled(false);
            removeButton.setEnabled(false);
            sortButton.setEnabled(false);
        } else {
            editButton.setEnabled(true);
            removeButton.setEnabled(true);
            sortButton.setEnabled(true);
        }

        this.pack();
    }


    public void pack() {
        super.pack();
        this.setMinimumSize(this.getSize());
    }


    public void dispose() {
        super.dispose();
        try {
            PasswordDecryptor.writeOtherPasswords(passwords);
            PasswordDecryptor.writeOptions();
        } catch (IOException ex) {
            Logger.getLogger(OtherPasswordFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(42);
    }

    public void addPassword(Password newPass) {
        passwords.add(newPass);
        passCursor = passwords.size() - 1;
        reassignPasswordPanel(newPass);
    }

    public void editPassword(Password old, Password newPass) {
        int index = passwords.indexOf(old);
        passwords.set(index, newPass);
        reassignPasswordPanel(newPass);
    }

    private Password password(int index) {
        return passwords.get(index);
    }

    private void sort() {
        Collections.sort(passwords);
        passCursor = 0;
        reassignPasswordPanel(passwords.get(passCursor));
    }

    private void blah(final Image icon) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        JPanel panel = new JPanel() {


            protected void paintComponent(Graphics grphcs) {
                super.paintComponent(grphcs);
                grphcs.drawImage(icon, 0, 0, this);
            }

        };
        frame.add(panel, BorderLayout.CENTER);
        frame.setSize(400, 400);
        frame.setVisible(true);
    }

}
