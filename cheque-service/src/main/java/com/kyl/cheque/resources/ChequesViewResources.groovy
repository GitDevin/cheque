package com.kyl.cheque.resources

//import com.codahale.metrics.annotation.Timed
import com.kyl.cheque.db.ChequeDAO
import com.kyl.cheque.views.ChequesView
import com.kyl.cheque.views.RecipientView

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

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

//    @Timed
    @GET
    @Path('/all')
    public ChequesView getChequesView() {
        return new ChequesView(this.dao.getAllCheques())
    }

//    @Timed
    @GET
    @Path('/recipient/{recipient}')
    public RecipientView getRecipientView(@PathParam('recipient') String recipient) {
        def cheques = this.dao.getAllChequesPaidTo(recipient)
        return new RecipientView(cheques)
    }

}
