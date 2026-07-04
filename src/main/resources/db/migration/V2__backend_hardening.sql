ALTER TABLE movies
    ALTER COLUMN status TYPE VARCHAR(32)
    USING CASE status
        WHEN 0 THEN 'NOW_SHOWING'
        WHEN 1 THEN 'COMING_SOON'
        WHEN 2 THEN 'ARCHIVED'
        ELSE 'ARCHIVED'
    END;

ALTER TABLE movies ALTER COLUMN slug TYPE VARCHAR(150);
ALTER TABLE cinemas ALTER COLUMN name TYPE VARCHAR(100);
ALTER TABLE cinemas ALTER COLUMN city TYPE VARCHAR(50);
ALTER TABLE cinemas ALTER COLUMN district TYPE VARCHAR(50);

ALTER TABLE payments ADD COLUMN refunded_at TIMESTAMP;

ALTER TABLE tickets ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE tickets ALTER COLUMN status DROP DEFAULT;
ALTER TABLE tickets ALTER COLUMN qr_code TYPE VARCHAR(1000);

CREATE INDEX idx_booking_seats_showtime ON booking_seats(showtime_id);
CREATE INDEX idx_bookings_showtime_status ON bookings(showtime_id, status);
CREATE INDEX idx_bookings_user_created ON bookings(user_id, created_at DESC);
CREATE INDEX idx_tickets_booking ON tickets(booking_id);
