/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.testing.engine;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.core.src4.logging.IStringable;

/**
 * Replace {@link System#out}
 * @author Charles Bentley
 *
 */
public class LoggedPrintStream extends PrintStream implements IStringable {

   TestFilterOutputStream os;

   private UCtx           uc;

   public LoggedPrintStream(TestFilterOutputStream os) {
      super(os);
      this.os = os;
   }

   /**
    * Creates a {@link LoggedPrintStream} 
    * @param toLog Usually System.out
    * @return
    */
   public static LoggedPrintStream create(UCtx uc, final PrintStream toLog) {
      try {
         Field f = FilterOutputStream.class.getDeclaredField("out");
         f.setAccessible(true);
         OutputStream psout = (OutputStream) f.get(toLog);
         //we filter all calls coming in by the System.out. They are not forwarded
         TestFilterOutputStream fos = new TestFilterOutputStream(uc, psout);
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

   public void flush() {
      try {
         os.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * Returns the accumulated strings as a big string
    * @return
    */
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

   //#mdebug
   public IDLog toDLog() {
      return toStringGetUCtx().toDLog();
   }

   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx dc) {
      dc.root(this, LoggedPrintStream.class, "@line5");
      toStringPrivate(dc);
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   private void toStringPrivate(Dctx dc) {
      dc.append(getBufferString());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, LoggedPrintStream.class);
      toStringPrivate(dc);
   }

   public void toStringSetUCtx(UCtx uc) {
      this.uc = uc;
   }

   public UCtx toStringGetUCtx() {
      return uc;
   }

   //#enddebug

}
