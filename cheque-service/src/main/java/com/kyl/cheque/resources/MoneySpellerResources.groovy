package com.kyl.cheque.resources

//import com.codahale.metrics.annotation.Timed
import com.kyl.cheque.core.Money
import com.kyl.cheque.core.MoneyFormatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

/**
 * Created on 2016-04-22.
 */
@Path("/money/service")
@Produces(MediaType.APPLICATION_JSON)
class MoneySpellerResources {
    private static final Logger LOG = LoggerFactory.getLogger(MoneySpellerResources.class)
    MoneyFormatter formatter

    public MoneySpellerResources(MoneyFormatter moneyFormatter) {
        this.formatter = moneyFormatter
    }

//    @Timed
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
