package com.kyl.cheque.resources

import com.codahale.metrics.annotation.Timed

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Created on 2016-05-02.
 */

@Path('/')
class HomepageViewResources {

    @Timed
    @GET
    @Path('/')
    Response getHomepage() {
        return Response.seeOther(URI.create('/cheque/view/all')).build()
    }
}
