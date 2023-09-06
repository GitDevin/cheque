package com.kyl.cheque.core

import com.fasterxml.jackson.databind.ObjectMapper
import io.dropwizard.jackson.Jackson
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals

/**
 * Created on 2016-09-03.
 */
class MoneyTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper()

    public static Money createMoney(int dollar, int cent) {
        def money = new Money()
        money.setDollar(dollar)
        money.setCent(cent)
        return money
    }

    @Test
    public void serializesToJSON() throws IOException {
        final def money = createMoney(34, 11)

        final def expected = MAPPER.writeValueAsString(
                MAPPER.readValue(getClass().getResource("/fixtures/money.json"), Money.class))

        assertEquals("BriefSummary serialization test.", MAPPER.writeValueAsString(money), expected)
    }

    @Test
    public void deserialize() {
        final def result = MAPPER.readValue(getClass().getResource('/fixtures/money.json'), Money.class)

        final def expected = createMoney(34, 11)

        assertEquals('Successfully deserialized money.json', expected, result)
    }
}
