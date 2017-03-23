package com.kyl.cheque.resources

import com.codahale.metrics.annotation.Timed
import com.kyl.cheque.core.Money
import com.kyl.cheque.core.MoneyFormatter
import groovy.json.JsonOutput
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created on 2016-04-22.
 */
@Path("/money/service")
@Produces(MediaType.APPLICATION_JSON)
class MoneySpellerResources {
    private static final Logger LOG = LoggerFactory.getLogger(MoneySpellerResources.class)
    MoneyFormatter formatter

    MoneySpellerResources(MoneyFormatter moneyFormatter) {
        this.formatter = moneyFormatter
    }

    @Timed
    @POST
    @Path("/format")
    @Consumes(MediaType.APPLICATION_JSON)
    Response format(@Valid Money money) {
        try {
            def result = this.formatter.formatMoney(money, "en_AU")
            return Response.accepted(JsonOutput.toJson(["result": result])).build()
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to convert ${money} to description.")
            def error = JsonOutput.toJson(["error": e.getMessage()])
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build()
        }
    }
}
