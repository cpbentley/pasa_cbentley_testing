package pasa.cbentley.testing.engine;

import pasa.cbentley.core.src4.logging.IDLogConfig;
import pasa.cbentley.core.src4.logging.ILogConfigurator;
import pasa.cbentley.core.src4.logging.ITechLvl;

public class LogConfiguratorTestBentleyDef implements ILogConfigurator, ITechLvl {

   public void apply(IDLogConfig log) {

      //log.setClassNegative(UCtx.class, true);

      log.setLevelGlobal(ITechLvl.LVL_05_FINE);

      log.setFlagTag(FLAG_17_PRINT_TEST, true);
      log.setFlagTag(FLAG_08_PRINT_EXCEPTION, true);
      log.setFlagTag(FLAG_25_PRINT_NULL, true);

      log.setFlagTag(FLAG_05_PRINT_UI, false);
      log.setFlagTag(FLAG_06_PRINT_WORK, false);
      log.setFlagTag(FLAG_07_PRINT_EVENT, false);
      log.setFlagTag(FLAG_11_PRINT_COMMANDS, false);
      log.setFlagTag(FLAG_20_PRINT_INIT, false);
   }
}
