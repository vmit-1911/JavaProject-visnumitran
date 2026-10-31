@echo off
echo ===================================================
echo Compiling Movie Ticket Booking System...
echo ===================================================

if not exist bin mkdir bin

javac -encoding UTF-8 -cp "lib/*" -d bin src/moviebooking/model/*.java src/moviebooking/util/*.java src/moviebooking/dao/*.java src/moviebooking/service/*.java src/moviebooking/ui/*.java src/moviebooking/web/*.java src/moviebooking/*.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %errorlevel%
)

echo [SUCCESS] Compilation successful! Output generated in bin/
