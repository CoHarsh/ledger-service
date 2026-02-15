package org.payment.ledger;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Timestamp;
import java.util.List;


@Path("/ledger")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LedgerController {

    @Inject
    LedgerService ledgerService;

    @POST
    @Path("/events")
    public Response ingestEvents(List<PayEventRequest> requests){
        List<PayEvent> events = requests.stream()
                .map(this::toLedgerEvent)
                .toList();
        ledgerService.processBatch(events);
        return Response.ok().build(); //TODO change to better and structured response
    }

    private PayEvent toLedgerEvent(PayEventRequest req){
        return PayEvent.builder()
                            .payEventId(req.payEventId)
                            .timestamp(new Timestamp(System.currentTimeMillis()))
                            .actorId(req.actorId)
                            .counterActorId(req.counterActorId)
                            .amount(req.amount)
                            .signature(req.signature)
                            .build();
    }
}
