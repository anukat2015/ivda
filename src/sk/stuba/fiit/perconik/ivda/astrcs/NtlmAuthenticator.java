package sk.stuba.fiit.perconik.ivda.astrcs;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by Seky on 2. 8. 2014.
 */
public class NtlmAuthenticator extends Authenticator {

    private final String username;
    private final char[] password;

    public NtlmAuthenticator(final String username, final String password) {
        super();
        this.username = new String(username);
        this.password = password.toCharArray();
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return (new PasswordAuthentication(username, password));
    }
}
