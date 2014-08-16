package sk.stuba.fiit.perconik.ivda.astrcs;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by Seky on 2. 8. 2014.
 * Trieda na prihlasenie do NTLM protokolu.
 */
public final class NtlmAuthenticator extends Authenticator {

    private final String m_username;
    private final char[] m_password;

    /**
     * Trieda na prihlasenie do NTLM protokolu.
     *
     * @param username
     * @param password
     */
    public NtlmAuthenticator(String username, String password) {
        super();
        m_username = username;
        m_password = password.toCharArray();
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(m_username, m_password);
    }
}
