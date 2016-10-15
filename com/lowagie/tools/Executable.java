package com.lowagie.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class Executable {
    public static String acroread = null;

    private static Process action(String string, String string2, boolean bl) throws IOException {
        Process process = null;
        string2 = string2.trim().length() > 0 ? " " + string2.trim() : "";
        if (acroread != null) {
            process = Runtime.getRuntime().exec(acroread + string2 + " \"" + string + "\"");
        } else if (Executable.isWindows()) {
            process = Executable.isWindows9X() ? Runtime.getRuntime().exec("command.com /C start acrord32" + string2 + " \"" + string + "\"") : Runtime.getRuntime().exec("cmd /c start acrord32" + string2 + " \"" + string + "\"");
        } else if (Executable.isMac()) {
            process = string2.trim().length() == 0 ? Runtime.getRuntime().exec(new String[]{"/usr/bin/open", string}) : Runtime.getRuntime().exec(new String[]{"/usr/bin/open", string2.trim(), string});
        }
        try {
            if (process != null && bl) {
                process.waitFor();
            }
        }
        catch (InterruptedException var4_4) {
            // empty catch block
        }
        return process;
    }

    public static final Process openDocument(String string, boolean bl) throws IOException {
        return Executable.action(string, "", bl);
    }

    public static final Process openDocument(File file, boolean bl) throws IOException {
        return Executable.openDocument(file.getAbsolutePath(), bl);
    }

    public static final Process openDocument(String string) throws IOException {
        return Executable.openDocument(string, false);
    }

    public static final Process openDocument(File file) throws IOException {
        return Executable.openDocument(file, false);
    }

    public static final Process printDocument(String string, boolean bl) throws IOException {
        return Executable.action(string, "/p", bl);
    }

    public static final Process printDocument(File file, boolean bl) throws IOException {
        return Executable.printDocument(file.getAbsolutePath(), bl);
    }

    public static final Process printDocument(String string) throws IOException {
        return Executable.printDocument(string, false);
    }

    public static final Process printDocument(File file) throws IOException {
        return Executable.printDocument(file, false);
    }

    public static final Process printDocumentSilent(String string, boolean bl) throws IOException {
        return Executable.action(string, "/p /h", bl);
    }

    public static final Process printDocumentSilent(File file, boolean bl) throws IOException {
        return Executable.printDocumentSilent(file.getAbsolutePath(), bl);
    }

    public static final Process printDocumentSilent(String string) throws IOException {
        return Executable.printDocumentSilent(string, false);
    }

    public static final Process printDocumentSilent(File file) throws IOException {
        return Executable.printDocumentSilent(file, false);
    }

    public static final void launchBrowser(String string) throws IOException {
        try {
            if (Executable.isMac()) {
                Class class_ = Class.forName("com.apple.mrj.MRJFileUtils");
                Class[] arrclass = new Class[1];
                Class class_2 = String.class;
                arrclass[0] = class_2;
                Method method = class_.getDeclaredMethod("openURL", arrclass);
                method.invoke(null, string);
            } else if (Executable.isWindows()) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + string);
            } else {
                String[] arrstring = new String[]{"firefox", "opera", "konqueror", "mozilla", "netscape"};
                String string2 = null;
                for (int i = 0; i < arrstring.length && string2 == null; ++i) {
                    if (Runtime.getRuntime().exec(new String[]{"which", arrstring[i]}).waitFor() != 0) continue;
                    string2 = arrstring[i];
                }
                if (string2 == null) {
                    throw new Exception("Could not find web browser.");
                }
                Runtime.getRuntime().exec(new String[]{string2, string});
            }
        }
        catch (Exception var1_3) {
            throw new IOException("Error attempting to launch web browser");
        }
    }

    public static boolean isWindows() {
        String string = System.getProperty("os.name").toLowerCase();
        return string.indexOf("windows") != -1 || string.indexOf("nt") != -1;
    }

    public static boolean isWindows9X() {
        String string = System.getProperty("os.name").toLowerCase();
        return string.equals("windows 95") || string.equals("windows 98");
    }

    public static boolean isMac() {
        String string = System.getProperty("os.name").toLowerCase();
        return string.indexOf("mac") != -1;
    }

    public static boolean isLinux() {
        String string = System.getProperty("os.name").toLowerCase();
        return string.indexOf("linux") != -1;
    }
}

