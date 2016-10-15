/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html;

import com.lowagie.text.xml.XmlPeer;
import java.util.Map;
import java.util.Properties;
import org.xml.sax.Attributes;

public class HtmlPeer
extends XmlPeer {
    public HtmlPeer(String string, String string2) {
        super(string, string2.toLowerCase());
    }

    public void addAlias(String string, String string2) {
        this.attributeAliases.put(string2.toLowerCase(), string);
    }

    public Properties getAttributes(Attributes attributes) {
        Properties properties = new Properties();
        properties.putAll(this.attributeValues);
        if (this.defaultContent != null) {
            properties.put("itext", this.defaultContent);
        }
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String string = this.getName(attributes.getQName(i).toLowerCase());
                String string2 = attributes.getValue(i);
                properties.setProperty(string, string2);
            }
        }
        return properties;
    }
}

