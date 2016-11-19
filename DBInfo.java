
package nlp;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.sql.*;

public class DBInfo {
    public DBInfo() {
        numTables=0;
        //tables = new DBTable[10]; // arbitrary maximum
        tableNames = new String[10];
        columnNames = new String[10][];
        databaseNames = new String[10];
        userNames = new String[10];
        passwords = new String[10];
        temp = new String[20];
    }

    public void clearTables() { numTables=0; }

    public void addTable(String a_name, String data_base,
                         String user_name, String a_password,
                         String col_names[])
    {
        if (numTables >= 8) {
            System.out.println("Too many tables");
            System.exit(1);
        }
        tableNames[numTables] = a_name;
        columnNames[numTables] = col_names;
        databaseNames[numTables] = data_base;
        userNames[numTables] = user_name;
        passwords[numTables] = a_password;
        numTables++;
    }

    public void debug() {
        for (int i=0; i<numTables; i++) {
            System.out.print("Table " + tableNames[i] + ": " +
                             databaseNames[i] + ", " +
                             userNames[i] + ": ");
            for (int j=0; j<columnNames[i].length; j++) {
                System.out.print(columnNames[i][j]);
                if (j!=columnNames[i].length-1) System.out.print(", ");
            }
            System.out.println("");
        }
    }

    public boolean isTable(String s) {
        for (int i=0; i<numTables; i++) {
            if (tableNames[i].equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    public boolean isColumn(String s) {
        for (int i=0; i<numTables; i++) {
            for (int j=0; j<columnNames[i].length; j++) {
                if (columnNames[i][j].equalsIgnoreCase(s)) return true;
            }
        }
        return false;
    }

    public String[] findColumnName(String cn) { // return table names
        int count=0;
        for (int i=0; i<numTables; i++) {
            for (int j=0; j<columnNames[i].length; j++) {
                if (cn.equalsIgnoreCase(columnNames[i][j])) {
                    temp[count++] = tableNames[i];
                }
            }
        }
        if (count==0)  return null;
        String ret[] = new String[count];
        for (int i=0; i<count; i++) ret[i] = temp[i];
        return ret;
    }

    //public DBTable tables[] = null;
    public String tableNames[] = null;
    public String columnNames[][] = null;
    public String databaseNames[] = null;
    public String userNames[] = null;
    public String passwords[] = null;

    public int numTables = 0;
    protected String temp[] = null;
}
