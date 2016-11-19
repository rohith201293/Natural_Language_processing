
package nlp;

import java.sql.*;

public class DBInterface {

    // url=database driver name + database name
    static public String query(String a_query, String url,
                               String user_id, String passwd) {
        String results = "";
        System.out.println("++ query: " + a_query);

        try {

            doInit();

            // Connect to the JDBC driver:
            Connection con =
                DriverManager.getConnection(url, user_id, passwd);
            checkConnection(con.getWarnings()); // connection OK?

            Statement stmt = con.createStatement();

            // Submit a query:
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery(a_query);
            } catch (SQLException se) {
                System.out.println("NO result set");
            }

            if (rs != null) {
                // Display all columns and rows from the result set
                results = resultSetToString(rs);
 
                // Close the result set
                rs.close();
            }

            // Close the statement
            stmt.close();

            // Close the connection
            con.close();
        }
        catch (SQLException ex) {
            while (ex != null) {
                System.out.println("SQL error message:  " + ex.getMessage());
                ex = ex.getNextException();
                System.out.println("");
                results = ex.getMessage();
            }
        }
        catch (java.lang.Exception ex) {
            ex.printStackTrace();
            results = ex.getMessage();
        }
        return results;
    }

    static public String update(String a_query, String url,
                                String user_id, String passwd) {
        System.out.println("\n\n++++++++++++++++++++++\nUpdate: " +
                           a_query + "\n");
        String results = "";

        try {

            doInit();

            // Find a driver:
            Connection con =
                DriverManager.getConnection(url, user_id, passwd);
            checkConnection(con.getWarnings()); // connection OK?

            Statement stmt = con.createStatement();

            try {
                int n = stmt.executeUpdate(a_query);
                results = "UPDATE affected " + n + " rows.";
            } catch (SQLException se) {
                results = "UPDATE had no effect on database.";
            }

            // Close the statement
            stmt.close();

            // Close the connection
            con.close();

        }
        catch (SQLException ex) {
            while (ex != null) {
                System.out.println("SQL error message:  " + ex.getMessage());
                ex = ex.getNextException();
                System.out.println("");
                results = ex.getMessage();
            }
        }
        catch (java.lang.Exception ex) {
            ex.printStackTrace();
            results = ex.getMessage();
        }
        System.out.println("DBInterface.Select() results: " + results);
        return results;
    }

    static private boolean needToInit = true;

    /**
     * common JDBC setup code used in both the query, update and getColumnNames methods.
     */
    static private void doInit() {
        if (needToInit) {
            try {
                // Load the JDBC driver
             //   Class.forName("jdbc.idbDriver");
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		
                //DriverManager.setLogStream(System.out); // uncomment for debug printout
            } catch (Exception e) {
                System.out.println("Could not set up JDBC: " + e);
            }
            needToInit=false;
        }
    }


    static private String resultSetToString(ResultSet rs) throws SQLException {
        int i;
        StringBuffer outputText = new StringBuffer();
        int numCols = rs.getMetaData().getColumnCount();
        boolean more = rs.next();
        while (more) {
            for (i=1; i<=numCols; i++) {
                if (i > 1) outputText.append(",");
                outputText.append(rs.getString(i));
            }
            if (i!=numCols)  outputText.append("\n");
            more = rs.next();
        }
        return new String(outputText);
    }

    static public String [] getColumnNames(String table_name, String url, String user_id, String passwd) {
        String ret [] = null;
        try {

            doInit();

            // Connect to the JDBC driver:
            Connection con =
                DriverManager.getConnection(url, user_id, passwd);
            checkConnection(con.getWarnings()); // connection OK?

            Statement stmt = con.createStatement();

            // Submit a query:
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery("select * from " + table_name);
            } catch (SQLException se) {
                System.out.println("NO result set");
                return null;  // error return
            }

            StringBuffer outputText = new StringBuffer();
            ResultSetMetaData meta = rs.getMetaData();
            int numCols = meta.getColumnCount();
            ret = new String[numCols];
            for (int i=0; i<numCols; i++) {
                ret[i] = meta.getColumnLabel(i + 1);
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Error getting column names: " + e);
            return null;
        }
        return ret;
    }


    static private boolean checkConnection(SQLWarning warning)
        throws SQLException  {
        boolean ret = false;
        if (warning != null) {
            System.out.println("\n *** Warning ***\n");
            ret = true;
            while (warning != null) {
                System.out.println("Message " + warning.getMessage());
                warning = warning.getNextWarning();
            }
        }
        return ret;
    }
}
