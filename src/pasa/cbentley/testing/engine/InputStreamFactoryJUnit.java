/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.testing.engine;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import pasa.cbentley.core.src4.interfaces.IInputStreamFactory;
import pasa.cbentley.testing.ctx.TestCtx;

public class InputStreamFactoryJUnit implements IInputStreamFactory {

   protected final TestCtx tc;
   private TestCase testCase;

   public InputStreamFactoryJUnit(TestCtx tc, TestCase testCase) {
      this.tc = tc;
      this.testCase = testCase;
      
   }
   
   public InputStream getResourceAsStream(String name) throws IOException {
      
      return testCase.getClass().getResourceAsStream(name);
   }

}
