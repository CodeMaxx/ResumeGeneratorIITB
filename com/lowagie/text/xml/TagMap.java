/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.xml.XmlPeer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class TagMap
extends HashMap {
    private static final long serialVersionUID = -6809383366554350820L;

    public TagMap(String string) {
        try {
            Class class_ = TagMap.class;
            this.init(class_.getClassLoader().getResourceAsStream(string));
        }
        catch (Exception var2_2) {
            try {
                this.init(new FileInputStream(string));
            }
            catch (FileNotFoundException var3_3) {
                throw new ExceptionConverter(var3_3);
            }
        }
    }

    public TagMap(InputStream inputStream) {
        this.init(inputStream);
    }

    protected void init(InputStream inputStream) {
        try {
            SAXParser sAXParser = SAXParserFactory.newInstance().newSAXParser();
            sAXParser.parse(new InputSource(inputStream), (DefaultHandler)new AttributeHandler(this));
        }
        catch (Exception var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    class AttributeHandler
    extends DefaultHandler {
        public static final String TAG = "tag";
        public static final String ATTRIBUTE = "attribute";
        public static final String NAME = "name";
        public static final String ALIAS = "alias";
        public static final String VALUE = "value";
        public static final String CONTENT = "content";
        private HashMap tagMap;
        private XmlPeer currentPeer;

        public AttributeHandler(HashMap hashMap) {
            this.tagMap = hashMap;
        }

        public void startElement(String string, String string2, String string3, Attributes attributes) {
            String string4 = attributes.getValue("name");
            String string5 = attributes.getValue("alias");
            String string6 = attributes.getValue("value");
            if (string4 != null) {
                if ("tag".equals(string3)) {
                    this.currentPeer = new XmlPeer(string4, string5);
                } else if ("attribute".equals(string3)) {
                    if (string5 != null) {
                        this.currentPeer.addAlias(string4, string5);
                    }
                    if (string6 != null) {
                        this.currentPeer.addValue(string4, string6);
                    }
                }
            }
            if ((string6 = attributes.getValue("content")) != null) {
                this.currentPeer.setContent(string6);
            }
        }

        public void ignorableWhitespace(char[] arrc, int n, int n2) {
        }

        public void characters(char[] arrc, int n, int n2) {
        }

        public void endElement(String string, String string2, String string3) {
            if ("tag".equals(string3)) {
                this.tagMap.put(this.currentPeer.getAlias(), this.currentPeer);
            }
        }
    }

}

