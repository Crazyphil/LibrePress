package it.kapfer.librepress.server.xml.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import it.kapfer.librepress.server.xml.ResponseMessage;

import java.time.LocalDate;

/**
 * This class is a Jackson object representation of the {@code message} element in this XML structure:
 * <pre>
 *         <message id="12345678" type="newspaper" sent-time="01/06/2026 12:34:56">
 *             <issue-id>sfdy2019021000000000001001</issue-id>
 *             <title>Brain Games</title>
 *             <pages>28</pages>
 *             <enable-smart>false</enable-smart>
 *             <country>United States</country>
 *             <language>English</language>
 *             <country-code>us</country-code>
 *             <language-code>en</language-code>
 *             <get-license-url>https://secure.example.com/activate?issue=sfdy2019021000000000001001&amp;certificateid=12345678</get-license-url>
 *             <get-license-url2>https://secure.example.com/activate2?issue=sfdy2019021000000000001001&amp;certificateid=12345678</get-license-url2>
 *             <get-license-url3>https://secure.example.com/activate3?issue=sfdy2019021000000000001001&amp;certificateid=12345678</get-license-url3>
 *             <sound-disabled>0</sound-disabled>
 *             <smartflow-disabled>0</smartflow-disabled>
 *             <replica-disabled>0</replica-disabled>
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
 *         </message>
 * </pre>
 */
public class NewspaperMessage extends ResponseMessage {
    @JacksonXmlProperty(localName = "issue-id")
    public String issueId;

    public String title;

    public int pages;

    @JacksonXmlProperty(localName = "enable-smart")
    public boolean enableSmart;

    public String country;

    public String language;

    @JacksonXmlProperty(localName = "country-code")
    public String countryCode;

    @JacksonXmlProperty(localName = "language-code")
    public String languageCode;

    @JacksonXmlProperty(localName = "get-license-url")
    public String getLicenseUrl;

    @JacksonXmlProperty(localName = "get-license-url2")
    public String getLicenseUrl2;

    @JacksonXmlProperty(localName = "get-license-url3")
    public String getLicenseUrl3;

    @JacksonXmlProperty(localName = "sound-disabled")
    public Byte soundDisabled;

    @JacksonXmlProperty(localName = "smartflow-disabled")
    public Byte smartflowDisabled;

    @JacksonXmlProperty(localName = "replica-disabled")
    public Byte replicaDisabled;

    @JacksonXmlProperty(localName = "bookmarks-restricted")
    public Byte bookmarksRestricted;

    @JacksonXmlProperty(localName = "page-printing-disabled")
    public Byte pagePrintingDisabled;

    @JacksonXmlProperty(localName = "article-printing-disabled")
    public Byte articlePrintingDisabled;

    @JacksonXmlProperty(localName = "comments-disabled")
    public Byte commentsDisabled;

    @JacksonXmlProperty(localName = "article-email-sharing-disabled")
    public Byte articleEmailSharingDisabled;

    @JacksonXmlProperty(localName = "diggit-disabled")
    public Byte diggitDisabled;

    @JacksonXmlProperty(localName = "delicious-disabled")
    public Byte deliciousDisabled;

    @JacksonXmlProperty(localName = "facebook-disabled")
    public Byte facebookDisabled;

    @JacksonXmlProperty(localName = "twitter-disabled")
    public Byte twitterDisabled;

    @JacksonXmlProperty(localName = "livejournal-disabled")
    public Byte livejournalDisabled;

    @JacksonXmlProperty(localName = "wordpress-disabled")
    public Byte wordpressDisabled;

    @JacksonXmlProperty(localName = "screenshot-disabled")
    public Byte screenshotDisabled;

    @JacksonXmlProperty(localName = "translation-disabled")
    public Byte translationDisabled;

    @JacksonXmlProperty(localName = "translation-disabled-by-publisher")
    public Byte translationDisabledByPublisher;

    @JacksonXmlProperty(localName = "copy-article-disabled")
    public Byte copyArticleDisabled;

    @JacksonXmlProperty(localName = "instapaper-disabled")
    public Byte instapaperDisabled;

    @JacksonXmlProperty(localName = "evernote-disabled")
    public Byte evernoteDisabled;

    @JacksonXmlProperty(localName = "onenote-disabled")
    public Byte onenoteDisabled;

    @JacksonXmlProperty(localName = "vote-disabled")
    public Byte voteDisabled;

    @JacksonXmlProperty(localName = "expiration-date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate expirationDate;
}
