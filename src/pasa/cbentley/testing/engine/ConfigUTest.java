package pasa.cbentley.testing.engine;

import pasa.cbentley.core.src4.ctx.ConfigUSettable;
import pasa.cbentley.core.src4.ctx.IConfigU;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;

public class ConfigUTest extends ConfigUSettable implements IConfigU {

   //#debug
   private UCtx uc;

   public boolean isEraseSettings() {
      return false;
   }

   public boolean isEraseSettingsAll() {
      return false;
   }

   public boolean isHardcoded() {
      return false;
   }

   public boolean isIgnoreSettings() {
      return false;
   }

   public boolean isIgnoreSettingsAll() {
      return false;
   }

   public void toStringSetDebugUCtx(UCtx uc) {
      this.uc = uc;
   }

   //#mdebug
   public IDLog toDLog() {
      return toStringGetUCtx().toDLog();
   }

   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx dc) {
      dc.root(this, "ConfigUTest");
      toStringPrivate(dc);
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "ConfigUTest");
      toStringPrivate(dc);
   }

   public UCtx toStringGetUCtx() {
      return uc;
   }

   public boolean isForceExceptions() {
      return false;
   }

   public boolean toStringIsUsingClassLinks() {
      return false;
   }

   //#enddebug

}
