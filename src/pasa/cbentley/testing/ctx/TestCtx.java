/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.testing.ctx;

import pasa.cbentley.core.src4.ctx.ACtx;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.testing.engine.ITechTesting;
import pasa.cbentley.testing.engine.TestCaseBentley;

/**
 * Any configuration done in the constructor of a {@link TestCaseBentley} can be ignored when {@link TestCtx}
 * flags
 * 
 * {@link ITechTesting}
 * 
 * Provides the basis for the tests
 * 
 * @author Charles Bentley
 *
 */
public class TestCtx extends ACtx implements ITechTesting {

   protected TestCaseBentley testCaseBentley;

   private int               testFlags;

   public TestCtx(UCtx uc) {
      super(uc);

      //#debug
      toDLog().pInit("Created", this, TestCtx.class, "TestCtx", LVL_05_FINE, true);
   }

   public String debugFlags() {
      return debugFlags(testFlags);
   }

   public String debugFlags(int flags) {
      StringBBuilder sb = new StringBBuilder(uc);
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_01_PRINT_ANYWAYS) ? "Print_Anyways" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_02_NO_DEBUG_SPECIFICS) ? " No_Debug_Specifics" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_03_HIDE_OUT_SUCCESSES) ? " Hide_Sysout_on_Successes" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_04_HIDE_OUT_FAILURES) ? " Hide_Sysout_on_Failures" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_05_SHOW_OUT_INIT) ? " Show_Constructor_Statements" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_06_TEAR_DOWN_RESULT) ? " Show_TearDown_Result" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_08_DEBUG_METHOD_NAMES) ? " Show_Method_Names" : "");
      return sb.toString();
   }

   public int getCtxID() {
      return 21;
   }

   public int getTestFlags() {
      return this.testFlags;
   }

   public boolean hasTestFlag(int flag) {
      return BitUtils.hasFlag(testFlags, flag);
   }

   public void setTestCase(TestCaseBentley testCaseBentley) {
      this.testCaseBentley = testCaseBentley;
   }

   public void setTestFlag(int flag, boolean v) {
      testFlags = BitUtils.setFlag(testFlags, flag, v);
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, TestCtx.class, "@line65");
      toStringPrivate(dc);
      super.toString(dc.sup());

      dc.nlLvl(testCaseBentley, "testCaseBentley");
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, TestCtx.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {
      String str = debugFlags(testFlags);
      dc.appendWithSpace(str);
   }

   //#enddebug

}
