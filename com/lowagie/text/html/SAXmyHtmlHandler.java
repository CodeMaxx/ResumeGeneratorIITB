/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.html.HtmlTagMap;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.xml.SAXiTextHandler;
import com.lowagie.text.xml.XmlPeer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import org.xml.sax.Attributes;

public class SAXmyHtmlHandler
extends SAXiTextHandler {
    private Properties bodyAttributes = new Properties();
    private boolean tableBorder = false;

    public SAXmyHtmlHandler(DocListener docListener) {
        super(docListener, new HtmlTagMap());
    }

    public SAXmyHtmlHandler(DocListener docListener, BaseFont baseFont) {
        super(docListener, new HtmlTagMap(), baseFont);
    }

    public SAXmyHtmlHandler(DocListener docListener, HashMap hashMap) {
        super(docListener, hashMap);
    }

    public void startElement(String string, String string2, String string3, Attributes attributes) {
        if (HtmlTagMap.isHtml(string3 = string3.toLowerCase())) {
            return;
        }
        if (HtmlTagMap.isHead(string3)) {
            return;
        }
        if (HtmlTagMap.isTitle(string3)) {
            return;
        }
        if (HtmlTagMap.isMeta(string3)) {
            String string4 = null;
            String string5 = null;
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); ++i) {
                    String string6 = attributes.getQName(i);
                    if (string6.equalsIgnoreCase("content")) {
                        string5 = attributes.getValue(i);
                        continue;
                    }
                    if (!string6.equalsIgnoreCase("name")) continue;
                    string4 = attributes.getValue(i);
                }
            }
            if (string4 != null && string5 != null) {
                this.bodyAttributes.put(string4, string5);
            }
            return;
        }
        if (HtmlTagMap.isLink(string3)) {
            return;
        }
        if (HtmlTagMap.isBody(string3)) {
            XmlPeer xmlPeer = new XmlPeer("itext", string3);
            xmlPeer.addAlias("top", "topmargin");
            xmlPeer.addAlias("bottom", "bottommargin");
            xmlPeer.addAlias("right", "rightmargin");
            xmlPeer.addAlias("left", "leftmargin");
            this.bodyAttributes.putAll(xmlPeer.getAttributes(attributes));
            this.handleStartingTags(xmlPeer.getTag(), this.bodyAttributes);
            return;
        }
        if (this.myTags.containsKey(string3)) {
            XmlPeer xmlPeer = (XmlPeer)this.myTags.get(string3);
            if ("table".equals(xmlPeer.getTag()) || "cell".equals(xmlPeer.getTag())) {
                String string7;
                Properties properties = xmlPeer.getAttributes(attributes);
                if ("table".equals(xmlPeer.getTag()) && (string7 = properties.getProperty("borderwidth")) != null && Float.parseFloat(string7 + "f") > 0.0f) {
                    this.tableBorder = true;
                }
                if (this.tableBorder) {
                    properties.put("left", String.valueOf(true));
                    properties.put("right", String.valueOf(true));
                    properties.put("top", String.valueOf(true));
                    properties.put("bottom", String.valueOf(true));
                }
                this.handleStartingTags(xmlPeer.getTag(), properties);
                return;
            }
            this.handleStartingTags(xmlPeer.getTag(), xmlPeer.getAttributes(attributes));
            return;
        }
        Properties properties = new Properties();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String string8 = attributes.getQName(i).toLowerCase();
                properties.setProperty(string8, attributes.getValue(i).toLowerCase());
            }
        }
        this.handleStartingTags(string3, properties);
    }

    public void endElement(String string, String string2, String string3) {
        if ("paragraph".equals(string3 = string3.toLowerCase())) {
            try {
                this.document.add((Element)this.stack.pop());
                return;
            }
            catch (DocumentException var4_4) {
                throw new ExceptionConverter(var4_4);
            }
        }
        if (HtmlTagMap.isHead(string3)) {
            return;
        }
        if (HtmlTagMap.isTitle(string3)) {
            if (this.currentChunk != null) {
                this.bodyAttributes.put("title", this.currentChunk.getContent());
            }
            return;
        }
        if (HtmlTagMap.isMeta(string3)) {
            return;
        }
        if (HtmlTagMap.isLink(string3)) {
            return;
        }
        if (HtmlTagMap.isBody(string3)) {
            return;
        }
        if (this.myTags.containsKey(string3)) {
            XmlPeer xmlPeer = (XmlPeer)this.myTags.get(string3);
            if ("table".equals(xmlPeer.getTag())) {
                this.tableBorder = false;
            }
            super.handleEndingTags(xmlPeer.getTag());
            return;
        }
        this.handleEndingTags(string3);
    }
}

