/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.events;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class IndexEvents
extends PdfPageEventHelper {
    private Map indextag = new TreeMap();
    private long indexcounter = 0;
    private List indexentry = new ArrayList();
    private Comparator comparator;

    public IndexEvents() {
        this.comparator = new Comparator(){

            public int compare(Object object, Object object2) {
                Entry entry = (Entry)object;
                Entry entry2 = (Entry)object2;
                int n = 0;
                if (entry.getIn1() != null && entry2.getIn1() != null && (n = entry.getIn1().compareToIgnoreCase(entry2.getIn1())) == 0 && entry.getIn2() != null && entry2.getIn2() != null && (n = entry.getIn2().compareToIgnoreCase(entry2.getIn2())) == 0 && entry.getIn3() != null && entry2.getIn3() != null) {
                    n = entry.getIn3().compareToIgnoreCase(entry2.getIn3());
                }
                return n;
            }
        };
    }

    public void onGenericTag(PdfWriter pdfWriter, Document document, Rectangle rectangle, String string) {
        this.indextag.put(string, new Integer(pdfWriter.getPageNumber()));
    }

    public Chunk create(String string, String string2, String string3, String string4) {
        Chunk chunk = new Chunk(string);
        String string5 = "idx_" + this.indexcounter++;
        chunk.setGenericTag(string5);
        chunk.setLocalDestination(string5);
        Entry entry = new Entry(string2, string3, string4, string5);
        this.indexentry.add(entry);
        return chunk;
    }

    public Chunk create(String string, String string2) {
        return this.create(string, string2, "", "");
    }

    public Chunk create(String string, String string2, String string3) {
        return this.create(string, string2, string3, "");
    }

    public void create(Chunk chunk, String string, String string2, String string3) {
        String string4 = "idx_" + this.indexcounter++;
        chunk.setGenericTag(string4);
        chunk.setLocalDestination(string4);
        Entry entry = new Entry(string, string2, string3, string4);
        this.indexentry.add(entry);
    }

    public void create(Chunk chunk, String string) {
        this.create(chunk, string, "", "");
    }

    public void create(Chunk chunk, String string, String string2) {
        this.create(chunk, string, string2, "");
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public List getSortedEntries() {
        HashMap<String, Entry> hashMap = new HashMap<String, Entry>();
        for (int i = 0; i < this.indexentry.size(); ++i) {
            Entry entry = (Entry)this.indexentry.get(i);
            String string = entry.getKey();
            Entry entry2 = (Entry)hashMap.get(string);
            if (entry2 != null) {
                entry2.addPageNumberAndTag(entry.getPageNumber(), entry.getTag());
                continue;
            }
            entry.addPageNumberAndTag(entry.getPageNumber(), entry.getTag());
            hashMap.put(string, entry);
        }
        ArrayList arrayList = new ArrayList(hashMap.values());
        Collections.sort(arrayList, this.comparator);
        return arrayList;
    }

    public class Entry {
        private String in1;
        private String in2;
        private String in3;
        private String tag;
        private List pagenumbers;
        private List tags;

        public Entry(String string, String string2, String string3, String string4) {
            this.pagenumbers = new ArrayList();
            this.tags = new ArrayList();
            this.in1 = string;
            this.in2 = string2;
            this.in3 = string3;
            this.tag = string4;
        }

        public String getIn1() {
            return this.in1;
        }

        public String getIn2() {
            return this.in2;
        }

        public String getIn3() {
            return this.in3;
        }

        public String getTag() {
            return this.tag;
        }

        public int getPageNumber() {
            int n = -1;
            Integer n2 = (Integer)IndexEvents.this.indextag.get(this.tag);
            if (n2 != null) {
                n = n2;
            }
            return n;
        }

        public void addPageNumberAndTag(int n, String string) {
            this.pagenumbers.add(new Integer(n));
            this.tags.add(string);
        }

        public String getKey() {
            return this.in1 + "!" + this.in2 + "!" + this.in3;
        }

        public List getPagenumbers() {
            return this.pagenumbers;
        }

        public List getTags() {
            return this.tags;
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(this.in1).append(' ');
            stringBuffer.append(this.in2).append(' ');
            stringBuffer.append(this.in3).append(' ');
            for (int i = 0; i < this.pagenumbers.size(); ++i) {
                stringBuffer.append(this.pagenumbers.get(i)).append(' ');
            }
            return stringBuffer.toString();
        }
    }

}

