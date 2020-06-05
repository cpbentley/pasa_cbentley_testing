/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.testing.engine;

import pasa.cbentley.core.src4.logging.IDLogConfig;
import pasa.cbentley.core.src4.logging.ILogConfigurator;
import pasa.cbentley.core.src4.logging.ITechLvl;

public class LogConfiguratorJUnit implements ILogConfigurator {

   public LogConfiguratorJUnit() {
   }

   public void apply(IDLogConfig log) {

      log.setLevelGlobal(ITechLvl.LVL_03_FINEST);

      log.setFlagTag(FLAG_17_PRINT_TEST, true);
      log.setFlagTag(FLAG_08_PRINT_EXCEPTION, true);
      log.setFlagTag(FLAG_25_PRINT_NULL, true);

   }

}
