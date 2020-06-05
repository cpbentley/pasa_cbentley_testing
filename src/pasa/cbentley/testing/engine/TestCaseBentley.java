package pasa.cbentley.testing.engine;

import java.io.PrintStream;
import java.util.StringTokenizer;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestResult;
import pasa.cbentley.core.src4.ctx.IConfigU;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.interfaces.IInputStreamFactory;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.core.src4.logging.IDLogConfig;
import pasa.cbentley.core.src4.logging.ILogConfigurator;
import pasa.cbentley.core.src4.logging.ILogEntryAppender;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.logging.ITechConfig;
import pasa.cbentley.core.src4.logging.ITechLvl;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.testing.ctx.TestCtx;

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
public abstract class TestCaseBentley extends TestCase implements IStringable, ITechTesting {

   public static String debugResult(TestResult tr) {
      return tr.wasSuccessful() + " Errors=" + tr.errorCount() + " Failures=" + tr.failureCount() + " Run=" + tr.runCount();
   }

   /**
    * Provides info about the current state.
    */
   private TestResult currentTestResult;

   private boolean    isPrintNotYetDone;

   private Integer    lock = new Integer(0);

   public String toStringColor(int c) {
      return "(" + ((c >> 24) & 0xFF) + "," + ((c >> 16) & 0xFF) + "," + ((c >> 8) & 0xFF) + "," + (c & 0xFF) + ")";
   }

   /**
    * 
    */
   protected LoggedPrintStream     lpsOut;

   /**
    * We have a specific Constructor Stream because we want to be able to switch off
    * constructor logging.
    */
   protected LoggedPrintStream     lpsOutCons;

   private int                     numLockRelease;

   public boolean                  printAnyways     = false;

   public boolean                  printConstructor = true;

   /**
    * Initiliazed with System.out
    */
   private PrintStream             standardOut;

   private AssertionFailedError    threadFailure;

   protected UCtx                  uc;

   /**
    * Cannot be final because it is set externally nu {@link TestCaseBentley#setTestCtx(TestCtx)}
    */
   protected TestCtx               tc;

   private InputStreamFactoryJUnit inputStreamFac;

   private boolean isSetup;

   /**
    * By default, logs are shown for failures only.
    */
   public TestCaseBentley() {
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
   public TestCaseBentley(boolean hide) {
      //when you are running into debugging mode, we want to see system out asap
      this(hide ? TEST_FLAG_1_HIDE_SYSTEM_OUT : 0);
   }

   /**
    * Create a default {@link TestCtx} with default hard coded flags.
    * 
    * In a {@link TestSuiteBentley}, you can create you own {@link TestCtx} that will replace the one
    * created here.
    * 
    * For sub classes, the right test ctx must be created in a constructor
    * 
    * @param testFlags
    */
   public TestCaseBentley(int ptestFlags) {
      if (standardOut == null) {
         standardOut = System.out;
      }
      IConfigU configu = createConfigU();
      if (configu == null) {
         uc = new UCtx();
      } else {
         uc = new UCtx(configu);
      }
      tc = createTestCtx();
   }

   public IInputStreamFactory getInputStreamFactory() {
      if (inputStreamFac == null) {
         inputStreamFac = new InputStreamFactoryJUnit(tc, this);
      }
      return inputStreamFac;
   }

   protected IConfigU createConfigU() {
      return new ConfigUTest();
   }

   /**
    * Overriding class may want to create a specialized {@link TestCtx}.
    * 
    * This method is called in the constructor!
    * @return
    */
   protected TestCtx createTestCtx() {
      return new TestCtx(uc);
   }

   public void setFlagHideSystemOutTrue() {
      tc.setTestFlag(TEST_FLAG_1_HIDE_SYSTEM_OUT, true);
   }

   public void setFlagHideSystemOutFalse() {
      tc.setTestFlag(TEST_FLAG_1_HIDE_SYSTEM_OUT, false);
   }

   public void assertEquals(boolean b, Boolean val) {
      assertNotNull(val);
      assertEquals(b, val.booleanValue());
   }

   public void assertEquals(int i, Integer integer) {
      assertNotNull(integer);
      assertEquals(i, integer.intValue());
   }

   public void assertNotSameReference(Object o1, Object o2) {
      assertEquals(true, o1 != o2);
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

   /**
    * Called by Suites when setting custom test context for the tests.
    * <br>
    * <br>
    * May provide various different context configurations. etc.
    * 
    * Cannot be set once setup method has been called.
    * @param tc cannot be null
    */
   public void setTestCtx(TestCtx tc) {
      if(tc == null) {
         throw new NullPointerException();
      }
      if(isSetup) {
         throw new IllegalStateException("Cannot set TestCtx once setup has been called");
      }
      this.tc = tc;
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

   public void assertNotNullThread(Object o) {
      try {
         assertNotNull(o);
      } catch (AssertionFailedError e) {
         //notify waiting thread and throw exception
         threadFailure = e;
         lockRelease("Assertion Failure In Thread.");
         throw threadFailure;
      }
   }

   public void assertNotReachable(String message) {
      assertFalse(message, true);
   }

   public void assertNotReachableThread(String message) {
      try {
         assertFalse(message, true);
      } catch (AssertionFailedError e) {
         //notify waiting thread and throw exception
         threadFailure = e;
         lockRelease("Assertion Failure In Thread.");
         throw threadFailure;
      }
   }

   public void assertReachable() {
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

   public void assertStringLineByLineDebug(String str1, String str2) {
      StringTokenizer st1 = new StringTokenizer(str1, "\n");
      StringTokenizer st2 = new StringTokenizer(str2, "\n");
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
         setFlagHideSystemOutFalse();
      }
      if (tc.hasTestFlag(TEST_FLAG_1_HIDE_SYSTEM_OUT)) {
         switchToHideSysout();
      } else {
         switchToShowSysout();
      }
      isPrintNotYetDone = true;

      if (hasTestFlag(TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
         System.out.println("#MordTestCase#Constructor " + tc.debugFlags());
      }
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

   public void execute(Runnable... runs) {
      for (Runnable run : runs) {
         new Thread(run).start();
      }
   }

   public UCtx getUC() {
      return uc;
   }

   public boolean hasTestFlag(int flag) {
      return tc.hasTestFlag(flag);
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

   /**
    * Tells to release the lock if the number of release if enough
    * @param message
    */
   public void lockRelease(String message) {
      synchronized (lock) {
         numLockRelease -= 1;
         if (numLockRelease <= 0) {
            //#debug
            toDLog().pTest(message, null, TestCaseBentley.class, "lockRelease", ITechLvl.LVL_04_FINER, true);
            lock.notifyAll();
         }
      }
   }

   /**
    * Waits for millis until a call to {@link TestCaseBentley#lockRelease(String)}
    * <br>
    * throws any assert failure set to {@link TestCaseBentley#threadFailure}
    * when a thread fails an assert
    * @param millis
    * @param message
    */
   public void lockWait(long millis, String message) {
      synchronized (lock) {
         //check if we have an assertion
         if (threadFailure != null) {
            throw threadFailure;
         }
         try {
            //#debug
            toDLog().pTest(message, null, TestCaseBentley.class, "lockWait", ITechLvl.LVL_04_FINER, true);
            lock.wait(millis);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }

         if (threadFailure != null) {
            throw threadFailure;
         }
      }
   }

   public void assertEqualsToStringColor(int color1, int color2) {
      assertEquals(toStringColor(color1), toStringColor(color2));
   }

   /**
    * Logs the strings along with their id num. 
    * @param num used to differentiate from competing threads log statements
    * @param str
    */
   public void logPrint(int num, String... str) {
      StringBBuilder sb = new StringBBuilder(uc);
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

   /**
    * TODO find a way to get Junit method name. not easy.
    */
   public void run(TestResult tr) {
      //very first set up
      beforeRunTestResult();

      if (hasTestFlag(TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
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
      if (hasTestFlag(TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
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
      toDLog().getDefault().getConfig().setFlagFormat(ITechConfig.CONFIG_FLAG_04_SHOW_THREAD, b);
   }

   protected void setNunLockReleased(int num) {
      numLockRelease = num;
   }

   public void setTestFlag(int flag, boolean v) {
      tc.setTestFlag(flag, v);
   }

   /**
    * Called during each start after runBare.
    * <br>
    * Creates a {@link LoggedPrintStream} from the settings of the test method.
    * <br>
    * Resets the print flag
    * 
    * Create a default {@link TestCtx} if none was set externally with {@link TestCaseBentley#setTestCtx(TestCtx)}
    */
   public void setUp() {
      isSetup = true;
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
      setupLogger();
      setupAbstract();
   }

   /**
    * Override for a different configurator
    * @return
    */
   protected ILogConfigurator createLogConfigurator() {
      return new LogConfiguratorJUnit();
   }

   protected void setupLogger() {
      ILogConfigurator logConfigurator = this.createLogConfigurator();
      //what if several logs? the launcher implementation must deal with it specifically
      ILogEntryAppender appender = uc.toDLog().getDefault();
      IDLogConfig config = appender.getConfig();
      logConfigurator.apply(config);
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
      lpsOut = LoggedPrintStream.create(uc, standardOut);
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
      if (hasTestFlag(TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
         System.out.println("#MordTestCase#tearDown");
      }
      if (currentTestResult != null) {
         //System.out.println();
      }
      //System.setOut(first);

   }

   private void tearDownError() {
      if (hasTestFlag(TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
         System.out.println("#MordTestCase#tearDownError");
      }
      doThePrint();
   }

   /**
    * Not Used anymore
    */
   private void tearDownNoError() {
      if (hasTestFlag(TEST_FLAG_4_DEBUG_METHOD_NAMES)) {
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
