/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html;

import com.lowagie.text.ElementTags;
import com.lowagie.text.html.HtmlPeer;
import com.lowagie.text.html.HtmlTags;
import java.util.HashMap;

public class HtmlTagMap
extends HashMap {
    private static final long serialVersionUID = 5287430058473705350L;

    public HtmlTagMap() {
        HtmlPeer htmlPeer = new HtmlPeer("itext", "html");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("phrase", "span");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("chunk", "font");
        htmlPeer.addAlias("font", "face");
        htmlPeer.addAlias("size", "point-size");
        htmlPeer.addAlias("color", "color");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("anchor", "a");
        htmlPeer.addAlias("name", "name");
        htmlPeer.addAlias("reference", "href");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("paragraph", "p");
        htmlPeer.addAlias("align", "align");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("paragraph", "div");
        htmlPeer.addAlias("align", "align");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("paragraph", HtmlTags.H[0]);
        htmlPeer.addValue("size", "20");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("paragraph", HtmlTags.H[1]);
        htmlPeer.addValue("size", "18");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("paragraph", HtmlTags.H[2]);
        htmlPeer.addValue("size", "16");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("paragraph", HtmlTags.H[3]);
        htmlPeer.addValue("size", "14");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("paragraph", HtmlTags.H[4]);
        htmlPeer.addValue("size", "12");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("paragraph", HtmlTags.H[5]);
        htmlPeer.addValue("size", "10");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("list", "ol");
        htmlPeer.addValue("numbered", "true");
        htmlPeer.addValue("symbolindent", "20");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("list", "ul");
        htmlPeer.addValue("numbered", "false");
        htmlPeer.addValue("symbolindent", "20");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("listitem", "li");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("phrase", "i");
        htmlPeer.addValue("fontstyle", "italic");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("phrase", "em");
        htmlPeer.addValue("fontstyle", "italic");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("phrase", "b");
        htmlPeer.addValue("fontstyle", "bold");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("phrase", "strong");
        htmlPeer.addValue("fontstyle", "bold");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("phrase", "s");
        htmlPeer.addValue("fontstyle", "line-through");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("phrase", "code");
        htmlPeer.addValue("font", "Courier");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("phrase", "var");
        htmlPeer.addValue("font", "Courier");
        htmlPeer.addValue("fontstyle", "italic");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("phrase", "u");
        htmlPeer.addValue("fontstyle", "underline");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("chunk", "sup");
        htmlPeer.addValue(ElementTags.SUBSUPSCRIPT, "6.0");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("chunk", "sub");
        htmlPeer.addValue(ElementTags.SUBSUPSCRIPT, "-6.0");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("horizontalrule", "hr");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("table", "table");
        htmlPeer.addAlias("width", "width");
        htmlPeer.addAlias("backgroundcolor", "bgcolor");
        htmlPeer.addAlias("bordercolor", "bordercolor");
        htmlPeer.addAlias("columns", "cols");
        htmlPeer.addAlias("cellpadding", "cellpadding");
        htmlPeer.addAlias("cellspacing", "cellspacing");
        htmlPeer.addAlias("borderwidth", "border");
        htmlPeer.addAlias("align", "align");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("row", "tr");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("cell", "td");
        htmlPeer.addAlias("width", "width");
        htmlPeer.addAlias("backgroundcolor", "bgcolor");
        htmlPeer.addAlias("bordercolor", "bordercolor");
        htmlPeer.addAlias("colspan", "colspan");
        htmlPeer.addAlias("rowspan", "rowspan");
        htmlPeer.addAlias("nowrap", "nowrap");
        htmlPeer.addAlias("horizontalalign", "align");
        htmlPeer.addAlias("verticalalign", "valign");
        htmlPeer.addValue("header", "false");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("cell", "th");
        htmlPeer.addAlias("width", "width");
        htmlPeer.addAlias("backgroundcolor", "bgcolor");
        htmlPeer.addAlias("bordercolor", "bordercolor");
        htmlPeer.addAlias("colspan", "colspan");
        htmlPeer.addAlias("rowspan", "rowspan");
        htmlPeer.addAlias("nowrap", "nowrap");
        htmlPeer.addAlias("horizontalalign", "align");
        htmlPeer.addAlias("verticalalign", "valign");
        htmlPeer.addValue("header", "true");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("image", "img");
        htmlPeer.addAlias("url", "src");
        htmlPeer.addAlias("alt", "alt");
        htmlPeer.addAlias("plainwidth", "width");
        htmlPeer.addAlias("plainheight", "height");
        this.put(htmlPeer.getAlias(), htmlPeer);
        htmlPeer = new HtmlPeer("newline", "br");
        this.put(htmlPeer.getAlias(), htmlPeer);
    }

    public static boolean isHtml(String string) {
        return "html".equalsIgnoreCase(string);
    }

    public static boolean isHead(String string) {
        return "head".equalsIgnoreCase(string);
    }

    public static boolean isMeta(String string) {
        return "meta".equalsIgnoreCase(string);
    }

    public static boolean isLink(String string) {
        return "link".equalsIgnoreCase(string);
    }

    public static boolean isTitle(String string) {
        return "title".equalsIgnoreCase(string);
    }

    public static boolean isBody(String string) {
        return "body".equalsIgnoreCase(string);
    }

    public static boolean isSpecialTag(String string) {
        return HtmlTagMap.isHtml(string) || HtmlTagMap.isHead(string) || HtmlTagMap.isMeta(string) || HtmlTagMap.isLink(string) || HtmlTagMap.isBody(string);
    }
}

