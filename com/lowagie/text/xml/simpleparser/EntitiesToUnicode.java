/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml.simpleparser;

import java.util.HashMap;

public class EntitiesToUnicode {
    public static final HashMap map = new HashMap();

    public static char decodeEntity(String string) {
        if (string.startsWith("#x")) {
            try {
                return (char)Integer.parseInt(string.substring(2), 16);
            }
            catch (NumberFormatException var1_1) {
                return '\u0000';
            }
        }
        if (string.startsWith("#")) {
            try {
                return (char)Integer.parseInt(string.substring(1));
            }
            catch (NumberFormatException var1_2) {
                return '\u0000';
            }
        }
        Character c = (Character)map.get(string);
        if (c == null) {
            return '\u0000';
        }
        return c.charValue();
    }

    public static String decodeString(String string) {
        int n = string.indexOf(38);
        if (n == -1) {
            return string;
        }
        StringBuffer stringBuffer = new StringBuffer(string.substring(0, n));
        do {
            int n2;
            if ((n2 = string.indexOf(59, n)) == -1) {
                stringBuffer.append(string.substring(n));
                return stringBuffer.toString();
            }
            int n3 = string.indexOf(38, n + 1);
            while (n3 != -1 && n3 < n2) {
                stringBuffer.append(string.substring(n, n3));
                n = n3;
                n3 = string.indexOf(38, n + 1);
            }
            char c = EntitiesToUnicode.decodeEntity(string.substring(n + 1, n2));
            if (string.length() < n2 + 1) {
                return stringBuffer.toString();
            }
            if (c == '\u0000') {
                stringBuffer.append(string.substring(n, n2 + 1));
            } else {
                stringBuffer.append(c);
            }
            n = string.indexOf(38, n2);
            if (n == -1) {
                stringBuffer.append(string.substring(n2 + 1));
                return stringBuffer.toString();
            }
            stringBuffer.append(string.substring(n2 + 1, n));
        } while (true);
    }

    static {
        map.put("nbsp", new Character('\u00a0'));
        map.put("iexcl", new Character('\u00a1'));
        map.put("cent", new Character('\u00a2'));
        map.put("pound", new Character('\u00a3'));
        map.put("curren", new Character('\u00a4'));
        map.put("yen", new Character('\u00a5'));
        map.put("brvbar", new Character('\u00a6'));
        map.put("sect", new Character('\u00a7'));
        map.put("uml", new Character('\u00a8'));
        map.put("copy", new Character('\u00a9'));
        map.put("ordf", new Character('\u00aa'));
        map.put("laquo", new Character('\u00ab'));
        map.put("not", new Character('\u00ac'));
        map.put("shy", new Character('\u00ad'));
        map.put("reg", new Character('\u00ae'));
        map.put("macr", new Character('\u00af'));
        map.put("deg", new Character('\u00b0'));
        map.put("plusmn", new Character('\u00b1'));
        map.put("sup2", new Character('\u00b2'));
        map.put("sup3", new Character('\u00b3'));
        map.put("acute", new Character('\u00b4'));
        map.put("micro", new Character('\u00b5'));
        map.put("para", new Character('\u00b6'));
        map.put("middot", new Character('\u00b7'));
        map.put("cedil", new Character('\u00b8'));
        map.put("sup1", new Character('\u00b9'));
        map.put("ordm", new Character('\u00ba'));
        map.put("raquo", new Character('\u00bb'));
        map.put("frac14", new Character('\u00bc'));
        map.put("frac12", new Character('\u00bd'));
        map.put("frac34", new Character('\u00be'));
        map.put("iquest", new Character('\u00bf'));
        map.put("Agrave", new Character('\u00c0'));
        map.put("Aacute", new Character('\u00c1'));
        map.put("Acirc", new Character('\u00c2'));
        map.put("Atilde", new Character('\u00c3'));
        map.put("Auml", new Character('\u00c4'));
        map.put("Aring", new Character('\u00c5'));
        map.put("AElig", new Character('\u00c6'));
        map.put("Ccedil", new Character('\u00c7'));
        map.put("Egrave", new Character('\u00c8'));
        map.put("Eacute", new Character('\u00c9'));
        map.put("Ecirc", new Character('\u00ca'));
        map.put("Euml", new Character('\u00cb'));
        map.put("Igrave", new Character('\u00cc'));
        map.put("Iacute", new Character('\u00cd'));
        map.put("Icirc", new Character('\u00ce'));
        map.put("Iuml", new Character('\u00cf'));
        map.put("ETH", new Character('\u00d0'));
        map.put("Ntilde", new Character('\u00d1'));
        map.put("Ograve", new Character('\u00d2'));
        map.put("Oacute", new Character('\u00d3'));
        map.put("Ocirc", new Character('\u00d4'));
        map.put("Otilde", new Character('\u00d5'));
        map.put("Ouml", new Character('\u00d6'));
        map.put("times", new Character('\u00d7'));
        map.put("Oslash", new Character('\u00d8'));
        map.put("Ugrave", new Character('\u00d9'));
        map.put("Uacute", new Character('\u00da'));
        map.put("Ucirc", new Character('\u00db'));
        map.put("Uuml", new Character('\u00dc'));
        map.put("Yacute", new Character('\u00dd'));
        map.put("THORN", new Character('\u00de'));
        map.put("szlig", new Character('\u00df'));
        map.put("agrave", new Character('\u00e0'));
        map.put("aacute", new Character('\u00e1'));
        map.put("acirc", new Character('\u00e2'));
        map.put("atilde", new Character('\u00e3'));
        map.put("auml", new Character('\u00e4'));
        map.put("aring", new Character('\u00e5'));
        map.put("aelig", new Character('\u00e6'));
        map.put("ccedil", new Character('\u00e7'));
        map.put("egrave", new Character('\u00e8'));
        map.put("eacute", new Character('\u00e9'));
        map.put("ecirc", new Character('\u00ea'));
        map.put("euml", new Character('\u00eb'));
        map.put("igrave", new Character('\u00ec'));
        map.put("iacute", new Character('\u00ed'));
        map.put("icirc", new Character('\u00ee'));
        map.put("iuml", new Character('\u00ef'));
        map.put("eth", new Character('\u00f0'));
        map.put("ntilde", new Character('\u00f1'));
        map.put("ograve", new Character('\u00f2'));
        map.put("oacute", new Character('\u00f3'));
        map.put("ocirc", new Character('\u00f4'));
        map.put("otilde", new Character('\u00f5'));
        map.put("ouml", new Character('\u00f6'));
        map.put("divide", new Character('\u00f7'));
        map.put("oslash", new Character('\u00f8'));
        map.put("ugrave", new Character('\u00f9'));
        map.put("uacute", new Character('\u00fa'));
        map.put("ucirc", new Character('\u00fb'));
        map.put("uuml", new Character('\u00fc'));
        map.put("yacute", new Character('\u00fd'));
        map.put("thorn", new Character('\u00fe'));
        map.put("yuml", new Character('\u00ff'));
        map.put("fnof", new Character('\u0192'));
        map.put("Alpha", new Character('\u0391'));
        map.put("Beta", new Character('\u0392'));
        map.put("Gamma", new Character('\u0393'));
        map.put("Delta", new Character('\u0394'));
        map.put("Epsilon", new Character('\u0395'));
        map.put("Zeta", new Character('\u0396'));
        map.put("Eta", new Character('\u0397'));
        map.put("Theta", new Character('\u0398'));
        map.put("Iota", new Character('\u0399'));
        map.put("Kappa", new Character('\u039a'));
        map.put("Lambda", new Character('\u039b'));
        map.put("Mu", new Character('\u039c'));
        map.put("Nu", new Character('\u039d'));
        map.put("Xi", new Character('\u039e'));
        map.put("Omicron", new Character('\u039f'));
        map.put("Pi", new Character('\u03a0'));
        map.put("Rho", new Character('\u03a1'));
        map.put("Sigma", new Character('\u03a3'));
        map.put("Tau", new Character('\u03a4'));
        map.put("Upsilon", new Character('\u03a5'));
        map.put("Phi", new Character('\u03a6'));
        map.put("Chi", new Character('\u03a7'));
        map.put("Psi", new Character('\u03a8'));
        map.put("Omega", new Character('\u03a9'));
        map.put("alpha", new Character('\u03b1'));
        map.put("beta", new Character('\u03b2'));
        map.put("gamma", new Character('\u03b3'));
        map.put("delta", new Character('\u03b4'));
        map.put("epsilon", new Character('\u03b5'));
        map.put("zeta", new Character('\u03b6'));
        map.put("eta", new Character('\u03b7'));
        map.put("theta", new Character('\u03b8'));
        map.put("iota", new Character('\u03b9'));
        map.put("kappa", new Character('\u03ba'));
        map.put("lambda", new Character('\u03bb'));
        map.put("mu", new Character('\u03bc'));
        map.put("nu", new Character('\u03bd'));
        map.put("xi", new Character('\u03be'));
        map.put("omicron", new Character('\u03bf'));
        map.put("pi", new Character('\u03c0'));
        map.put("rho", new Character('\u03c1'));
        map.put("sigmaf", new Character('\u03c2'));
        map.put("sigma", new Character('\u03c3'));
        map.put("tau", new Character('\u03c4'));
        map.put("upsilon", new Character('\u03c5'));
        map.put("phi", new Character('\u03c6'));
        map.put("chi", new Character('\u03c7'));
        map.put("psi", new Character('\u03c8'));
        map.put("omega", new Character('\u03c9'));
        map.put("thetasym", new Character('\u03d1'));
        map.put("upsih", new Character('\u03d2'));
        map.put("piv", new Character('\u03d6'));
        map.put("bull", new Character('\u2022'));
        map.put("hellip", new Character('\u2026'));
        map.put("prime", new Character('\u2032'));
        map.put("Prime", new Character('\u2033'));
        map.put("oline", new Character('\u203e'));
        map.put("frasl", new Character('\u2044'));
        map.put("weierp", new Character('\u2118'));
        map.put("image", new Character('\u2111'));
        map.put("real", new Character('\u211c'));
        map.put("trade", new Character('\u2122'));
        map.put("alefsym", new Character('\u2135'));
        map.put("larr", new Character('\u2190'));
        map.put("uarr", new Character('\u2191'));
        map.put("rarr", new Character('\u2192'));
        map.put("darr", new Character('\u2193'));
        map.put("harr", new Character('\u2194'));
        map.put("crarr", new Character('\u21b5'));
        map.put("lArr", new Character('\u21d0'));
        map.put("uArr", new Character('\u21d1'));
        map.put("rArr", new Character('\u21d2'));
        map.put("dArr", new Character('\u21d3'));
        map.put("hArr", new Character('\u21d4'));
        map.put("forall", new Character('\u2200'));
        map.put("part", new Character('\u2202'));
        map.put("exist", new Character('\u2203'));
        map.put("empty", new Character('\u2205'));
        map.put("nabla", new Character('\u2207'));
        map.put("isin", new Character('\u2208'));
        map.put("notin", new Character('\u2209'));
        map.put("ni", new Character('\u220b'));
        map.put("prod", new Character('\u220f'));
        map.put("sum", new Character('\u2211'));
        map.put("minus", new Character('\u2212'));
        map.put("lowast", new Character('\u2217'));
        map.put("radic", new Character('\u221a'));
        map.put("prop", new Character('\u221d'));
        map.put("infin", new Character('\u221e'));
        map.put("ang", new Character('\u2220'));
        map.put("and", new Character('\u2227'));
        map.put("or", new Character('\u2228'));
        map.put("cap", new Character('\u2229'));
        map.put("cup", new Character('\u222a'));
        map.put("int", new Character('\u222b'));
        map.put("there4", new Character('\u2234'));
        map.put("sim", new Character('\u223c'));
        map.put("cong", new Character('\u2245'));
        map.put("asymp", new Character('\u2248'));
        map.put("ne", new Character('\u2260'));
        map.put("equiv", new Character('\u2261'));
        map.put("le", new Character('\u2264'));
        map.put("ge", new Character('\u2265'));
        map.put("sub", new Character('\u2282'));
        map.put("sup", new Character('\u2283'));
        map.put("nsub", new Character('\u2284'));
        map.put("sube", new Character('\u2286'));
        map.put("supe", new Character('\u2287'));
        map.put("oplus", new Character('\u2295'));
        map.put("otimes", new Character('\u2297'));
        map.put("perp", new Character('\u22a5'));
        map.put("sdot", new Character('\u22c5'));
        map.put("lceil", new Character('\u2308'));
        map.put("rceil", new Character('\u2309'));
        map.put("lfloor", new Character('\u230a'));
        map.put("rfloor", new Character('\u230b'));
        map.put("lang", new Character('\u2329'));
        map.put("rang", new Character('\u232a'));
        map.put("loz", new Character('\u25ca'));
        map.put("spades", new Character('\u2660'));
        map.put("clubs", new Character('\u2663'));
        map.put("hearts", new Character('\u2665'));
        map.put("diams", new Character('\u2666'));
        map.put("quot", new Character('\"'));
        map.put("amp", new Character('&'));
        map.put("apos", new Character('\''));
        map.put("lt", new Character('<'));
        map.put("gt", new Character('>'));
        map.put("OElig", new Character('\u0152'));
        map.put("oelig", new Character('\u0153'));
        map.put("Scaron", new Character('\u0160'));
        map.put("scaron", new Character('\u0161'));
        map.put("Yuml", new Character('\u0178'));
        map.put("circ", new Character('\u02c6'));
        map.put("tilde", new Character('\u02dc'));
        map.put("ensp", new Character('\u2002'));
        map.put("emsp", new Character('\u2003'));
        map.put("thinsp", new Character('\u2009'));
        map.put("zwnj", new Character('\u200c'));
        map.put("zwj", new Character('\u200d'));
        map.put("lrm", new Character('\u200e'));
        map.put("rlm", new Character('\u200f'));
        map.put("ndash", new Character('\u2013'));
        map.put("mdash", new Character('\u2014'));
        map.put("lsquo", new Character('\u2018'));
        map.put("rsquo", new Character('\u2019'));
        map.put("sbquo", new Character('\u201a'));
        map.put("ldquo", new Character('\u201c'));
        map.put("rdquo", new Character('\u201d'));
        map.put("bdquo", new Character('\u201e'));
        map.put("dagger", new Character('\u2020'));
        map.put("Dagger", new Character('\u2021'));
        map.put("permil", new Character('\u2030'));
        map.put("lsaquo", new Character('\u2039'));
        map.put("rsaquo", new Character('\u203a'));
        map.put("euro", new Character('\u20ac'));
    }
}

