@echo off
echo ===================================================
echo Starting BookMyShow Web Server on localhost:8080...
echo ===================================================

if not exist bin (
    echo Compiling first...
    call compile.bat
)

echo.
echo ===================================================
echo [SUCCESS] Web Server started!
echo Open your browser at: http://localhost:8080
echo ===================================================
echo.

java -cp "bin;lib/*;." moviebooking.web.WebServer

pause
