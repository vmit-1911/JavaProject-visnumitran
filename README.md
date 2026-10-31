# 🎬 BookMyShow - Movie Ticket Booking System (Java)

A complete, production-grade **Movie Ticket Booking System** built using **Java (Swing/AWT)** and **MySQL (JDBC)**. Designed with a dark navy & crimson aesthetic inspired by **BookMyShow**, featuring an interactive cinema seating layout, ACID database transaction handling with double-booking prevention, simulated multi-method payments, printable/saveable cinema tickets, customer booking management, and an administration console.

---

## 🌟 Key Features

1. **Modern Cinema UI & Theme**
   - Sleek dark aesthetic (`#0B0F19` deep space navy, `#182034` surface cards, `#E11D48` crimson red accents).
   - Dynamic movie poster cards generated programmatically with ratings, certifications, and genres.
   - Smooth transitions between views using `CardLayout` inside a maximized responsive desktop window.

2. **Authentication & Session Management**
   - User sign-in & new user registration with input validation (regex email check, 10-digit phone verification, minimum password length).
   - Dedicated Administrator login with direct access to management metrics and operations.
   - Show/hide password toggles.

3. **Catalogue Browsing & Filtering**
   - **Now Showing** and **Upcoming Releases** tabs.
   - Search by movie title, language, or genre.
   - Multi-criteria filtering by Genre (Sci-Fi, Action, Drama, etc.) and Language (English, Hindi, Tamil, Telugu).
   - Sorting by audience rating (IMDb score) and release date.

4. **Theatre & Showtimes Selector**
   - Multi-city selection (Mumbai, Delhi NCR, Bangalore, Chennai).
   - Theatres grouped with screen names (IMAX Laser, Dolby Atmos 4K, Onyx LED) and showtimes with ticket prices.

5. **Interactive Seat Selection (Core Module)**
   - **Curved Cinema Screen** indicator at the top (*"Screen this way • All eyes towards the screen"*).
   - **Seat Tiers**:
     - **PREMIUM** (Rows A-B, ₹350)
     - **GOLD** (Rows C-E, ₹250)
     - **SILVER** (Rows F-H, ₹150)
   - **Real-Time Seat Status**:
     - 🟢 **Available (Green)**: Click to select.
     - 🔵 **Selected (Blue)**: Click again to deselect.
     - 🔴 **Booked (Red)**: Disabled; tooltip indicates seat is occupied.
   - Dynamic real-time calculation of base price, convenience fee, GST, and grand total.

6. **ACID Transaction & Concurrency Lock**
   - Atomic database transactions with `conn.setAutoCommit(false)`, `commit()`, and `rollback()`.
   - Before confirming any booking, re-verifies seat availability in the database to prevent two users from booking the same seat simultaneously.

7. **Simulated Payment Gateway**
   - Supports **UPI** (GPay / PhonePe / Paytm / BHIM), **Credit Card**, **Debit Card**, **Net Banking**, and **Cash at Box Office**.
   - Generates unique transaction references (`TXN-UPI-XXXXX`, `TXN-CC-XXXXX`).
   - Secure simulation: never stores sensitive card credentials.

8. **Cinema Ticket Generation & Printing**
   - High-fidelity visual ticket card with perforated edge styling, customer info, movie metadata, and simulated QR verification code.
   - **Export as File**: Save ticket as standalone HTML receipt or Plain Text file.
   - **Print Ticket / PDF**: Integrated with Java Print API (`java.awt.print.PrinterJob`) for direct paper printing or saving via "Microsoft Print to PDF".

9. **My Bookings History & Seat Release Cancellation**
   - Customer booking history audit table.
   - Real-time search filter for past bookings.
   - **Cancel Booking**: Reverses the booking, processes simulated refund, and **immediately releases the seats** back to available status for other moviegoers.

10. **Administrator Management Console**
    - KPI Metrics Cards: Total Movies, Total Theatres, Active Shows, Total Bookings, and Gross Revenue.
    - **Movie Management**: JTable CRUD (Add, Edit, Delete movies).
    - **Theatres & Screens**: Add and configure cinema locations.
    - **Show Scheduler**: Schedule shows with custom dates, timings, and ticket pricing.
    - **Sales & Revenue Report**: Financial audit breakdown categorized by movie title and payment method.

---

## 🏗️ Architecture & Package Structure

```
moviebooking/
├── model/                  # POJO Domain Entities
│   ├── User.java           # Customer account model
│   ├── Admin.java          # Administrator model
│   ├── Movie.java          # Movie details, duration, rating, status
│   ├── Theatre.java        # Cinema theatre and city details
│   ├── Screen.java         # Screen / auditorium details
│   ├── Seat.java           # Seat row, number, category, price, status
│   ├── Show.java           # Movie showtime at screen
│   ├── Booking.java        # Confirmed booking metadata
│   └── Payment.java        # Payment transaction model
│
├── dao/                    # Data Access Objects (JDBC PreparedStatement)
│   ├── UserDAO.java        # User authentication & registration
│   ├── MovieDAO.java       # Movie CRUD & search queries
│   ├── TheatreDAO.java     # Theatres, screens, and seat layouts
│   ├── ShowDAO.java        # Show listings & seat inventory
│   ├── BookingDAO.java     # Transactional booking & seat release
│   └── PaymentDAO.java     # Transaction logs
│
├── service/                # Business Logic Layer
│   ├── AuthenticationService.java
│   ├── MovieService.java
│   ├── SeatService.java
│   ├── BookingService.java
│   └── PaymentService.java
│
├── ui/                     # Java Swing / AWT Presentation Layer
│   ├── LoginFrame.java            # Sign-in & Admin toggle
│   ├── RegisterFrame.java         # New customer registration
│   ├── HomeFrame.java             # Main window & CardLayout host
│   ├── MoviePanel.java            # Catalogue browsing & filters
│   ├── MovieDetailsPanel.java     # Synopsis & movie overview
│   ├── TheatreSelectionPanel.java # City, cinema, and timings
│   ├── SeatSelectionPanel.java    # Interactive visual cinema layout
│   ├── BookingSummaryPanel.java   # Cost breakdown & confirmation
│   ├── PaymentPanel.java          # Simulated payment gateway
│   ├── TicketPanel.java           # Perforated ticket card & print
│   ├── MyBookingsPanel.java       # Customer history & cancellation
│   └── AdminDashboard.java        # Admin KPI metrics & CRUD tables
│
├── util/                   # Utility & Infrastructure Helpers
│   ├── DBConnection.java          # MySQL JDBC manager & fallback
│   ├── MockDatabaseStore.java     # ACID synchronized in-memory store
│   ├── UITheme.java               # BookMyShow colors, typography & buttons
│   ├── ValidationUtil.java        # Email, phone, password validators
│   └── TicketGenerator.java       # HTML, text, and printer renderer
│
├── Main.java               # Application bootstrap
└── TestSystem.java         # Headless verification test suite
```

---

## 🗄️ Database Setup (MySQL)

### Option 1: Live MySQL Database
1. Open **MySQL Workbench** or command line client:
   ```bash
   mysql -u root -p
   ```
2. Execute the schema script:
   ```sql
   source src/database/schema.sql;
   ```
3. Seed sample blockbusters, theatres, screens, and shows:
   ```sql
   source src/database/sample_data.sql;
   ```
4. Verify your credentials in `db.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/movie_booking_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=root
   db.password=YOUR_PASSWORD
   db.enableFallback=true
   ```

### Option 2: Automatic Zero-Config Fallback
If MySQL is not installed or the password differs on the demonstration machine, `DBConnection` **automatically activates the embedded in-memory fallback store**. The entire application runs out-of-the-box with 100% feature parity, data persistence during execution, and ACID locking!

---

## 🔑 Default Credentials

| Role | Username / Email | Password |
|---|---|---|
| **Customer** | `mitran@example.com` | `password123` |
| **Customer 2** | `rahul@example.com` | `rahul123` |
| **Admin** | `admin` | `admin123` |

---

## 🚀 How to Compile and Run

### 1-Click Execution (Windows Batch)
- **To compile**: Double-click `compile.bat` or run:
  ```cmd
  compile.bat
  ```
- **To launch the application**: Double-click `run.bat` or run:
  ```cmd
  run.bat
  ```

### Manual Terminal Commands
```cmd
# 1. Compile
javac -encoding UTF-8 -cp "lib/*" -d bin src/moviebooking/model/*.java src/moviebooking/util/*.java src/moviebooking/dao/*.java src/moviebooking/service/*.java src/moviebooking/ui/*.java src/moviebooking/*.java

# 2. Run
java -cp "bin;lib/*;." moviebooking.Main
```

### Automated Headless Test Suite
To verify all DAOs, services, and concurrency locks:
```cmd
java -cp "bin;lib/*;." moviebooking.TestSystem
```

---

## 🎬 Demonstration Walkthrough for Viva / Presentation

1. **Login & Registration**:
   - Launch application; log in as `mitran@example.com` with `password123`.
   - Or click "Create Account" to demonstrate input validation (try invalid email or mismatched passwords).
2. **Movie Browsing**:
   - Filter by genre (e.g. *Sci-Fi*), switch between *Now Showing* and *Upcoming Releases*, or search for *"Interstellar"*.
3. **Showtime Selection**:
   - Select *Interstellar* -> Click *"Select Theatre & Shows"* -> Select city *"Mumbai"* -> Click the **10:30 AM** showtime.
4. **Interactive Seat Map**:
   - Observe the curved cinema screen at top.
   - Note occupied seats (already in Red).
   - Select seats (e.g. `A1`, `A2`) -> turns Dodger Blue -> price updates live in the bottom bar.
5. **Booking Summary & Payment**:
   - Review base amount, ₹30 convenience fee, and 18% GST calculation.
   - Choose payment method (UPI / Credit Card) and confirm payment.
6. **Ticket Generation & Printing**:
   - View the perforated cinema ticket card with QR code.
   - Click *"Print Ticket / Save to PDF"* to show Windows Print Dialog.
7. **My Bookings & Cancellation**:
   - Navigate to *"My Bookings"*.
   - Cancel the booking -> Observe the seats being instantly freed in the database!
8. **Admin Console**:
   - Click *"Admin Console"* in the top navigation bar.
   - View live KPI metrics cards (Gross Revenue, Total Bookings, Shows).
   - Add or edit movies in the JTable management tab.
