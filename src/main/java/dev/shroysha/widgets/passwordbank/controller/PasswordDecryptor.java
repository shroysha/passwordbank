package dev.shroysha.widgets.passwordbank.controller;

import dev.shroysha.widgets.passwordbank.model.Password;
import dev.shroysha.widgets.passwordbank.model.WebsitePassword;
import dev.shroysha.widgets.passwordbank.view.MainPasswordFrame;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PasswordDecryptor {

    private static final int SCRAMBLE_INDEX = 42;
    private static final File baseDir = new File(System.getProperty("user.home") + "/.PasswordDecryptor/");
    private static final File otherPasswordFile = new File(System.getProperty("user.home") + "/.PasswordDecryptor/otherPasswords");
    private static final File mainPasswordFile = new File(System.getProperty("user.home") + "/.PasswordDecryptor/mainPassword");
    private static final File optionFile = new File(System.getProperty("user.home") + "/.PasswordDecryptor/options");


    public static void main(String[] args) {
        test();

        boolean needInstalled = doesNeedToBeInstalled();
        System.out.println(baseDir.getAbsolutePath());
        if (needInstalled)
            try {
                install();
            } catch (IOException ex) {
                Logger.getLogger(PasswordDecryptor.class.getName()).log(Level.SEVERE, null, ex);
            }

        MainPasswordFrame mpf = new MainPasswordFrame();
        mpf.setVisible(true);

    }

    public static boolean isMainPasswordCorrect(String password) throws IOException {
        FileReader fr = new FileReader(mainPasswordFile);
        BufferedReader reader = new BufferedReader(fr);
        String real = unscramblePassword(reader.readLine());
        reader.close();
        return real.equals(password);
    }

    protected static String scramblePassword(String password) {
        StringBuilder scrambled = new StringBuilder();

        for (int i = 0; i < password.length(); i++) {
            char nextChar = password.charAt(i);
            byte nextByte = (byte) nextChar;
            nextByte += SCRAMBLE_INDEX;
            char scrambledChar = (char) nextByte;
            scrambled.append(scrambledChar);
        }

        return scrambled.toString();
    }

    protected static String unscramblePassword(String password) {
        StringBuilder unscrambled = new StringBuilder();

        for (int i = 0; i < password.length(); i++) {
            char nextChar = password.charAt(i);
            byte nextByte = (byte) nextChar;
            nextByte -= SCRAMBLE_INDEX;
            char scrambledChar = (char) nextByte;
            unscrambled.append(scrambledChar);
        }

        return unscrambled.toString();
    }

    private static boolean doesNeedToBeInstalled() {
        return !mainPasswordFile.exists() || !otherPasswordFile.exists();
    }

    private static void install() throws IOException {
        String mainPassword = getNewMainPassword();


        System.out.println(mainPasswordFile.getPath());

        baseDir.mkdir();

        if (!mainPasswordFile.exists())
            mainPasswordFile.createNewFile();

        if (!otherPasswordFile.exists())
            otherPasswordFile.createNewFile();

        optionFile.exists();

        writeNewMainPassword(mainPassword);


    }

    @SuppressWarnings("SleepWhileInLoop")
    private static String getNewMainPassword() {
        String first;
        String comfirm;
        boolean firstTime = true;
        MainPasswordFrame mpf = new MainPasswordFrame();
        mpf.setVisible(true);

        do {

            if (firstTime)
                mpf.setDescrptionLabelText("Welcome! Enter your main password");
            else
                mpf.setDescrptionLabelText("Passwords did not match. Try again.");

            while (mpf.didHitEnter()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PasswordDecryptor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            first = mpf.getEnteredPassword();
            mpf.setHitEnter(false);

            mpf.setDescrptionLabelText("Reenter your main password");

            while (mpf.didHitEnter()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PasswordDecryptor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            comfirm = mpf.getEnteredPassword();
            mpf.setHitEnter(false);

            firstTime = false;

        } while (!first.equals(comfirm));

        mpf.dispose();

        return first;
    }

    private static void writeNewMainPassword(String mainPassword) throws IOException {
        FileWriter writer = new FileWriter(mainPasswordFile);
        writer.write(scramblePassword(mainPassword));
        writer.close();
    }

    private static void test() {
        mainPasswordFile.delete();
        otherPasswordFile.delete();
        optionFile.delete();
        baseDir.delete();
    }

    public static ArrayList<Password> getOtherPasswords() {
        try {
            Scanner scanner = new Scanner(otherPasswordFile);
            ArrayList<Password> passwords = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String web = scanner.nextLine();

                boolean isWebsitePassword;
                isWebsitePassword = web.equals("Website");

                String type = scanner.nextLine();
                String scrambledUsername = scanner.nextLine();
                String unscrambledUsername = unscramblePassword(scrambledUsername);
                String scrambledPass = scanner.nextLine();
                String unscrambledPass = unscramblePassword(scrambledPass);

                Password password;

                if (isWebsitePassword) {
                    String website = scanner.nextLine();
                    WebsitePassword wp = new WebsitePassword(type, unscrambledUsername, unscrambledPass, website);
                    if (Options.isGetWesbiteIcons()) {
                        wp.obtainWebsiteIcon();
                    }
                    password = wp;
                } else {
                    password = new Password(type, unscrambledUsername, unscrambledPass);
                }

                passwords.add(password);
                System.out.println("Added " + password);
            }


            return passwords;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PasswordDecryptor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public static void writeOtherPasswords(ArrayList<Password> passwords) throws IOException {
        FileWriter fw = new FileWriter(otherPasswordFile);
        BufferedWriter writer = new BufferedWriter(fw);

        Iterator<Password> it = passwords.iterator();
        while (it.hasNext()) {
            Password next = it.next();
            boolean isWebsitePassword = next instanceof WebsitePassword;
            if (isWebsitePassword)
                writer.write("Website\n");
            else
                writer.write("Password\n");

            writer.write(next.getType() + "\n");
            writer.write(scramblePassword(next.getUsername()) + "\n");
            writer.write(scramblePassword(next.getPassword()));

            if (isWebsitePassword) {
                writer.write("\n");
                WebsitePassword pass = (WebsitePassword) next;
                writer.write(pass.getWebsite());
            }

            if (it.hasNext())
                writer.write("\n");
        }

        writer.close();
        System.out.println("Wrote em");
    }

    public static void writeOptions() throws IOException {
        final String getWebsiteIcons = "gwi-";

        FileWriter fw = new FileWriter(optionFile);
        BufferedWriter writer = new BufferedWriter(fw);

        writer.write(getWebsiteIcons + Options.isGetWesbiteIcons());
    }

    public static void readOptions() throws IOException {
        Scanner scanner = new Scanner(optionFile);

        final String getWebsiteIcons = "gwi-";

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.startsWith(getWebsiteIcons)) {
                line.replaceFirst(getWebsiteIcons, "");
                boolean gwi = Boolean.parseBoolean(line);
                Options.setGetWesbiteIcons(gwi);
            }

        }

    }
}
