package com.kyl.cheque.core

import com.fasterxml.jackson.databind.ObjectMapper
import io.dropwizard.jackson.Jackson
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static org.junit.Assert.assertEquals

/**
 * Created on 2016-09-03.
 */
class ChequeTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper()

    static Cheque createCheque(int dollar, int cent, String recipient, String paymentDate) {
        def cheque = new Cheque()
        cheque.setDollar(dollar)
        cheque.setCent(cent)
        cheque.setRecipient(recipient)
        cheque.setPaymentDate(LocalDate.parse(paymentDate))
        return cheque
    }

    static void assertCheque(Cheque cheque, int dollar, int cent, String recipient, String paymentDate) {
        assertEquals("\$${dollar} in the first cheque.", cheque.getDollar(), dollar)
        assertEquals("${cent} cents in the first cheque.", cheque.getCent(), cent)
        assertEquals("Cheque payable to ${recipient}.", cheque.getRecipient(), recipient)
        def expected = LocalDate.parse(paymentDate)

        assertEquals("Cheque payment date should be ${expected}.", cheque.getPaymentDate(), expected)
    }

    @Test
    void serializesToJSON() throws IOException {
        final def cheque = createCheque(20, 30, 'receiver', '2016-06-14')

        final def expected = MAPPER.writeValueAsString(
                MAPPER.readValue(getClass().getResource("/fixtures/cheque.json"), Cheque.class))

        assertEquals("BriefSummary serialization test.", MAPPER.writeValueAsString(cheque), expected)
    }

    @Test
    void deserialize() {
        def cheque = MAPPER.readValue(getClass().getResource('/fixtures/cheque.json'), Cheque.class)
        def expected = createCheque(20, 30, 'receiver', '2016-06-14')

        assertEquals('Cheque successfully deserialized.', expected, cheque)
    }
}
