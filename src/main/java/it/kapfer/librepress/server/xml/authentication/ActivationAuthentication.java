package it.kapfer.librepress.server.xml.authentication;

import it.kapfer.librepress.server.xml.Authentication;

public class ActivationAuthentication extends Authentication {
    public ActivationAuthentication(int clientNumber, String activationNumber) {
        this.clientNumber = clientNumber;
        this.activationNumber = activationNumber;
    }
}
