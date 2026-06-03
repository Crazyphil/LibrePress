package it.kapfer.librepress.server.xml.authentication;

import it.kapfer.librepress.server.xml.Authentication;

public class UsernamePasswordAuthentication extends Authentication {
    public UsernamePasswordAuthentication(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
    }
}
