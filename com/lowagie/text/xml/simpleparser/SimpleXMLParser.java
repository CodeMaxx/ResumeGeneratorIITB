/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml.simpleparser;

import com.lowagie.text.xml.simpleparser.EntitiesToUnicode;
import com.lowagie.text.xml.simpleparser.IanaEncodings;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandlerComment;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Stack;

public final class SimpleXMLParser {
    private static final int UNKNOWN = 0;
    private static final int TEXT = 1;
    private static final int TAG_ENCOUNTERED = 2;
    private static final int EXAMIN_TAG = 3;
    private static final int TAG_EXAMINED = 4;
    private static final int IN_CLOSETAG = 5;
    private static final int SINGLE_TAG = 6;
    private static final int CDATA = 7;
    private static final int COMMENT = 8;
    private static final int PI = 9;
    private static final int ENTITY = 10;
    private static final int QUOTE = 11;
    private static final int ATTRIBUTE_KEY = 12;
    private static final int ATTRIBUTE_EQUAL = 13;
    private static final int ATTRIBUTE_VALUE = 14;
    Stack stack;
    int character = 0;
    int previousCharacter = -1;
    int lines = 1;
    int columns = 0;
    boolean eol = false;
    int state;
    boolean html;
    StringBuffer text = new StringBuffer();
    StringBuffer entity = new StringBuffer();
    String tag = null;
    HashMap attributes = null;
    SimpleXMLDocHandler doc;
    SimpleXMLDocHandlerComment comment;
    int nested = 0;
    int quoteCharacter = 34;
    String attributekey = null;
    String attributevalue = null;

    private SimpleXMLParser(SimpleXMLDocHandler simpleXMLDocHandler, SimpleXMLDocHandlerComment simpleXMLDocHandlerComment, boolean bl) {
        this.doc = simpleXMLDocHandler;
        this.comment = simpleXMLDocHandlerComment;
        this.html = bl;
        this.stack = new Stack();
        this.state = bl ? 1 : 0;
    }

    private void go(Reader reader) throws IOException {
        BufferedReader bufferedReader = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
        this.doc.startDocument();
        block17 : do {
            if (this.previousCharacter == -1) {
                this.character = bufferedReader.read();
            } else {
                this.character = this.previousCharacter;
                this.previousCharacter = -1;
            }
            if (this.character == -1) {
                if (this.html) {
                    if (this.html && this.state == 1) {
                        this.flush();
                    }
                    this.doc.endDocument();
                } else {
                    this.throwException("Missing end tag");
                }
                return;
            }
            if (this.character == 10 && this.eol) {
                this.eol = false;
                continue;
            }
            if (this.eol) {
                this.eol = false;
            } else if (this.character == 10) {
                ++this.lines;
                this.columns = 0;
            } else if (this.character == 13) {
                this.eol = true;
                this.character = 10;
                ++this.lines;
                this.columns = 0;
            } else {
                ++this.columns;
            }
            switch (this.state) {
                case 0: {
                    if (this.character != 60) break;
                    this.saveState(1);
                    this.state = 2;
                    break;
                }
                case 1: {
                    if (this.character == 60) {
                        this.flush();
                        this.saveState(this.state);
                        this.state = 2;
                        break;
                    }
                    if (this.character == 38) {
                        this.saveState(this.state);
                        this.entity.setLength(0);
                        this.state = 10;
                        break;
                    }
                    this.text.append((char)this.character);
                    break;
                }
                case 2: {
                    this.initTag();
                    if (this.character == 47) {
                        this.state = 5;
                        break;
                    }
                    if (this.character == 63) {
                        this.restoreState();
                        this.state = 9;
                        break;
                    }
                    this.text.append((char)this.character);
                    this.state = 3;
                    break;
                }
                case 3: {
                    if (this.character == 62) {
                        this.doTag();
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    if (this.character == 47) {
                        this.state = 6;
                        break;
                    }
                    if (this.character == 45 && this.text.toString().equals("!-")) {
                        this.flush();
                        this.state = 8;
                        break;
                    }
                    if (this.character == 91 && this.text.toString().equals("![CDATA")) {
                        this.flush();
                        this.state = 7;
                        break;
                    }
                    if (this.character == 69 && this.text.toString().equals("!DOCTYP")) {
                        this.flush();
                        this.state = 9;
                        break;
                    }
                    if (Character.isWhitespace((char)this.character)) {
                        this.doTag();
                        this.state = 4;
                        break;
                    }
                    this.text.append((char)this.character);
                    break;
                }
                case 4: {
                    if (this.character == 62) {
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    if (this.character == 47) {
                        this.state = 6;
                        break;
                    }
                    if (Character.isWhitespace((char)this.character)) continue block17;
                    this.text.append((char)this.character);
                    this.state = 12;
                    break;
                }
                case 5: {
                    if (this.character == 62) {
                        this.doTag();
                        this.processTag(false);
                        if (!this.html && this.nested == 0) {
                            return;
                        }
                        this.state = this.restoreState();
                        break;
                    }
                    if (Character.isWhitespace((char)this.character)) break;
                    this.text.append((char)this.character);
                    break;
                }
                case 6: {
                    if (this.character != 62) {
                        this.throwException("Expected > for tag: <" + this.tag + "/>");
                    }
                    this.doTag();
                    this.processTag(true);
                    this.processTag(false);
                    this.initTag();
                    if (!this.html && this.nested == 0) {
                        this.doc.endDocument();
                        return;
                    }
                    this.state = this.restoreState();
                    break;
                }
                case 7: {
                    if (this.character == 62 && this.text.toString().endsWith("]]")) {
                        this.text.setLength(this.text.length() - 2);
                        this.flush();
                        this.state = this.restoreState();
                        break;
                    }
                    this.text.append((char)this.character);
                    break;
                }
                case 8: {
                    if (this.character == 62 && this.text.toString().endsWith("--")) {
                        this.text.setLength(this.text.length() - 2);
                        this.flush();
                        this.state = this.restoreState();
                        break;
                    }
                    this.text.append((char)this.character);
                    break;
                }
                case 9: {
                    if (this.character != 62) break;
                    this.state = this.restoreState();
                    if (this.state != 1) break;
                    this.state = 0;
                    break;
                }
                case 10: {
                    if (this.character == 59) {
                        this.state = this.restoreState();
                        String string = this.entity.toString();
                        this.entity.setLength(0);
                        char c = EntitiesToUnicode.decodeEntity(string);
                        if (c == '\u0000') {
                            this.text.append('&').append(string).append(';');
                            break;
                        }
                        this.text.append(c);
                        break;
                    }
                    if (!((this.character == 35 || this.character >= 48 && this.character <= 57 || this.character >= 97 && this.character <= 122 || this.character >= 65 && this.character <= 90) && this.entity.length() < 7)) {
                        this.state = this.restoreState();
                        this.previousCharacter = this.character;
                        this.text.append('&').append(this.entity.toString());
                        this.entity.setLength(0);
                        break;
                    }
                    this.entity.append((char)this.character);
                    break;
                }
                case 11: {
                    if (this.html && this.quoteCharacter == 32 && this.character == 62) {
                        this.flush();
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    if (this.html && this.quoteCharacter == 32 && Character.isWhitespace((char)this.character)) {
                        this.flush();
                        this.state = 4;
                        break;
                    }
                    if (this.html && this.quoteCharacter == 32) {
                        this.text.append((char)this.character);
                        break;
                    }
                    if (this.character == this.quoteCharacter) {
                        this.flush();
                        this.state = 4;
                        break;
                    }
                    if (" \r\n\t".indexOf(this.character) >= 0) {
                        this.text.append(' ');
                        break;
                    }
                    if (this.character == 38) {
                        this.saveState(this.state);
                        this.state = 10;
                        this.entity.setLength(0);
                        break;
                    }
                    this.text.append((char)this.character);
                    break;
                }
                case 12: {
                    if (Character.isWhitespace((char)this.character)) {
                        this.flush();
                        this.state = 13;
                        break;
                    }
                    if (this.character == 61) {
                        this.flush();
                        this.state = 14;
                        break;
                    }
                    if (this.html && this.character == 62) {
                        this.text.setLength(0);
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    this.text.append((char)this.character);
                    break;
                }
                case 13: {
                    if (this.character == 61) {
                        this.state = 14;
                        break;
                    }
                    if (Character.isWhitespace((char)this.character)) continue block17;
                    if (this.html && this.character == 62) {
                        this.text.setLength(0);
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    if (this.html && this.character == 47) {
                        this.flush();
                        this.state = 6;
                        break;
                    }
                    if (this.html) {
                        this.flush();
                        this.text.append((char)this.character);
                        this.state = 12;
                        break;
                    }
                    this.throwException("Error in attribute processing.");
                    break;
                }
                case 14: {
                    if (this.character == 34 || this.character == 39) {
                        this.quoteCharacter = this.character;
                        this.state = 11;
                        break;
                    }
                    if (Character.isWhitespace((char)this.character)) continue block17;
                    if (this.html && this.character == 62) {
                        this.flush();
                        this.processTag(true);
                        this.initTag();
                        this.state = this.restoreState();
                        break;
                    }
                    if (this.html) {
                        this.text.append((char)this.character);
                        this.quoteCharacter = 32;
                        this.state = 11;
                        break;
                    }
                    this.throwException("Error in attribute processing");
                }
            }
        } while (true);
    }

    private int restoreState() {
        if (!this.stack.empty()) {
            return (Integer)this.stack.pop();
        }
        return 0;
    }

    private void saveState(int n) {
        this.stack.push(new Integer(n));
    }

    private void flush() {
        switch (this.state) {
            case 1: 
            case 7: {
                if (this.text.length() <= 0) break;
                this.doc.text(this.text.toString());
                break;
            }
            case 8: {
                if (this.comment == null) break;
                this.comment.comment(this.text.toString());
                break;
            }
            case 12: {
                this.attributekey = this.text.toString();
                if (!this.html) break;
                this.attributekey = this.attributekey.toLowerCase();
                break;
            }
            case 11: 
            case 14: {
                this.attributevalue = this.text.toString();
                this.attributes.put(this.attributekey, this.attributevalue);
                break;
            }
        }
        this.text.setLength(0);
    }

    private void initTag() {
        this.tag = null;
        this.attributes = new HashMap();
    }

    private void doTag() {
        if (this.tag == null) {
            this.tag = this.text.toString();
        }
        if (this.html) {
            this.tag = this.tag.toLowerCase();
        }
        this.text.setLength(0);
    }

    private void processTag(boolean bl) {
        if (bl) {
            ++this.nested;
            this.doc.startElement(this.tag, this.attributes);
        } else {
            --this.nested;
            this.doc.endElement(this.tag);
        }
    }

    private void throwException(String string) throws IOException {
        throw new IOException(string + " near line " + this.lines + ", column " + this.columns);
    }

    public static void parse(SimpleXMLDocHandler simpleXMLDocHandler, SimpleXMLDocHandlerComment simpleXMLDocHandlerComment, Reader reader, boolean bl) throws IOException {
        SimpleXMLParser simpleXMLParser = new SimpleXMLParser(simpleXMLDocHandler, simpleXMLDocHandlerComment, bl);
        simpleXMLParser.go(reader);
    }

    public static void parse(SimpleXMLDocHandler simpleXMLDocHandler, InputStream inputStream) throws IOException {
        byte[] arrby = new byte[4];
        int n = inputStream.read(arrby);
        if (n != 4) {
            throw new IOException("Insufficient length.");
        }
        String string = SimpleXMLParser.getEncodingName(arrby);
        String string2 = null;
        if (string.equals("UTF-8")) {
            int n2;
            StringBuffer stringBuffer = new StringBuffer();
            while ((n2 = inputStream.read()) != -1 && n2 != 62) {
                stringBuffer.append((char)n2);
            }
            string2 = stringBuffer.toString();
        } else if (string.equals("CP037")) {
            int n3;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((n3 = inputStream.read()) != -1 && n3 != 110) {
                byteArrayOutputStream.write(n3);
            }
            string2 = new String(byteArrayOutputStream.toByteArray(), "CP037");
        }
        if (string2 != null && (string2 = SimpleXMLParser.getDeclaredEncoding(string2)) != null) {
            string = string2;
        }
        SimpleXMLParser.parse(simpleXMLDocHandler, new InputStreamReader(inputStream, IanaEncodings.getJavaEncoding(string)));
    }

    private static String getDeclaredEncoding(String string) {
        int n;
        if (string == null) {
            return null;
        }
        int n2 = string.indexOf("encoding");
        if (n2 < 0) {
            return null;
        }
        int n3 = string.indexOf(34, n2);
        if (n3 == (n = string.indexOf(39, n2))) {
            return null;
        }
        if (n3 < 0 && n > 0 || n > 0 && n < n3) {
            int n4 = string.indexOf(39, n + 1);
            if (n4 < 0) {
                return null;
            }
            return string.substring(n + 1, n4);
        }
        if (n < 0 && n3 > 0 || n3 > 0 && n3 < n) {
            int n5 = string.indexOf(34, n3 + 1);
            if (n5 < 0) {
                return null;
            }
            return string.substring(n3 + 1, n5);
        }
        return null;
    }

    public static void parse(SimpleXMLDocHandler simpleXMLDocHandler, Reader reader) throws IOException {
        SimpleXMLParser.parse(simpleXMLDocHandler, null, reader, false);
    }

    public static String escapeXML(String string, boolean bl) {
        char[] arrc = string.toCharArray();
        int n = arrc.length;
        StringBuffer stringBuffer = new StringBuffer();
        block7 : for (int i = 0; i < n; ++i) {
            char c = arrc[i];
            switch (c) {
                case '<': {
                    stringBuffer.append("&lt;");
                    continue block7;
                }
                case '>': {
                    stringBuffer.append("&gt;");
                    continue block7;
                }
                case '&': {
                    stringBuffer.append("&amp;");
                    continue block7;
                }
                case '\"': {
                    stringBuffer.append("&quot;");
                    continue block7;
                }
                case '\'': {
                    stringBuffer.append("&apos;");
                    continue block7;
                }
                default: {
                    if (bl && c > '') {
                        stringBuffer.append("&#").append((int)c).append(';');
                        continue block7;
                    }
                    stringBuffer.append(c);
                }
            }
        }
        return stringBuffer.toString();
    }

    private static String getEncodingName(byte[] arrby) {
        int n = arrby[0] & 255;
        int n2 = arrby[1] & 255;
        if (n == 254 && n2 == 255) {
            return "UTF-16BE";
        }
        if (n == 255 && n2 == 254) {
            return "UTF-16LE";
        }
        int n3 = arrby[2] & 255;
        if (n == 239 && n2 == 187 && n3 == 191) {
            return "UTF-8";
        }
        int n4 = arrby[3] & 255;
        if (n == 0 && n2 == 0 && n3 == 0 && n4 == 60) {
            return "ISO-10646-UCS-4";
        }
        if (n == 60 && n2 == 0 && n3 == 0 && n4 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (n == 0 && n2 == 0 && n3 == 60 && n4 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (n == 0 && n2 == 60 && n3 == 0 && n4 == 0) {
            return "ISO-10646-UCS-4";
        }
        if (n == 0 && n2 == 60 && n3 == 0 && n4 == 63) {
            return "UTF-16BE";
        }
        if (n == 60 && n2 == 0 && n3 == 63 && n4 == 0) {
            return "UTF-16LE";
        }
        if (n == 76 && n2 == 111 && n3 == 167 && n4 == 148) {
            return "CP037";
        }
        return "UTF-8";
    }
}

