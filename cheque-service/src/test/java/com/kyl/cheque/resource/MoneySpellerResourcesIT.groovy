package com.kyl.cheque.resource

import com.kyl.cheque.core.ICUMoneyFormatter
import com.kyl.cheque.core.MoneyFormatter
import com.kyl.cheque.core.MoneyTest
import com.kyl.cheque.resources.MoneySpellerResources
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import io.dropwizard.testing.junit5.ResourceExtension
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import jakarta.ws.rs.client.Entity
import jakarta.ws.rs.core.GenericType
import jakarta.ws.rs.core.Response

/**
*  Created on 2016-09-04.
*/
@ExtendWith(DropwizardExtensionsSupport.class)
class MoneySpellerResourcesIT {

    static MoneyFormatter moneyFormatter = new ICUMoneyFormatter()

    private static final ResourceExtension EXT = ResourceExtension.builder()
            .setTestContainerFactory(new InMemoryTestContainerFactory())
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
                request().post(Entity.json([nodollar: 32, nocent: 23]))

        Assertions.assertEquals(Response.Status.ACCEPTED.getStatusCode(),
                response.getStatus(), 'Status code should be ACCEPTED')
        def result = response.readEntity(new GenericType<Map<String, String>>(){})
        Assertions.assertEquals("zero dollar and zero cent", result['result'])
    }

    @Test
    public void testFormatMoneyWithThreeDigitCents() {
        final int badCent = 223
        def response = EXT.target('/money/service/format').
                request().post(Entity.json([dollar: 32, cent: badCent]))

        Assertions.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus(), 'Status code should be BAD_REQUEST')
    }
}
