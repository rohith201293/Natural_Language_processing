
package nlp;

import java.io.*;

public class Util {

  // PUBLIC UTILITY:
  public static String [] parseStrings(String s) {
    for (int i=0; i<s.length(); i++) {
      if (s.substring(i,i+1).equals("/")) {
        s = s.substring(0, i) + " " + s.substring(i+1);
      }
      if (s.substring(i,i+1).equals(".")) {
        s = s.substring(0, i) + " " + s.substring(i+1);
      }
      if (s.substring(i,i+1).equals("-")) {
        s = s.substring(0, i) + " " + s.substring(i+1);
      }
    }
    int num=0;
    String words[] = new String[20];
    try {
        s=s.trim();
        StreamTokenizer st = new StreamTokenizer(new StringReader(s));
        st.whitespaceChars(';', ';');
        try {
            int type;
            while ((type = st.nextToken()) != StreamTokenizer.TT_EOF) {
                if (type==StreamTokenizer.TT_WORD) {
                    if (num < 18) words[num++] = st.sval;
                } else if (type==StreamTokenizer.TT_NUMBER) {
                    if (num < 18) words[num++] = (new Integer((int)st.nval)).toString();
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace ();
        }
    } catch (Exception ioe) {
        System.out.println("Error: " + ioe.getMessage());
        ioe.printStackTrace ();
    }
    if (num>0) {
        String ret[] = new String[num];
        for (int i=0; i<num; i++) {
            ret[i]=words[i];
        }
        return ret;
    }
    String temp2[] = {"no tables"};
    return temp2;
  }

  public static String removeExtraSpaces(String s) {
    for (int i=0; i<10; i++) {
      int idx = s.indexOf("  ");
      if (idx>-1) {
        s = s.substring(0, idx+1) + s.substring(idx+2);
      } else {
        break;
      }
    }
    if (s.startsWith(" ")) s = s.substring(1);
    return s;
  }
}
