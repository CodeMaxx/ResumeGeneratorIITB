/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class SequenceList {
    protected static final int COMMA = 1;
    protected static final int MINUS = 2;
    protected static final int NOT = 3;
    protected static final int TEXT = 4;
    protected static final int NUMBER = 5;
    protected static final int END = 6;
    protected static final char EOT = '\uffff';
    private static final int FIRST = 0;
    private static final int DIGIT = 1;
    private static final int OTHER = 2;
    private static final int DIGIT2 = 3;
    private static final String NOT_OTHER = "-,!0123456789";
    protected char[] text;
    protected int ptr = 0;
    protected int number;
    protected String other;
    protected int low;
    protected int high;
    protected boolean odd;
    protected boolean even;
    protected boolean inverse;

    protected SequenceList(String string) {
        this.text = string.toCharArray();
    }

    protected char nextChar() {
        char c;
        do {
            if (this.ptr < this.text.length) continue;
            return '\uffff';
        } while ((c = this.text[this.ptr++]) <= ' ');
        return c;
    }

    protected void putBack() {
        --this.ptr;
        if (this.ptr < 0) {
            this.ptr = 0;
        }
    }

    protected int getType() {
        StringBuffer stringBuffer = new StringBuffer();
        int n = 0;
        do {
            char c;
            if ((c = this.nextChar()) == '\uffff') {
                if (n == 1) {
                    this.other = stringBuffer.toString();
                    this.number = Integer.parseInt(this.other);
                    return 5;
                }
                if (n == 2) {
                    this.other = stringBuffer.toString().toLowerCase();
                    return 4;
                }
                return 6;
            }
            switch (n) {
                case 0: {
                    switch (c) {
                        case '!': {
                            return 3;
                        }
                        case '-': {
                            return 2;
                        }
                        case ',': {
                            return 1;
                        }
                    }
                    stringBuffer.append(c);
                    if (c >= '0' && c <= '9') {
                        n = 1;
                        break;
                    }
                    n = 2;
                    break;
                }
                case 1: {
                    if (c >= '0' && c <= '9') {
                        stringBuffer.append(c);
                        break;
                    }
                    this.putBack();
                    this.other = stringBuffer.toString();
                    this.number = Integer.parseInt(this.other);
                    return 5;
                }
                case 2: {
                    if ("-,!0123456789".indexOf(c) < 0) {
                        stringBuffer.append(c);
                        break;
                    }
                    this.putBack();
                    this.other = stringBuffer.toString().toLowerCase();
                    return 4;
                }
            }
        } while (true);
    }

    private void otherProc() {
        if (this.other.equals("odd") || this.other.equals("o")) {
            this.odd = true;
            this.even = false;
        } else if (this.other.equals("even") || this.other.equals("e")) {
            this.odd = false;
            this.even = true;
        }
    }

    protected boolean getAttributes() {
        this.low = -1;
        this.high = -1;
        this.inverse = false;
        this.even = false;
        this.odd = false;
        int n = 2;
        do {
            int n2;
            if ((n2 = this.getType()) == 6 || n2 == 1) {
                if (n == 1) {
                    this.high = this.low;
                }
                return n2 == 6;
            }
            block0 : switch (n) {
                case 2: {
                    switch (n2) {
                        case 3: {
                            this.inverse = true;
                            break block0;
                        }
                        case 2: {
                            n = 3;
                            break block0;
                        }
                    }
                    if (n2 == 5) {
                        this.low = this.number;
                        n = 1;
                        break;
                    }
                    this.otherProc();
                    break;
                }
                case 1: {
                    switch (n2) {
                        case 3: {
                            this.inverse = true;
                            n = 2;
                            this.high = this.low;
                            break block0;
                        }
                        case 2: {
                            n = 3;
                            break block0;
                        }
                    }
                    this.high = this.low;
                    n = 2;
                    this.otherProc();
                    break;
                }
                case 3: {
                    switch (n2) {
                        case 3: {
                            this.inverse = true;
                            n = 2;
                            break block0;
                        }
                        case 2: {
                            break block0;
                        }
                        case 5: {
                            this.high = this.number;
                            n = 2;
                            break block0;
                        }
                    }
                    n = 2;
                    this.otherProc();
                }
            }
        } while (true);
    }

    public static List expand(String string, int n) {
        SequenceList sequenceList = new SequenceList(string);
        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        boolean bl = false;
        while (!bl) {
            bl = sequenceList.getAttributes();
            if (sequenceList.low == -1 && sequenceList.high == -1 && !sequenceList.even && !sequenceList.odd) continue;
            if (sequenceList.low < 1) {
                sequenceList.low = 1;
            }
            if (sequenceList.high < 1 || sequenceList.high > n) {
                sequenceList.high = n;
            }
            if (sequenceList.low > n) {
                sequenceList.low = n;
            }
            int n2 = 1;
            if (sequenceList.inverse) {
                if (sequenceList.low > sequenceList.high) {
                    int n3 = sequenceList.low;
                    sequenceList.low = sequenceList.high;
                    sequenceList.high = n3;
                }
                ListIterator listIterator = linkedList.listIterator();
                while (listIterator.hasNext()) {
                    int n4 = (Integer)listIterator.next();
                    if (sequenceList.even && (n4 & 1) == 1 || sequenceList.odd && (n4 & 1) == 0 || n4 < sequenceList.low || n4 > sequenceList.high) continue;
                    listIterator.remove();
                }
                continue;
            }
            if (sequenceList.low > sequenceList.high) {
                n2 = -1;
                if (sequenceList.odd || sequenceList.even) {
                    --n2;
                    sequenceList.low = sequenceList.even ? (sequenceList.low &= -2) : (sequenceList.low -= (sequenceList.low & 1) == 1 ? 0 : 1);
                }
                for (int i = sequenceList.low; i >= sequenceList.high; i += n2) {
                    linkedList.add(new Integer(i));
                }
                continue;
            }
            if (sequenceList.odd || sequenceList.even) {
                ++n2;
                sequenceList.low = sequenceList.odd ? (sequenceList.low |= 1) : (sequenceList.low += (sequenceList.low & 1) == 1 ? 1 : 0);
            }
            for (int i = sequenceList.low; i <= sequenceList.high; i += n2) {
                linkedList.add(new Integer(i));
            }
        }
        return linkedList;
    }
}

