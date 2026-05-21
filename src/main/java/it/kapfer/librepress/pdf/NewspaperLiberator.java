package it.kapfer.librepress.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Decrypts PDF files encrypted with NDPD (NewspaperDirect) custom encryption.
 * <p>
 * The NDPD encryption uses a custom filter "NDPD:CryptHandler" with version 2,
 * which is handled by the NDSecurityHandler class.
 */
public class NewspaperLiberator {
    private final PDDocument document;

    /**
     * Creates a new NewspaperLiberator to decrypt a PDF file.
     *
     * @param pdfStream     input stream containing the encrypted PDF
     * @param encryptionKey the 16-byte encryption key from the user's certificate
     * @throws IllegalArgumentException if the PDF cannot be decrypted
     */
    public NewspaperLiberator(InputStream pdfStream, byte[] encryptionKey) {
        NDSecurityHandler.register();

        try {
            String password = new String(encryptionKey, StandardCharsets.UTF_8);
            document = Loader.loadPDF(pdfStream.readAllBytes(), password);
            document.setAllSecurityToBeRemoved(true);
        } catch (IOException e) {
            throw new IllegalArgumentException("PDF file cannot be opened or decrypted", e);
        }
    }

    /**
     * Creates a new NewspaperLiberator to decrypt a PDF file.
     *
     * @param pdfFile       the encrypted PDF file
     * @param encryptionKey the 16-byte encryption key from the user's certificate
     * @throws FileNotFoundException if the file does not exist
     */
    public NewspaperLiberator(File pdfFile, byte[] encryptionKey) throws FileNotFoundException {
        this(new FileInputStream(pdfFile), encryptionKey);
    }

    /**
     * Saves the decrypted PDF to the specified output file.
     *
     * @param outputFile the file to save the decrypted PDF to
     * @throws IOException if saving fails
     */
    public void liberateFile(File outputFile) throws IOException {
        document.save(outputFile);
    }

    /**
     * {@return the underlying {@link PDDocument} for further processing}
     */
    public PDDocument getDocument() {
        return document;
    }
}
