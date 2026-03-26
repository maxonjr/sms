@echo off
echo Cleaning...
if exist bin rmdir /s /q bin
mkdir bin

echo Compiling...
javac -cp "lib/mysql-connector-j-9.5.0.jar" -d bin src/utils/*.java src/database/*.java src/models/*.java src/gui/*.java src/Main.java

if %errorlevel% == 0 (
    echo Compilation successful!
    echo Running program...
    java -cp "lib/mysql-connector-j-9.5.0.jar;bin" Main
) else (
    echo Compilation failed!
    pause
)
