package com.kyl.cheque.resource

import com.kyl.cheque.resources.HomepageViewResources
import io.dropwizard.testing.junit5.ResourceExtension
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import javax.ws.rs.core.Response


/**
 * Created on 2016-09-04.
 */
class HomePageResourcesIT {

    private static final ResourceExtension EXT = ResourceExtension.builder()
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .addResource(new HomepageViewResources())
            .build()

    @Test
    public void testHomepageViewResources() {
        def response = EXT.client().target('/').request().get()

        Assertions.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus(), 'Response code should be 404')

        Assertions.assertTrue(response.context.resolvedUri.toString().contains('cheque/view/all'))
    }
}
