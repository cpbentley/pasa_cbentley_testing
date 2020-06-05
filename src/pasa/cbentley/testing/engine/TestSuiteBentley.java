/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.testing.engine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.testing.ctx.TestCtx;

public class TestSuiteBentley extends TestSuite {

   protected final TestCtx tc;

   public TestSuiteBentley() {
      tc = new TestCtx(new UCtx());
   }

   public TestSuiteBentley(String string) {
      super(string);
      tc = new TestCtx(new UCtx());
   }

   public TestSuiteBentley(TestCtx tc, String title) {
      super(title);
      this.tc = tc;
   }

   /**
    * Adds the tests from the given class to the suite
    */
   public void addTestSuite(Class<? extends TestCase> testClass) {
      addTest(new TestSuiteBentley(tc, testClass));
   }

   /**
    * Constructs a TestSuite from the given class. Adds all the methods
    * starting with "test" as test cases to the suite.
    * Parts of this method were written at 2337 meters in the Hueffihuette,
    * Kanton Uri
    */
   public TestSuiteBentley(TestCtx tc, final Class<?> theClass) {
      this.tc = tc;
      addTestsFromTestCase(theClass);
   }

   private void addTestsFromTestCase(final Class<?> theClass) {
      String className = theClass.getName();
      setName(className);
      try {
         getTestConstructor(theClass); // Avoid generating multiple error messages
      } catch (NoSuchMethodException e) {
         addTest(warning("Class " + theClass.getName() + " has no public constructor TestCase(String name) or TestCase()"));
         return;
      }

      if (!Modifier.isPublic(theClass.getModifiers())) {
         addTest(warning("Class " + theClass.getName() + " is not public"));
         return;
      }

      Class<?> superClass = theClass;
      List<String> names = new ArrayList<String>();
      while (Test.class.isAssignableFrom(superClass)) {
         for (Method each : superClass.getDeclaredMethods()) {
            addTestMethod(each, names, theClass);
         }
         superClass = superClass.getSuperclass();
      }
      if (testCount() == 0) {
         addTest(warning("No tests found in " + theClass.getName()));
      }
   }

   private boolean isPublicTestMethod(Method m) {
      return isTestMethod(m) && Modifier.isPublic(m.getModifiers());
   }

   private boolean isTestMethod(Method m) {
      return m.getParameterTypes().length == 0 && m.getName().startsWith("test") && m.getReturnType().equals(Void.TYPE);
   }

   private void addTestMethod(Method m, List<String> names, Class<?> theClass) {
      String name = m.getName();
      if (names.contains(name)) {
         return;
      }
      if (!isPublicTestMethod(m)) {
         if (isTestMethod(m))
            addTest(warning("Test method isn't public: " + m.getName() + "(" + theClass.getCanonicalName() + ")"));
         return;
      }
      names.add(name);
      addTest(createTestBentley(theClass, name));
   }

   /**
    * ...as the moon sets over the early morning Merlin, Oregon
    * mountains, our intrepid adventurers type...
    */
   public Test createTestBentley(Class<?> theClass, String name) {
      Constructor<?> constructor;
      try {
         constructor = getTestConstructor(theClass);
      } catch (NoSuchMethodException e) {
         return warning("Class " + theClass.getName() + " has no public constructor TestCase(String name) or TestCase()");
      }
      Object test;
      try {
         if (constructor.getParameterTypes().length == 0) {
            test = constructor.newInstance(new Object[0]);
            if (test instanceof TestCaseBentley) {
               TestCaseBentley testBentley = (TestCaseBentley) test;
               testBentley.setTestCtx(tc);
               testBentley.setName(name);
            } else if (test instanceof TestCase) {
               ((TestCase) test).setName(name);
            }
         } else {
            test = constructor.newInstance(new Object[] { name });
         }
      } catch (InstantiationException e) {
         return (warning("Cannot instantiate test case: " + name + " (" + exceptionToString(e) + ")"));
      } catch (InvocationTargetException e) {
         return (warning("Exception in constructor: " + name + " (" + exceptionToString(e.getTargetException()) + ")"));
      } catch (IllegalAccessException e) {
         return (warning("Cannot access test case: " + name + " (" + exceptionToString(e) + ")"));
      }
      return (Test) test;
   }

   /**
    * Converts the stack trace into a string
    */
   private String exceptionToString(Throwable t) {
      StringWriter stringWriter = new StringWriter();
      PrintWriter writer = new PrintWriter(stringWriter);
      t.printStackTrace(writer);
      return stringWriter.toString();
   }

}
