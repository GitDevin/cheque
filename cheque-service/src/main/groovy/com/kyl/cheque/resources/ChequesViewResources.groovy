package com.kyl.cheque.resources

import com.codahale.metrics.annotation.Timed
import com.kyl.cheque.db.ChequeDAO
import com.kyl.cheque.views.ChequesView
import com.kyl.cheque.views.RecipientView

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created on 2016-04-21.
 */
@Path('/cheque/view')
@Produces(MediaType.TEXT_HTML)
class ChequesViewResources {

    ChequeDAO dao

    ChequesViewResources(ChequeDAO dao) {
        this.dao = dao
    }

    @Timed
    @GET
    @Path('/all')
    public ChequesView getChequesView() {
        return new ChequesView(this.dao.getAllCheques())
    }

    @Timed
    @GET
    @Path('/recipient/{recipient}')
    public RecipientView getRecipientView(@PathParam('recipient') String recipient) {
        def cheques = this.dao.getAllChequesPaidTo(recipient)
        return new RecipientView(cheques)
    }

}
