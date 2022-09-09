package thirdparty.seatbooking;

import uk.gov.dwp.uc.pairtest.PropertiesFactory;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;

import java.util.Properties;

public class SeatReservationServiceImpl implements SeatReservationService{
    @Override
    public void reserveSeat(long accountId, int totalSeatsToAllocate) {
        //Real implementation omitted, assume a work code will do the reservation
    }
}
