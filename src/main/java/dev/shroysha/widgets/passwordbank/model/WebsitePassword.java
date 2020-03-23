package dev.shroysha.widgets.passwordbank.model;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;


public class WebsitePassword extends Password {

    private final String website;
    private Image websiteIcon;

    public WebsitePassword(String aType, String aUsername, String aPassword, String aWebsite) {
        super(aType, aUsername, aPassword);
        website = aWebsite;
    }

    public Image getWebsiteIcon() {
        return websiteIcon;
    }

    public String getWebsite() {
        return website;
    }

    public void obtainWebsiteIcon() {
        try {
            try {
                URL url = new URL(website);
                obtainWebsiteIcon(url);
            } catch (MalformedURLException ex) {
                try {
                    URL url = new URL("http://" + website);
                    obtainWebsiteIcon(url);
                } catch (MalformedURLException ex1) {
                    try {
                        URL url = new URL("http://www." + website);
                        obtainWebsiteIcon(url);
                    } catch (MalformedURLException ex2) {
                        ex2.printStackTrace(System.err);
                        websiteIcon = null;
                    }
                }
            }
        } catch (Exception ex) {
            websiteIcon = null;
            ex.printStackTrace(System.err);
        }
    }

    private void obtainWebsiteIcon(URL url) throws Exception {
        url = new URL(url.toURI().toString() + "/favicon.ico");
        System.out.println(url.toURI().toString());

        if (websiteIcon == null)
            System.out.println("null");
    }
}
