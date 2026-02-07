@echo off
rem Simple helper to build the Maven project, copy dependencies and run the HttpServer (Windows)
cd /d %~dp0

echo Packaging project...
call mvn -DskipTests package

echo Copying dependencies to target\dependency...
call mvn dependency:copy-dependencies -DoutputDirectory=target\dependency

echo Starting HttpServer...
java -cp "target/classes;target/dependency/*" com.jhs.httpserver.HttpServer
