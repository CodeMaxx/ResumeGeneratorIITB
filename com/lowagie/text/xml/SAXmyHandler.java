/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml;

import com.lowagie.text.DocListener;
import com.lowagie.text.xml.SAXiTextHandler;
import com.lowagie.text.xml.XmlPeer;
import java.util.HashMap;
import java.util.Properties;
import org.xml.sax.Attributes;

public class SAXmyHandler
extends SAXiTextHandler {
    public SAXmyHandler(DocListener docListener, HashMap hashMap) {
        super(docListener, hashMap);
    }

    public void startElement(String string, String string2, String string3, Attributes attributes) {
        if (this.myTags.containsKey(string3)) {
            XmlPeer xmlPeer = (XmlPeer)this.myTags.get(string3);
            this.handleStartingTags(xmlPeer.getTag(), xmlPeer.getAttributes(attributes));
        } else {
            Properties properties = new Properties();
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); ++i) {
                    String string4 = attributes.getQName(i);
                    properties.setProperty(string4, attributes.getValue(i));
                }
            }
            this.handleStartingTags(string3, properties);
        }
    }

    public void endElement(String string, String string2, String string3) {
        if (this.myTags.containsKey(string3)) {
            XmlPeer xmlPeer = (XmlPeer)this.myTags.get(string3);
            this.handleEndingTags(xmlPeer.getTag());
        } else {
            this.handleEndingTags(string3);
        }
    }
}

