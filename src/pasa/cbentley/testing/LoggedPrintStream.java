package pasa.cbentley.testing;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

public class LoggedPrintStream extends PrintStream {

   TestFilterOutputStream os;

   public LoggedPrintStream(TestFilterOutputStream os) {
      super(os);
      this.os = os;
   }

   /**
    * Creates a {@link LoggedPrintStream} 
    * @param toLog Usually System.out
    * @return
    */
   public static LoggedPrintStream create(final PrintStream toLog) {
      try {
         Field f = FilterOutputStream.class.getDeclaredField("out");
         f.setAccessible(true);
         OutputStream psout = (OutputStream) f.get(toLog);
         //we filter all calls coming in by the System.out. They are not forwarded
         TestFilterOutputStream fos = new TestFilterOutputStream(psout);
         LoggedPrintStream lps = new LoggedPrintStream(fos);
         return lps;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   public void resetBuf() {
      os.resetBuf();
   }

   public String getBufferString() {
      return os.getBufferString();
   }

   /**
    *Prints the content
    */
   public void printAll(PrintStream out) {
      os.printAll(out);
   }

   public int getCount() {
      return os.getCount();
   }

   /**
    * 
    * @param std
    * @param buff
    */
   public void setFlags(boolean std, boolean buff) {
      os.setFlags(std, buff);
   }
}
