package org.payment.ledger;

import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.payment.util.CurrencyConverter;
import org.payment.util.CurrencyFactory;

import java.time.Instant;
import java.util.List;

@Path("/ledger")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class LedgerController {

    @Inject
    LedgerService ledgerService;

    @POST
    @Path("/events")
    @Blocking
    public Response ingestEvents(@Valid List<PayEventRequest> requests) {

        if (requests.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Empty batch not allowed")
                    .build();
        }
        try {
            List<PayEvent> events = requests.stream()
                    .map(this::toLedgerEvent)
                    .toList();
            ledgerService.processBatch(events);
            return Response.ok("Events processed successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error processing events: " + e.getMessage())
                    .build();
        }
    }

    private PayEvent toLedgerEvent(PayEventRequest req) {
        CurrencyConverter converter = CurrencyFactory.defaultCurrency(); //INR only
        return PayEvent.builder()
                .payEventId(req.payEventId)
                .timestamp(Instant.now())
                .actorId(req.actorId)
                .counterActorId(req.counterActorId)
                .amount(converter.toMinorUnit(req.amount))
                .signature(req.signature)
                .build();
    }

}
