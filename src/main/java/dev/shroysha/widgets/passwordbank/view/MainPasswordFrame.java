package dev.shroysha.widgets.passwordbank.view;

import dev.shroysha.widgets.passwordbank.controller.PasswordDecryptor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainPasswordFrame extends JFrame {

    private JPanel tryAgainPanel, contentPane;
    private JLabel descriptionLabel;
    private JPasswordField mainPasswordField;
    private boolean hitEnter = false;
    private boolean installing = false;
    private String entered;

    public MainPasswordFrame() {
        super("Main Password");
        init();
    }

    private void init() {

        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JButton submitButton = new JButton("Submit");

        descriptionLabel = new JLabel("Enter your main password");
        descriptionLabel.setForeground(Color.white);
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        mainPasswordField = new JPasswordField();
        mainPasswordField.addActionListener(ae -> submitButton.doClick());

        submitButton.addActionListener(ae -> {
            entered = getEnteredPassword();
            mainPasswordField.setText("");

            if (entered == null || entered.equals("")) {
                descriptionLabel.setText("Password may not be blank");
                return;
            }

            if (installing) {
                hitEnter = true;
            } else {
                try {
                    if (PasswordDecryptor.isMainPasswordCorrect(entered)) {
                        System.out.println("Sucess");
                        OtherPasswordFrame frame = new OtherPasswordFrame();
                        frame.setVisible(true);
                        MainPasswordFrame.this.dispose();
                    } else
                        MainPasswordFrame.this.show(tryAgainPanel);
                } catch (IOException ex) {
                    Logger.getLogger(MainPasswordFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setBackground(Color.black);

        contentPane.add(descriptionLabel, BorderLayout.NORTH);
        contentPane.add(mainPasswordField, BorderLayout.CENTER);
        contentPane.add(submitButton, BorderLayout.SOUTH);

        this.add(contentPane, BorderLayout.CENTER);

        tryAgainPanel = new JPanel(new BorderLayout());
        tryAgainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        tryAgainPanel.setBackground(Color.black);

        JLabel tryAgainLabel = new JLabel("Wrong password");
        tryAgainLabel.setForeground(Color.white);

        JButton tryAgainButton = new JButton("Try Again");
        tryAgainButton.addActionListener(ae -> MainPasswordFrame.this.show(contentPane));

        tryAgainPanel.add(tryAgainLabel, BorderLayout.NORTH);
        tryAgainPanel.add(tryAgainButton, BorderLayout.CENTER);

        this.pack();
    }

    private void show(JPanel panel) {
        this.getContentPane().removeAll();
        this.getContentPane().add(panel, BorderLayout.CENTER);
        SwingUtilities.updateComponentTreeUI(this.getContentPane());
    }

    public void setDescrptionLabelText(String text) {
        installing = true;
        descriptionLabel.setText(text);
        this.pack();
    }

    public String getEnteredPassword() {
        if (hitEnter)
            return entered;

        char[] chars = mainPasswordField.getPassword();
        StringBuilder entered = new StringBuilder();
        for (char next : chars) {
            entered.append(next);
        }

        return entered.toString();
    }

    public boolean didHitEnter() {
        return !hitEnter;
    }

    public void setHitEnter(boolean entered) {
        hitEnter = entered;
    }
}
