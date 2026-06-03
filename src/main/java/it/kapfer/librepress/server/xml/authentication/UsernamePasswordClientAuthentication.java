package it.kapfer.librepress.server.xml.authentication;

public class UsernamePasswordClientAuthentication extends UsernamePasswordAuthentication {
    public UsernamePasswordClientAuthentication(String userName, String userPassword, int clientNumber) {
        super(userName, userPassword);

        this.clientNumber = clientNumber;
    }
}
