package nlp;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

//
//        Help Dialog utility class
//
public class Help extends Dialog {
  private Button close;
  private TextArea text;
  private Frame my_frame;

  Help(Frame parent) {
    super(parent, "Help dialog", true);
    my_frame = parent;
    close = new Button("close");
    close.addMouseListener(new MouseClose());

    String str =
      "Help information\n" +
      "-----------------------\n\n" +
      "This natural language parser used in this product\n" +
      "has a large built in vocabulary and examines all\n" +
      "available databases to learn the names of table\n" +
      "column names.\n\n" +
      "Try clicking on the \"Examples\" button to print out\n" +
      "a few natural language queries in the application's\n" +
      "text output area.\n\n" +
      "Type your natural language queries in the input window\n" +
      "located just below the row of command buttons.\n" +
      "Your natural language query will be converted to\n" +
      "SQL and the database(s) accessed -- the results will\n" +
      "appear in the large scrolling text field at the bottom\n" +
      "of the application's window.\n\n" +
      "The three window database viewer is very useful to see\n" +
      "what local databases are available, the tables contained\n" +
      "in each available database, and the column names in each\n" +
      "table.  The left most window shows available databases.\n" +
      "Assuming that at least one database is available, click\n" +
      "on any database in the left most window; you should then\n" +
      "see the middle window fill with the table names for the\n" +
      "database that you just selected.  If you click on any\n" +
      "table name in the middle window, the the right most\n" +
      "window will display the column names for that selected\n" +
      "table.  Remember that the natural language parser knows\n" +
      "all of the column names in all available tables in all\n" +
      "available databases, so feel free to use the column\n" +
      "names in your queries.  If a column name is two distinct\n" +
      "words, enclose them in single quotes (e.g., 'last name').\n\n" +
      "Hints for getting along with the parser\n" +
      "---------------------------------------------------\n\n" +
      "You should remember that the natural langauge parser\n" +
      "does not really understand what you are asking the way\n" +
      "that a human assistant would.  The parser does have a very\n" +
      "large vocabulary of words tagged with possible parts of\n" +
      "speech for each word.  The parser uses many \"semantic\"\n" +
      "templates for specific sentence and command formats; the\n" +
      "parser tries to fit your natural language queries into\n" +
      "each known pattern. This involves trying hundreds of\n" +
      "possible combinations, but this process usually takes\n" +
      "less than a second or two; most delays that you notice\n" +
      "are caused by the database access time.\n\n";

    text = new TextArea(str, 10, 48);
    text.setEditable(false);
    Panel p = new Panel();
    p.add(text); p.add(close);
    my_frame.add(p);
    my_frame.pack();
    myself=this;
  }

  public void setVisible(boolean b) {
    my_frame.setVisible(b);
  }

  private Help myself;  // ha ha

  class MouseClose extends MouseAdapter implements Serializable {
    public void mouseReleased(MouseEvent mevt) {
      myself.setVisible(false);
    }
  }

}
