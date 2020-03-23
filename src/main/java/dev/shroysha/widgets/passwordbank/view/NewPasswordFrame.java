package dev.shroysha.widgets.passwordbank.view;

import dev.shroysha.widgets.passwordbank.model.Password;
import dev.shroysha.widgets.passwordbank.model.WebsitePassword;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class NewPasswordFrame extends JDialog {

    private final OtherPasswordFrame parent;
    private Password editFrom;
    private boolean edited;
    private JTextField typeField, usernameField, websiteField;
    private JPasswordField passwordField;
    private JButton confirmButton;
    private boolean isWebsiteShowing = true;
    private JLabel websiteLabel;
    private Component box1, box2;

    public NewPasswordFrame(OtherPasswordFrame parent) {
        this(null, parent);
    }

    public NewPasswordFrame(Password edit, OtherPasswordFrame parent) {
        super(parent, true);
        editFrom = edit;
        this.parent = parent;
        init();
    }

    private void init() {
        if (editFrom == null) {
            this.setTitle("Add");
            editFrom = new WebsitePassword("", "", "", "");
            edited = false;
        } else {
            this.setTitle("Edit");
            edited = true;
        }

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JPanel contentPane = new JPanel();
        BoxLayout layout = new BoxLayout(contentPane, BoxLayout.PAGE_AXIS);
        contentPane.setLayout(layout);
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel typeLabel = new JLabel("Type");
        typeField = new JTextField(editFrom.getType(), 20);
        typeField.addActionListener(ae -> usernameField.requestFocus());

        JLabel usernameLabel = new JLabel("Username");
        usernameField = new JTextField(editFrom.getUsername(), 20);
        usernameField.addActionListener(ae -> passwordField.requestFocus());

        JLabel passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField(editFrom.getPassword(), 20);
        passwordField.addActionListener(ae -> {
            if (!isWebsiteShowing)
                confirmButton.doClick();
            else
                websiteField.requestFocus();
        });

        confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(ae -> {
            String type = typeField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String website = websiteField.getText().trim();
            boolean websiteGood = !website.equals("") || !isWebsiteShowing;

            try {
                if (type.equals("") || username.equals("") || password.equals("") || !websiteGood)
                    throw new Exception("Please leave no fields blank");
            } catch (Exception ex) {
                /*
                 * parentComponent - determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used
message - the object to display in the dialog; a Component object is rendered as a Component; a String object is rendered as a string; other objects are converted to a String using the toString method
title - the title string for the dialog
optionType - an integer designating the options available on the dialog: YES_NO_OPTION, or YES_NO_CANCEL_OPTION
messageType - an integer designating the kind of message this is, primarily used to determine the icon from the pluggable Look and Feel: ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE
                 */
                JOptionPane.showMessageDialog(NewPasswordFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Password pass;
            if (isWebsiteShowing) {
                pass = new WebsitePassword(type, username, password, website);
                WebsitePassword wp = (WebsitePassword) pass;
                wp.obtainWebsiteIcon();
            } else
                pass = new Password(type, username, password);

            if (!edited) {
                parent.addPassword(pass);
            } else {
                parent.editPassword(editFrom, pass);
            }

            NewPasswordFrame.this.dispose();
        });


        websiteLabel = new JLabel("Website");

        if (editFrom instanceof WebsitePassword) {
            WebsitePassword wp = (WebsitePassword) editFrom;
            websiteField = new JTextField(wp.getWebsite(), 20);
        } else
            websiteField = new JTextField("", 20);

        websiteField.addActionListener(ae -> confirmButton.doClick());

        JButton showWebsiteButton = new JButton("Website");
        showWebsiteButton.addActionListener(ae -> showAndHideWebsiteStuff());

        JPanel temp = new JPanel(new FlowLayout());
        temp.add(confirmButton);
        temp.add(showWebsiteButton);

        box1 = Box.createVerticalStrut(5);
        box2 = Box.createVerticalStrut(10);


        contentPane.add(typeLabel, Box.LEFT_ALIGNMENT);
        contentPane.add(Box.createVerticalStrut(5));
        contentPane.add(typeField, Box.LEFT_ALIGNMENT);
        contentPane.add(Box.createVerticalStrut(10));
        contentPane.add(usernameLabel, Box.LEFT_ALIGNMENT);
        contentPane.add(Box.createVerticalStrut(5));
        contentPane.add(usernameField, Box.LEFT_ALIGNMENT);
        contentPane.add(Box.createVerticalStrut(10));
        contentPane.add(passwordLabel, Box.LEFT_ALIGNMENT);
        contentPane.add(Box.createVerticalStrut(5));
        contentPane.add(passwordField, Box.LEFT_ALIGNMENT);
        contentPane.add(Box.createVerticalStrut(10));
        contentPane.add(websiteLabel, Box.LEFT_ALIGNMENT);
        contentPane.add(box1, Box.LEFT_ALIGNMENT);
        contentPane.add(websiteField, Box.LEFT_ALIGNMENT);
        contentPane.add(box2, Box.LEFT_ALIGNMENT);
        contentPane.add(temp, Box.LEFT_ALIGNMENT);

        this.add(contentPane, BorderLayout.CENTER);
        this.pack();
    }

    private void showAndHideWebsiteStuff() {
        if (isWebsiteShowing) {
            websiteLabel.setVisible(false);
            websiteField.setVisible(false);
            box1.setVisible(false);
            box2.setVisible(false);
        } else {
            websiteLabel.setVisible(true);
            websiteField.setVisible(true);
            box1.setVisible(true);
            box2.setVisible(true);
        }
        isWebsiteShowing = !isWebsiteShowing;
    }


    public void pack() {
        this.setResizable(true);
        super.pack();
        this.setResizable(false);
    }


}
