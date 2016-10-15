/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

public class ElementTags {
    public static final String ITEXT = "itext";
    public static final String TITLE = "title";
    public static final String SUBJECT = "subject";
    public static final String KEYWORDS = "keywords";
    public static final String AUTHOR = "author";
    public static final String CREATIONDATE = "creationdate";
    public static final String PRODUCER = "producer";
    public static final String CHAPTER = "chapter";
    public static final String SECTION = "section";
    public static final String NUMBERDEPTH = "numberdepth";
    public static final String DEPTH = "depth";
    public static final String NUMBER = "number";
    public static final String INDENT = "indent";
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String PHRASE = "phrase";
    public static final String ANCHOR = "anchor";
    public static final String LIST = "list";
    public static final String LISTITEM = "listitem";
    public static final String PARAGRAPH = "paragraph";
    public static final String LEADING = "leading";
    public static final String ALIGN = "align";
    public static final String KEEPTOGETHER = "keeptogether";
    public static final String NAME = "name";
    public static final String REFERENCE = "reference";
    public static final String LISTSYMBOL = "listsymbol";
    public static final String NUMBERED = "numbered";
    public static final String LETTERED = "lettered";
    public static final String FIRST = "first";
    public static final String SYMBOLINDENT = "symbolindent";
    public static final String INDENTATIONLEFT = "indentationleft";
    public static final String INDENTATIONRIGHT = "indentationright";
    public static final String IGNORE = "ignore";
    public static final String ENTITY = "entity";
    public static final String ID = "id";
    public static final String CHUNK = "chunk";
    public static final String ENCODING = "encoding";
    public static final String EMBEDDED = "embedded";
    public static final String COLOR = "color";
    public static final String RED = "red";
    public static final String GREEN = "green";
    public static final String BLUE = "blue";
    public static final String SUBSUPSCRIPT = "SUBSUPSCRIPT".toLowerCase();
    public static final String LOCALGOTO = "LOCALGOTO".toLowerCase();
    public static final String REMOTEGOTO = "REMOTEGOTO".toLowerCase();
    public static final String LOCALDESTINATION = "LOCALDESTINATION".toLowerCase();
    public static final String GENERICTAG = "GENERICTAG".toLowerCase();
    public static final String TABLE = "table";
    public static final String ROW = "row";
    public static final String CELL = "cell";
    public static final String COLUMNS = "columns";
    public static final String LASTHEADERROW = "lastHeaderRow";
    public static final String CELLPADDING = "cellpadding";
    public static final String CELLSPACING = "cellspacing";
    public static final String OFFSET = "offset";
    public static final String WIDTHS = "widths";
    public static final String TABLEFITSPAGE = "tablefitspage";
    public static final String CELLSFITPAGE = "cellsfitpage";
    public static final String CONVERT2PDFP = "convert2pdfp";
    public static final String HORIZONTALALIGN = "horizontalalign";
    public static final String VERTICALALIGN = "verticalalign";
    public static final String COLSPAN = "colspan";
    public static final String ROWSPAN = "rowspan";
    public static final String HEADER = "header";
    public static final String NOWRAP = "nowrap";
    public static final String BORDERWIDTH = "borderwidth";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    public static final String WIDTH = "width";
    public static final String BORDERCOLOR = "bordercolor";
    public static final String BACKGROUNDCOLOR = "backgroundcolor";
    public static final String BGRED = "bgred";
    public static final String BGGREEN = "bggreen";
    public static final String BGBLUE = "bgblue";
    public static final String GRAYFILL = "grayfill";
    public static final String IMAGE = "image";
    public static final String URL = "url";
    public static final String UNDERLYING = "underlying";
    public static final String TEXTWRAP = "textwrap";
    public static final String ALT = "alt";
    public static final String ABSOLUTEX = "absolutex";
    public static final String ABSOLUTEY = "absolutey";
    public static final String PLAINWIDTH = "plainwidth";
    public static final String PLAINHEIGHT = "plainheight";
    public static final String SCALEDWIDTH = "scaledwidth";
    public static final String SCALEDHEIGHT = "scaledheight";
    public static final String ROTATION = "rotation";
    public static final String NEWPAGE = "newpage";
    public static final String NEWLINE = "newline";
    public static final String ANNOTATION = "annotation";
    public static final String FILE = "file";
    public static final String DESTINATION = "destination";
    public static final String PAGE = "page";
    public static final String NAMED = "named";
    public static final String APPLICATION = "application";
    public static final String PARAMETERS = "parameters";
    public static final String OPERATION = "operation";
    public static final String DEFAULTDIR = "defaultdir";
    public static final String LLX = "llx";
    public static final String LLY = "lly";
    public static final String URX = "urx";
    public static final String URY = "ury";
    public static final String CONTENT = "content";
    public static final String ALIGN_LEFT = "Left";
    public static final String ALIGN_CENTER = "Center";
    public static final String ALIGN_RIGHT = "Right";
    public static final String ALIGN_JUSTIFIED = "Justify";
    public static final String ALIGN_JUSTIFIED_ALL = "JustifyAll";
    public static final String ALIGN_TOP = "Top";
    public static final String ALIGN_MIDDLE = "Middle";
    public static final String ALIGN_BOTTOM = "Bottom";
    public static final String ALIGN_BASELINE = "Baseline";
    public static final String DEFAULT = "Default";
    public static final String UNKNOWN = "unknown";
    public static final String FONT = "font";
    public static final String SIZE = "size";
    public static final String STYLE = "fontstyle";
    public static final String HORIZONTALRULE = "horizontalrule";
    public static final String PAGE_SIZE = "pagesize";
    public static final String ORIENTATION = "orientation";
    public static final String ALIGN_INDENTATION_ITEMS = "alignindent";
    public static final String AUTO_INDENT_ITEMS = "autoindent";
    public static final String LOWERCASE = "lowercase";
    public static final String FACE = "face";
    public static final String SRC = "src";

    public static String getAlignment(int n) {
        switch (n) {
            case 0: {
                return "Left";
            }
            case 1: {
                return "Center";
            }
            case 2: {
                return "Right";
            }
            case 3: 
            case 8: {
                return "Justify";
            }
            case 4: {
                return "Top";
            }
            case 5: {
                return "Middle";
            }
            case 6: {
                return "Bottom";
            }
            case 7: {
                return "Baseline";
            }
        }
        return "Default";
    }

    public static int alignmentValue(String string) {
        if (string == null) {
            return -1;
        }
        if ("Center".equalsIgnoreCase(string)) {
            return 1;
        }
        if ("Left".equalsIgnoreCase(string)) {
            return 0;
        }
        if ("Right".equalsIgnoreCase(string)) {
            return 2;
        }
        if ("Justify".equalsIgnoreCase(string)) {
            return 3;
        }
        if ("JustifyAll".equalsIgnoreCase(string)) {
            return 8;
        }
        if ("Top".equalsIgnoreCase(string)) {
            return 4;
        }
        if ("Middle".equalsIgnoreCase(string)) {
            return 5;
        }
        if ("Bottom".equalsIgnoreCase(string)) {
            return 6;
        }
        if ("Baseline".equalsIgnoreCase(string)) {
            return 7;
        }
        return -1;
    }
}

