package it.kapfer.librepress.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class NewspaperReaderTest {
    @ParameterizedTest
    @ValueSource(strings = {"brain-games", "guardian-weekly-2021-30"})
    void testRealFile(String testFilenamePrefix) throws IOException {
        try (InputStream propertiesFile = getClass().getResourceAsStream(testFilenamePrefix + ".properties")) {
            try (InputStream testFile = getClass().getResourceAsStream(testFilenamePrefix + ".pdn")) {
                assumeFalse(propertiesFile == null, "Properties file not found in resources");
                assumeFalse(testFile == null, "Test file not found in resources");

                Properties properties = new Properties();
                properties.load(propertiesFile);
                executeRealFileTest(properties, testFile);
            }
        }
    }

    void executeRealFileTest(Properties testProperties, InputStream testFile) throws IOException {
        NewspaperReader liberator = new NewspaperReader(testFile, Base64.getDecoder().decode(testProperties.getProperty("encryptionKey")));
        assertNotNull(liberator);

        PDDocument document = liberator.getDocument();
        PDDocumentInformation documentInformation = document.getDocumentInformation();

        // Verify decryption of string fields (using RC4 decryption)
        assertEquals(testProperties.getProperty("documentTitle"), documentInformation.getTitle());
        assertNotNull(documentInformation.getCreationDate());
        assertEquals(Instant.parse(testProperties.getProperty("creationDate")), documentInformation.getCreationDate().toInstant());

        // Verify decryption of stream fields (using RC4 decryption)
        PDPage page0 = document.getPage(0);
        try (var page0Contents = page0.getContents()) {
            assertEquals(Integer.parseInt(testProperties.getProperty("page1Contents")), page0Contents.available(), "Page 1 has to contain content");
        }
    }
}
