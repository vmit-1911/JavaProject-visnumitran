@echo off
echo ===================================================
echo Starting Movie Ticket Booking System...
echo ===================================================

if not exist bin (
    echo bin/ directory not found. Compiling first...
    call compile.bat
)

java -cp "bin;lib/*;." moviebooking.Main

pause
