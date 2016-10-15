/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml;

import java.util.Map;
import java.util.Properties;
import org.xml.sax.Attributes;

public class XmlPeer {
    protected String tagname;
    protected String customTagname;
    protected Properties attributeAliases = new Properties();
    protected Properties attributeValues = new Properties();
    protected String defaultContent = null;

    public XmlPeer(String string, String string2) {
        this.tagname = string;
        this.customTagname = string2;
    }

    public String getTag() {
        return this.tagname;
    }

    public String getAlias() {
        return this.customTagname;
    }

    public Properties getAttributes(Attributes attributes) {
        Properties properties = new Properties();
        properties.putAll(this.attributeValues);
        if (this.defaultContent != null) {
            properties.put("itext", this.defaultContent);
        }
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String string = this.getName(attributes.getQName(i));
                properties.setProperty(string, attributes.getValue(i));
            }
        }
        return properties;
    }

    public void addAlias(String string, String string2) {
        this.attributeAliases.put(string2, string);
    }

    public void addValue(String string, String string2) {
        this.attributeValues.put(string, string2);
    }

    public void setContent(String string) {
        this.defaultContent = string;
    }

    public String getName(String string) {
        String string2 = this.attributeAliases.getProperty(string);
        if (string2 != null) {
            return string2;
        }
        return string;
    }

    public Properties getDefaultValues() {
        return this.attributeValues;
    }
}

