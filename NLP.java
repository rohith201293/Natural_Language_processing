
package nlp;

/**
 * NLP - a very simple text scanner.
 */
public class NLP {

    public NLP(DBInfo dbi) {
        dbinfo=dbi;
    }

    final private int AND=1;
    final private int OR=2;

    public void parse(String s) {

        // parse a new sentence:
        currentWords = Util.parseStrings(s);
        if (currentWords==null) {
            System.out.println("Error parsing: " + s);
            return;
        }

        // Check for a REFINED QUERY that builds on the
        // last query (in which case, we do not want to
        // blank-out the slots from the last parse.
        //
        // Support:
        //   Mode 0:
        //         new query
        //
        //   Mode 1:
        //         and <column name>   -- adds a display column name
        //
        //   Mode 2:
        //         and <condition>     -- adds a SQL WHERE condition
        //

        int mode = 0; // 0->new query
        if (currentWords[0].equalsIgnoreCase("and") ||
            currentWords[0].equalsIgnoreCase("add"))
            {
                if (currentWords.length < 3)   mode=1;
                else                           mode=2;
            }


        System.out.println("parse(" + s + "): number of words in sentence=" + currentWords.length);
        currentWordIndex=0;

        if (mode==0) {
            tableName=null;
            time_after=null;
            time_before=null;
            num_temp_col_names = 0;
            currentAction=NO_OP;
            displayColumnName = "*";
            searchColumnName = null;
            searchString="*";
            searchColumnName2 = null;
            searchString2=null;
            searchColumnName3 = null;
            searchString3=null;
        }  else if (mode==1) {
            System.out.println("processing 'add/and <column name>'");
            currentWordIndex++;
            String cname = eatColumnName(null);
            if (cname != null) {
                displayColumnName = displayColumnName + ", " + cname;
                return;
            }
        } else if (mode==2) {
            System.out.println("processing 'and/add <condition>'");
            currentWordIndex++;
            String cname1 = eatColumnName(null);
            if (cname1 != null) {
                System.out.println("   cname1=" + cname1);
                // look for a condition:
                if (eatWord(equals_is)) {
                    System.out.println("  equals_is matched **");
                    if (currentWordIndex < currentWords.length) {
                        searchColumnName2 = cname1;
                        searchString2 = currentWords[currentWordIndex];
                        return;
                    }
                }
            }
            return;
        }


        if (eatWord(show) == false) return;
        eatWord(noise1); // optional; also check for column names
        displayColumnName = eatColumnName(null);

        // check for more column names of the form:
        //   <cn>, <cn>, and <cn>
        // NOTE: "," chars are already removed.

        eatWord(and);
        String temp = eatColumnName(null);
        if (temp!=null) {
            displayColumnName = displayColumnName + ", " + temp;
            eatWord(and);
            temp = eatColumnName(null);
            if (temp!=null) {
                displayColumnName = displayColumnName + ", " + temp;
            }
        }

        if (displayColumnName==null) displayColumnName="*";
        eatWord(where);   // WHERE
        searchColumnName = eatColumnName(null);  // displayColumnName);
        System.out.println("searchColumnName=" + searchColumnName);
        currentAction=LIST;
        eatWord(is); // skip 'is'

        quantity=-999.0f;
        compareMode=NONE;

        if (eatWord(less)) {
            eatWord(than);  // skip 'than'
            String quan = currentWords[currentWordIndex];
            try {
                Float f = new Float(quan);
                quantity = f.floatValue();
                compareMode=LESS;
                currentWordIndex++;
                System.out.println("less than " + quantity);
            } catch (Exception e) { }
        }

        if (eatWord(more)) {
            eatWord(than);  // skip 'than'
            String quan = currentWords[currentWordIndex];
            try {
                Float f = new Float(quan);
                quantity = f.floatValue();
                compareMode=MORE;
                currentWordIndex++;
                System.out.println("more than " + quantity);
            } catch (Exception e) { }
        }

        if (eatWord(after)) {
            if (currentWords.length > currentWordIndex+2) {
                String test = currentWords[currentWordIndex] + " " +
                    currentWords[currentWordIndex+1] + " " +
                    currentWords[currentWordIndex+2];
                time_after = new SmartDate(test);
                if (time_after.valid()==false)  time_after=null;
                else currentWordIndex+=3;
            }
            if (time_after==null & currentWords.length > currentWordIndex+1) {
                String test = currentWords[currentWordIndex] + " " +
                    currentWords[currentWordIndex+1];
                time_after = new SmartDate(test);
                if (time_after.valid()==false)  time_after=null;
                else currentWordIndex+=2;
            }
            if (time_after==null & currentWords.length > currentWordIndex) {
                String test = currentWords[currentWordIndex];
                time_after = new SmartDate(test);
                if (time_after.valid()==false)  time_after=null;
                else currentWordIndex+=1;
            }
        }
        if (time_after!=null) {
            System.out.println("parsed 'after' time OK:");
            System.out.println("  year:  " + time_after.getYear());
            System.out.println("  month: " + time_after.getMonth());
            System.out.println("  day:   " + time_after.getDayOfMonth());
        }



        if (eatWord(before)) {
            if (currentWords.length > currentWordIndex+2) {
                String test = currentWords[currentWordIndex] + " " +
                    currentWords[currentWordIndex+1] + " " +
                    currentWords[currentWordIndex+2];
                time_before = new SmartDate(test);
                if (time_before.valid()==false)  time_before=null;
                else currentWordIndex+=3;
            }
            if (time_before==null & currentWords.length > currentWordIndex+1) {
                String test = currentWords[currentWordIndex] + " " +
                    currentWords[currentWordIndex+1];
                time_before = new SmartDate(test);
                if (time_before.valid()==false)  time_before=null;
                else currentWordIndex+=2;
            }
            if (time_before==null & currentWords.length > currentWordIndex) {
                String test = currentWords[currentWordIndex];
                time_before = new SmartDate(test);
                if (time_before.valid()==false)  time_before=null;
                else currentWordIndex+=1;
            }
        }
        if (time_before!=null) {
            System.out.println("parsed 'before' time OK:");
            System.out.println("  year:  " + time_before.getYear());
            System.out.println("  month: " + time_before.getMonth());
            System.out.println("  day:   " + time_before.getDayOfMonth());
        }



        conditionMode = 0;

        if (searchColumnName==null) return;
        if (eatWord(and)) {   // check for AND condition
            System.out.println("processing 'and/add <condition>'");
            String cname1 = eatColumnName(null);
            if (cname1 != null) {
                System.out.println("   cname1=" + cname1);
                // look for a condition:
                if (eatWord(equals_is)) {
                    System.out.println("  equals_is matched **");
                    if (currentWordIndex < currentWords.length) {
                        searchColumnName2 = cname1;
                        searchString2 = currentWords[currentWordIndex];
                        conditionMode = AND;
                    }
                }
            }
        }
        if (eatWord(or)) {   // check for OR condition
            System.out.println("processing 'and/add <condition>'");
            String cname1 = eatColumnName(null);
            if (cname1 != null) {
                System.out.println("   cname1=" + cname1);
                // look for a condition:
                if (eatWord(equals_is)) {
                    System.out.println("  equals_is matched **");
                    if (currentWordIndex < currentWords.length) {
                        searchColumnName2 = cname1;
                        searchString2 = currentWords[currentWordIndex];
                        conditionMode = OR;
                    }
                }
            }
        }

        if (eatWord(equals)==false) return;
        if (currentWordIndex<currentWords.length) {
            searchString=currentWords[currentWordIndex];
        }

    }

    public String getSQL() {
        if (currentAction==NO_OP) {
            System.out.println("getSQL(): currentAction is NO_OP!");
            return "";
        }
        if (currentAction==LIST) {
            // Start by making sure that the 'tableName' string does not
            // include any tables that are not referenced in column name
            // string:
            int index = displayColumnName.indexOf(".");
            if (index>-1) {
                tableName = displayColumnName.substring(0,index);
            }
            if (searchColumnName!=null) {
                index = searchColumnName.indexOf(".");
                if (index>-1) {
                    searchColumnName = tableName + searchColumnName.substring(index);
                }
            }
            if (searchColumnName2!=null) {
                index = searchColumnName2.indexOf(".");
                if (index>-1) {
                    searchColumnName2 = tableName + searchColumnName2.substring(index);
                }
            }
            if (searchColumnName3!=null) {
                index = searchColumnName3.indexOf(".");
                if (index>-1) {
                    searchColumnName3 = tableName + searchColumnName3.substring(index);
                }
            }
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT ");
            if (displayColumnName!=null) sb.append(displayColumnName);
            else                         sb.append("*");
            if (time_after!=null && time_after.valid()) {
                if (searchColumnName!=null) {
                    sb.append(" FROM " + tableName + " WHERE " + searchColumnName +
                              " > '" + time_after.toString() + "'");
                    // Note: for Microsoft Access: both ' should be # in last line
                } else {
                    sb.append(" FROM " + tableName);
                }
            } else if (time_before!=null && time_before.valid()) {
                if (searchColumnName!=null) {
                    sb.append(" FROM " + tableName + " WHERE " + searchColumnName +
                              " < #" + time_before.toString() + "#");
                } else {
                    sb.append(" FROM " + tableName);
                }
            } else if (compareMode!=NONE && quantity!=-999.0f) {
                if (compareMode==LESS) {
                    if (searchColumnName!=null) {
                        sb.append(" FROM " + tableName + " WHERE " + searchColumnName +
                                  " < " + quantity);
                    } else {
                        sb.append(" FROM " + tableName);
                    }
                } else { // MORE
                    if (searchColumnName!=null) {
                        sb.append(" FROM " + tableName + " WHERE " + searchColumnName +
                                  " > " + quantity);
                    } else {
                        sb.append(" FROM " + tableName);
                    }
                }
            } else {
                if (searchColumnName!=null) {
                    sb.append(" FROM " + tableName + " WHERE " + searchColumnName +
                              " = " + quoteLiteral(searchString));
                } else {
                    sb.append(" FROM " + tableName);
                }
            }
            if (searchString2!=null && conditionMode==AND) {
                sb.append(" AND " + searchColumnName2 + " = " +
                          quoteLiteral(searchString2));
            }
            if (searchString2!=null && conditionMode==OR) {
                sb.append(" OR " + searchColumnName2 + " = " +
                          quoteLiteral(searchString2));
            }
            //sb.append(" ;");
            return new String(sb);
        }
        return "";
    }

    public String toEnglish(String [] r, String [] syns,
                            String [] origs, int num,
                            int num_rows_from_database) {
        int count = r.length / num_rows_from_database;
        StringBuffer sb = new StringBuffer();
        for (int ii=0; ii<num_rows_from_database; ii++) {
            sb.append("The value of ");
            for (int i=0; i<count; i++) {
                String s = temp_col_names[i];
                // check for synonym substitution:
                for (int j=0; j<num; j++) {
                    if (s.equalsIgnoreCase(syns[j])) {
                        s = origs[j];
                    }
                }
                sb.append(s + " is " + r[i + (ii * count)]);
                if (i<count - 2) sb.append(", ");
                if (i==count- 2) sb.append(" and ");
            }
            sb.append(".\n");
        }
        return new String(sb);
    }

    // String constants need ' ' marks, while numbers can
    // not have surrounding ' ' marks:
    private String quoteLiteral(String s) {
        if (s.startsWith("0")) return s;
        if (s.startsWith("1")) return s;
        if (s.startsWith("2")) return s;
        if (s.startsWith("3")) return s;
        if (s.startsWith("4")) return s;
        if (s.startsWith("5")) return s;
        if (s.startsWith("6")) return s;
        if (s.startsWith("7")) return s;
        if (s.startsWith("8")) return s;
        if (s.startsWith("9")) return s;
        return "'" + s + "'";
    }


    // Semantic action codes:
    public final static int NO_OP=0;
    public final static int LIST=1;
    public final static int DELETE_DATA=2;
    public final static int MODIFY_DATA=3;
    public final static int NONE=0;
    public final static int LESS=1;
    public final static int MORE=2;

    private String show[] = {"show", "list", "display","retrive","pick"};
    private String noise1[] = {"data", "info", "information", "any", "all", "everything"};
    private String where[] = {"where"};
    private String equals[] = {"equals", "contains"};
    private String is[] = {"is"};
    private String equals_is[] = {"equals", "contains", "is"};
    private String after[] = {"after"};
    private String before[] = {"before"};
    private String and[] = {"and"};
    private String or[] = {"or"};
    private String less[] = {"less", "smaller"};
    private String more[] = {"more", "greater", "larger"};
    private String than[] = {"than"};

    private int currentAction=NO_OP;
    private String displayColumnName = null;
    private String searchColumnName = null;
    private String searchString=null;
    private String searchColumnName2 = null;
    private String searchString2=null;
    private String searchColumnName3 = null;
    private String searchString3=null;
    private int conditionMode = 0;
    private String tableName="";

    private SmartDate time_after = null;
    private SmartDate time_before= null;

    private float quantity=-999.0f;
    private int compareMode=NONE;

    private String currentWords[];
    private int currentWordIndex=0;

    private boolean eatWord(String s[]) {
        if (currentWordIndex>=currentWords.length) {
            return false;
        }
        for (int i=0; i<s.length; i++) {
            if (currentWords[currentWordIndex].equalsIgnoreCase(s[i])) {
                currentWordIndex++;
                return true;
            }
        }
        return false;
    }

    private String eatColumnName(String current_column_name) {
        if (currentWordIndex>=currentWords.length) {
            return null;
        }
        // Check for a column name of the form <Table name>.<column name>:
        if (current_column_name!=null) {
            int index = current_column_name.indexOf(".");
            if (index>-1) {
                current_column_name = current_column_name.substring(index+1);
            }
        }
        String ret_col_name=null;
        for (int i=0; i<dbinfo.numTables; i++) {
            for (int j=0; j<dbinfo.columnNames[i].length; j++) {
                if (currentWordIndex>=currentWords.length) break;
                if (currentWords[currentWordIndex].equalsIgnoreCase(dbinfo.columnNames[i][j])) {
                    if (current_column_name!=null &&
                        current_column_name.indexOf(dbinfo.columnNames[i][j]) >-1)
                        continue;
                    currentWordIndex++;

                    temp_col_names[num_temp_col_names++] = dbinfo.columnNames[i][j];

                    if (tableName==null) {
                        tableName=dbinfo.tableNames[i];
                    } else {
                        if (tableName.equalsIgnoreCase(dbinfo.tableNames[i])==false) {
                            tableName=tableName + ", " + dbinfo.tableNames[i];
                        }
                    }
                    if (ret_col_name==null) {
                        ret_col_name=dbinfo.tableNames[i] + "." + dbinfo.columnNames[i][j];
                    } else {
                        if (ret_col_name.equalsIgnoreCase(dbinfo.columnNames[i][j])==false) {
                            ret_col_name=ret_col_name + ", " +
                                dbinfo.tableNames[i] + "." + dbinfo.columnNames[i][j];
                        }
                    }
                }
            }
        }
        return ret_col_name;
    }

    private DBInfo dbinfo;
    private String [] temp_col_names = new String[11];
    private int num_temp_col_names = 0;

}
