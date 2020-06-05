/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.testing.engine;

import pasa.cbentley.core.src4.interfaces.ITech;

public interface ITechTesting extends ITech {
   /**
    * Hides all the system out of test method, even construction
    */
   public static final int TEST_FLAG_1_HIDE_SYSTEM_OUT    = 1;

   /**
    * When set to true, hides the Sysout in debug mode in all cases.
    */
   public static final int TEST_FLAG_2_HIDE_IN_DEBUG      = 2;

   /**
    * 
    */
   public static final int TEST_FLAG_3_PRINT_ANYWAYS      = 4;

   public static final int TEST_FLAG_4_DEBUG_METHOD_NAMES = 8;
}
