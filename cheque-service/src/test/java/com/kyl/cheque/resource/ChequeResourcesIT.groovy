package com.kyl.cheque.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.kyl.cheque.core.Cheque
import com.kyl.cheque.core.ChequeTest
import com.kyl.cheque.core.MoneyFormatter
import com.kyl.cheque.db.ChequeDAO
import com.kyl.cheque.resources.ChequesResources
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport

import io.dropwizard.testing.junit5.ResourceExtension
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import jakarta.ws.rs.client.Entity
import jakarta.ws.rs.core.GenericType
import jakarta.ws.rs.core.Response


import static org.mockito.Mockito.mock
import static org.mockito.Mockito.reset
import static org.mockito.Mockito.when

/**
 * Created on 2016-09-04.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
class ChequeResourcesIT {

    static ChequeDAO dao = mock(ChequeDAO.class)
    static MoneyFormatter formatter = mock(MoneyFormatter.class)

    private static final ResourceExtension EXT = ResourceExtension.builder()
            .setTestContainerFactory(new InMemoryTestContainerFactory())
            .addResource(new ChequesResources(dao, formatter))
            .build()

    static ObjectMapper MAPPER = new ObjectMapper()

    @BeforeAll
    static void setUpClass() {
        MAPPER.registerModule(new JavaTimeModule())
    }

    @AfterEach
    void tearDown() {
        reset(dao)
        reset(formatter)
    }

    @Test
    void testGetAllChequesWithNullCheques() {
        when(dao.getAllCheques()).thenReturn(null)

        def response = EXT.target('/cheque/service/all').request().get()

        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus(), "Status should be NO_CONTENT.")
    }

    @Test
    void testGetAllChequesWithEmptyCheques() {
        when(dao.getAllCheques()).thenReturn(Collections.emptyList())

        def response = EXT.target('/cheque/service/all').request().get()

        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus(), "Status should be NO_CONTENT.")
    }

    @Test
    void testGetAllCheques() {
        final def cheques = [ChequeTest.createCheque(32, 12, 'Sam', '2016-08-19'),
                             ChequeTest.createCheque(84, 42, 'Tom', '2016-08-12'),
                             ChequeTest.createCheque(72, 62, 'Sam', '2016-08-23')]

        when(dao.getAllCheques()).thenReturn(cheques)

        def response = EXT.target('/cheque/service/all').request().get()

        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Status should be OK.")

        def result = response.readEntity(new GenericType<List<Cheque>>(){})

        Assertions.assertEquals(cheques, result, 'Cheques should not change.')
    }

    @Test
    void testGetNonExistingCheque() {
        def response = EXT.target('/cheque/service/id/' + 99999L).request().get()

        Assertions.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus(), 'Status should be 404.')
    }

    @Test
    void testGetValidCheque() {
        final def chequeID = 1
        final def expected = ChequeTest.createCheque(20, 30, 'Sam', '2016-06-12')

        when(dao.getCheque(chequeID)).thenReturn(expected)

        def response = EXT.target('/cheque/service/id/' + chequeID).request().get()

        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), 'Status should be OK.')

        def result = response.readEntity(Cheque.class)

        Assertions.assertEquals(expected, result, 'Cheque is not expected value.')
    }

    @Test
    void testGetNonExistingRecipient() {
        final def recipient = 'no one'
        final List<Cheque> expected = Collections.emptyList()

        when(dao.getAllChequesPaidTo(recipient)).thenReturn(expected)

        def response = EXT.target('/cheque/service/recipient/' + recipient).request().get()

        Assertions.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus(), 'Status should be 404.')
    }

    @Test
    void testGetRecipient() {
        final def recipient = 'Sam'
        final def cheques = [ChequeTest.createCheque(32, 12, recipient, '2016-08-19'),
                             ChequeTest.createCheque(72, 62, recipient, '2016-08-23')]

        when(dao.getAllChequesPaidTo(recipient)).thenReturn(cheques)

        def response = EXT.target('/cheque/service/recipient/' + recipient).request().get()

        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), 'Status should be OK.')

        def result = response.readEntity(new GenericType<List<Cheque>>(){})

        Assertions.assertEquals(cheques, result, 'Expecting list of cheques.')
    }

    @Test void testPutNegativeDollar() {
        final def cheque = ChequeTest.createCheque(-1, 30, 'sam', '2016-08-23')

        def response = EXT.target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        Assertions.assertEquals(422, response.getStatus(), 'Status should be UNPROCESSABLE_ENTITY.')

        def errors = response.readEntity(Map.class)
        Assertions.assertTrue(
                ['dollar must be greater than or equal to 0', 'Amount must be greater than 0.0'].contains(
                        errors['errors'][0]), 'Dollar validation error message')
    }

    @Test
    void testPutNegativeCent() {
        final def cheque = ChequeTest.createCheque(20, -1, 'sam', '2016-08-23')

        def response = EXT.target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        Assertions.assertEquals(422, response.getStatus(), 'Status should be UNPROCESSABLE_ENTITY.')

        def errors = response.readEntity(Map.class)
        Assertions.assertEquals(
                'cent must be greater than or equal to 0', errors['errors'][0], 'Cent validation error message')
    }

    @Test
    void testPutChequeWithBadCent() {
        final def cheque = ChequeTest.createCheque(20, 130, 'sam', '2016-05-14')

        def response = EXT.target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        Assertions.assertEquals(422, response.getStatus(), 'Status should be UNPROCESSABLE_ENTITY.')

        def errors = response.readEntity(Map.class)
        Assertions.assertEquals('cent must be less than or equal to 99', errors['errors'][0], 'Cent validation error message')
    }

    @Test
    void testPutZeroAmount() {
        final def cheque = ChequeTest.createCheque(0, 0, 'sam', '2016-08-23')

        def response = EXT.target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        Assertions.assertEquals(422, response.getStatus(), 'Status should be UNPROCESSABLE_ENTITY.')
    }

    @Test
    void testPutChequeWithNullRecipient() {
        final def cheque = ChequeTest.createCheque(20, 30, 'NULL', '2016-05-14')
        final def jsonString = MAPPER.writeValueAsString(cheque).replace('"NULL"', 'null')

        def response = EXT.target('/cheque/service/put')
                .request().put(Entity.json(jsonString))

        Assertions.assertEquals(422, response.getStatus(), 'Status should be UNPROCESSABLE_ENTITY.')
    }

    @Test
    void testPutChequeWithEmptyRecipient() {
        final def cheque = ChequeTest.createCheque(20, 30, '', '2016-05-14')

        def response = EXT.target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        Assertions.assertEquals(422, response.getStatus(), 'Status should be UNPROCESSABLE_ENTITY.')
    }

    @Test
    void testPutChequeWithNullPaymentDate() {
        final def cheque = ChequeTest.createCheque(20, 30, 'Sam', '2016-06-14')
        final def jsonString = MAPPER.writeValueAsString(cheque).replace("[2016,6,14]", 'null')

        def response = EXT.target('/cheque/service/put')
                .request().put(Entity.json(jsonString))

        Assertions.assertEquals(422, response.getStatus(), 'Status should be UNPROCESSABLE_ENTITY.')
    }

    @Test
    void testPutChequeWithInvalidPaymentDate() {
        final def cheque = ChequeTest.createCheque(20, 30, 'Sam', '2016-06-14')
        final def jsonString = MAPPER.writeValueAsString(cheque).replace("[2016,6,14]", '[dasfa]')

        def response = EXT.target('/cheque/service/put')
                .request().put(Entity.json(jsonString))

        Assertions.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus(), 'Status should be BAD_REQUEST.')
    }

    @Test
    void testPutValidCheque() {
        final def cheque = ChequeTest.createCheque(20, 30, 'Sam', '2016-06-14')
        final def chequeID = 1l

        when(dao.insertCheque(cheque)).thenReturn(chequeID)

        def response = EXT.target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        Assertions.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus(), 'Status should be CREATED.')

        Assertions.assertTrue(response.getLocation().toString().contains('/cheque/service/id/' + chequeID),
                'Location in response should point to the new cheque.')
    }

    @Test
    void testPutValidChequeBadInsert() {
        final def cheque = ChequeTest.createCheque(20, 30, 'Sam', '2016-06-14')
        final def chequeID = 0l

        when(dao.insertCheque(cheque)).thenReturn(chequeID)

        def response = EXT.target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        Assertions.assertEquals(
                Response.Status.NOT_MODIFIED.getStatusCode(),
                response.getStatus(), 'Status should be NOT_MODIFIED.')
    }

    @Test
    void testPutChequeWithInsertException() {
        final cheque = ChequeTest.createCheque(1, 0, 'Sam','2016-05-13')

        when(dao.insertCheque(cheque)).thenThrow(RuntimeException.class)

        def response = EXT.target('/cheque/service/put').request().put(Entity.json(cheque))

        Assertions.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                response.getStatus(), 'Exception when insert into database.')
    }
}
