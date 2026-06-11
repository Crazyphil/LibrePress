package it.kapfer.librepress.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Opens PDF files encrypted with NDPD (NewspaperDirect) custom encryption.
 * <p>
 * The NDPD encryption uses a custom filter "NDPD:CryptHandler" with version 2, which is handled by the {@link NDSecurityHandler} class.
 */
public class NewspaperReader implements AutoCloseable {
    private final PDDocument document;

    /**
     * Creates a new NewspaperReader to open a PDF file.
     *
     * @param pdfStream     input stream containing the encrypted PDF
     * @param encryptionKey the 16-byte encryption key from the activation certificate
     * @throws IllegalArgumentException if the PDF cannot be decrypted
     */
    public NewspaperReader(InputStream pdfStream, byte[] encryptionKey) {
        NDSecurityHandler.register();

        try {
            // Use ISO-8859-1 (1:1 byte - char mapping) to preserve raw key bytes through the
            // String password mechanism. NDSecurityHandler recovers the bytes with getBytes(ISO-8859-1).
            String password = new String(encryptionKey, StandardCharsets.ISO_8859_1);
            document = Loader.loadPDF(pdfStream.readAllBytes(), password);
        } catch (IOException e) {
            throw new IllegalArgumentException("PDF file cannot be opened or decrypted", e);
        }
    }

    /**
     * Creates a new NewspaperReader to open a PDF file.
     *
     * @param pdfFile       the encrypted PDF file
     * @param encryptionKey the 16-byte encryption key from the activation certificate
     * @throws FileNotFoundException    if the file does not exist
     * @throws IllegalArgumentException if the PDF cannot be decrypted
     */
    public NewspaperReader(File pdfFile, byte[] encryptionKey) throws FileNotFoundException {
        this(new FileInputStream(pdfFile), encryptionKey);
    }

    /**
     * {@return the underlying {@link PDDocument} for further processing}
     */
    public PDDocument getDocument() {
        return document;
    }

    /**
     * Closes the PDF document.
     *
     * @throws IOException if there is an error releasing resources
     */
    @Override
    public void close() throws IOException {
        document.close();
    }
}
