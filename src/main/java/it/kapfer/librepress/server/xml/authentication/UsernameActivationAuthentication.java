package it.kapfer.librepress.server.xml.authentication;

public class UsernameActivationAuthentication extends ActivationAuthentication {
    public UsernameActivationAuthentication(String userName, int clientNumber, String activationNumber) {
        super(clientNumber, activationNumber);
        
        this.userName = userName;
    }
}
