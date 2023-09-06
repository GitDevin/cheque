package com.kyl.cheque.resource

import com.kyl.cheque.core.ICUMoneyFormatter
import com.kyl.cheque.core.MoneyFormatter
import com.kyl.cheque.core.MoneyTest
import com.kyl.cheque.resources.MoneySpellerResources
import io.dropwizard.testing.junit5.ResourceExtension
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory
import org.junit.jupiter.api.Test

import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response

/**
*  Created on 2016-09-04.
*/
class MoneySpellerResourcesIT {

    static MoneyFormatter moneyFormatter = new ICUMoneyFormatter()

    private static final ResourceExtension EXT = ResourceExtension.builder()
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .addResource(new MoneySpellerResources(moneyFormatter))
            .build()

    @Test
    public void testFormatMoney() {
        def money = MoneyTest.createMoney(30, 56)

        def response = EXT.target('/money/service/format').
                request().post(Entity.json(money))

        Assertions.assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus(), 'Status code should be ACCEPTED')
        def result = response.readEntity(new GenericType<Map<String, String>>(){})
        Assertions.assertEquals("thirty dollars and fifty-six cents", result['result'])
    }

    @Test
    public void testFormatInvalidMoney() {
        def response = EXT.target('/money/service/format').
                request().post(Entity.json('{"dollar": "32", "nocents":"23"}'))

        Assertions.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus(), 'Status code should be BAD_REQUEST')
    }

    @Test
    public void testFormatMoneyWithThreeDigitCents() {
        final int badCent = 223

        def response = EXT.target('/money/service/format').
                request().post(Entity.json("{\"dollar\": \"32\", \"cent\":\"${badCent}\"}"))

        Assertions.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus(), 'Status code should be BAD_REQUEST')
    }
}
