package pasa.cbentley.testing;

import java.io.PrintStream;
import java.util.StringTokenizer;

import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestResult;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.logging.ITechConfig;
import pasa.cbentley.core.src4.logging.ITechLvl;
import pasa.cbentley.core.src4.utils.BitUtils;

/**
 * Super class where one can configure the System.out calls
 * <br>
 * The most desirable is to show System.out.println output only for test methods that failed.
 * <br>
 * <br>
 * 
 * @author Charles Bentley
 *
 */
public abstract class BentleyTestCase extends TestCase implements IStringable {

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

   public static String debugFlags(int flags) {
      StringBBuilder sb = StringBBuilder.getBig();
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_1_HIDE_SYSTEM_OUT) ? " Hide Sysout" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_2_HIDE_IN_DEBUG) ? " Hide In Debug" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_3_PRINT_ANYWAYS) ? " Print Anyways" : "");
      sb.append(BitUtils.hasFlag(flags, TEST_FLAG_4_DEBUG_METHOD_NAMES) ? " Show Method Names" : "");
      return sb.toString();
   }

   public static String debugResult(TestResult tr) {
      return tr.wasSuccessful() + " Errors=" + tr.errorCount() + " Failures=" + tr.failureCount() + " Run=" + tr.runCount();
   }

   /**
    * Provides info about the current state.
    */
   private TestResult           currentTestResult;

   private boolean              isPrintNotYetDone;

   private Integer              lock             = new Integer(0);

   /**
    * 
    */
   protected LoggedPrintStream  lpsOut;

   /**
    * We have a specific Constructor Stream because we want to be able to switch off
    * constructor logging.
    */
   protected LoggedPrintStream  lpsOutCons;

   public boolean               printAnyways     = false;

   public boolean               printConstructor = true;

   /**
    * Initiliazed with System.out
    */
   private PrintStream          standardOut;

   protected int                testFlags        = 0;

   private AssertionFailedError threadFailure;

   protected UCtx               uc;

   public TestRule              watcher          = new TestWatcher() {
                                                    protected void starting(Description description) {
                                                       System.out.println("Starting test: " + description.getMethodName());
                                                    }
                                                 };

   /**
    * By default, logs are shown for failures only.
    */
   public BentleyTestCase() {
      this(TEST_FLAG_1_HIDE_SYSTEM_OUT);
   }

   /**
    * 
    * @param hide when true, the Junit test system.out calls are not showned when test is successful.
    * The method sets the {@link System#setOut(PrintStream)}.
    * That special log will keep the prints until the end of the method. if there is an error, it will print 
    * it on System.out.
    * 
    */
   public BentleyTestCase(boolean hide) {
      //when you are running into debugging mode, we want to see system out asap
      this(hide ? TEST_FLAG_1_HIDE_SYSTEM_OUT : 0);
   }

   /**
    * 
    * @param testFlags
    */
   public BentleyTestCase(int ptestFlags) {
      this.testFlags = ptestFlags;
      if (standardOut == null) {
         standardOut = System.out;
      }
      uc = new UCtx();
   }

   public void assertEquals(boolean b, Boolean val) {
      assertNotNull(val);
      assertEquals(b, val.booleanValue());
   }

   public void assertEquals(int i, Integer integer) {
      assertNotNull(integer);
      assertEquals(i, integer.intValue());
   }

   /**
    * This method does null checks
    * @param data
    * @param ch
    */
   public void assertEqualsArrayNull(byte[] data, byte[] ch) {
      if (data == null) {
         assertNull(ch);
         return;
      }
      if (ch == null) {
         assertNull(data);
         return;
      }
      assertEquals(data.length, ch.length);
      for (int i = 0; i < ch.length; i++) {
         assertEquals(data[i], ch[i]);
      }
   }

   public void assertEqualsByteArray(byte[] d, byte[] e) {
      assertEquals(d.length, d.length);
      for (int i = 0; i < e.length; i++) {
         assertEquals(d[i], e[i]);
      }
   }

   public void assertEqualsIntArray(int[] d, int[] e) {
      assertEquals(d.length, d.length);
      for (int i = 0; i < e.length; i++) {
         assertEquals((d[i]), e[i]);
      }
   }

   public void assertNotReachable(String message) {
      assertFalse(message, true);
   }

   public void assertStringLineByLine(String str1, String str2) {
      StringTokenizer st1 = new StringTokenizer(str1, "\n");
      StringTokenizer st2 = new StringTokenizer(str2, "\n");
      assertEquals("Number of Lines", st1.countTokens(), st2.countTokens());
      while (st1.hasMoreTokens()) {
         String tok = st1.nextToken();
         if (st2.hasMoreTokens()) {
            String tok2 = st2.nextToken();
            assertEquals(tok, tok2);
         } else {
            assertEquals(tok, "");
         }
      }
   }

   public void assertStringLineByLineNoLineNumberCheck(String str1, String str2) {
      StringTokenizer st1 = new StringTokenizer(str1, "\n");
      StringTokenizer st2 = new StringTokenizer(str2, "\n");
      int lineCount = 1;
      while (st1.hasMoreTokens()) {
         String tok = st1.nextToken();
         if (st2.hasMoreTokens()) {
            String tok2 = st2.nextToken();
            assertEquals("Line " + lineCount, tok, tok2);
         } else {
            assertEquals(tok, "");
         }
         lineCount++;
      }
   }

   private void beforeRunTestResult() {
      boolean isDebug = isRunningDebug();
      if (isDebug && !hasTestFlag(TEST_FLAG_2_HIDE_IN_DEBUG)) {
         testFlags = BitUtils.setFlag(testFlags, TEST_FLAG_1_HIDE_SYSTEM_OUT, false);
      }
      if (BitUtils.hasFlag(testFlags, TEST_FLAG_1_HIDE_SYSTEM_OUT)) {
         switchToHideSysout();
      } else {
         switchToShowSysout();
      }
      isPrintNotYetDone = true;
      if (hasTestFlag(BentleyTestCase.TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
         System.out.println("#MordTestCase#Constructor " + debugFlags(testFlags));
      }
   }

   protected IDLog dlog() {
      return uc.toDLog();
   }

   /**
    * Print the accumulated log buffer to the standard output.
    * <br>
    * <br>
    * This method is called at the end of a testMethod when adding failures or errors.
    * <br>
    * <br>
    * 
    */
   public void doThePrint() {
      //only special print action if
      if (lpsOut != null && isPrintNotYetDone) {
         System.setOut(standardOut); //give back
         String str = lpsOut.getBufferString();

         //eventual sysout 
         standardOut.println(str);

         isPrintNotYetDone = false;
      }
   }

   public boolean hasTestFlag(int flag) {
      return BitUtils.hasFlag(testFlags, flag);
   }

   public boolean isEquals(byte[] d1, byte[] d2) {
      if (d1 == null && d2 == null)
         return true;
      if (d1 == null || d2 == null)
         return false;
      if (d1.length != d2.length)
         return false;
      for (int i = 0; i < d2.length; i++) {
         if (d2[i] != d1[i]) {
            return false;
         }
      }
      return true;
   }

   public boolean isRunningDebug() {
      return java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

   }

   public void lockRelease(String message) {
      synchronized (lock) {
         //#debug
         toDLog().pTest(message, null, BentleyTestCase.class, "lockRelease", ITechLvl.LVL_04_FINER, true);
         lock.notifyAll();
      }
   }

   public void lockWait(long millis, String message) {
      synchronized (lock) {
         //check if we have an assertion
         if (threadFailure != null) {
            throw threadFailure;
         }
         try {
            //#debug
            toDLog().pTest(message, null, BentleyTestCase.class, "lockWait", ITechLvl.LVL_04_FINER, true);
            lock.wait(millis);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }

         if (threadFailure != null) {
            throw threadFailure;
         }
      }
   }

   public void logPrint(int num, String... str) {
      StringBBuilder sb = new StringBBuilder();
      sb.append(num);
      for (int i = 0; i < str.length; i++) {
         sb.tab();
         sb.append(str[i]);
      }
      toDLog().pTest(sb.toString(), null, getClass(), "logPrint");
   }

   public void logPrint(IStringable o) {
      toDLog().pTest("", o, getClass(), "logPrint");
   }

   public void logPrint(String str) {
      toDLog().pTest(str, null, getClass(), "logPrint");
   }

   public void printTest(String msg) {
      logPrint(msg);
   }

   /**
    * TODO find a way to get JUNIt method name. not easy.
    */
   public void run(TestResult tr) {
      //very first set up
      beforeRunTestResult();

      if (hasTestFlag(BentleyTestCase.TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
         System.out.println("#MordTestCase#runTestResult " + debugResult(tr));
      }
      if (lpsOut != null) {
         lpsOut.resetBuf();
      }
      if (currentTestResult == null) {
         currentTestResult = tr;
         //currentTr.addListener(new TG());
      }
      super.run(tr);

   }

   /**
    * Overriden in case you may want to use tearDownNoError
    */
   public void runBare() throws Throwable {
      if (hasTestFlag(BentleyTestCase.TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
         System.out.println("#MordTestCase#runBare");
      }

      try {
         super.runBare();
      } catch (Throwable e) {
         tearDownError();
         //e.printStackTrace();
         throw e;
      }

      tearDownNoError();
   }

   public void setEnableThreadName(boolean b) {
      dlog().getDefault().getConfig().setFlagFormat(ITechConfig.CONFIG_FLAG_04_SHOW_THREAD, b);
   }

   public void setTestFlag(int flag, boolean v) {
      testFlags = BitUtils.setFlag(testFlags, flag, v);
   }

   /**
    * Called during each start after runBare.
    * <br>
    * Creates a {@link LoggedPrintStream} from the settings of the test method.
    * <br>
    * Resets the print flag
    */
   public void setUp() {
      threadFailure = null;
      if (hasTestFlag(TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
         System.out.println("#MordTestCase#setUp");
      }
      //      if (hasTestFlag(TEST_FLAG_1_HIDE_SYSTEM_OUT)) {
      //         lpsOut = LoggedPrintStream.create(first);
      //         setBufferModeMode();
      //         lpsOut.resetBuf();
      //         isPrintNotYetDone = true;
      //      }
      setupAbstract();
   }

   /**
    * Called at the end of {@link TestCase#setUp}.
    * <br>
    * <br>
    * Replaces the set up method.
    */
   public abstract void setupAbstract();

   public void sleep(long millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   /**
    * 
    */
   private void switchToHideSysout() {
      setTestFlag(TEST_FLAG_1_HIDE_SYSTEM_OUT, true);
      lpsOut = LoggedPrintStream.create(standardOut);
      System.setOut(lpsOut);
      isPrintNotYetDone = true;
   }

   private void switchToShowSysout() {
      //if already hidden
      setTestFlag(TEST_FLAG_1_HIDE_SYSTEM_OUT, false);
      System.setOut(standardOut);
   }

   /**
    * Called by the user in its test method or setup to force
    */
   protected void switchToShowSysoutUser(boolean v) {
      if (v) {
         if (hasTestFlag(TEST_FLAG_1_HIDE_SYSTEM_OUT)) {
            //print the stored data
            doThePrint();
         }
         //show
         switchToShowSysout();
      } else {
         switchToHideSysout();
      }
   }

   /**
    * 
    */
   public void tearDown() {
      if (hasTestFlag(BentleyTestCase.TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
         System.out.println("#MordTestCase#tearDown");
      }
      if (currentTestResult != null) {
         //System.out.println();
      }
      //System.setOut(first);

   }

   private void tearDownError() {
      if (hasTestFlag(BentleyTestCase.TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
         System.out.println("#MordTestCase#tearDownError");
      }
      doThePrint();
   }

   /**
    * Not Used anymore
    */
   private void tearDownNoError() {
      if (hasTestFlag(BentleyTestCase.TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
         System.out.println("#MordTestCase#tearDownNoError");
      }
   }

   /**
    * Test equality in a thread that is not the JUnit thread.
    * It assertion fails, 
    * @param ex
    * @param d
    */
   public void threadAssertEquals(int ex, int d) {
      try {
         assertEquals(ex, d);
      } catch (AssertionFailedError e) {
         //notify waiting thread and throw exception
         threadFailure = e;
         lockRelease("Assertion Failure In Thread.");
         throw threadFailure;
      }
   }

   public IDLog toDLog() {
      return uc.toDLog();
   }

   //#mdebug
   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx dc) {
      dc.root(this, "BentleyTestCase");
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "BentleyTestCase");
   }

   public UCtx toStringGetUCtx() {
      return uc;
   }

   /**
    * 
    */
   public void waitLock() {
      synchronized (lock) {
         try {
            lock.wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   //#enddebug

}
