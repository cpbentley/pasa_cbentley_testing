package pasa.cbentley.testing.engine;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestWatcherStart extends TestWatcher {
   protected void starting(Description description) {
      System.out.println("Starting test: " + description.getMethodName());
   }
}
