
package nlp;

import java.io.StringReader;
import java.io.BufferedReader;

public class NLEngine {

    public NLEngine() {
        // Create a DBInfo object and initialize it with the current
        // database properties:
        dbinfo = new DBInfo();

        nlp = new NLP(dbinfo);
    }

    public void addDB(String name, String user, String passwd, String tbls) {
        if (numDB < 4) {
            databaseName[numDB] = name;
            userName[numDB] = user;
            password[numDB] = passwd;
            tableList[numDB] = tbls;
            numDB++;
        }
    }

    public void initDB() {
        for (int d=0; d<numDB; d++) {
            if (databaseName[d]!=null) {
                String tl[] = Util.parseStrings(tableList[d]);
                if (tl!=null) {
                    for (int i=0; i<tl.length; i++) {
                        try {
                            String cols[] = DBInterface.getColumnNames(tl[i],
                                                                       databaseName[d],
                                                                       userName[d],
                                                                       password[d]);
                            if (cols!=null) {
                                dbinfo.addTable(tl[i], databaseName[d], userName[d], password[d], cols);
                            }
                        } catch (Exception e) { }
                    }
                }
            }
        }

        dbinfo.debug();
    }

    //                     NLP data and methods:

    public void parse(String s) {

        // Remove any commas from the input text:
        for (int i=0; i<20; i++) {
            int idx = s.indexOf(",");
            if (idx>-1) {
                s = s.substring(0,idx) + s.substring(idx+1);
            } else {
                break;
            }
        }
        // Remove any periods from the input text:
        for (int i=0; i<10; i++) {
            int idx = s.indexOf(".");
            if (idx>-1) {
                s = s.substring(0,idx) + s.substring(idx+1);
            } else {
                break;
            }
        }

        // remove extra spaces and convert to lower case:
        s = " " + Util.removeExtraSpaces(s).toLowerCase() + " ";

        // before calling the NLP class parse method, we
        // need to replace synonyms:

        numSynReplacements = 0;
        for (int i=0; i<numSynonyms; i++) {
            // allow for multiple uses of the same synonym:
            for (int repeat=0; repeat<4; repeat++) {
                int idx = s.indexOf(synonymDescription[i]);
                if (idx>-1) {
                    s = s.substring(0,idx+1) +
                        synonymColumnName[i] +
                        s.substring(idx + synonymDescription[i].length() - 1);
                    syns[numSynReplacements] = synonymColumnName[i];
                    origs[numSynReplacements] = synonymDescription[i];
                    numSynReplacements++;
                } else {
                    break;
                }
            }
        }

        // remove extra spaces (just to make sure!):
        s = Util.removeExtraSpaces(s);

        nlp.parse(s);
    }

    public String getSQL() {
        return nlp.getSQL();
    }
    private int num_rows_from_database = 0;
    public String [] breakLines(String s) {
        String [] ret = new String[40];
        int count = 0;
        num_rows_from_database = 0;
        try {
            StringReader sr = new StringReader(s);
            BufferedReader br = new BufferedReader(sr);
            while (true) {
                String s2 = br.readLine();
                if (s2 == null || s2.length() < 1)  break;
                num_rows_from_database++;
                // change for InstantDB: comma separated terms:
                int index = 0;
                while (true) {
                    index = s2.indexOf(",");
                    if (index == -1) {
                        if (count > 38) break;
                        ret[count++] = s2.trim();
                        break;
                    } else {
                        if (count > 38) break;
                        ret[count++] = s2.substring(0, index);
                        s2 = s2.substring(index + 1);
                    }
                }
            }
            String [] ret2 = new String[count];
            for (int i=0; i<count; i++) ret2[i] = ret[i];
            return ret2;
        } catch (Exception e) { }
        return null;
    }

    public String toEnglish(String r) {
        System.out.println("NLEngineLocal.toEnglish(" + r + ")");
        return nlp.toEnglish(breakLines(r), syns, origs,
                             numSynReplacements, num_rows_from_database);
    }

    public String [] getColumnNames(String sql_query, String database,
                                    String user, String password) {
        try {
            return DBInterface.getColumnNames(sql_query, database,
                                              user, password);
        } catch (Exception e) { }
        return null;
    }

    public String getRows(String sql_query, String database,
                          String user, String password) {
        try {
            return DBInterface.query(sql_query, database,
                                     user, password);
        } catch (Exception e) { }
        return null;
    }

    public  DBInfo dbinfo;
    private NLP nlp;

    // Allow developers to define a few synonyms for a particular
    // application:
    private String synonymColumnName[] = new String[11];
    private String synonymDescription[] = new String[11];
    private int numSynonyms = 0;

    // For use in generating nglish output for queries:
    private String syns[] = new String[11];
    private String origs[] = new String[11];
    private int numSynReplacements=0;

    public void clearSynonyms() {
        numSynonyms = 0;
    }

    public void addSynonym(String column, String description) {
        if (numSynonyms<10) {
            synonymColumnName[numSynonyms] = column;
            synonymDescription[numSynonyms] = " " + description + " ";
            numSynonyms++;
        }
    }

    // for four properties each for up to five databases:
    private String databaseName[] = new String[5];
    private String userName[] = new String[5];
    private String password[] = new String[5];
    private String tableList[] = new String[5];
    private int numDB=0;

}
