package com.kyl.cheque.resources

//import com.codahale.metrics.annotation.Timed

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response

/**
 * Created on 2016-05-02.
 */

@Path('/')
class HomepageViewResources {

//    @Timed
    @GET
    @Path('/')
    public Response getHomepage() {
        return Response.seeOther(URI.create('/cheque/view/all')).build()
    }
}
