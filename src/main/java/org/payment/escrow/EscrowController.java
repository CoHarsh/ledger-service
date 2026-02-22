package org.payment.escrow;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.payment.ledger.LedgerService;

@Path("/wallet")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
@ApplicationScoped
public class EscrowController {

    @Inject LedgerService ledgerService;
    @Inject EscrowService escrowService;


    @POST
    @Path("/load")
    @Blocking
    public Response loadBalance(@Valid EscrowRequest request) {
        log.debug("Received load balance request: {}", request);
        try {
            EscrowResponse res = escrowService.loadBalance(request);
            return Response.status(Response.Status.CREATED)
                    .entity(res)
                    .build();
        } catch (Exception e) {
            log.error("Error creating escrow for request: {}", request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating escrow: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/balance/{actorId}")
    @Blocking
    public Response getBalance(@PathParam("actorId") String actorId) {
        log.debug("Received get balance request for actorId: {}", actorId);
        try {
            EscrowResponse res = ledgerService.getEscrowBalance(actorId);
            return Response.status(Response.Status.OK)
                    .entity(res)
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving escrow balance for actorId: {}", actorId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving escrow balance: " + e.getMessage())
                    .build();
        }
    }

}
