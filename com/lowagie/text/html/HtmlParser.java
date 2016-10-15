/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html;

import com.lowagie.text.DocListener;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.html.SAXmyHtmlHandler;
import com.lowagie.text.xml.XmlParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HtmlParser
extends XmlParser {
    public void go(DocListener docListener, InputSource inputSource) {
        try {
            this.parser.parse(inputSource, (DefaultHandler)new SAXmyHtmlHandler(docListener));
        }
        catch (SAXException var3_3) {
            throw new ExceptionConverter(var3_3);
        }
        catch (IOException var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    public static void parse(DocListener docListener, InputSource inputSource) {
        HtmlParser htmlParser = new HtmlParser();
        htmlParser.go(docListener, inputSource);
    }

    public void go(DocListener docListener, String string) {
        try {
            this.parser.parse(string, (DefaultHandler)new SAXmyHtmlHandler(docListener));
        }
        catch (SAXException var3_3) {
            throw new ExceptionConverter(var3_3);
        }
        catch (IOException var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    public static void parse(DocListener docListener, String string) {
        HtmlParser htmlParser = new HtmlParser();
        htmlParser.go(docListener, string);
    }

    public void go(DocListener docListener, InputStream inputStream) {
        try {
            this.parser.parse(new InputSource(inputStream), (DefaultHandler)new SAXmyHtmlHandler(docListener));
        }
        catch (SAXException var3_3) {
            throw new ExceptionConverter(var3_3);
        }
        catch (IOException var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    public static void parse(DocListener docListener, InputStream inputStream) {
        HtmlParser htmlParser = new HtmlParser();
        htmlParser.go(docListener, new InputSource(inputStream));
    }

    public void go(DocListener docListener, Reader reader) {
        try {
            this.parser.parse(new InputSource(reader), (DefaultHandler)new SAXmyHtmlHandler(docListener));
        }
        catch (SAXException var3_3) {
            throw new ExceptionConverter(var3_3);
        }
        catch (IOException var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    public static void parse(DocListener docListener, Reader reader) {
        HtmlParser htmlParser = new HtmlParser();
        htmlParser.go(docListener, new InputSource(reader));
    }
}

