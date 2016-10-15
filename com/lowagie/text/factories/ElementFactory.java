/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.factories;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.Chunk;
import com.lowagie.text.ElementTags;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
import com.lowagie.text.Utilities;
import com.lowagie.text.html.Markup;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

public class ElementFactory {
    public static Chunk getChunk(Properties properties) {
        Chunk chunk = new Chunk();
        chunk.setFont(FontFactory.getFont(properties));
        String string = properties.getProperty("itext");
        if (string != null) {
            chunk.append(string);
        }
        if ((string = properties.getProperty(ElementTags.LOCALGOTO)) != null) {
            chunk.setLocalGoto(string);
        }
        if ((string = properties.getProperty(ElementTags.REMOTEGOTO)) != null) {
            String string2 = properties.getProperty("page");
            if (string2 != null) {
                chunk.setRemoteGoto(string, Integer.parseInt(string2));
            } else {
                String string3 = properties.getProperty("destination");
                if (string3 != null) {
                    chunk.setRemoteGoto(string, string3);
                }
            }
        }
        if ((string = properties.getProperty(ElementTags.LOCALDESTINATION)) != null) {
            chunk.setLocalDestination(string);
        }
        if ((string = properties.getProperty(ElementTags.SUBSUPSCRIPT)) != null) {
            chunk.setTextRise(Float.parseFloat(string + "f"));
        }
        if ((string = properties.getProperty("vertical-align")) != null && string.endsWith("%")) {
            float f = Float.parseFloat(string.substring(0, string.length() - 1) + "f") / 100.0f;
            chunk.setTextRise(f * chunk.getFont().getSize());
        }
        if ((string = properties.getProperty(ElementTags.GENERICTAG)) != null) {
            chunk.setGenericTag(string);
        }
        if ((string = properties.getProperty("backgroundcolor")) != null) {
            chunk.setBackground(Markup.decodeColor(string));
        }
        return chunk;
    }

    public static Phrase getPhrase(Properties properties) {
        Phrase phrase = new Phrase();
        phrase.setFont(FontFactory.getFont(properties));
        String string = properties.getProperty("leading");
        if (string != null) {
            phrase.setLeading(Float.parseFloat(string + "f"));
        }
        if ((string = properties.getProperty("line-height")) != null) {
            phrase.setLeading(Markup.parseLength(string, 12.0f));
        }
        if ((string = properties.getProperty("itext")) != null) {
            Chunk chunk = new Chunk(string);
            string = properties.getProperty(ElementTags.GENERICTAG);
            if (string != null) {
                chunk.setGenericTag(string);
            }
            phrase.add(chunk);
        }
        return phrase;
    }

    public static Anchor getAnchor(Properties properties) {
        Anchor anchor = new Anchor(ElementFactory.getPhrase(properties));
        String string = properties.getProperty("name");
        if (string != null) {
            anchor.setName(string);
        }
        if ((string = (String)properties.remove("reference")) != null) {
            anchor.setReference(string);
        }
        return anchor;
    }

    public static Paragraph getParagraph(Properties properties) {
        Paragraph paragraph = new Paragraph(ElementFactory.getPhrase(properties));
        String string = properties.getProperty("align");
        if (string != null) {
            paragraph.setAlignment(string);
        }
        if ((string = properties.getProperty("indentationleft")) != null) {
            paragraph.setIndentationLeft(Float.parseFloat(string + "f"));
        }
        if ((string = properties.getProperty("indentationright")) != null) {
            paragraph.setIndentationRight(Float.parseFloat(string + "f"));
        }
        return paragraph;
    }

    public static ListItem getListItem(Properties properties) {
        ListItem listItem = new ListItem(ElementFactory.getParagraph(properties));
        return listItem;
    }

    public static List getList(Properties properties) {
        List list = new List();
        list.setNumbered(Utilities.checkTrueOrFalse(properties, "numbered"));
        list.setLettered(Utilities.checkTrueOrFalse(properties, "lettered"));
        list.setLowercase(Utilities.checkTrueOrFalse(properties, "lowercase"));
        list.setAutoindent(Utilities.checkTrueOrFalse(properties, "autoindent"));
        list.setAlignindent(Utilities.checkTrueOrFalse(properties, "alignindent"));
        String string = properties.getProperty("first");
        if (string != null) {
            char c = string.charAt(0);
            if (Character.isLetter(c)) {
                list.setFirst(c);
            } else {
                list.setFirst(Integer.parseInt(string));
            }
        }
        if ((string = properties.getProperty("listsymbol")) != null) {
            list.setListSymbol(new Chunk(string, FontFactory.getFont(properties)));
        }
        if ((string = properties.getProperty("indentationleft")) != null) {
            list.setIndentationLeft(Float.parseFloat(string + "f"));
        }
        if ((string = properties.getProperty("indentationright")) != null) {
            list.setIndentationRight(Float.parseFloat(string + "f"));
        }
        if ((string = properties.getProperty("symbolindent")) != null) {
            list.setSymbolIndent(Float.parseFloat(string));
        }
        return list;
    }

    public static Cell getCell(Properties properties) {
        Cell cell = new Cell();
        cell.setHorizontalAlignment(properties.getProperty("horizontalalign"));
        cell.setVerticalAlignment(properties.getProperty("verticalalign"));
        String string = properties.getProperty("width");
        if (string != null) {
            cell.setWidth(string);
        }
        if ((string = properties.getProperty("colspan")) != null) {
            cell.setColspan(Integer.parseInt(string));
        }
        if ((string = properties.getProperty("rowspan")) != null) {
            cell.setRowspan(Integer.parseInt(string));
        }
        if ((string = properties.getProperty("leading")) != null) {
            cell.setLeading(Float.parseFloat(string + "f"));
        }
        cell.setHeader(Utilities.checkTrueOrFalse(properties, "header"));
        if (Utilities.checkTrueOrFalse(properties, "nowrap")) {
            cell.setMaxLines(1);
        }
        ElementFactory.setRectangleProperties(cell, properties);
        return cell;
    }

    public static Table getTable(Properties properties) {
        try {
            Table table;
            String string = properties.getProperty("widths");
            if (string != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(string, ";");
                ArrayList<String> arrayList = new ArrayList<String>();
                while (stringTokenizer.hasMoreTokens()) {
                    arrayList.add(stringTokenizer.nextToken());
                }
                table = new Table(arrayList.size());
                float[] arrf = new float[table.getColumns()];
                for (int i = 0; i < arrayList.size(); ++i) {
                    string = (String)arrayList.get(i);
                    arrf[i] = Float.parseFloat(string + "f");
                }
                table.setWidths(arrf);
            } else {
                string = properties.getProperty("columns");
                try {
                    table = new Table(Integer.parseInt(string));
                }
                catch (Exception var3_3) {
                    table = new Table(1);
                }
            }
            table.setBorder(15);
            table.setBorderWidth(1.0f);
            table.getDefaultCell().setBorder(15);
            string = properties.getProperty("lastHeaderRow");
            if (string != null) {
                table.setLastHeaderRow(Integer.parseInt(string));
            }
            if ((string = properties.getProperty("align")) != null) {
                table.setAlignment(string);
            }
            if ((string = properties.getProperty("cellspacing")) != null) {
                table.setSpacing(Float.parseFloat(string + "f"));
            }
            if ((string = properties.getProperty("cellpadding")) != null) {
                table.setPadding(Float.parseFloat(string + "f"));
            }
            if ((string = properties.getProperty("offset")) != null) {
                table.setOffset(Float.parseFloat(string + "f"));
            }
            if ((string = properties.getProperty("width")) != null) {
                if (string.endsWith("%")) {
                    table.setWidth(Float.parseFloat(string.substring(0, string.length() - 1) + "f"));
                } else {
                    table.setWidth(Float.parseFloat(string + "f"));
                    table.setLocked(true);
                }
            }
            table.setTableFitsPage(Utilities.checkTrueOrFalse(properties, "tablefitspage"));
            table.setCellsFitPage(Utilities.checkTrueOrFalse(properties, "cellsfitpage"));
            table.setConvert2pdfptable(Utilities.checkTrueOrFalse(properties, "convert2pdfp"));
            ElementFactory.setRectangleProperties(table, properties);
            return table;
        }
        catch (BadElementException var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    private static void setRectangleProperties(Rectangle rectangle, Properties properties) {
        int n;
        int n2;
        int n3;
        String string = properties.getProperty("borderwidth");
        if (string != null) {
            rectangle.setBorderWidth(Float.parseFloat(string + "f"));
        }
        int n4 = 0;
        if (Utilities.checkTrueOrFalse(properties, "left")) {
            n4 |= 4;
        }
        if (Utilities.checkTrueOrFalse(properties, "right")) {
            n4 |= 8;
        }
        if (Utilities.checkTrueOrFalse(properties, "top")) {
            n4 |= 1;
        }
        if (Utilities.checkTrueOrFalse(properties, "bottom")) {
            n4 |= 2;
        }
        rectangle.setBorder(n4);
        String string2 = properties.getProperty("red");
        String string3 = properties.getProperty("green");
        String string4 = properties.getProperty("blue");
        if (string2 != null || string3 != null || string4 != null) {
            n = 0;
            n3 = 0;
            n2 = 0;
            if (string2 != null) {
                n = Integer.parseInt(string2);
            }
            if (string3 != null) {
                n3 = Integer.parseInt(string3);
            }
            if (string4 != null) {
                n2 = Integer.parseInt(string4);
            }
            rectangle.setBorderColor(new Color(n, n3, n2));
        } else {
            rectangle.setBorderColor(Markup.decodeColor(properties.getProperty("bordercolor")));
        }
        string2 = (String)properties.remove("bgred");
        string3 = (String)properties.remove("bggreen");
        string4 = (String)properties.remove("bgblue");
        string = properties.getProperty("backgroundcolor");
        if (string2 != null || string3 != null || string4 != null) {
            n = 0;
            n3 = 0;
            n2 = 0;
            if (string2 != null) {
                n = Integer.parseInt(string2);
            }
            if (string3 != null) {
                n3 = Integer.parseInt(string3);
            }
            if (string4 != null) {
                n2 = Integer.parseInt(string4);
            }
            rectangle.setBackgroundColor(new Color(n, n3, n2));
        } else if (string != null) {
            rectangle.setBackgroundColor(Markup.decodeColor(string));
        } else {
            string = properties.getProperty("grayfill");
            if (string != null) {
                rectangle.setGrayFill(Float.parseFloat(string + "f"));
            }
        }
    }

    public static ChapterAutoNumber getChapter(Properties properties) {
        ChapterAutoNumber chapterAutoNumber = new ChapterAutoNumber("");
        ElementFactory.setSectionParameters(chapterAutoNumber, properties);
        return chapterAutoNumber;
    }

    public static Section getSection(Section section, Properties properties) {
        Section section2 = section.addSection("");
        ElementFactory.setSectionParameters(section2, properties);
        return section2;
    }

    private static void setSectionParameters(Section section, Properties properties) {
        String string = properties.getProperty("numberdepth");
        if (string != null) {
            section.setNumberDepth(Integer.parseInt(string));
        }
        if ((string = properties.getProperty("indent")) != null) {
            section.setIndentation(Float.parseFloat(string + "f"));
        }
        if ((string = properties.getProperty("indentationleft")) != null) {
            section.setIndentationLeft(Float.parseFloat(string + "f"));
        }
        if ((string = properties.getProperty("indentationright")) != null) {
            section.setIndentationRight(Float.parseFloat(string + "f"));
        }
    }

    public static Image getImage(Properties properties) throws BadElementException, MalformedURLException, IOException {
        String string = properties.getProperty("url");
        if (string == null) {
            throw new MalformedURLException("The URL of the image is missing.");
        }
        Image image = Image.getInstance(string);
        string = properties.getProperty("align");
        int n = 0;
        if (string != null) {
            if ("Left".equalsIgnoreCase(string)) {
                n |= false;
            } else if ("Right".equalsIgnoreCase(string)) {
                n |= 2;
            } else if ("Middle".equalsIgnoreCase(string)) {
                n |= 1;
            }
        }
        if ("true".equalsIgnoreCase(properties.getProperty("underlying"))) {
            n |= 8;
        }
        if ("true".equalsIgnoreCase(properties.getProperty("textwrap"))) {
            n |= 4;
        }
        image.setAlignment(n);
        string = properties.getProperty("alt");
        if (string != null) {
            image.setAlt(string);
        }
        String string2 = properties.getProperty("absolutex");
        String string3 = properties.getProperty("absolutey");
        if (string2 != null && string3 != null) {
            image.setAbsolutePosition(Float.parseFloat(string2 + "f"), Float.parseFloat(string3 + "f"));
        }
        if ((string = properties.getProperty("plainwidth")) != null) {
            image.scaleAbsoluteWidth(Float.parseFloat(string + "f"));
        }
        if ((string = properties.getProperty("plainheight")) != null) {
            image.scaleAbsoluteHeight(Float.parseFloat(string + "f"));
        }
        if ((string = properties.getProperty("rotation")) != null) {
            image.setRotation(Float.parseFloat(string + "f"));
        }
        return image;
    }

    public static Annotation getAnnotation(Properties properties) {
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        float f4 = 0.0f;
        String string = properties.getProperty("llx");
        if (string != null) {
            f = Float.parseFloat(string + "f");
        }
        if ((string = properties.getProperty("lly")) != null) {
            f2 = Float.parseFloat(string + "f");
        }
        if ((string = properties.getProperty("urx")) != null) {
            f3 = Float.parseFloat(string + "f");
        }
        if ((string = properties.getProperty("ury")) != null) {
            f4 = Float.parseFloat(string + "f");
        }
        String string2 = properties.getProperty("title");
        String string3 = properties.getProperty("content");
        if (string2 != null || string3 != null) {
            return new Annotation(string2, string3, f, f2, f3, f4);
        }
        string = properties.getProperty("url");
        if (string != null) {
            return new Annotation(f, f2, f3, f4, string);
        }
        string = properties.getProperty("named");
        if (string != null) {
            return new Annotation(f, f2, f3, f4, Integer.parseInt(string));
        }
        String string4 = properties.getProperty("file");
        String string5 = properties.getProperty("destination");
        String string6 = (String)properties.remove("page");
        if (string4 != null) {
            if (string5 != null) {
                return new Annotation(f, f2, f3, f4, string4, string5);
            }
            if (string6 != null) {
                return new Annotation(f, f2, f3, f4, string4, Integer.parseInt(string6));
            }
        }
        return new Annotation("", "", f, f2, f3, f4);
    }
}

