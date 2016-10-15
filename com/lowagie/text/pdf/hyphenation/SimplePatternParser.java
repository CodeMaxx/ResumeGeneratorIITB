/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.hyphenation;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.hyphenation.Hyphen;
import com.lowagie.text.pdf.hyphenation.PatternConsumer;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class SimplePatternParser
implements SimpleXMLDocHandler,
PatternConsumer {
    int currElement;
    PatternConsumer consumer;
    StringBuffer token = new StringBuffer();
    ArrayList exception;
    char hyphenChar = 45;
    SimpleXMLParser parser;
    static final int ELEM_CLASSES = 1;
    static final int ELEM_EXCEPTIONS = 2;
    static final int ELEM_PATTERNS = 3;
    static final int ELEM_HYPHEN = 4;

    public void parse(InputStream inputStream, PatternConsumer patternConsumer) {
        this.consumer = patternConsumer;
        try {
            SimpleXMLParser.parse((SimpleXMLDocHandler)this, inputStream);
        }
        catch (IOException var3_4) {
            throw new ExceptionConverter(var3_4);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (Exception var5_6) {}
        }
    }

    protected static String getPattern(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            if (Character.isDigit(string.charAt(i))) continue;
            stringBuffer.append(string.charAt(i));
        }
        return stringBuffer.toString();
    }

    protected ArrayList normalizeException(ArrayList arrayList) {
        ArrayList<Object> arrayList2 = new ArrayList<Object>();
        for (int i = 0; i < arrayList.size(); ++i) {
            Object e = arrayList.get(i);
            if (e instanceof String) {
                String string = (String)e;
                StringBuffer stringBuffer = new StringBuffer();
                for (int j = 0; j < string.length(); ++j) {
                    char c = string.charAt(j);
                    if (c != this.hyphenChar) {
                        stringBuffer.append(c);
                        continue;
                    }
                    arrayList2.add(stringBuffer.toString());
                    stringBuffer.setLength(0);
                    char[] arrc = new char[]{this.hyphenChar};
                    arrayList2.add(new Hyphen(new String(arrc), null, null));
                }
                if (stringBuffer.length() <= 0) continue;
                arrayList2.add(stringBuffer.toString());
                continue;
            }
            arrayList2.add(e);
        }
        return arrayList2;
    }

    protected String getExceptionWord(ArrayList arrayList) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayList.size(); ++i) {
            Object e = arrayList.get(i);
            if (e instanceof String) {
                stringBuffer.append((String)e);
                continue;
            }
            if (((Hyphen)e).noBreak == null) continue;
            stringBuffer.append(((Hyphen)e).noBreak);
        }
        return stringBuffer.toString();
    }

    protected static String getInterletterValues(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        String string2 = string + "a";
        int n = string2.length();
        for (int i = 0; i < n; ++i) {
            char c = string2.charAt(i);
            if (Character.isDigit(c)) {
                stringBuffer.append(c);
                ++i;
                continue;
            }
            stringBuffer.append('0');
        }
        return stringBuffer.toString();
    }

    public void endDocument() {
    }

    public void endElement(String string) {
        if (this.token.length() > 0) {
            String string2 = this.token.toString();
            switch (this.currElement) {
                case 1: {
                    this.consumer.addClass(string2);
                    break;
                }
                case 2: {
                    this.exception.add(string2);
                    this.exception = this.normalizeException(this.exception);
                    this.consumer.addException(this.getExceptionWord(this.exception), (ArrayList)this.exception.clone());
                    break;
                }
                case 3: {
                    this.consumer.addPattern(SimplePatternParser.getPattern(string2), SimplePatternParser.getInterletterValues(string2));
                    break;
                }
            }
            if (this.currElement != 4) {
                this.token.setLength(0);
            }
        }
        this.currElement = this.currElement == 4 ? 2 : 0;
    }

    public void startDocument() {
    }

    public void startElement(String string, HashMap hashMap) {
        if (string.equals("hyphen-char")) {
            String string2 = (String)hashMap.get("value");
            if (string2 != null && string2.length() == 1) {
                this.hyphenChar = string2.charAt(0);
            }
        } else if (string.equals("classes")) {
            this.currElement = 1;
        } else if (string.equals("patterns")) {
            this.currElement = 3;
        } else if (string.equals("exceptions")) {
            this.currElement = 2;
            this.exception = new ArrayList();
        } else if (string.equals("hyphen")) {
            if (this.token.length() > 0) {
                this.exception.add(this.token.toString());
            }
            this.exception.add(new Hyphen((String)hashMap.get("pre"), (String)hashMap.get("no"), (String)hashMap.get("post")));
            this.currElement = 4;
        }
        this.token.setLength(0);
    }

    public void text(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        while (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken();
            switch (this.currElement) {
                case 1: {
                    this.consumer.addClass(string2);
                    break;
                }
                case 2: {
                    this.exception.add(string2);
                    this.exception = this.normalizeException(this.exception);
                    this.consumer.addException(this.getExceptionWord(this.exception), (ArrayList)this.exception.clone());
                    this.exception.clear();
                    break;
                }
                case 3: {
                    this.consumer.addPattern(SimplePatternParser.getPattern(string2), SimplePatternParser.getInterletterValues(string2));
                }
            }
        }
    }

    public void addClass(String string) {
        System.out.println("class: " + string);
    }

    public void addException(String string, ArrayList arrayList) {
        System.out.println("exception: " + string + " : " + arrayList.toString());
    }

    public void addPattern(String string, String string2) {
        System.out.println("pattern: " + string + " : " + string2);
    }

    public static void main(String[] arrstring) throws Exception {
        try {
            if (arrstring.length > 0) {
                SimplePatternParser simplePatternParser = new SimplePatternParser();
                simplePatternParser.parse(new FileInputStream(arrstring[0]), simplePatternParser);
            }
        }
        catch (Exception var1_2) {
            var1_2.printStackTrace();
        }
    }
}

