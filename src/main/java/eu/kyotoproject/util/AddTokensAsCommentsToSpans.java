package eu.kyotoproject.util;

import eu.kyotoproject.kaf.CorefTarget;
import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kaf.KafWordForm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 12/15/12
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddTokensAsCommentsToSpans {


    static public String getTokenStringFromTermIds (KafSaxParser kafSaxParser, ArrayList<String> termIds) {
        ArrayList<String> tokenIds = AddTokensAsCommentsToSpans.convertTermSpanToTokenSpan(kafSaxParser, termIds);
        String tokenString = AddTokensAsCommentsToSpans.getTokenString(kafSaxParser, tokenIds);
        return tokenString;
    }



    static public String getTokenString (KafSaxParser kafSaxParser, ArrayList<String> tokenSpan) {
        String str = "";
        for (int i = 0; i < tokenSpan.size(); i++) {
            String id = tokenSpan.get(i);
            if (kafSaxParser.wordFormMap.containsKey(id)) {
                KafWordForm kafWordForm = kafSaxParser.wordFormMap.get(id);
                str += kafWordForm.getWf()+" ";
            }
        }
        return str.trim();
    }
    static public String getTokenString (KafSaxParser kafSaxParser, String termId) {
        String str = "";
        ArrayList<String> tokenSpan = convertTermSpanToTokenSpan(kafSaxParser, termId);
        for (int i = 0; i < tokenSpan.size(); i++) {
            String id = tokenSpan.get(i);
            if (kafSaxParser.wordFormMap.containsKey(id)) {
                KafWordForm kafWordForm = kafSaxParser.wordFormMap.get(id);
                str += kafWordForm.getWf()+" ";
            }
        }
        return str.trim();
    }

    static public ArrayList<String> convertTermSpanToTokenSpan (KafSaxParser kafSaxParser, String termId) {
        ArrayList<String> tokenSpan = new ArrayList<String>();
        if (kafSaxParser.TermToWord.containsKey(termId)) {
            ArrayList<String> tokens = kafSaxParser.TermToWord.get(termId);
            for (int j = 0; j < tokens.size(); j++) {
                String t = tokens.get(j);
                tokenSpan.add(t);
            }
        }
        else {
            int idx = termId.lastIndexOf(";");
            if (idx>-1) {
                termId = termId.substring(idx);
                if (kafSaxParser.TermToWord.containsKey(termId)) {
                    ArrayList<String> tokens = kafSaxParser.TermToWord.get(termId);
                    for (int j = 0; j < tokens.size(); j++) {
                        String t = tokens.get(j);
                        tokenSpan.add(t);
                    }
                }
            }
           // System.out.println("s = " + s);
        }
        return tokenSpan;
    }

    static public ArrayList<String> convertTermSpanToTokenSpan (KafSaxParser kafSaxParser, ArrayList<String> span) {
        ArrayList<String> tokenSpan = new ArrayList<String>();
        for (int i = 0; i < span.size(); i++) {
            String s = span.get(i);
            //System.out.println("s = " + s);
            if (kafSaxParser.TermToWord.containsKey(s)) {
                ArrayList<String> tokens = kafSaxParser.TermToWord.get(s);
                for (int j = 0; j < tokens.size(); j++) {
                    String t = tokens.get(j);
                    tokenSpan.add(t);
                }
            }
            else {
                int idx = s.lastIndexOf(";");
                if (idx>-1) {
                    s = s.substring(idx);
                    if (kafSaxParser.TermToWord.containsKey(s)) {
                        ArrayList<String> tokens = kafSaxParser.TermToWord.get(s);
                        for (int j = 0; j < tokens.size(); j++) {
                            String t = tokens.get(j);
                            tokenSpan.add(t);
                        }
                    }
                }
               // System.out.println("s = " + s);
            }

        }
        return tokenSpan;
    }

    static public ArrayList<String> convertCorefTargetsToTokenSpan (KafSaxParser kafSaxParser, ArrayList<CorefTarget> span) {
        ArrayList<String> tokenSpan = new ArrayList<String>();
        for (int i = 0; i < span.size(); i++) {
            CorefTarget corefTarget = span.get(i);
            if (!kafSaxParser.TermToWord.containsKey(corefTarget.getId())) {
                System.out.println("kafSaxParser = " + kafSaxParser.getKafMetaData().getUrl());
                System.out.println("Cannot find word for corefTarget.getId() = " + corefTarget.getId());
            }
            else {
                ArrayList<String> tokens = kafSaxParser.TermToWord.get(corefTarget.getId());
                for (int j = 0; j < tokens.size(); j++) {
                    String t = tokens.get(j);
                    tokenSpan.add(t);
                }
            }
        }
        return tokenSpan;
    }

    static public void main (String [] args) {
        String kafFolder = args[0];
        String extension = args[1];
        KafSaxParser kafSaxParser = new KafSaxParser();
        if (!kafFolder.isEmpty()) {
            ArrayList<File> files = makeFlatFileList(kafFolder, extension);
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                System.out.println("file.getName() = " + file.getName());
                kafSaxParser.init();
                kafSaxParser.parseFile(file);
                try {
                    FileOutputStream fos = new FileOutputStream(file.getAbsolutePath()+".comment.kaf");
                    kafSaxParser.writeKafToStream(fos);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    static public ArrayList<File> makeFlatFileList(String inputPath, String extension) {
        ArrayList<File> acceptedFileList = new ArrayList<File>();
        File[] theFileList = null;
        File lF = new File(inputPath);
        if ((lF.canRead()) && lF.isDirectory()) {
            theFileList = lF.listFiles();
            for (int i = 0; i < theFileList.length; i++) {
                if (theFileList[i].isFile()) {
                    if (theFileList[i].getAbsolutePath().endsWith(extension)) {
                        acceptedFileList.add(theFileList[i]);
                    }
                }
            }
        }
        return acceptedFileList;
    }

}
