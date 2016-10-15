/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.hyphenation;

import com.lowagie.text.pdf.hyphenation.CharVector;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Stack;

public class TernaryTree
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 5313366505322983510L;
    protected char[] lo;
    protected char[] hi;
    protected char[] eq;
    protected char[] sc;
    protected CharVector kv;
    protected char root;
    protected char freenode;
    protected int length;
    protected static final int BLOCK_SIZE = 2048;

    TernaryTree() {
        this.init();
    }

    protected void init() {
        this.root = '\u0000';
        this.freenode = '\u0001';
        this.length = 0;
        this.lo = new char[2048];
        this.hi = new char[2048];
        this.eq = new char[2048];
        this.sc = new char[2048];
        this.kv = new CharVector();
    }

    public void insert(String string, char c) {
        int n = string.length() + 1;
        if (this.freenode + n > this.eq.length) {
            this.redimNodeArrays(this.eq.length + 2048);
        }
        char[] arrc = new char[n--];
        string.getChars(0, n, arrc, 0);
        arrc[n] = '\u0000';
        this.root = this.insert(this.root, arrc, 0, c);
    }

    public void insert(char[] arrc, int n, char c) {
        int n2 = TernaryTree.strlen(arrc) + 1;
        if (this.freenode + n2 > this.eq.length) {
            this.redimNodeArrays(this.eq.length + 2048);
        }
        this.root = this.insert(this.root, arrc, n, c);
    }

    private char insert(char c, char[] arrc, int n, char c2) {
        char c3;
        int n2 = TernaryTree.strlen(arrc, n);
        if (c == '\u0000') {
            char c4 = this.freenode;
            this.freenode = (char)(c4 + '\u0001');
            c = c4;
            this.eq[c] = c2;
            ++this.length;
            this.hi[c] = '\u0000';
            if (n2 > 0) {
                this.sc[c] = 65535;
                this.lo[c] = (char)this.kv.alloc(n2 + 1);
                TernaryTree.strcpy(this.kv.getArray(), this.lo[c], arrc, n);
            } else {
                this.sc[c] = '\u0000';
                this.lo[c] = '\u0000';
            }
            return c;
        }
        if (this.sc[c] == '\uffff') {
            char c5 = this.freenode;
            this.freenode = (char)(c5 + '\u0001');
            c3 = c5;
            this.lo[c3] = this.lo[c];
            this.eq[c3] = this.eq[c];
            this.lo[c] = '\u0000';
            if (n2 > 0) {
                this.sc[c] = this.kv.get(this.lo[c3]);
                this.eq[c] = c3;
                char[] arrc2 = this.lo;
                char c6 = c3;
                arrc2[c6] = (char)(arrc2[c6] + '\u0001');
                if (this.kv.get(this.lo[c3]) == '\u0000') {
                    this.lo[c3] = '\u0000';
                    this.sc[c3] = '\u0000';
                    this.hi[c3] = '\u0000';
                } else {
                    this.sc[c3] = 65535;
                }
            } else {
                this.sc[c3] = 65535;
                this.hi[c] = c3;
                this.sc[c] = '\u0000';
                this.eq[c] = c2;
                ++this.length;
                return c;
            }
        }
        if ((c3 = arrc[n]) < this.sc[c]) {
            this.lo[c] = this.insert(this.lo[c], arrc, n, c2);
        } else if (c3 == this.sc[c]) {
            this.eq[c] = c3 != '\u0000' ? this.insert(this.eq[c], arrc, n + 1, c2) : c2;
        } else {
            this.hi[c] = this.insert(this.hi[c], arrc, n, c2);
        }
        return c;
    }

    public static int strcmp(char[] arrc, int n, char[] arrc2, int n2) {
        while (arrc[n] == arrc2[n2]) {
            if (arrc[n] == '\u0000') {
                return 0;
            }
            ++n;
            ++n2;
        }
        return arrc[n] - arrc2[n2];
    }

    public static int strcmp(String string, char[] arrc, int n) {
        int n2;
        int n3 = string.length();
        for (n2 = 0; n2 < n3; ++n2) {
            int n4 = string.charAt(n2) - arrc[n + n2];
            if (n4 != 0) {
                return n4;
            }
            if (arrc[n + n2] != '\u0000') continue;
            return n4;
        }
        if (arrc[n + n2] != '\u0000') {
            return - arrc[n + n2];
        }
        return 0;
    }

    public static void strcpy(char[] arrc, int n, char[] arrc2, int n2) {
        while (arrc2[n2] != '\u0000') {
            arrc[n++] = arrc2[n2++];
        }
        arrc[n] = '\u0000';
    }

    public static int strlen(char[] arrc, int n) {
        int n2 = 0;
        for (int i = n; i < arrc.length && arrc[i] != '\u0000'; ++i) {
            ++n2;
        }
        return n2;
    }

    public static int strlen(char[] arrc) {
        return TernaryTree.strlen(arrc, 0);
    }

    public int find(String string) {
        int n = string.length();
        char[] arrc = new char[n + 1];
        string.getChars(0, n, arrc, 0);
        arrc[n] = '\u0000';
        return this.find(arrc, 0);
    }

    public int find(char[] arrc, int n) {
        char c = this.root;
        int n2 = n;
        while (c != '\u0000') {
            if (this.sc[c] == '\uffff') {
                if (TernaryTree.strcmp(arrc, n2, this.kv.getArray(), this.lo[c]) == 0) {
                    return this.eq[c];
                }
                return -1;
            }
            char c2 = arrc[n2];
            int n3 = c2 - this.sc[c];
            if (n3 == 0) {
                if (c2 == '\u0000') {
                    return this.eq[c];
                }
                ++n2;
                c = this.eq[c];
                continue;
            }
            if (n3 < 0) {
                c = this.lo[c];
                continue;
            }
            c = this.hi[c];
        }
        return -1;
    }

    public boolean knows(String string) {
        return this.find(string) >= 0;
    }

    private void redimNodeArrays(int n) {
        int n2 = n < this.lo.length ? n : this.lo.length;
        char[] arrc = new char[n];
        System.arraycopy(this.lo, 0, arrc, 0, n2);
        this.lo = arrc;
        arrc = new char[n];
        System.arraycopy(this.hi, 0, arrc, 0, n2);
        this.hi = arrc;
        arrc = new char[n];
        System.arraycopy(this.eq, 0, arrc, 0, n2);
        this.eq = arrc;
        arrc = new char[n];
        System.arraycopy(this.sc, 0, arrc, 0, n2);
        this.sc = arrc;
    }

    public int size() {
        return this.length;
    }

    public Object clone() {
        TernaryTree ternaryTree = new TernaryTree();
        ternaryTree.lo = (char[])this.lo.clone();
        ternaryTree.hi = (char[])this.hi.clone();
        ternaryTree.eq = (char[])this.eq.clone();
        ternaryTree.sc = (char[])this.sc.clone();
        ternaryTree.kv = (CharVector)this.kv.clone();
        ternaryTree.root = this.root;
        ternaryTree.freenode = this.freenode;
        ternaryTree.length = this.length;
        return ternaryTree;
    }

    protected void insertBalanced(String[] arrstring, char[] arrc, int n, int n2) {
        if (n2 < 1) {
            return;
        }
        int n3 = n2 >> 1;
        this.insert(arrstring[n3 + n], arrc[n3 + n]);
        this.insertBalanced(arrstring, arrc, n, n3);
        this.insertBalanced(arrstring, arrc, n + n3 + 1, n2 - n3 - 1);
    }

    public void balance() {
        int n = 0;
        int n2 = this.length;
        String[] arrstring = new String[n2];
        char[] arrc = new char[n2];
        Iterator iterator = new Iterator();
        while (iterator.hasMoreElements()) {
            arrc[n] = iterator.getValue();
            arrstring[n++] = (String)iterator.nextElement();
        }
        this.init();
        this.insertBalanced(arrstring, arrc, 0, n2);
    }

    public void trimToSize() {
        this.balance();
        this.redimNodeArrays(this.freenode);
        CharVector charVector = new CharVector();
        charVector.alloc(1);
        TernaryTree ternaryTree = new TernaryTree();
        this.compact(charVector, ternaryTree, this.root);
        this.kv = charVector;
        this.kv.trimToSize();
    }

    private void compact(CharVector charVector, TernaryTree ternaryTree, char c) {
        if (c == '\u0000') {
            return;
        }
        if (this.sc[c] == '\uffff') {
            int n = ternaryTree.find(this.kv.getArray(), this.lo[c]);
            if (n < 0) {
                n = charVector.alloc(TernaryTree.strlen(this.kv.getArray(), this.lo[c]) + 1);
                TernaryTree.strcpy(charVector.getArray(), n, this.kv.getArray(), this.lo[c]);
                ternaryTree.insert(charVector.getArray(), n, (char)n);
            }
            this.lo[c] = (char)n;
        } else {
            this.compact(charVector, ternaryTree, this.lo[c]);
            if (this.sc[c] != '\u0000') {
                this.compact(charVector, ternaryTree, this.eq[c]);
            }
            this.compact(charVector, ternaryTree, this.hi[c]);
        }
    }

    public Enumeration keys() {
        return new Iterator();
    }

    public void printStats() {
        System.out.println("Number of keys = " + Integer.toString(this.length));
        System.out.println("Node count = " + Integer.toString(this.freenode));
        System.out.println("Key Array length = " + Integer.toString(this.kv.length()));
    }

    public class Iterator
    implements Enumeration {
        int cur;
        String curkey;
        Stack ns;
        StringBuffer ks;

        public Iterator() {
            this.cur = -1;
            this.ns = new Stack();
            this.ks = new StringBuffer();
            this.rewind();
        }

        public void rewind() {
            this.ns.removeAllElements();
            this.ks.setLength(0);
            this.cur = TernaryTree.this.root;
            this.run();
        }

        public Object nextElement() {
            String string = this.curkey;
            this.cur = this.up();
            this.run();
            return string;
        }

        public char getValue() {
            if (this.cur >= 0) {
                return TernaryTree.this.eq[this.cur];
            }
            return '\u0000';
        }

        public boolean hasMoreElements() {
            return this.cur != -1;
        }

        private int up() {
            Item item = new Item(this);
            int n = 0;
            if (this.ns.empty()) {
                return -1;
            }
            if (this.cur != 0 && TernaryTree.this.sc[this.cur] == '\u0000') {
                return TernaryTree.this.lo[this.cur];
            }
            boolean bl = true;
            block4 : while (bl) {
                item = (Item)this.ns.pop();
                item.child = (char)(item.child + '\u0001');
                switch (item.child) {
                    case '\u0001': {
                        if (TernaryTree.this.sc[item.parent] != '\u0000') {
                            n = TernaryTree.this.eq[item.parent];
                            this.ns.push(item.clone());
                            this.ks.append(TernaryTree.this.sc[item.parent]);
                        } else {
                            item.child = (char)(item.child + '\u0001');
                            this.ns.push(item.clone());
                            n = TernaryTree.this.hi[item.parent];
                        }
                        bl = false;
                        continue block4;
                    }
                    case '\u0002': {
                        n = TernaryTree.this.hi[item.parent];
                        this.ns.push(item.clone());
                        if (this.ks.length() > 0) {
                            this.ks.setLength(this.ks.length() - 1);
                        }
                        bl = false;
                        continue block4;
                    }
                }
                if (this.ns.empty()) {
                    return -1;
                }
                bl = true;
            }
            return n;
        }

        private int run() {
            block9 : {
                if (this.cur == -1) {
                    return -1;
                }
                boolean bl = false;
                do {
                    if (this.cur != 0) {
                        if (TernaryTree.this.sc[this.cur] == '\uffff') {
                            bl = true;
                        } else {
                            this.ns.push(new Item(this, (char)this.cur, '\u0000'));
                            if (TernaryTree.this.sc[this.cur] == '\u0000') {
                                bl = true;
                            } else {
                                this.cur = TernaryTree.this.lo[this.cur];
                                continue;
                            }
                        }
                    }
                    if (bl) break block9;
                    this.cur = this.up();
                    if (this.cur == -1) break;
                } while (true);
                return -1;
            }
            StringBuffer stringBuffer = new StringBuffer(this.ks.toString());
            if (TernaryTree.this.sc[this.cur] == '\uffff') {
                int n = TernaryTree.this.lo[this.cur];
                while (TernaryTree.this.kv.get(n) != '\u0000') {
                    stringBuffer.append(TernaryTree.this.kv.get(n++));
                }
            }
            this.curkey = stringBuffer.toString();
            return 0;
        }

        private class Item
        implements Cloneable {
            char parent;
            char child;
            private final /* synthetic */ Iterator this$1;

            public Item(Iterator iterator) {
                this.this$1 = iterator;
                this.parent = '\u0000';
                this.child = '\u0000';
            }

            public Item(Iterator iterator, char c, char c2) {
                this.this$1 = iterator;
                this.parent = c;
                this.child = c2;
            }

            public Object clone() {
                return new Item(this.this$1, this.parent, this.child);
            }
        }

    }

}

