package com.kyl.cheque.resource

import com.kyl.cheque.resources.HomepageViewResources
import io.dropwizard.testing.junit.ResourceTestRule
import org.junit.ClassRule
import org.junit.jupiter.api.Test

import javax.ws.rs.core.Response

import static junit.framework.TestCase.assertTrue
import static org.junit.Assert.assertEquals

/**
 * Created on 2016-09-04.
 */
class HomePageResourcesIT {

    @ClassRule
    public static final ResourceTestRule resource = ResourceTestRule.builder()
            .addResource(new HomepageViewResources())
            .build()

    @Test
    public void testHomepageViewResources() {
        def response = resource.client().target('/').request().get()

        assertEquals('Response code should be 404', Response.Status.NOT_FOUND.getStatusCode(), response.getStatus())

        assertTrue(response.context.resolvedUri.toString().contains('cheque/view/all'))
    }
}
