package cc.robotdreams;

public class BookingId {
    private int bookingid;
    private Booking booking;

    public BookingId(int bookingid, Booking booking) {
        this.bookingid = bookingid;
        this.booking = booking;
    }

    public BookingId() {
    }

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @Override
    public String toString() {
        return "BookingId{" +
                "bookingid=" + bookingid +
                ", booking=" + booking +
                '}';
    }
}
