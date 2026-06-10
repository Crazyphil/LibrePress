package it.kapfer.librepress.server.xml.message;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * This class is a Jackson object representation of the {@code message} element in this XML structure:
 * <pre>
 *         <message id="12345678" type="newspaper-update" sent-time="01/06/2026 12:34:56">
 *             <issue-id>69572023090100000000001001</issue-id>
 *             <title>The Guardian Weekly</title>
 *             <pages>64</pages>
 *             <enable-smart>true</enable-smart>
 *             <country>United Kingdom</country>
 *             <language>English</language>
 *             <country-code>gb</country-code>
 *             <language-code>en</language-code>
 *             <get-license-url>https://secure.example.com/activate?issue=69572023090100000000001001&amp;certificateid=12345678</get-license-url>
 *             <get-license-url2>https://secure.example.com/activate2?issue=69572023090100000000001001&amp;certificateid=12345678</get-license-url2>
 *             <get-license-url3>https://secure.example.com/activate3?issue=69572023090100000000001001&amp;certificateid=12345678</get-license-url3>
 *             <sound-disabled>0</sound-disabled>
 *             <smartflow-disabled>0</smartflow-disabled>
 *             <bookmarks-restricted>0</bookmarks-restricted>
 *             <page-printing-disabled>0</page-printing-disabled>
 *             <article-printing-disabled>0</article-printing-disabled>
 *             <comments-disabled>0</comments-disabled>
 *             <article-email-sharing-disabled>0</article-email-sharing-disabled>
 *             <diggit-disabled>0</diggit-disabled>
 *             <delicious-disabled>0</delicious-disabled>
 *             <facebook-disabled>0</facebook-disabled>
 *             <twitter-disabled>0</twitter-disabled>
 *             <livejournal-disabled>0</livejournal-disabled>
 *             <wordpress-disabled>0</wordpress-disabled>
 *             <screenshot-disabled>0</screenshot-disabled>
 *             <translation-disabled>0</translation-disabled>
 *             <translation-disabled-by-publisher>0</translation-disabled-by-publisher>
 *             <copy-article-disabled>0</copy-article-disabled>
 *             <instapaper-disabled>0</instapaper-disabled>
 *             <evernote-disabled>0</evernote-disabled>
 *             <onenote-disabled>0</onenote-disabled>
 *             <vote-disabled>0</vote-disabled>
 *             <expiration-date>2292-12-31</expiration-date>
 *             <update-type>1</update-type>
 *         </message>
 * </pre>
 */
public class NewspaperUpdateMessage extends NewspaperMessage {
    @JacksonXmlProperty(localName = "update-type")
    public int updateType;
}
