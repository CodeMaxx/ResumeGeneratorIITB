/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntHashtable
implements Cloneable {
    private transient Entry[] table;
    private transient int count;
    private int threshold;
    private float loadFactor;

    public IntHashtable() {
        this(150, 0.75f);
    }

    public IntHashtable(int n) {
        this(n, 0.75f);
    }

    public IntHashtable(int n, float f) {
        if (n < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + n);
        }
        if (f <= 0.0f) {
            throw new IllegalArgumentException("Illegal Load: " + f);
        }
        if (n == 0) {
            n = 1;
        }
        this.loadFactor = f;
        this.table = new Entry[n];
        this.threshold = (int)((float)n * f);
    }

    public int size() {
        return this.count;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public boolean contains(int n) {
        Entry[] arrentry = this.table;
        int n2 = arrentry.length;
        while (n2-- > 0) {
            Entry entry = arrentry[n2];
            while (entry != null) {
                if (entry.value == n) {
                    return true;
                }
                entry = entry.next;
            }
        }
        return false;
    }

    public boolean containsValue(int n) {
        return this.contains(n);
    }

    public boolean containsKey(int n) {
        Entry[] arrentry = this.table;
        int n2 = n;
        int n3 = (n2 & Integer.MAX_VALUE) % arrentry.length;
        Entry entry = arrentry[n3];
        while (entry != null) {
            if (entry.hash == n2 && entry.key == n) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    public int get(int n) {
        Entry[] arrentry = this.table;
        int n2 = n;
        int n3 = (n2 & Integer.MAX_VALUE) % arrentry.length;
        Entry entry = arrentry[n3];
        while (entry != null) {
            if (entry.hash == n2 && entry.key == n) {
                return entry.value;
            }
            entry = entry.next;
        }
        return 0;
    }

    protected void rehash() {
        int n = this.table.length;
        Entry[] arrentry = this.table;
        int n2 = n * 2 + 1;
        Entry[] arrentry2 = new Entry[n2];
        this.threshold = (int)((float)n2 * this.loadFactor);
        this.table = arrentry2;
        int n3 = n;
        while (n3-- > 0) {
            Entry entry = arrentry[n3];
            while (entry != null) {
                Entry entry2 = entry;
                entry = entry.next;
                int n4 = (entry2.hash & Integer.MAX_VALUE) % n2;
                entry2.next = arrentry2[n4];
                arrentry2[n4] = entry2;
            }
        }
    }

    public int put(int n, int n2) {
        Entry[] arrentry = this.table;
        int n3 = n;
        int n4 = (n3 & Integer.MAX_VALUE) % arrentry.length;
        Entry entry = arrentry[n4];
        while (entry != null) {
            if (entry.hash == n3 && entry.key == n) {
                int n5 = entry.value;
                entry.value = n2;
                return n5;
            }
            entry = entry.next;
        }
        if (this.count >= this.threshold) {
            this.rehash();
            arrentry = this.table;
            n4 = (n3 & Integer.MAX_VALUE) % arrentry.length;
        }
        arrentry[n4] = entry = new Entry(n3, n, n2, arrentry[n4]);
        ++this.count;
        return 0;
    }

    public int remove(int n) {
        Entry[] arrentry = this.table;
        int n2 = n;
        int n3 = (n2 & Integer.MAX_VALUE) % arrentry.length;
        Entry entry = arrentry[n3];
        Entry entry2 = null;
        while (entry != null) {
            if (entry.hash == n2 && entry.key == n) {
                if (entry2 != null) {
                    entry2.next = entry.next;
                } else {
                    arrentry[n3] = entry.next;
                }
                --this.count;
                int n4 = entry.value;
                entry.value = 0;
                return n4;
            }
            entry2 = entry;
            entry = entry.next;
        }
        return 0;
    }

    public void clear() {
        Entry[] arrentry = this.table;
        int n = arrentry.length;
        while (--n >= 0) {
            arrentry[n] = null;
        }
        this.count = 0;
    }

    public Iterator getEntryIterator() {
        return new IntHashtableIterator(this.table);
    }

    public int[] toOrderedKeys() {
        int[] arrn = this.getKeys();
        Arrays.sort(arrn);
        return arrn;
    }

    public int[] getKeys() {
        int[] arrn = new int[this.count];
        int n = 0;
        int n2 = this.table.length;
        Entry entry = null;
        do {
            if (entry == null) {
                while (n2-- > 0 && (entry = this.table[n2]) == null) {
                }
            }
            if (entry == null) break;
            Entry entry2 = entry;
            entry = entry2.next;
            arrn[n++] = entry2.key;
        } while (true);
        return arrn;
    }

    public int getOneKey() {
        if (this.count == 0) {
            return 0;
        }
        int n = this.table.length;
        Entry entry = null;
        while (n-- > 0 && (entry = this.table[n]) == null) {
        }
        if (entry == null) {
            return 0;
        }
        return entry.key;
    }

    public Object clone() {
        try {
            IntHashtable intHashtable = (IntHashtable)super.clone();
            intHashtable.table = new Entry[this.table.length];
            int n = this.table.length;
            while (n-- > 0) {
                intHashtable.table[n] = this.table[n] != null ? (Entry)this.table[n].clone() : null;
            }
            return intHashtable;
        }
        catch (CloneNotSupportedException var1_2) {
            throw new InternalError();
        }
    }

    static class IntHashtableIterator
    implements Iterator {
        int index;
        Entry[] table;
        Entry entry;

        IntHashtableIterator(Entry[] arrentry) {
            this.table = arrentry;
            this.index = arrentry.length;
        }

        public boolean hasNext() {
            if (this.entry != null) {
                return true;
            }
            while (this.index-- > 0) {
                this.entry = this.table[this.index];
                if (this.entry == null) continue;
                return true;
            }
            return false;
        }

        public Object next() {
            if (this.entry == null) {
                while (this.index-- > 0 && (this.entry = this.table[this.index]) == null) {
                }
            }
            if (this.entry != null) {
                Entry entry = this.entry;
                this.entry = entry.next;
                return entry;
            }
            throw new NoSuchElementException("IntHashtableIterator");
        }

        public void remove() {
            throw new UnsupportedOperationException("remove() not supported.");
        }
    }

    static class Entry {
        int hash;
        int key;
        int value;
        Entry next;

        protected Entry(int n, int n2, int n3, Entry entry) {
            this.hash = n;
            this.key = n2;
            this.value = n3;
            this.next = entry;
        }

        public int getKey() {
            return this.key;
        }

        public int getValue() {
            return this.value;
        }

        protected Object clone() {
            Entry entry = new Entry(this.hash, this.key, this.value, this.next != null ? (Entry)this.next.clone() : null);
            return entry;
        }
    }

}

