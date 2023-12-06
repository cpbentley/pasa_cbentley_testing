/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.testing.engine;

import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.interfaces.ITech;

/**
 * Defines 8 of the 32 possibles Test flags.
 * 
 * Sub modules extends this interface 09
 * 
 * @author Charles Bentley
 *
 */
public interface ITechTesting extends ITech {

   /**
    * When set, ignores all other flags and print
    * 
    * Ignores
    * <li> {@link ITechTesting#TEST_FLAG_03_HIDE_OUT_SUCCESSES}
    * <li> {@link ITechTesting#TEST_FLAG_02_NO_DEBUG_SPECIFICS}
    * 
    * Does not work on {@link ITechTesting#TEST_FLAG_08_DEBUG_METHOD_NAMES}
    * <p>
    * By Default, we don't want to force print. This is a temporary flag when we quickly want to override all other flags
    * </p>
    */
   public static final int TEST_FLAG_01_PRINT_ANYWAYS      = 1 << 0;

   /**
    * Ignores debug specific behaviours.
    * 
    * Flag if u are annoyed by text when debugging. Flag is beaten by {@link ITechTesting#TEST_FLAG_01_PRINT_ANYWAYS}
    * <p>
    * By default, we don't want to hide debug statements
    * </p>
    */
   public static final int TEST_FLAG_02_NO_DEBUG_SPECIFICS = 1 << 1;

   /**
    * Hides all the system out of test method, even construction unless the method fails,
    * in which case a Lump Print is done of all collected printouts
    * 
    * <p>
    * By default, we don't want to hide debug statements
    * </p>
    */
   public static final int TEST_FLAG_03_HIDE_OUT_SUCCESSES = 1 << 2;

   /**
    * This flag works when {@link ITechTesting#TEST_FLAG_03_HIDE_OUT_SUCCESSES} is set to true.
    * 
    * Ignores 
    * When Lump Print flag is set, the print out is not done unless a method fails
    * 
    * <p>
    * By default, we want to see debug statements on test failures
    * </p>
    */
   public static final int TEST_FLAG_04_HIDE_OUT_FAILURES  = 1 << 3;

   /**
    * When this flag is set, you want the collected stream from constructor
    * to runBar to be printed.
    * But this action still depends on the {@link ITechTesting#TEST_FLAG_03_HIDE_OUT_SUCCESSES}
    * 
    * So if {@link ITechTesting#TEST_FLAG_03_HIDE_OUT_SUCCESSES} is set, it won't print
    * 
    * <p>
    * By default, we don't want to show most often useless init text of {@link UCtx}
    * </p>
    */
   public static final int TEST_FLAG_05_SHOW_OUT_INIT      = 1 << 4;

   /**
    * 
    */
   public static final int TEST_FLAG_06_                   = 1 << 5;

   /**
    * 
    */
   public static final int TEST_FLAG_07_                   = 1 << 6;

   /**
    * Display a debug statement when every method of {@link TestCaseBentley} class is called
    * 
    * Its might be used to debug {@link TestCaseBentley}
    * 
    * <p>
    * By Default, we don't want them printed. 
    * </p>
    */
   public static final int TEST_FLAG_08_DEBUG_METHOD_NAMES = 1 << 7;
}
