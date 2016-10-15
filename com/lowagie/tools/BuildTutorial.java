package com.lowagie.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class BuildTutorial {
    static String root;
    static FileWriter build;

    public static void main(String[] arrstring) {
        if (arrstring.length == 4) {
            File file = new File(arrstring[0]);
            File file2 = new File(arrstring[1]);
            File file3 = new File(file, arrstring[2]);
            File file4 = new File(file, arrstring[3]);
            try {
                System.out.print("Building tutorial: ");
                root = new File(arrstring[1], file.getName()).getCanonicalPath();
                System.out.println(root);
                build = new FileWriter(new File(root, "build.xml"));
                build.write("<project name=\"tutorial\" default=\"all\" basedir=\".\">\n");
                build.write("<target name=\"all\">\n");
                BuildTutorial.action(file, file2, file3, file4);
                build.write("</target>\n</project>");
                build.flush();
                build.close();
            }
            catch (IOException var5_5) {
                var5_5.printStackTrace();
            }
        } else {
            System.err.println("Wrong number of parameters.\nUsage: BuildSite srcdr destdir xsl_examples xsl_site");
        }
    }

    public static void action(File file, File file2, File file3, File file4) throws IOException {
        if (".svn".equals(file.getName())) {
            return;
        }
        System.out.print(file.getName());
        if (file.isDirectory()) {
            System.out.print(" ");
            System.out.println(file.getCanonicalPath());
            File file5 = new File(file2, file.getName());
            file5.mkdir();
            File[] arrfile = file.listFiles();
            if (arrfile != null) {
                for (int i = 0; i < arrfile.length; ++i) {
                    File file6 = arrfile[i];
                    BuildTutorial.action(file6, file5, file3, file4);
                }
            } else {
                System.out.println("... skipped");
            }
        } else if (file.getName().equals("index.xml")) {
            System.out.println("... transformed");
            BuildTutorial.convert(file, file4, new File(file2, "index.php"));
            File file7 = new File(file2, "build.xml");
            String string = file7.getCanonicalPath().substring(root.length());
            string = string.replace(File.separatorChar, '/');
            if ("/build.xml".equals(string)) {
                return;
            }
            BuildTutorial.convert(file, file3, file7);
            build.write("\t<ant antfile=\"${basedir}");
            build.write(string);
            build.write("\" target=\"install\" inheritAll=\"false\" />\n");
        } else {
            System.out.println("... skipped");
        }
    }

    public static void convert(File file, File file2, File file3) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Templates templates = transformerFactory.newTemplates(new StreamSource(new FileInputStream(file2)));
            Transformer transformer = templates.newTransformer();
            String string = file3.getParentFile().getCanonicalPath().substring(root.length());
            string = string.replace(File.separatorChar, '/');
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < string.length(); ++i) {
                if (string.charAt(i) != '/') continue;
                stringBuffer.append("/..");
            }
            transformer.setParameter("branch", string);
            transformer.setParameter("root", stringBuffer.toString());
            StreamSource streamSource = new StreamSource(new FileInputStream(file));
            StreamResult streamResult = new StreamResult(new FileOutputStream(file3));
            transformer.transform(streamSource, streamResult);
        }
        catch (Exception var3_4) {
            var3_4.printStackTrace();
        }
    }
}

