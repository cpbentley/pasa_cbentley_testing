/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.testing.engine;

import pasa.cbentley.core.src4.ctx.ConfigUSettable;
import pasa.cbentley.core.src4.ctx.IConfigU;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;

/**
 * {@link IConfigU} do not have {@link UCtx} in the constructor parameter. It is set later 
 * by {@link UCtx} using {@link IConfigU#toStringSetDebugUCtx(UCtx)}
 * 
 * @author Charles Bentley
 *
 */
public class ConfigUTest extends ConfigUSettable implements IConfigU {


   public ConfigUTest() {
      ToStringSetUsingClassLinks(true);
   }


   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, ConfigUTest.class, 30);
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, ConfigUTest.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {
      
   }
   //#enddebug
   


}
