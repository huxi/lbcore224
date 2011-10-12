# Overview

This project replicates the problem described in
[LBCORE-224](http://jira.qos.ch/browse/LBCORE-224) without being dependent
on either Logback or ZooKeeper.

Something seems to be broken concerning locks in general and/or ReentrantReadWriteLock.

The entry in the Java Bug Database is (or rather: will be) [#7099601](http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7099601).

## Background Information
The files contained in the `lib` were created based on
http://svn.apache.org/repos/asf/zookeeper/trunk/ rev 1178579.

According to [LBCORE-224](http://jira.qos.ch/browse/LBCORE-224) running the
tests multiple times will fail very mysteriously on some systems (at least Windows and Mac)
with Java 1.6.x.

It currently looks like only IDEA/Eclipse and Maven are able to reproduce it.
Gradle seems to be immune for unknown reasons.

The supposedly problematic code in AppenderAttachableImpl looks like this:

    public int appendLoopOnAppenders(E e) {
      int size = 0;
      r.lock();
      try {
        for (Appender<E> appender : appenderList) {
          appender.doAppend(e);
          size++;
        }
      } finally {
        r.unlock();
      }
      return size;
    }

The following exception is thrown:

    java.lang.IllegalMonitorStateException
        at java.util.concurrent.locks.ReentrantReadWriteLock$Sync.tryReleaseShared(ReentrantReadWriteLock.java:363)
        at java.util.concurrent.locks.AbstractQueuedSynchronizer.releaseShared(AbstractQueuedSynchronizer.java:1317)
        at java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock.unlock(ReentrantReadWriteLock.java:745)
        at ch.qos.logback.core.spi.AppenderAttachableImpl.appendLoopOnAppenders(AppenderAttachableImpl.java:68)

As you can see in the snippet above, there is no way that the lock should be able to unlock without being locked before.
Taking a look at the source of [AppenderAttachableImpl](https://github.com/ceki/logback/blob/v_0.9.30/logback-core/src/main/java/ch/qos/logback/core/spi/AppenderAttachableImpl.java)
you'll also see that any other code referencing `r` looks correct, too.

Please see issue [LBCORE-224](http://jira.qos.ch/browse/LBCORE-224) for some more informations.

## Maven

Execute `mvn clean install`.
This should result in a failed test.

## Gradle

Interestingly, Gradle build works as expected without throwing an exception.

### Building

Execute either `gradlew.bat` (windows) or `./gradlew` (anything else).

### IDEA

Execute either `gradlew.bat idea` (windows) or `./gradlew idea` (anything else) to generate IDEA project files.

This will execute the test. If the build does not fail it means that this bug did not show up. Try it multiple times.

### Eclipse
Execute either `gradlew.bat eclipse` (windows) or `./gradlew eclipse` (anything else) to generate Eclipse project files.

## First hints...

Given that testing with Gradle works but the identical test fails in both Maven and
IDEA, this issue may be related to some difference in the way the tests are executed.

### Part of Gradle stacktrace

    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:300)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:51)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:63)
    at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:49)

### Part of IDEA stacktrace

    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:300)
    at org.junit.runner.JUnitCore.run(JUnitCore.java:157)
    at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:71)

### Part of Maven stacktrace

    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:300)
    at org.apache.maven.surefire.junit4.JUnit4TestSet.execute(JUnit4TestSet.java:35)

