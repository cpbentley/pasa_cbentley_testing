package pasa.cbentley.testing.engine;

import junit.framework.TestResult;
import pasa.cbentley.core.src4.ctx.ObjectU;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;

public class TestResultStringable extends ObjectU {

   private TestResult tr;

   public TestResultStringable(UCtx uc, TestResult tr) {
      super(uc);
      this.tr = tr;
   }
   
   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, TestResultStringable.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());
      dc.appendVarWithSpace("errorCount", tr.errorCount());
      dc.appendVarWithSpace("failureCount", tr.failureCount());
      dc.appendVarWithSpace("runCount", tr.runCount());
   }

   private void toStringPrivate(Dctx dc) {
      
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, TestResultStringable.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug
   

}
