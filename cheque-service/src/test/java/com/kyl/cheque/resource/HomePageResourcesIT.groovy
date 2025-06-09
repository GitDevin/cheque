package com.kyl.cheque.resource

import com.kyl.cheque.resources.HomepageViewResources
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import io.dropwizard.testing.junit5.ResourceExtension
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import jakarta.ws.rs.core.GenericType
import jakarta.ws.rs.core.Response


/**
 * Created on 2016-09-04.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
class HomePageResourcesIT {

    private static final ResourceExtension EXT = ResourceExtension.builder()
            .setTestContainerFactory(new InMemoryTestContainerFactory())
            .addResource(new HomepageViewResources())
            .build()

    @Test
    public void testHomepageViewResources() {
        def response = EXT.client().target('/').request().get()

        def result = response.readEntity(new GenericType<Map<String, String>>(){})

        Assertions.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus(), 'Response code should be 404')

        Assertions.assertTrue(response.context.resolvedUri.toString().contains('cheque/view/all'))
    }
}
