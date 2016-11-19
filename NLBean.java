
package nlp;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.*;
import java.util.*;
import java.beans.*;
import java.sql.*;

public class NLBean extends Panel {

    //    Demo database information (you can have up to 4 databases - add your own!)
    private String [] databaseNames = {"jdbc:odbc:nlp","jdbc:odbc:msdsn"};
    private String [] userNames = {"nlp","sa"};
    private String [] passwords = {"nlp","sn"};
    // set up a list of table names for each database:
    private String [] tableLists= {"dept;emp;bonus;student;salgrade;books;a","NodeA;NodeB;NodeC"};
    // (You can add up to three additional databases, but some modification
    // to the method DBInterface.doInit() may be required.)

    protected NLEngine engine = null;

    public NLBean() {
        super();
        engine = new NLEngine();

        //              Set up USER INTERFACE:

        Frame help_frame = new Frame();
	help_frame.setBackground(Color.red);
        help = new Help(help_frame);

        setFont(new Font("Dialog", Font.PLAIN, 12));
        setLayout(null);
	setBackground(Color.orange);
	setForeground(Color.blue);
        Label l1 = new Label("Natural Language Database Access");
        l1.setFont(new Font("Dialog", Font.BOLD, 28));
        add(l1);
        l1.setBounds(2, 1, 600, 34);

        list1 = new java.awt.List(3, false);
        for (int i=0; i<databaseNames.length;i++)
        list1.add(databaseNames[i]);
        list2 = new java.awt.List(3, false);
        list3 = new java.awt.List(3, false);

        add(list1); add(list2); add(list3);
        list1.setBounds(2, 40, 220, 90);
        list2.setBounds(232, 40, 170, 90);
        list3.setBounds(412, 40, 170, 90);
        list1.addMouseListener(new MouseSelect1());
        list2.addMouseListener(new MouseSelect2());
        list3.addMouseListener(new MouseSelect3());

        Button q_button = new Button("Do query");
        q_button.addMouseListener(new MouseQuery());
        add(q_button);  q_button.setBounds(2, 140, 160, 30);
        Button help_button = new Button("Help");
        help_button.addMouseListener(new MouseHelp());
        add(help_button); help_button.setBounds(172, 140, 40, 30);

        Label label22 = new Label("Query:");
        label22.setFont(new Font("Dialog", Font.BOLD, 14));
        add(label22);  label22.setBounds(2, 180, 60, 22);  label22.setVisible(true);

        //        inputText = new SmartTextField("list Salary where EmpName equals Mark", 64);
        inputText = new TextField("list Salary where EmpName equals Mark", 64);
        add(inputText);  inputText.setBounds(80, 180, 500, 22);


        choice = new Choice();
        choiceChanged = false;
        choice.addItem("Examples                                        ");
        for (int i=(examples.length - 1); i>=0; i--) choice.insert(examples[i], 1);
        choice.addItemListener(new ChoiceListener());
        add(choice);  choice.setBounds(2, 210, 582, 25);

        Label label23 = new Label("Generated SQL:");
        label23.setFont(new Font("Dialog", Font.BOLD, 12));
        add(label23);   label23.setBounds(2, 240, 120, 30);
        sqlText = new TextArea("",1,80,TextArea.SCROLLBARS_NONE);
        sqlText.setEditable(false);
        add(sqlText);  sqlText.setBounds(130, 240, 455, 40);

        outputText = new TextArea("Natural Language Processing", 8, 74);
        add(outputText);  outputText.setBounds(2, 285, 582, 150);

        Label l1x = new Label("Natural Language Processing");
        l1x.setFont(new Font("Dialog", Font.BOLD, 16));
        add(l1x);
        l1x.setBounds(5, 442, 537, 19);


        list1_last_selection=-1; list2_last_selection=-1; list3_last_selection=-1;
        setBounds(20, 20, 590, 464);

        for (int i=0; i<synonyms.length; i++) addSynonym(synonyms[i]);

        for (int i=0; i<databaseNames.length; i++) {
            engine.addDB(databaseNames[i], userNames[i], passwords[i], tableLists[i]);
        }
        engine.initDB();
    }

    /**
     * write a string to the output text area on the GUI
     */
    private void putText(String s) {
        outputText.append(s);
     }

    /**
     * if <b>set_flag</b> is false, then return the contents of the input text
     * field; otherwise, set the input text field (from one of the example strings)
     * and return a zero length string.
     */
    synchronized private String inText(String new_val, boolean set_flag) {
        if (set_flag) {
            inputText.setText(new_val);
            return "";
        }
        return inputText.getText();
    }
           
    /** 
     * utility method for getting the input text (either that the user typed
     * in or set from one of the example strings), calling the NLP engine's
     * <b>getSQL</b> method to convert NLP text to SQL, and performing the
     * generated SQL query against to sample database
     */
    synchronized private void query() {
        sqlText.setText("");
        String a_query = inText("", false);
        System.out.println("query(): a_query=" + a_query);
        String sql_query=null;
        if (a_query.startsWith("SELECT") || a_query.startsWith("select")) {
            sql_query = a_query;
        } else {
            engine.parse(a_query);
            sql_query = engine.getSQL();
        }
        if (sql_query==null) {
            System.out.println("No SQL for " + a_query);
            return;
        }
        sqlText.setText(sql_query);
        try {
            putText("Query results:\n");
            String data = engine.getRows(sql_query, databaseNames[0], userNames[0], passwords[0]);
            putText(engine.toEnglish(data) + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSynonym(String def_string) {
        int pos = def_string.indexOf("=");
        String description = def_string.substring(0,pos);
        String column = def_string.substring(pos+1);
        if (engine!=null) {
            engine.addSynonym(column, description);
        }
    }

    // Use an inner classes for mouse event handling:
    class MouseQuery extends MouseAdapter implements Serializable {
        // The magic: access the public query method in the
        // containing class:
        synchronized public void mouseReleased(MouseEvent mevt) {
            query();
        }
    }

    // Use an inner classes for mouse event handling:
    class MouseSelect1 extends MouseAdapter implements Serializable {
        synchronized public void mouseReleased(MouseEvent mevt) {
            if (list1_last_selection != list1.getSelectedIndex()) {
                list1_last_selection = list1.getSelectedIndex();
                String s="";
                if (list1_last_selection >= 0 && list1_last_selection < tableLists.length) {
                    s = tableLists[list1_last_selection];
                }
                System.out.println("s=" + s);
                String temp [] = Util.parseStrings(s);
                list2.removeAll();  list3.removeAll();
                for (int i=0; i<temp.length; i++) list2.addItem(temp[i]);
            }

        }
    }

    // Use an inner classes for mouse event handling:
    class MouseSelect2 extends MouseAdapter implements Serializable {
        synchronized public void mouseReleased(MouseEvent mevt) {
            if (list2_last_selection != list2.getSelectedIndex()) {
                list2_last_selection = list2.getSelectedIndex();
                list3.removeAll();
                String sel1 [] = list1.getSelectedItems();
                if (sel1!=null) {
                    if (sel1.length>0) {
                        String sel2 [] = list2.getSelectedItems();
                        if (sel2!=null) {
                            if (sel2.length>0) {
                                String user=" ";
                                String pass=" ";
                                if (list1_last_selection >= 0 && list1_last_selection < userNames.length) {
                                    user = userNames[list1_last_selection];
				pass = passwords[list1_last_selection];
                                }
                                try {

                                    String cols[] = engine.getColumnNames(sel2[0],
                                                                          sel1[0], user, pass);

                                    if (cols!=null) {
                                        for (int j=0; j<cols.length; j++) {
                                            list3.addItem(cols[j]);
                                        }
                                    }
                                } catch (Exception e) { }

                            }
                        }
                    }
                }
            }
        }
    }


    // Use an inner classes for mouse event handling:
    class MouseSelect3 extends MouseAdapter implements Serializable {
        synchronized public void mouseReleased(MouseEvent mevt) {
            if (list3_last_selection != list3.getSelectedIndex()) {
                System.out.println("TESTING 3rd list selection");
                list3_last_selection = list3.getSelectedIndex();
                String [] sel1x = list1.getSelectedItems();
                if (sel1x!=null) {
                    if (sel1x.length>0) {
                        String sel2 [] = list2.getSelectedItems();
                        String sel3 [] = list3.getSelectedItems();
                        if (sel2!=null && sel3!=null) {
                            if (sel3.length>0) {
                                String user=" ";
                                String pass=" ";
                                if (list1_last_selection >= 0 && list1_last_selection < userNames.length) {
                                    user = userNames[list1_last_selection];
                                    pass = passwords[list1_last_selection];
                                }
                                try {

                                    String r = engine.getRows("SELECT " + sel3[0] + " FROM " + sel2[0],
                                                              sel1x[0],
                                                              user,
                                                              pass);
                                    if (r==null)  return;
                                    putText(r + "\n");
                                } catch (Exception e) { }
                            }
                        }
                    }
                }
            }
        }
    }

    private Choice choice;
    transient private boolean choiceChanged = false;

    class ChoiceListener implements ItemListener {
        public void itemStateChanged(ItemEvent ie) {
            System.out.println("choice menu: " + ie.paramString());
            System.out.println("choice menu: " + (String)ie.getItem());
            String sel = (String)ie.getItem();
            if (sel.equals("Examples"))  return;
            inText(sel, true);
            if (choiceChanged==false) {
                choice.remove(0);
                choiceChanged=true;
            }
            System.out.println("choice menu: <returning>");
        }
    }

    private Help help;

    class MouseHelp extends MouseAdapter implements Serializable {
        public void mouseReleased(MouseEvent mevt) {
            help.setVisible(true);
        }
    }

    public static  void main(String args[]) {
        Frame f = new Frame();
        NLBean sc = new NLBean();
        f.add(sc);
        f.setTitle("Natural Language Processing Version 1.0");
        f.pack();
        f.show();
        f.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
    }

    private TextArea outputText;
    private /* Smart */ TextField inputText;
    private TextArea sqlText;

    private java.awt.List list1, list2, list3;
    private int list1_last_selection, list2_last_selection,  list3_last_selection;

    /**
     * data for specifying a few sample natural language
     * queries to place at the beginning of the 'Example' 'choice' control.
     */
    private String [] examples = {
        "list email address where name equals Mark",
        "list salary where employee name equals Mark",
        "list salary where hire date is after 1993/1/5 and employee name equals Mark",
        "list name, phone number, and email address where name equals Mark",
        "list employee name, salary, and hire date where hire date is after January 10, 1993",
        "list salary where hire date is after January 1, 1993 or employee name equals Carol",
        "list product name where cost is less than $20"
    };

    /**
     * Set up properties for synonyms:
     */
    private String [] synonyms = {
        "employee name=EmpName",
        "hire date=HireDate",
        "phone number=PhoneNumber",
        "email address=Email",
        "product name=productname",
        "products=productname",
        "product=productname"
    };

    //
    //            Inner class to handle text input field:
    //

    transient protected int startWordPos=0, stopWordPos=0;
}
