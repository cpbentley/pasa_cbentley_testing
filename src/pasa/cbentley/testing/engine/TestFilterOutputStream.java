package pasa.cbentley.testing.engine;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.helpers.StringBBuilder;

/**
 * 
 * @author Charles Bentley
 *
 */
public class TestFilterOutputStream extends FilterOutputStream {

   private StringBBuilder buf;

   boolean                sendToBuffer = true;

   /**
    * Write output data to a stream 
    */
   ByteArrayOutputStream  baos;

   boolean                sendToStdOut = false;

   protected final UCtx   uc;

   /**
    * 
    * @param uc 
    * @param os {@link OutputStream} which will be printed to when sendToStdOut is true
    */
   public TestFilterOutputStream(UCtx uc, OutputStream os) {
      super(os);
      this.uc = uc;
      this.buf = new StringBBuilder(uc, 3600);
      baos = new ByteArrayOutputStream();
   }

   public void write(int b) throws IOException {
      if (sendToBuffer) {
         char cb = (char) b;
         buf.append(cb);
         baos.write(b);
      }
      if (sendToStdOut) {
         super.write(b);
      }
   }

   public void resetBuf() {
      buf.reset();
   }

   private int count = 0;

   /**
    *Prints the content
    */
   public void printAll(PrintStream out) {
      String str = buf.toString();
      count += str.length();
      out.print(str);
   }

   public int getCount() {
      return count;
   }

   public void setFlags(boolean std, boolean buff) {
      sendToStdOut = std;
      sendToBuffer = buff;
   }

   public String getBufferString() {
      StringBBuilder sb = new StringBBuilder(uc);
      try {
         byte[] data = baos.toByteArray();
         ByteArrayInputStream bi = new ByteArrayInputStream(data);
         BufferedReader br = new BufferedReader(new InputStreamReader(bi));
         String line = br.readLine();
         while (line != null) {
            sb.append(line);
            sb.append("\r\n");
            line = br.readLine();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return sb.toString();
   }
}
