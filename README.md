Overview
========
This project tries to replicate the problem described in
[LBCORE-224](http://jira.qos.ch/browse/LBCORE-224) without being dependent
on either Logback or ZooKeeper.

Building
--------
Execute either `gradlew.bat` (windows) or `./gradlew` (anything else).

IDEA
----
Execute either `gradlew.bat idea` (windows) or `./gradlew idea` (anything else) to generate IDEA project files.

This will execute the test. If the build does not fail it means that this bug did not show up. Try it multiple times.

Eclipse
-------
Execute either `gradlew.bat eclipse` (windows) or `./gradlew eclipse` (anything else) to generate Eclipse project files.

Background Information
----------------------
The files contained in the `lib` were created based on
http://svn.apache.org/repos/asf/zookeeper/trunk/ rev 1178579.

According to [LBCORE-224](http://jira.qos.ch/browse/LBCORE-224) running the
tests multiple times should fail very mysteriously in case certain system
configurations running windows and Java 1.6.x. Please see the issue for more
specific informations.

It currently looks like only IDEA/Eclipse and Maven are able to reproduce it.
Gradle seems to be immune for unknown reasons.
