package com.kyl.cheque.resources

import com.codahale.metrics.annotation.Timed
import com.kyl.cheque.db.ChequeDAO
import com.kyl.cheque.views.ChequeView

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created on 2016-04-25.
 */
@Path('/cheque/view')
@Produces(MediaType.TEXT_HTML)
class ChequeViewResources {
    ChequeDAO dao

    ChequeViewResources(ChequeDAO dao) {
        this.dao = dao
    }

    @Timed
    @GET
    @Path('/id/{chequeId}')
    public ChequeView getRecipientView(@PathParam('chequeId') long chequeId) {
        return new ChequeView(this.dao.getCheque(chequeId), true)
    }

    @Timed
    @GET
    @Path('/create')
    public ChequeView getCreateChequeView() {
        return new ChequeView(null, false)
    }
}
