/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Font;
import com.lowagie.text.LargeElement;
import com.lowagie.text.MarkedObject;
import com.lowagie.text.MarkedSection;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.TextElementArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Section
extends ArrayList
implements TextElementArray,
LargeElement {
    public static final int NUMBERSTYLE_DOTTED = 0;
    public static final int NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT = 1;
    private static final long serialVersionUID = 3324172577544748043L;
    protected Paragraph title;
    protected String bookmarkTitle;
    protected int numberDepth;
    protected int numberStyle = 0;
    protected float indentationLeft;
    protected float indentationRight;
    protected float indentation;
    protected boolean bookmarkOpen = true;
    protected boolean triggerNewPage = false;
    protected int subsections = 0;
    protected ArrayList numbers = null;
    protected boolean complete = true;
    protected boolean addedCompletely = false;
    protected boolean notAddedYet = true;

    protected Section() {
        this.title = new Paragraph();
        this.numberDepth = 1;
    }

    protected Section(Paragraph paragraph, int n) {
        this.numberDepth = n;
        this.title = paragraph;
    }

    public boolean process(ElementListener elementListener) {
        try {
            Iterator iterator = this.iterator();
            while (iterator.hasNext()) {
                Element element = (Element)iterator.next();
                elementListener.add(element);
            }
            return true;
        }
        catch (DocumentException var2_4) {
            return false;
        }
    }

    public int type() {
        return 13;
    }

    public boolean isChapter() {
        return this.type() == 16;
    }

    public boolean isSection() {
        return this.type() == 13;
    }

    public ArrayList getChunks() {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            arrayList.addAll(((Element)iterator.next()).getChunks());
        }
        return arrayList;
    }

    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return false;
    }

    public void add(int n, Object object) {
        block4 : {
            if (this.isAddedCompletely()) {
                throw new IllegalStateException("This LargeElement has already been added to the Document.");
            }
            try {
                Element element = (Element)object;
                if (element.isNestable()) {
                    super.add(n, element);
                    break block4;
                }
                throw new ClassCastException("You can't add a " + element.getClass().getName() + " to a Section.");
            }
            catch (ClassCastException var3_4) {
                throw new ClassCastException("Insertion of illegal Element: " + var3_4.getMessage());
            }
        }
    }

    public boolean add(Object object) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException("This LargeElement has already been added to the Document.");
        }
        try {
            Element element = (Element)object;
            if (element.type() == 13) {
                Section section = (Section)object;
                section.setNumbers(++this.subsections, this.numbers);
                return super.add(section);
            }
            if (object instanceof MarkedSection && ((MarkedObject)object).element.type() == 13) {
                MarkedSection markedSection = (MarkedSection)object;
                Section section = (Section)markedSection.element;
                section.setNumbers(++this.subsections, this.numbers);
                return super.add(markedSection);
            }
            if (element.isNestable()) {
                return super.add(object);
            }
            throw new ClassCastException("You can't add a " + element.getClass().getName() + " to a Section.");
        }
        catch (ClassCastException var2_3) {
            throw new ClassCastException("Insertion of illegal Element: " + var2_3.getMessage());
        }
    }

    public boolean addAll(Collection collection) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            this.add(iterator.next());
        }
        return true;
    }

    public Section addSection(float f, Paragraph paragraph, int n) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException("This LargeElement has already been added to the Document.");
        }
        Section section = new Section(paragraph, n);
        section.setIndentation(f);
        this.add(section);
        return section;
    }

    public Section addSection(float f, Paragraph paragraph) {
        return this.addSection(f, paragraph, this.numberDepth + 1);
    }

    public Section addSection(Paragraph paragraph, int n) {
        return this.addSection(0.0f, paragraph, n);
    }

    public MarkedSection addMarkedSection() {
        MarkedSection markedSection = new MarkedSection(new Section(null, this.numberDepth + 1));
        this.add(markedSection);
        return markedSection;
    }

    public Section addSection(Paragraph paragraph) {
        return this.addSection(0.0f, paragraph, this.numberDepth + 1);
    }

    public Section addSection(float f, String string, int n) {
        return this.addSection(f, new Paragraph(string), n);
    }

    public Section addSection(String string, int n) {
        return this.addSection(new Paragraph(string), n);
    }

    public Section addSection(float f, String string) {
        return this.addSection(f, new Paragraph(string));
    }

    public Section addSection(String string) {
        return this.addSection(new Paragraph(string));
    }

    public void setTitle(Paragraph paragraph) {
        this.title = paragraph;
    }

    public Paragraph getTitle() {
        return Section.constructTitle(this.title, this.numbers, this.numberDepth, this.numberStyle);
    }

    public static Paragraph constructTitle(Paragraph paragraph, ArrayList arrayList, int n, int n2) {
        if (paragraph == null) {
            return null;
        }
        int n3 = Math.min(arrayList.size(), n);
        if (n3 < 1) {
            return paragraph;
        }
        StringBuffer stringBuffer = new StringBuffer(" ");
        for (int i = 0; i < n3; ++i) {
            stringBuffer.insert(0, ".");
            stringBuffer.insert(0, (Integer)arrayList.get(i));
        }
        if (n2 == 1) {
            stringBuffer.deleteCharAt(stringBuffer.length() - 2);
        }
        Paragraph paragraph2 = new Paragraph(paragraph);
        paragraph2.add(0, new Chunk(stringBuffer.toString(), paragraph.getFont()));
        return paragraph2;
    }

    public void setNumberDepth(int n) {
        this.numberDepth = n;
    }

    public int getNumberDepth() {
        return this.numberDepth;
    }

    public void setNumberStyle(int n) {
        this.numberStyle = n;
    }

    public int getNumberStyle() {
        return this.numberStyle;
    }

    public void setIndentationLeft(float f) {
        this.indentationLeft = f;
    }

    public float getIndentationLeft() {
        return this.indentationLeft;
    }

    public void setIndentationRight(float f) {
        this.indentationRight = f;
    }

    public float getIndentationRight() {
        return this.indentationRight;
    }

    public void setIndentation(float f) {
        this.indentation = f;
    }

    public float getIndentation() {
        return this.indentation;
    }

    public void setBookmarkOpen(boolean bl) {
        this.bookmarkOpen = bl;
    }

    public boolean isBookmarkOpen() {
        return this.bookmarkOpen;
    }

    public void setTriggerNewPage(boolean bl) {
        this.triggerNewPage = bl;
    }

    public boolean isTriggerNewPage() {
        return this.triggerNewPage && this.notAddedYet;
    }

    public void setBookmarkTitle(String string) {
        this.bookmarkTitle = string;
    }

    public Paragraph getBookmarkTitle() {
        if (this.bookmarkTitle == null) {
            return this.getTitle();
        }
        return new Paragraph(this.bookmarkTitle);
    }

    public void setChapterNumber(int n) {
        this.numbers.set(this.numbers.size() - 1, new Integer(n));
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            Object e = iterator.next();
            if (!(e instanceof Section)) continue;
            ((Section)e).setChapterNumber(n);
        }
    }

    public int getDepth() {
        return this.numbers.size();
    }

    private void setNumbers(int n, ArrayList arrayList) {
        this.numbers = new ArrayList();
        this.numbers.add(new Integer(n));
        this.numbers.addAll(arrayList);
    }

    public boolean isNotAddedYet() {
        return this.notAddedYet;
    }

    public void setNotAddedYet(boolean bl) {
        this.notAddedYet = bl;
    }

    protected boolean isAddedCompletely() {
        return this.addedCompletely;
    }

    protected void setAddedCompletely(boolean bl) {
        this.addedCompletely = bl;
    }

    public void flushContent() {
        this.setNotAddedYet(false);
        this.title = null;
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            Element element = (Element)iterator.next();
            if (element instanceof Section) {
                Section section = (Section)element;
                if (!section.isComplete() && this.size() == 1) {
                    section.flushContent();
                    return;
                }
                section.setAddedCompletely(true);
            }
            iterator.remove();
        }
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setComplete(boolean bl) {
        this.complete = bl;
    }

    public void newPage() {
        this.add(Chunk.NEXTPAGE);
    }
}

