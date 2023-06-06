cd %~dp0
set JAVA_HOME=C:\Java\openlogic-openjdk-8u372-b07-windows-64

call C:\Java\apache-maven-3.2.5\bin\mvn install:install-file -Dfile=./ojdbc8-12.2.0.1.jar -DgroupId=com.oracle.jdbc -DartifactId=ojdbc8 -Dversion=12.2.0.1 -Dpackaging=jar

pause
