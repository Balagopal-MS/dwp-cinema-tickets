package uk.gov.dwp.uc.pairtest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImplTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    TicketService ticketService = new TicketServiceImpl();

    @Test
    public void throw_exception_invalid_acc_id()
    {
        thrown.expect(InvalidPurchaseException.class);
        thrown.expectMessage("Invalid Account Id. Must be greater than or equal to 1");

        ticketService.purchaseTickets(0L, new TicketTypeRequest[1]);
    }

    @Test
    public void throw_exception_ticket_max_count_exceeded()
    {
        TicketTypeRequest t1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 16);
        TicketTypeRequest t2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 8);
        TicketTypeRequest[] ticketTypeRequests = {t1,t2};

        thrown.expect(InvalidPurchaseException.class);
        thrown.expectMessage("Total ticket count must be less than or equal to 20");

        ticketService.purchaseTickets(1002L, ticketTypeRequests);
    }

    @Test
    public void throw_exception_adult_not_present_1()
    {
        TicketTypeRequest t1 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5);
        TicketTypeRequest t2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 8);
        TicketTypeRequest[] ticketTypeRequests = {t1,t2};

        thrown.expect(InvalidPurchaseException.class);
        thrown.expectMessage("Adult ticket must be brought along with infant/child tickets");

        ticketService.purchaseTickets(1002L, ticketTypeRequests);
    }

    @Test
    public void throw_exception_adult_not_present_2()
    {
        TicketTypeRequest t2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 8);
        TicketTypeRequest[] ticketTypeRequests = {t2};

        thrown.expect(InvalidPurchaseException.class);
        thrown.expectMessage("Adult ticket must be brought along with infant/child tickets");

        ticketService.purchaseTickets(1002L, ticketTypeRequests);
    }

    @Test
    public void do_payment_and_reserve_seat()
    {
        TicketTypeRequest t2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 8);
        TicketTypeRequest t1 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5);
        TicketTypeRequest t3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        TicketTypeRequest[] ticketTypeRequests = {t1,t2,t3};

        ticketService.purchaseTickets(1002L, ticketTypeRequests);
    }
}
