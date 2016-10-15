/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml;

import com.lowagie.text.DocListener;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.xml.SAXiTextHandler;
import com.lowagie.text.xml.SAXmyHandler;
import com.lowagie.text.xml.TagMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlParser {
    protected SAXParser parser;

    public XmlParser() {
        try {
            this.parser = SAXParserFactory.newInstance().newSAXParser();
        }
        catch (ParserConfigurationException var1_1) {
            throw new ExceptionConverter(var1_1);
        }
        catch (SAXException var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    public void go(DocListener docListener, InputSource inputSource) {
        try {
            this.parser.parse(inputSource, (DefaultHandler)new SAXiTextHandler(docListener));
        }
        catch (SAXException var3_3) {
            throw new ExceptionConverter(var3_3);
        }
        catch (IOException var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    public void go(DocListener docListener, InputSource inputSource, String string) {
        try {
            this.parser.parse(inputSource, (DefaultHandler)new SAXmyHandler(docListener, new TagMap(string)));
        }
        catch (SAXException var4_4) {
            throw new ExceptionConverter(var4_4);
        }
        catch (IOException var4_5) {
            throw new ExceptionConverter(var4_5);
        }
    }

    public void go(DocListener docListener, InputSource inputSource, InputStream inputStream) {
        try {
            this.parser.parse(inputSource, (DefaultHandler)new SAXmyHandler(docListener, new TagMap(inputStream)));
        }
        catch (SAXException var4_4) {
            throw new ExceptionConverter(var4_4);
        }
        catch (IOException var4_5) {
            throw new ExceptionConverter(var4_5);
        }
    }

    public void go(DocListener docListener, InputSource inputSource, HashMap hashMap) {
        try {
            this.parser.parse(inputSource, (DefaultHandler)new SAXmyHandler(docListener, hashMap));
        }
        catch (SAXException var4_4) {
            throw new ExceptionConverter(var4_4);
        }
        catch (IOException var4_5) {
            throw new ExceptionConverter(var4_5);
        }
    }

    public void go(DocListener docListener, String string) {
        try {
            this.parser.parse(string, (DefaultHandler)new SAXiTextHandler(docListener));
        }
        catch (SAXException var3_3) {
            throw new ExceptionConverter(var3_3);
        }
        catch (IOException var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    public void go(DocListener docListener, String string, String string2) {
        try {
            this.parser.parse(string, (DefaultHandler)new SAXmyHandler(docListener, new TagMap(string2)));
        }
        catch (SAXException var4_4) {
            throw new ExceptionConverter(var4_4);
        }
        catch (IOException var4_5) {
            throw new ExceptionConverter(var4_5);
        }
    }

    public void go(DocListener docListener, String string, HashMap hashMap) {
        try {
            this.parser.parse(string, (DefaultHandler)new SAXmyHandler(docListener, hashMap));
        }
        catch (SAXException var4_4) {
            throw new ExceptionConverter(var4_4);
        }
        catch (IOException var4_5) {
            throw new ExceptionConverter(var4_5);
        }
    }

    public static void parse(DocListener docListener, InputSource inputSource) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, inputSource);
    }

    public static void parse(DocListener docListener, InputSource inputSource, String string) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, inputSource, string);
    }

    public static void parse(DocListener docListener, InputSource inputSource, HashMap hashMap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, inputSource, hashMap);
    }

    public static void parse(DocListener docListener, String string) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, string);
    }

    public static void parse(DocListener docListener, String string, String string2) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, string, string2);
    }

    public static void parse(DocListener docListener, String string, HashMap hashMap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, string, hashMap);
    }

    public static void parse(DocListener docListener, InputStream inputStream) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, new InputSource(inputStream));
    }

    public static void parse(DocListener docListener, InputStream inputStream, String string) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, new InputSource(inputStream), string);
    }

    public static void parse(DocListener docListener, InputStream inputStream, HashMap hashMap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, new InputSource(inputStream), hashMap);
    }

    public static void parse(DocListener docListener, Reader reader) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, new InputSource(reader));
    }

    public static void parse(DocListener docListener, Reader reader, String string) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, new InputSource(reader), string);
    }

    public static void parse(DocListener docListener, Reader reader, HashMap hashMap) {
        XmlParser xmlParser = new XmlParser();
        xmlParser.go(docListener, new InputSource(reader), hashMap);
    }
}

