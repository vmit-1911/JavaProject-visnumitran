-- =====================================================================
-- Movie Ticket Booking System - Seed / Sample Data
-- =====================================================================

USE `movie_booking_db`;

-- Admins
INSERT INTO `admins` (`admin_id`, `username`, `name`, `email`, `password`, `role`) VALUES
(1, 'admin', 'System Administrator', 'admin@cinema.com', 'admin123', 'SUPER_ADMIN'),
(2, 'manager', 'Theatre Manager', 'manager@cinema.com', 'manager123', 'THEATRE_MANAGER')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- Users
INSERT INTO `users` (`user_id`, `name`, `email`, `phone`, `password`) VALUES
(1, 'Mitran Sharma', 'mitran@example.com', '9876543210', 'password123'),
(2, 'Rahul Verma', 'rahul@example.com', '9812345678', 'rahul123'),
(3, 'Priya Patel', 'priya@example.com', '9123456780', 'priya123')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- Movies
INSERT INTO `movies` (`movie_id`, `title`, `genre`, `language`, `duration_minutes`, `release_date`, `certification`, `description`, `rating`, `poster_path`, `banner_path`, `status`) VALUES
(1, 'Interstellar', 'Sci-Fi / Adventure / Drama', 'English', 169, '2024-03-15', 'U/A', 'When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot, Joseph Cooper, is tasked to pilot a spacecraft along with a team of researchers to find a new planet for humans.', 9.2, 'poster_interstellar.png', 'banner_interstellar.png', 'NOW_SHOWING'),
(2, 'Dune: Part Two', 'Sci-Fi / Action / Epic', 'English', 166, '2024-03-01', 'U/A', 'Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.', 8.8, 'poster_dune.png', 'banner_dune.png', 'NOW_SHOWING'),
(3, 'Oppenheimer', 'Biography / Drama / History', 'English', 180, '2023-07-21', 'A', 'The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II.', 8.9, 'poster_oppenheimer.png', 'banner_oppenheimer.png', 'NOW_SHOWING'),
(4, 'The Dark Knight', 'Action / Crime / Drama', 'English', 152, '2023-11-10', 'U/A', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', 9.0, 'poster_darkknight.png', 'banner_darkknight.png', 'NOW_SHOWING'),
(5, 'Inception', 'Action / Sci-Fi / Thriller', 'English', 148, '2024-01-20', 'U/A', 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 8.8, 'poster_inception.png', 'banner_inception.png', 'NOW_SHOWING'),
(6, 'Avatar: The Way of Water', 'Action / Sci-Fi / Adventure', 'English', 192, '2024-06-15', 'U/A', 'Jake Sully lives with his newfound family formed on the extrasolar moon Pandora. Once a familiar threat returns to finish what was previously started, Jake must work with Neytiri and the army of the Na\'vi.', 8.4, 'poster_avatar.png', 'banner_avatar.png', 'UPCOMING'),
(7, 'Spider-Man: Beyond the Spider-Verse', 'Animation / Action / Adventure', 'English', 140, '2024-09-01', 'U', 'Miles Morales catapults across the Multiverse, where he encounters a team of Spider-People charged with protecting its very existence.', 9.1, 'poster_spiderman.png', 'banner_spiderman.png', 'UPCOMING'),
(8, 'Gladiator II', 'Action / Adventure / Drama', 'English', 150, '2024-11-22', 'A', 'Years after witnessing the death of Maximus at the hands of his uncle, Lucius must enter the Colosseum after the powerful emperors of Rome conquer his home.', 8.6, 'poster_gladiator.png', 'banner_gladiator.png', 'UPCOMING')
ON DUPLICATE KEY UPDATE `title`=VALUES(`title`);

-- Theatres
INSERT INTO `theatres` (`theatre_id`, `name`, `city`, `address`, `total_screens`) VALUES
(1, 'PVR: ICON Gold Class', 'Mumbai', 'Phoenix Palladium, Lower Parel', 3),
(2, 'INOX: Megaplex IMAX', 'Mumbai', 'Inorbit Mall, Malad West', 3),
(3, 'Cinepolis: VIP Luxe', 'Delhi NCR', 'DLF Avenue, Saket', 3),
(4, 'PVR: Superplex', 'Bangalore', 'Forum Mall, Koramangala', 3),
(5, 'SPI Cinemas: Sathyam', 'Chennai', 'Royapettah Main Road', 3)
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- Screens for Theatre 1 & 2
INSERT INTO `screens` (`screen_id`, `theatre_id`, `screen_number`, `screen_name`, `total_seats`) VALUES
(1, 1, 1, 'Audi 1 - IMAX Laser', 64),
(2, 1, 2, 'Audi 2 - Dolby Atmos 4K', 64),
(3, 1, 3, 'Audi 3 - Gold VIP Luxe', 64),
(4, 2, 1, 'Screen 1 - Onyx Cinema LED', 64),
(5, 2, 2, 'Screen 2 - INSIGNIA Luxe', 64),
(6, 3, 1, 'Audi 1 - Macro XE', 64),
(7, 4, 1, 'Screen 1 - P[XL] Large Format', 64),
(8, 5, 1, 'Main Screen - Sathyam RGB Laser', 64)
ON DUPLICATE KEY UPDATE `screen_name`=VALUES(`screen_name`);

-- Seats generation for Screen 1 (64 seats: A-H, 1-8)
-- A, B = PREMIUM (350), C, D, E = GOLD (250), F, G, H = SILVER (150)
INSERT IGNORE INTO `seats` (`screen_id`, `row_char`, `seat_number`, `category`, `base_price`) VALUES
-- Premium Row A
(1, 'A', 1, 'PREMIUM', 350.00), (1, 'A', 2, 'PREMIUM', 350.00), (1, 'A', 3, 'PREMIUM', 350.00), (1, 'A', 4, 'PREMIUM', 350.00),
(1, 'A', 5, 'PREMIUM', 350.00), (1, 'A', 6, 'PREMIUM', 350.00), (1, 'A', 7, 'PREMIUM', 350.00), (1, 'A', 8, 'PREMIUM', 350.00),
-- Premium Row B
(1, 'B', 1, 'PREMIUM', 350.00), (1, 'B', 2, 'PREMIUM', 350.00), (1, 'B', 3, 'PREMIUM', 350.00), (1, 'B', 4, 'PREMIUM', 350.00),
(1, 'B', 5, 'PREMIUM', 350.00), (1, 'B', 6, 'PREMIUM', 350.00), (1, 'B', 7, 'PREMIUM', 350.00), (1, 'B', 8, 'PREMIUM', 350.00),
-- Gold Row C
(1, 'C', 1, 'GOLD', 250.00), (1, 'C', 2, 'GOLD', 250.00), (1, 'C', 3, 'GOLD', 250.00), (1, 'C', 4, 'GOLD', 250.00),
(1, 'C', 5, 'GOLD', 250.00), (1, 'C', 6, 'GOLD', 250.00), (1, 'C', 7, 'GOLD', 250.00), (1, 'C', 8, 'GOLD', 250.00),
-- Gold Row D
(1, 'D', 1, 'GOLD', 250.00), (1, 'D', 2, 'GOLD', 250.00), (1, 'D', 3, 'GOLD', 250.00), (1, 'D', 4, 'GOLD', 250.00),
(1, 'D', 5, 'GOLD', 250.00), (1, 'D', 6, 'GOLD', 250.00), (1, 'D', 7, 'GOLD', 250.00), (1, 'D', 8, 'GOLD', 250.00),
-- Gold Row E
(1, 'E', 1, 'GOLD', 250.00), (1, 'E', 2, 'GOLD', 250.00), (1, 'E', 3, 'GOLD', 250.00), (1, 'E', 4, 'GOLD', 250.00),
(1, 'E', 5, 'GOLD', 250.00), (1, 'E', 6, 'GOLD', 250.00), (1, 'E', 7, 'GOLD', 250.00), (1, 'E', 8, 'GOLD', 250.00),
-- Silver Row F
(1, 'F', 1, 'SILVER', 150.00), (1, 'F', 2, 'SILVER', 150.00), (1, 'F', 3, 'SILVER', 150.00), (1, 'F', 4, 'SILVER', 150.00),
(1, 'F', 5, 'SILVER', 150.00), (1, 'F', 6, 'SILVER', 150.00), (1, 'F', 7, 'SILVER', 150.00), (1, 'F', 8, 'SILVER', 150.00),
-- Silver Row G
(1, 'G', 1, 'SILVER', 150.00), (1, 'G', 2, 'SILVER', 150.00), (1, 'G', 3, 'SILVER', 150.00), (1, 'G', 4, 'SILVER', 150.00),
(1, 'G', 5, 'SILVER', 150.00), (1, 'G', 6, 'SILVER', 150.00), (1, 'G', 7, 'SILVER', 150.00), (1, 'G', 8, 'SILVER', 150.00),
-- Silver Row H
(1, 'H', 1, 'SILVER', 150.00), (1, 'H', 2, 'SILVER', 150.00), (1, 'H', 3, 'SILVER', 150.00), (1, 'H', 4, 'SILVER', 150.00),
(1, 'H', 5, 'SILVER', 150.00), (1, 'H', 6, 'SILVER', 150.00), (1, 'H', 7, 'SILVER', 150.00), (1, 'H', 8, 'SILVER', 150.00);

-- Populate Screen 2 seats identically
INSERT IGNORE INTO `seats` (`screen_id`, `row_char`, `seat_number`, `category`, `base_price`)
SELECT 2, `row_char`, `seat_number`, `category`, `base_price` FROM `seats` WHERE `screen_id` = 1;

-- Populate Screen 3 seats identically
INSERT IGNORE INTO `seats` (`screen_id`, `row_char`, `seat_number`, `category`, `base_price`)
SELECT 3, `row_char`, `seat_number`, `category`, `base_price` FROM `seats` WHERE `screen_id` = 1;

-- Shows
INSERT INTO `shows` (`show_id`, `movie_id`, `screen_id`, `show_date`, `start_time`, `ticket_price`, `available_seats`) VALUES
(1, 1, 1, CURDATE(), '10:30:00', 350.00, 60),
(2, 1, 1, CURDATE(), '14:15:00', 350.00, 64),
(3, 1, 1, CURDATE(), '18:45:00', 380.00, 58),
(4, 1, 1, CURDATE(), '22:30:00', 350.00, 64),
(5, 2, 2, CURDATE(), '11:00:00', 320.00, 64),
(6, 2, 2, CURDATE(), '15:30:00', 320.00, 62),
(7, 2, 2, CURDATE(), '19:45:00', 350.00, 64),
(8, 3, 3, CURDATE(), '12:00:00', 300.00, 64),
(9, 3, 3, CURDATE(), '17:00:00', 320.00, 64),
(10, 4, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00:00', 300.00, 64),
(11, 5, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '18:00:00', 300.00, 64)
ON DUPLICATE KEY UPDATE `ticket_price`=VALUES(`ticket_price`);

-- Sample Existing Booking
INSERT INTO `bookings` (`booking_id`, `booking_reference`, `user_id`, `show_id`, `total_seats`, `base_amount`, `convenience_fee`, `gst_amount`, `total_amount`, `booking_status`, `booking_date`) VALUES
(1, 'BMS-2026-94812', 1, 1, 2, 700.00, 60.00, 136.80, 896.80, 'CONFIRMED', NOW()),
(2, 'BMS-2026-31952', 2, 3, 3, 750.00, 90.00, 151.20, 991.20, 'CONFIRMED', NOW())
ON DUPLICATE KEY UPDATE `booking_reference`=VALUES(`booking_reference`);

-- Booked Seats for Show 1 (Row A Seats 4, 5)
INSERT IGNORE INTO `booking_seats` (`booking_id`, `show_id`, `seat_id`, `seat_label`, `category`, `price`, `status`)
SELECT 1, 1, s.seat_id, CONCAT(s.row_char, s.seat_number), s.category, s.base_price, 'BOOKED'
FROM `seats` s
WHERE s.screen_id = 1 AND s.row_char = 'A' AND s.seat_number IN (4, 5);

-- Booked Seats for Show 3 (Row C Seats 3, 4, 5)
INSERT IGNORE INTO `booking_seats` (`booking_id`, `show_id`, `seat_id`, `seat_label`, `category`, `price`, `status`)
SELECT 2, 3, s.seat_id, CONCAT(s.row_char, s.seat_number), s.category, s.base_price, 'BOOKED'
FROM `seats` s
WHERE s.screen_id = 1 AND s.row_char = 'C' AND s.seat_number IN (3, 4, 5);

-- Payments
INSERT INTO `payments` (`payment_id`, `booking_id`, `transaction_id`, `payment_method`, `amount`, `payment_status`, `payment_date`) VALUES
(1, 1, 'TXN-UPI-98421045', 'UPI', 896.80, 'SUCCESS', NOW()),
(2, 2, 'TXN-CC-77319542', 'CREDIT_CARD', 991.20, 'SUCCESS', NOW())
ON DUPLICATE KEY UPDATE `transaction_id`=VALUES(`transaction_id`);
