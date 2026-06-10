package it.kapfer.librepress.server.xml;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class is a Jackson object representation of the {@code ActivationResponse} element in this XML structure:
 * <pre>
 * <?xml version="1.0" encoding="utf-8"?>
 * <ActivationResponse xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://tempuri.org/">
 *   <Certificate>***</Certificate>
 *   <CertificateId>96eab2ac-4533-4dbf-b22c-a35c90abbae6</CertificateId>
 *   <DocumentInfo>
 *     <Country>United States</Country>
 *     <DefaultPageSize H="2884" W="2060" />
 *     <IsRightToLeft>false</IsRightToLeft>
 *     <Language>English</Language>
 *     <Pages>
 *       <Page H="980" PageName="1" PageNumber="1" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="2" PageNumber="2" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="3" PageNumber="3" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="4" PageNumber="4" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="5" PageNumber="5" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="6" PageNumber="6" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="7" PageNumber="7" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="8" PageNumber="8" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="9" PageNumber="9" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="10" PageNumber="10" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="11" PageNumber="11" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="12" PageNumber="12" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="13" PageNumber="13" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="14" PageNumber="14" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="15" PageNumber="15" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="16" PageNumber="16" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="17" PageNumber="17" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="18" PageNumber="18" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="19" PageNumber="19" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="20" PageNumber="20" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="21" PageNumber="21" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="22" PageNumber="22" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="23" PageNumber="23" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="24" PageNumber="24" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="25" PageNumber="25" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="26" PageNumber="26" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="27" PageNumber="27" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *       <Page H="980" PageName="28" PageNumber="28" Scale="10" Section="" W="767" ZoomScales="268,188" />
 *     </Pages>
 *     <Title>Brain Games</Title>
 *   </DocumentInfo>
 *   <DocumentOptions>
 *     <AllowExport>true</AllowExport>
 *     <AllowMobile>true</AllowMobile>
 *     <BuyIssueBeforePrinting>false</BuyIssueBeforePrinting>
 *     <EnablePrinting>true</EnablePrinting>
 *     <EnableTTS>true</EnableTTS>
 *     <EnableViewing>true</EnableViewing>
 *   </DocumentOptions>
 *   <DownloadUrls>
 *     <Url>http://cdn.ndcds.net/cds/files?***</Url>
 *     <Url>http://cdn.ndcds.net/cds/files?***</Url>
 *     <Url>http://cds212.ndcds.net/cds/files?***</Url>
 *     <Url>http://cds211.ndcds.net/cds/files?***</Url>
 *     <Url>http://cds202.ndcds.net/cds/files?***</Url>
 *   </DownloadUrls>
 *   <EncryptionType>2</EncryptionType>
 *   <ExpirationDate>2053-10-18</ExpirationDate>
 *   <ExpirationTime>2053-10-18T00:00:00.000Z</ExpirationTime>
 *   <ExpungeVersion>0</ExpungeVersion>
 *   <Issue>sfdy2019021000000000001001</Issue>
 *   <IssueId>5990991</IssueId>
 *   <LayoutVersion>0</LayoutVersion>
 *   <StatusCode>Ok</StatusCode>
 *   <ThumbnailUrls>
 *     <Url>https://t.prcdn.co/img</Url>
 *   </ThumbnailUrls>
 *   <SmartLayoutUrls>
 *     <Url>http://ndcds.trafficmanager.net/cds/files?***</Url>
 *     <Url>http://ndcds.trafficmanager.net/cds/files?***</Url>
 *     <Url>http://cds211.ndcds.net/cds/files?***</Url>
 *     <Url>http://cds212.ndcds.net/cds/files?***</Url>
 *     <Url>http://cds201.ndcds.net/cds/files?***</Url>
 *     <Url>http://cds202.ndcds.net/cds/files?***</Url>
 *   </SmartLayoutUrls>
 *   <ThumbnailUrlsByHeight>
 *     <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds211.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds212.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds202.ndcds.net/cds/files?thumbs%253a***</Url>
 *   </ThumbnailUrlsByHeight>
 *   <ThumbnailUrlsByWidth>
 *     <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds212.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds211.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds202.ndcds.net/cds/files?thumbs%253a***</Url>
 *   </ThumbnailUrlsByWidth>
 *   <UrlExpirationTime>2026-07-02 23:59:59</UrlExpirationTime>
 *   <UrlTTL>43887</UrlTTL>
 *   <ZoomUrls>
 *     <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds211.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds212.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds202.ndcds.net/cds/files?thumbs%253a***</Url>
 *   </ZoomUrls>
 *   <ZoomUrls2>
 *     <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cdn.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds212.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds211.ndcds.net/cds/files?thumbs%253a***</Url>
 *     <Url>http://cds202.ndcds.net/cds/files?thumbs%253a***</Url>
 *   </ZoomUrls2>
 *   <WordIndexUrls>
 *     <Url>http://ndcds.trafficmanager.net/cds/files?***</Url>
 *     <Url>http://ndcds.trafficmanager.net/cds/files?***</Url>
 *     <Url>http://cds211.ndcds.net/cds/files?***</Url>
 *     <Url>http://cds212.ndcds.net/cds/files?***</Url>
 *     <Url>http://cds202.ndcds.net/cds/files?***</Url>
 *     <Url>http://cds201.ndcds.net/cds/files?***</Url>
 *   </WordIndexUrls>
 * </ActivationResponse>
 * </pre>
 */
@JsonRootName("ActivationResponse")
@JsonFormat(with = Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class ActivationResponse {
    public String certificate;

    public String certificateId;

    public DocumentInfo documentInfo;

    public DocumentOptions documentOptions;

    @JacksonXmlElementWrapper(localName = "DownloadUrls")
    public List<String> downloadUrls;

    public int encryptionType;

    public String expirationDate;

    public String expirationTime;

    public int expungeVersion;

    public String gotoUrl;

    public String issue;

    public long issueId;

    public int layoutVersion;

    public String statusCode;

    public String statusMessage;

    @JacksonXmlElementWrapper(localName = "ThumbnailUrls")
    public List<String> thumbnailUrls;

    @JacksonXmlElementWrapper(localName = "ThumbnailUrlsByHeight")
    public List<String> thumbnailUrlsByHeight;

    @JacksonXmlElementWrapper(localName = "ThumbnailUrlsByWidth")
    public List<String> thumbnailUrlsByWidth;

    @JacksonXmlElementWrapper(localName = "SmartLayoutUrls")
    public List<String> smartLayoutUrls;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime urlExpirationTime;

    public long urlTTL;

    @JacksonXmlElementWrapper(localName = "ZoomUrls")
    public List<String> zoomUrls;

    @JacksonXmlElementWrapper(localName = "ZoomUrls2")
    public List<String> zoomUrls2;

    @JacksonXmlElementWrapper(localName = "WordIndexUrls")
    public List<String> wordIndexUrls;

    @JsonFormat(with = Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    public static class DocumentInfo {
        public String country;

        public DefaultPageSize defaultPageSize;

        public boolean isRightToLeft;

        public String language;

        @JacksonXmlElementWrapper(localName = "Pages")
        public List<Page> pages;

        public String title;

        public static class DefaultPageSize {
            @JacksonXmlProperty(isAttribute = true, localName = "H")
            public int h;
            @JacksonXmlProperty(isAttribute = true, localName = "W")
            public int w;
        }

        @JsonFormat(with = Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        public static class Page {
            @JacksonXmlProperty(isAttribute = true)
            public int h;

            @JacksonXmlProperty(isAttribute = true)
            public int w;

            @JacksonXmlProperty(isAttribute = true)
            public String pageName;

            @JacksonXmlProperty(isAttribute = true)
            public int pageNumber;

            @JacksonXmlProperty(isAttribute = true)
            public int scale;

            @JacksonXmlProperty(isAttribute = true)
            public String section;

            @JacksonXmlProperty(isAttribute = true)
            public String zoomScales;
        }
    }

    @JsonFormat(with = Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    public static class DocumentOptions {
        public boolean allowExport;

        public boolean allowMobile;

        public boolean buyIssueBeforePrinting;

        public boolean enablePrinting;

        public boolean enableTTS;

        public boolean enableViewing;
    }
}
