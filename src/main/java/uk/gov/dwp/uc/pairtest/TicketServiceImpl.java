package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketServiceImpl implements TicketService {

    private final static Logger LOG = Logger.getLogger(TicketServiceImpl.class.getName());

    private final static String INVALID_ACC_ID_MSG = "Invalid Account Id. Must be greater than or equal to %s";
    private final static String MAX_TICKET_COUNT_EXCEEDED_MSG = "Total ticket count must be less than or equal to %s";
    private final static String ADULT_REQUIRED_MSG = "Adult ticket must be brought along with infant/child tickets";
    private final static String TICKET_PRICE_SUFFIX = "ticket.price.";
    private final static String SEAT_COUNT_SUFFIX = "seats.allotted.";
    private final static String ACC_ID_VALID_FROM = "accountId.validFrom";
    private final static String TICKETS_MAX_ALLOWED_COUNT = "tickets.maxAllowed.count";

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;
    private final Properties properties;

    public TicketServiceImpl()
    {
        ticketPaymentService = new TicketPaymentServiceImpl();
        seatReservationService = new SeatReservationServiceImpl();
        properties = PropertiesFactory.createPropertyInstance();
    }

    /**
     * Should only have private methods other than the one below.
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException
    {
        validateAccountId(accountId);
        validateTicketCount(ticketTypeRequests);
        validateInfantAndChildAllowed(ticketTypeRequests);

        LOG.log(Level.INFO, "Making payment for the account id: "+ accountId);
        ticketPaymentService.makePayment(accountId, calculateAmountToPay(ticketTypeRequests));
        LOG.log(Level.INFO, "Reserving seats for the account id: "+ accountId);
        seatReservationService.reserveSeat(accountId, calculateTotalSeatsNeeded(ticketTypeRequests));
    }

    private int calculateTotalSeatsNeeded(TicketTypeRequest... ticketTypeRequests)
    {
        int totalSeatsNeeded = 0;
        for (TicketTypeRequest each : ticketTypeRequests)
            totalSeatsNeeded +=  Integer.parseInt(properties.getProperty(SEAT_COUNT_SUFFIX + each.getTicketType().name()));

        return totalSeatsNeeded;
    }

    private int calculateAmountToPay(TicketTypeRequest... ticketTypeRequests)
    {
        int amountToPay = 0;
        for (TicketTypeRequest each : ticketTypeRequests)
            amountToPay +=  Integer.parseInt(properties.getProperty(TICKET_PRICE_SUFFIX + each.getTicketType().name()));

        return amountToPay;
    }

    private void validateAccountId(Long accountId)
    {
        long accountIdValidFrom = Long.parseLong(properties.getProperty(ACC_ID_VALID_FROM));
        if (accountId < accountIdValidFrom)
            throw new InvalidPurchaseException(String.format(INVALID_ACC_ID_MSG,accountIdValidFrom));
    }

    private void validateTicketCount(TicketTypeRequest... ticketTypeRequests)
    {
        int maxAllowedCount = Integer.parseInt(properties.getProperty(TICKETS_MAX_ALLOWED_COUNT));
        int totalCount = 0;

        for (TicketTypeRequest each : ticketTypeRequests)
            totalCount += each.getNoOfTickets();

        if (totalCount > maxAllowedCount)
            throw new InvalidPurchaseException(String.format(MAX_TICKET_COUNT_EXCEEDED_MSG, maxAllowedCount));
    }

    private void validateInfantAndChildAllowed(TicketTypeRequest... ticketTypeRequests)
    {
        boolean infantOrChildPresent = false;
        boolean adultPresent = false;

        for (TicketTypeRequest each : ticketTypeRequests) {
            if (each.getTicketType().equals(TicketTypeRequest.Type.CHILD) ||
                    each.getTicketType().equals(TicketTypeRequest.Type.INFANT)) {
                infantOrChildPresent = true;
                break;
            }
        }

        if (infantOrChildPresent)
        {
            for (TicketTypeRequest each : ticketTypeRequests) {
                if (each.getTicketType().equals(TicketTypeRequest.Type.ADULT)) {
                    adultPresent = true;
                    break;
                }
            }

            if(!adultPresent)
                throw new InvalidPurchaseException(ADULT_REQUIRED_MSG);
        }
    }
}
