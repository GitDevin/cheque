package com.kyl.cheque.resources

import com.codahale.metrics.annotation.Timed
import com.kyl.cheque.core.Cheque
import com.kyl.cheque.core.MoneyFormatter
import com.kyl.cheque.db.ChequeDAO
import groovy.json.JsonOutput
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created on 2016-04-20.
 */
@Path('/cheque/service')
@Produces(MediaType.APPLICATION_JSON)
class ChequesResources {
    private static final Logger LOG = LoggerFactory.getLogger(ChequesResources.class)
    ChequeDAO dao
    MoneyFormatter formatter

    ChequesResources(ChequeDAO dao, MoneyFormatter formatter) {
        this.dao = dao
        this.formatter = formatter
    }

    @Timed
    @GET
    @Path('/all')
    Response getAllCheques() {
        def cheques = this.dao.getAllCheques()

        if (cheques == null || cheques.isEmpty()) {
            return Response.noContent().build()
        }

        return Response.ok(cheques).build()
    }

    @Timed
    @PUT
    @Path('/put')
    @Consumes(MediaType.APPLICATION_JSON)
    Response createCheque(@Valid Cheque cheque) {
        try {
            cheque.amountDesc = formatter.formatMoney(cheque.dollar, cheque.cent, "en_AU")
        } catch (IllegalArgumentException e) {
            def errorJson = JsonOutput.toJson(["error": e.getMessage()])
            LOG.error("Failed to create cheque: ${e.getMessage()}")
            return Response.status(Response.Status.BAD_REQUEST).entity(errorJson).build()
        }

        long chequeId = this.dao.insertCheque(cheque)
        if (chequeId == 0) {
            LOG.error("Failed to insert cheque into the database: ${cheque}")
            return Response.notModified().build()
        }
        cheque.setChequeId(chequeId)
        LOG.info("Created cheque ${cheque}")
        return Response.created(URI.create("/cheque/service/id/${chequeId}")).build()
    }

    @Timed
    @GET
    @Path("/recipient/{recipient}")
    Response getAllChequesPaidTo(@PathParam('recipient') String recipient) {
        def cheques = this.dao.getAllChequesPaidTo(recipient)
        if (cheques == null || cheques.isEmpty()) {
            LOG.info("Recipient ${recipient} is not found.")
            return Response.status(Response.Status.NOT_FOUND).build()
        }
        return Response.ok(cheques).build()
    }

    @Timed
    @GET
    @Path("/id/{chequeId}")
    Response getCheque(@PathParam('chequeId') long chequeId) {
        def cheque = this.dao.getCheque(chequeId)
        if (cheque == null) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }
        return Response.ok(cheque).build()
    }

}
