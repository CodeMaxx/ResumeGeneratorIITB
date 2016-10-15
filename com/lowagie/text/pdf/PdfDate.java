/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfString;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class PdfDate
extends PdfString {
    private static final int[] DATE_SPACE = new int[]{1, 4, 0, 2, 2, -1, 5, 2, 0, 11, 2, 0, 12, 2, 0, 13, 2, 0};

    public PdfDate(Calendar calendar) {
        StringBuffer stringBuffer = new StringBuffer("D:");
        stringBuffer.append(this.setLength(calendar.get(1), 4));
        stringBuffer.append(this.setLength(calendar.get(2) + 1, 2));
        stringBuffer.append(this.setLength(calendar.get(5), 2));
        stringBuffer.append(this.setLength(calendar.get(11), 2));
        stringBuffer.append(this.setLength(calendar.get(12), 2));
        stringBuffer.append(this.setLength(calendar.get(13), 2));
        int n = (calendar.get(15) + calendar.get(16)) / 3600000;
        if (n == 0) {
            stringBuffer.append('Z');
        } else if (n < 0) {
            stringBuffer.append('-');
            n = - n;
        } else {
            stringBuffer.append('+');
        }
        if (n != 0) {
            stringBuffer.append(this.setLength(n, 2)).append('\'');
            int n2 = Math.abs((calendar.get(15) + calendar.get(16)) / 60000) - n * 60;
            stringBuffer.append(this.setLength(n2, 2)).append('\'');
        }
        this.value = stringBuffer.toString();
    }

    public PdfDate() {
        this(new GregorianCalendar());
    }

    private String setLength(int n, int n2) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(n);
        while (stringBuffer.length() < n2) {
            stringBuffer.insert(0, "0");
        }
        stringBuffer.setLength(n2);
        return stringBuffer.toString();
    }

    public String getW3CDate() {
        return PdfDate.getW3CDate(this.value);
    }

    public static String getW3CDate(String string) {
        if (string.startsWith("D:")) {
            string = string.substring(2);
        }
        StringBuffer stringBuffer = new StringBuffer();
        if (string.length() < 4) {
            return "0000";
        }
        stringBuffer.append(string.substring(0, 4));
        string = string.substring(4);
        if (string.length() < 2) {
            return stringBuffer.toString();
        }
        stringBuffer.append('-').append(string.substring(0, 2));
        string = string.substring(2);
        if (string.length() < 2) {
            return stringBuffer.toString();
        }
        stringBuffer.append('-').append(string.substring(0, 2));
        string = string.substring(2);
        if (string.length() < 2) {
            return stringBuffer.toString();
        }
        stringBuffer.append('T').append(string.substring(0, 2));
        string = string.substring(2);
        if (string.length() < 2) {
            stringBuffer.append(":00Z");
            return stringBuffer.toString();
        }
        stringBuffer.append(':').append(string.substring(0, 2));
        string = string.substring(2);
        if (string.length() < 2) {
            stringBuffer.append('Z');
            return stringBuffer.toString();
        }
        stringBuffer.append(':').append(string.substring(0, 2));
        string = string.substring(2);
        if (string.startsWith("-") || string.startsWith("+")) {
            String string2 = string.substring(0, 1);
            string = string.substring(1);
            String string3 = "00";
            String string4 = "00";
            if (string.length() >= 2) {
                string3 = string.substring(0, 2);
                if (string.length() > 2 && (string = string.substring(3)).length() >= 2) {
                    string4 = string.substring(0, 2);
                }
                stringBuffer.append(string2).append(string3).append(':').append(string4);
                return stringBuffer.toString();
            }
        }
        stringBuffer.append('Z');
        return stringBuffer.toString();
    }

    public static Calendar decode(String string) {
        try {
            GregorianCalendar gregorianCalendar;
            int n;
            if (string.startsWith("D:")) {
                string = string.substring(2);
            }
            int n2 = string.length();
            int n3 = string.indexOf(90);
            if (n3 >= 0) {
                n2 = n3;
                gregorianCalendar = new GregorianCalendar(new SimpleTimeZone(0, "ZPDF"));
            } else {
                n = 1;
                n3 = string.indexOf(43);
                if (n3 < 0 && (n3 = string.indexOf(45)) >= 0) {
                    n = -1;
                }
                if (n3 < 0) {
                    gregorianCalendar = new GregorianCalendar();
                } else {
                    int n4 = Integer.parseInt(string.substring(n3 + 1, n3 + 3)) * 60;
                    if (n3 + 5 < string.length()) {
                        n4 += Integer.parseInt(string.substring(n3 + 4, n3 + 6));
                    }
                    gregorianCalendar = new GregorianCalendar(new SimpleTimeZone(n4 * n * 60000, "ZPDF"));
                    n2 = n3;
                }
            }
            gregorianCalendar.clear();
            n3 = 0;
            for (n = 0; n < DATE_SPACE.length && n3 < n2; n3 += PdfDate.DATE_SPACE[n + 1], n += 3) {
                gregorianCalendar.set(DATE_SPACE[n], Integer.parseInt(string.substring(n3, n3 + DATE_SPACE[n + 1])) + DATE_SPACE[n + 2]);
            }
            return gregorianCalendar;
        }
        catch (Exception var1_4) {
            return null;
        }
    }
}

