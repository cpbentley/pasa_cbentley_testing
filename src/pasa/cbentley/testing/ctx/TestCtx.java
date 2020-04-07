package pasa.cbentley.testing.ctx;

import pasa.cbentley.core.src4.ctx.ACtx;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.testing.engine.ITechTesting;
import pasa.cbentley.testing.engine.TestCaseBentley;

/**
 * Any configuration done in the constructor of a {@link TestCaseBentley} can be ignored when {@link TestCtx}
 * flags
 * 
 * {@link ITechTesting}
 * 
 * @author Charles Bentley
 *
 */
public class TestCtx extends ACtx implements ITechTesting {

   private int testFlags;

   public TestCtx(UCtx uc) {
      super(uc);
   }

   public int getTestFlags() {
      return this.testFlags;
   }

   public void setTestFlag(int flag, boolean v) {
      testFlags = BitUtils.setFlag(testFlags, flag, v);
   }

   public boolean hasTestFlag(int flag) {
      return BitUtils.hasFlag(testFlags, flag);
   }

   public String debugFlags() {
      return debugFlags(testFlags);
   }

   public String debugFlags(int flags) {
      StringBBuilder sb = new StringBBuilder(uc);
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_1_HIDE_SYSTEM_OUT) ? " Hide Sysout" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_2_HIDE_IN_DEBUG) ? " Hide In Debug" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_3_PRINT_ANYWAYS) ? " Print Anyways" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_4_DEBUG_METHOD_NAMES) ? " Show Method Names" : "");
      return sb.toString();
   }
}
