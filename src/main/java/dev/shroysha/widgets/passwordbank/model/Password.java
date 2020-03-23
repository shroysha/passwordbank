package dev.shroysha.widgets.passwordbank.model;


public class Password implements Comparable<Password> {

    private final String password;
    private final String type;
    private final String username;

    public Password(String aType, String aUsername, String aPassword) {
        super();
        type = aType;
        username = aUsername;
        password = aPassword;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }


    public int compareTo(Password t) {
        boolean iOfThis = this instanceof WebsitePassword;
        boolean iOfOther = t instanceof WebsitePassword;

        if (iOfThis && iOfOther) return type.compareTo(t.getType());
        else if (iOfThis) return -1;
        else return 1;
    }
}
