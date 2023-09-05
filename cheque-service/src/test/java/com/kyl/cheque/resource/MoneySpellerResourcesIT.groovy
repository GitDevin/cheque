package com.kyl.cheque.resource

import com.kyl.cheque.core.ICUMoneyFormatter
import com.kyl.cheque.core.MoneyFormatter
import com.kyl.cheque.core.MoneyTest
import com.kyl.cheque.resources.MoneySpellerResources
import io.dropwizard.testing.junit.ResourceTestRule
import org.junit.ClassRule
import org.junit.jupiter.api.Test

import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response

import static org.junit.Assert.assertEquals

/**
*  Created on 2016-09-04.
*/
class MoneySpellerResourcesIT {

    static MoneyFormatter moneyFormatter = new ICUMoneyFormatter()

    @ClassRule
    public static final ResourceTestRule resource = ResourceTestRule.builder()
            .addResource(new MoneySpellerResources(moneyFormatter))
            .build()

    @Test
    public void testFormatMoney() {
        def money = MoneyTest.createMoney(30, 56)

        def response = resource.client().target('/money/service/format').
                request().post(Entity.json(money))

        assertEquals('Status code should be ACCEPTED', Response.Status.ACCEPTED.getStatusCode(), response.getStatus())
        def result = response.readEntity(new GenericType<Map<String, String>>(){})
        assertEquals("thirty dollars and fifty-six cents", result['result'])
    }

    @Test
    public void testFormatInvalidMoney() {
        def response = resource.client().target('/money/service/format').
                request().post(Entity.json('{"dollar": "32", "nocents":"23"}'))

        assertEquals('Status code should be BAD_REQUEST', Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus())
    }

    @Test
    public void testFormatMoneyWithThreeDigitCents() {
        final int badCent = 223

        def response = resource.client().target('/money/service/format').
                request().post(Entity.json("{\"dollar\": \"32\", \"cent\":\"${badCent}\"}"))

        assertEquals('Status code should be BAD_REQUEST', Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus())
    }
}
