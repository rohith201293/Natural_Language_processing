
package nlp;

import java.text.*;
import java.util.*;
import java.io.*;

// Utility class to handle dates in fairly arbitrary natural language formats:
// Super KLUDGE, but this works:
class SmartDate {
  private java.util.Date date = null;
  private Calendar calendar = null;
  public SmartDate(String s) {
    System.out.println("\n*****  SmartDate(" + s + ")");
    // Try to create a 'SimpleDate' parse string for this string:
    SimpleDateFormat sdf;
    try {
      if (date == null) {
    sdf = new SimpleDateFormat("MMM d yyyy");
    try {
      date = sdf.parse(s);
    } catch (Exception pe) { date = null; }
      }
      if (date == null) {
    sdf = new SimpleDateFormat("yyyy MM dd hh mm ss");
    try {
      date = sdf.parse(s);
    } catch (Exception pe) { date = null; }
      }
      if (date == null) {
    sdf = new SimpleDateFormat("yyyy mm dd");
    try {
      date = sdf.parse(s);
    } catch (Exception pe) { date = null; }
      }
      if (date == null) {
    sdf = new SimpleDateFormat("yy mm dd");
    try {
      date = sdf.parse(s);
    } catch (Exception pe) { date = null; }
      }
      if (date == null) {
    sdf = new SimpleDateFormat("EEE MMM d yyyy");
    try {
      date = sdf.parse(s);
    } catch (Exception pe) { date = null; }
      }
      if (date == null) {
    sdf = new SimpleDateFormat("EEE MMM d ''yy");
    try {
      date = sdf.parse(s);
    } catch (Exception pe) { date = null; }
      }
      if (date == null) {
    sdf = new SimpleDateFormat("yyy-mm-dd hh:mm:ss");
    try {
      date = sdf.parse(s);
    } catch (Exception pe) { date = null; }
      }
      if (date == null) {
    sdf = new SimpleDateFormat("MMM d ''yy");
    try {
      date = sdf.parse(s);
    } catch (Exception pe) { date = null; }
      }
    } catch (Exception e) {
      System.out.println("Parse error: " + s + ", " + e);
    }
    if (date!=null) {
      calendar = GregorianCalendar.getInstance();
      calendar.setTime(date);
      System.out.print("YEAR: " + calendar.get(Calendar.YEAR) + ". ");
      System.out.print("MONTH: " + calendar.get(Calendar.MONTH) + ". ");
      System.out.print("DATE: " + calendar.get(Calendar.DATE) + ". ");
      System.out.print("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH) + ". ");
      System.out.println("");
    } else {
      System.out.println("Parse error: " + s);
    }
  }
  public int getYear() {
    if (date!=null && calendar!=null) {
      return calendar.get(Calendar.YEAR);
    }
    return 0;
  }
  public int getMonth() {
    if (date!=null && calendar!=null) {
      return calendar.get(Calendar.MONTH);
    }
    return 0;
  }
  public int getDayOfMonth() {
    if (date!=null && calendar!=null) {
      return calendar.get(Calendar.DAY_OF_MONTH);
    }
    return 0;
  }
  public long getMilliseconds() {
    if (date!=null && calendar!=null) {
      return calendar.get(Calendar.MILLISECOND);
    }
    return 0;
  }
  public boolean valid() {
    if (calendar==null) return false;
    if (calendar.get(Calendar.MILLISECOND)!=0) return true;
    if (calendar.get(Calendar.YEAR)!=0) return true;
    if (calendar.get(Calendar.DAY_OF_MONTH)!=0) return true;
    if (calendar.get(Calendar.DATE)!=0) return true;
    return false;
  }
  public String toString() {
    if (valid()==false) return "<not valid date>";
    //return getMonth() + "/" + getDayOfMonth()+1 + "/" + getYear();
    int month = getMonth() + 1;
    return getYear() + "-" + month + "-" + getDayOfMonth();
    //SimpleDateFormat formatter
    //  = new SimpleDateFormat ("mm/dd/yyyy");
    //return formatter.format(date);
  }
}
