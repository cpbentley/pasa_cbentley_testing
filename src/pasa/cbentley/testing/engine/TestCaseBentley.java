/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.testing.engine;

import java.io.PrintStream;
import java.util.StringTokenizer;

import org.junit.Rule;
import org.junit.rules.TestName;

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
   private TestResult              currentTestResult;

   /**
    * Sugar for easy false
    */
   protected final boolean         f    = false;

   private InputStreamFactoryJUnit inputStreamFac;

   /**
    * Flag telling us current sysout is the standard output
    */
   private boolean                 isCurrentOutStandard;

   private boolean                 isSetup;

   private Integer                 lock = new Integer(0);

   /**
    * 
    */
   protected LoggedPrintStream     lpsOutTest;

   /**
    * We have a specific Constructor Stream because we want to be able to switch off
    * constructor logging.
    */
   protected LoggedPrintStream     lpsOutConstructor;

   private int                     numLockRelease;

   /**
    * Initiliazed with System.out
    */
   private static PrintStream      standardOut;

   /**
    * Sugar for easy true
    */
   protected final boolean         t    = true;

   /**
    * Cannot be final because it is set externally nu {@link TestCaseBentley#setTestCtx(TestCtx)}
    */
   protected TestCtx               tc;

   private AssertionFailedError    threadFailure;

   protected UCtx                  uc;

   private boolean                 isDebug;

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
      throw new RuntimeException();
   }

   /**
    * JUnit constructors are called twice when running a single test method.
    * Create a default {@link TestCtx} with default hard coded flags.
    * 
    * In a {@link TestSuiteBentley}, you can create you own {@link TestCtx} that will replace the one
    * created here.
    * 
    * For sub classes, the right test ctx must be created in a constructor
    * 
    * @param testFlags
    */
   public TestCaseBentley() {
      //save original System.out
      //you can only trust System.out on the first run so use a static private field
      if (standardOut == null) {
         standardOut = System.out;
      }
      //System.out.println("hashCode of System.out = " + standardOut.hashCode() + " ");
      //this constructor is instantiated twice for a single method test
      //no way to avoid.. simply hugh loggers 
      lpsOutConstructor = LoggedPrintStream.create(uc, standardOut);
      System.setOut(lpsOutConstructor);
      isCurrentOutStandard = false;
      //print to the init Printstream.
      //so in order to completely white out the system out 

      //we switch out asap

      //System.out.println("TestCaseBentley");
      IConfigU configu = createConfigU();
      if (configu == null) {
         uc = new UCtx();
      } else {
         uc = new UCtx(configu);
      }

      //#debug
      lpsOutConstructor.toStringSetUCtx(uc);

      tc = createTestCtx();
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

   public void assertEqualsToStringColor(int color1, int color2) {
      assertEquals(toStringColor(color1), toStringColor(color2));
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

   public void assertNotSameReference(Object o1, Object o2) {
      assertEquals(true, o1 != o2);
   }

   public void assertReachable() {
   }

   public void assertReachableNot(String message) {
      this.assertNotReachable(message);
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

   /**
    * Print the accumulated log buffer to the standard output.
    * <br>
    * <br>
    * This method is called at the end of a testMethod when adding failures or errors.
    * <br>
    * <br>
    * 
    */
   public void printTestStream() {
      if (hasTestFlag(TEST_FLAG_08_DEBUG_METHOD_NAMES)) {
         System.out.println("#TestCaseBentley#printTestStream");
      }
      //only special print action if
      if (lpsOutTest != null) {
         if (!isCurrentOutStandard) {
            System.setOut(standardOut); //give back
            isCurrentOutStandard = true;
         }
         String str = lpsOutTest.getBufferString();
         standardOut.println(str);
         lpsOutTest.resetBuf();
      }
   }

   public void printConstructorStream() {
      if (hasTestFlag(TEST_FLAG_08_DEBUG_METHOD_NAMES)) {
         System.out.println("#TestCaseBentley#printConstructorStream");
      }
      if (lpsOutConstructor != null) {

         if (!isCurrentOutStandard) {
            //if not already
            System.setOut(standardOut); //give back
            isCurrentOutStandard = true;
         }
         String str = lpsOutConstructor.getBufferString();
         standardOut.println(str);
         lpsOutConstructor.resetBuf();
      }
   }

   public void execute(Runnable... runs) {
      for (Runnable run : runs) {
         new Thread(run).start();
      }
   }

   public IInputStreamFactory getInputStreamFactory() {
      if (inputStreamFac == null) {
         inputStreamFac = new InputStreamFactoryJUnit(tc, this);
      }
      return inputStreamFac;
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
    * 
    */
   public void lockWait() {
      synchronized (lock) {
         try {
            lock.wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
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
    * 
    */
   public void run(TestResult tr) {
      if (hasTestFlag(TEST_FLAG_08_DEBUG_METHOD_NAMES)) {
         System.out.println("#TestCaseBentley#run name=" + getName() + " isDebug=" + isDebug + " TestResult=" + debugResult(tr));
      }
      if (hasTestFlag(TEST_FLAG_02_NO_DEBUG_SPECIFICS)) {
         isDebug = false;
      } else {
         isDebug = isRunningDebug();
      }

      if (hasTestFlag(TEST_FLAG_01_PRINT_ANYWAYS)) {
         tc.setTestFlag(TEST_FLAG_03_HIDE_OUT_SUCCESSES, false);
         tc.setTestFlag(TEST_FLAG_04_HIDE_OUT_FAILURES, false);
         tc.setTestFlag(TEST_FLAG_05_SHOW_OUT_INIT, true);
      }

      //in debug mode, we always want to prevent hiding the sysout out
      if (isDebug && tc.hasTestFlag(TEST_FLAG_03_HIDE_OUT_SUCCESSES)) {
         tc.setTestFlag(TEST_FLAG_03_HIDE_OUT_SUCCESSES, false);
      }

      if (hasTestFlag(TEST_FLAG_03_HIDE_OUT_SUCCESSES)) {
         lpsOutTest = LoggedPrintStream.create(uc, standardOut);
         System.setOut(lpsOutTest);
         isCurrentOutStandard = false;
      } else {
         System.setOut(standardOut);
         isCurrentOutStandard = true;
         if (hasTestFlag(TEST_FLAG_01_PRINT_ANYWAYS) || hasTestFlag(TEST_FLAG_05_SHOW_OUT_INIT)) {
            printConstructorStream();
         } else {
            //erase it from memory
            lpsOutConstructor = null;
         }
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
      if (hasTestFlag(TEST_FLAG_08_DEBUG_METHOD_NAMES)) {
         System.out.println("#TestCaseBentley#runBare");
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
      toDLog().getDefault().getConfig().setFlagFormat(ITechConfig.FORMAT_FLAG_04_THREAD, b);
   }

   public void setFlagHideSystemOutFalse() {
      tc.setTestFlag(TEST_FLAG_03_HIDE_OUT_SUCCESSES, false);
   }

   public void setFlagHideSystemOutTrue() {
      tc.setTestFlag(TEST_FLAG_03_HIDE_OUT_SUCCESSES, true);
   }

   protected void setNunLockReleased(int num) {
      numLockRelease = num;
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
      if (tc == null) {
         throw new NullPointerException();
      }
      if (isSetup) {
         throw new IllegalStateException("Cannot set TestCtx once setup has been called");
      }
      this.tc = tc;
   }

   public void setTestFlag(int flag, boolean v) {
      if (hasTestFlag(TEST_FLAG_08_DEBUG_METHOD_NAMES)) {
         System.out.println("#TestCaseBentley#setTestFlag. flag=" + flag + " = " + v);
      }
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
      if (hasTestFlag(TEST_FLAG_08_DEBUG_METHOD_NAMES)) {
         System.out.println("#TestCaseBentley#setUp. Calling method setupAbstract()");
      }
      isSetup = true;
      threadFailure = null;

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
   public void tearDown() {
      if (hasTestFlag(TEST_FLAG_08_DEBUG_METHOD_NAMES)) {
         System.out.println("#TestCaseBentley#tearDown");
      }

      if (currentTestResult != null) {
         //#debug
         toDLog().pTest("", new TestResultStringable(uc, currentTestResult), TestCaseBentley.class, "tearDown", LVL_05_FINE, false);
      }

   }

   private void tearDownError() {
      if (hasTestFlag(TEST_FLAG_08_DEBUG_METHOD_NAMES)) {
         System.out.println("#TestCaseBentley#tearDownError");
      }
      //avoid double prints
      if (!tc.hasTestFlag(TEST_FLAG_04_HIDE_OUT_FAILURES)) {
         printConstructorStream();
         printTestStream();
      }
   }

   /**
    * Not Used anymore
    */
   private void tearDownNoError() {
      if (hasTestFlag(TEST_FLAG_08_DEBUG_METHOD_NAMES)) {
         System.out.println("#TestCaseBentley#tearDownNoError");
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
      dc.root(this, TestCaseBentley.class, "@line654");
      toStringPrivate(dc);
      dc.nlLvl(tc);
      dc.nl();
      dc.appendVarWithSpace("isCurrentOutStandard", isCurrentOutStandard);
      dc.appendVarWithSpace("isDebug", isDebug);
      dc.appendVarWithSpace("isSetup", isSetup);
      dc.appendVarWithSpace("numLockRelease", numLockRelease);

      dc.nl();
      if (standardOut == null) {
         dc.append("standardOut is null");
      } else {
         dc.append("standardOut hashcode is " + standardOut.hashCode());
         dc.nl();
         dc.append("standardOut toString START ");
         dc.nl();
         dc.append(standardOut.toString());
         dc.nl();
         dc.append("standardOut toString END ");
      }
      dc.nl();
      if (lpsOutTest == null) {
         dc.append("lpsOutTest is null");
      } else {
         dc.append("lpsOutTest hashcode is " + lpsOutTest.hashCode());
         dc.nl();
         dc.append("lpsOutTest BufferString START ");
         dc.nl();
         dc.append(lpsOutTest.getBufferString());
         dc.nl();
         dc.append("lpsOutTest BufferString END ");
      }
      dc.nl();
      if (lpsOutConstructor == null) {
         dc.append("lpsOutConstructor is null");
      } else {
         dc.append("lpsOutConstructor hashcode is " + lpsOutConstructor.hashCode());
         dc.nl();
         dc.append("lpsOutConstructor BufferString START ");
         dc.nl();
         dc.append(lpsOutConstructor.getBufferString());
         dc.nl();
         dc.append("lpsOutConstructor BufferString END ");
      }
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, TestCaseBentley.class);
      toStringPrivate(dc);
   }

   public String toStringColor(int c) {
      return "(" + ((c >> 24) & 0xFF) + "," + ((c >> 16) & 0xFF) + "," + ((c >> 8) & 0xFF) + "," + (c & 0xFF) + ")";
   }

   public UCtx toStringGetUCtx() {
      return uc;
   }

   private void toStringPrivate(Dctx dc) {
      dc.appendVarWithSpace("isSetup", isSetup);

   }
   //#enddebug

}
