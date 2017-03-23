package com.kyl.cheque.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.kyl.cheque.core.Cheque
import com.kyl.cheque.core.ChequeTest
import com.kyl.cheque.core.MoneyFormatter
import com.kyl.cheque.db.ChequeDAO
import com.kyl.cheque.resources.ChequesResources
import io.dropwizard.testing.junit.ResourceTestRule
import org.junit.After
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test

import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response

import static junit.framework.TestCase.assertEquals
import static junit.framework.TestCase.assertTrue
import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.reset
import static org.mockito.Mockito.when

/**
 * Created on 2016-09-04.
 */
class ChequeResourcesTest {

    static ChequeDAO dao = mock(ChequeDAO.class)
    static MoneyFormatter formatter = mock(MoneyFormatter.class)

    @ClassRule
    public static final ResourceTestRule resource = ResourceTestRule.builder()
            .addResource(new ChequesResources(dao, formatter))
            .build()

    static ObjectMapper MAPPER = new ObjectMapper()

    @BeforeClass
    static void setUpClass() {
        MAPPER.registerModule(new JavaTimeModule())
    }

    @After
    void tearDown() {
        reset(dao)
        reset(formatter)
    }

    @Test
    void testGetAllChequesWithNullCheques() {
        when(dao.getAllCheques()).thenReturn(null)

        def response = resource.client().target('/cheque/service/all').request().get()

        assertEquals("Status should be NO_CONTENT.", Response.Status.NO_CONTENT.getStatusCode(), response.getStatus())
    }

    @Test
    void testGetAllChequesWithEmptyCheques() {
        when(dao.getAllCheques()).thenReturn(Collections.emptyList())

        def response = resource.client().target('/cheque/service/all').request().get()

        assertEquals("Status should be NO_CONTENT.", Response.Status.NO_CONTENT.getStatusCode(), response.getStatus())
    }

    @Test
    void testGetAllCheques() {
        final def cheques = [ChequeTest.createCheque(32, 12, 'Sam', '2016-08-19'),
                             ChequeTest.createCheque(84, 42, 'Tom', '2016-08-12'),
                             ChequeTest.createCheque(72, 62, 'Sam', '2016-08-23')]

        when(dao.getAllCheques()).thenReturn(cheques)

        def response = resource.client().target('/cheque/service/all').request().get()

        assertEquals("Status should be OK.", Response.Status.OK.getStatusCode(), response.getStatus())

        def result = response.readEntity(new GenericType<List<Cheque>>(){})

        assertEquals('Cheques should not change.', cheques, result)
    }

    @Test
    void testGetNonExistingCheque() {
        def response = resource.client().target('/cheque/service/id/' + 99999L).request().get()

        assertEquals('Status should be 404.', Response.Status.NOT_FOUND.getStatusCode(), response.getStatus())
    }

    @Test
    void testGetValidCheque() {
        final def chequeID = 1
        final def expected = ChequeTest.createCheque(20, 30, 'Sam', '2016-06-12')

        when(dao.getCheque(chequeID)).thenReturn(expected)

        def response = resource.client().target('/cheque/service/id/' + chequeID).request().get()

        assertEquals('Status should be OK.', Response.Status.OK.getStatusCode(), response.getStatus())

        def result = response.readEntity(Cheque.class)

        assertEquals('Cheque is not expected value.', expected, result)
    }

    @Test
    void testGetNonExistingRecipient() {
        final def recipient = 'no one'
        final List<Cheque> expected = Collections.emptyList()

        when(dao.getAllChequesPaidTo(recipient)).thenReturn(expected)

        def response = resource.client().target('/cheque/service/recipient/' + recipient).request().get()

        assertEquals('Status should be 404.', Response.Status.NOT_FOUND.getStatusCode(), response.getStatus())
    }

    @Test
    void testGetRecipient() {
        final def recipient = 'Sam'
        final def cheques = [ChequeTest.createCheque(32, 12, recipient, '2016-08-19'),
                             ChequeTest.createCheque(72, 62, recipient, '2016-08-23')]

        when(dao.getAllChequesPaidTo(recipient)).thenReturn(cheques)

        def response = resource.client().target('/cheque/service/recipient/' + recipient).request().get()

        assertEquals('Status should be OK.', Response.Status.OK.getStatusCode(), response.getStatus())

        def result = response.readEntity(new GenericType<List<Cheque>>(){})

        assertEquals('Expecting list of cheques.', cheques, result)
    }

    @Test
    void testPutNegativeDollar() {
        final def cheque = ChequeTest.createCheque(-1, 30, 'sam', '2016-08-23')

        def response = resource.client().target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        assertEquals('Status should be UNPROCESSABLE_ENTITY.', 422, response.getStatus())

        def errors = response.readEntity(Map.class)
        assertTrue('Dollar validation error message',
                ['dollar must be greater than or equal to 0', 'Amount must be greater than 0.0'].contains(
                        errors['errors'][0]))
    }

    @Test
    void testPutNegativeCent() {
        final def cheque = ChequeTest.createCheque(20, -1, 'sam', '2016-08-23')

        def response = resource.client().target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        assertEquals('Status should be UNPROCESSABLE_ENTITY.', 422, response.getStatus())

        def errors = response.readEntity(Map.class)
        assertEquals('Cent validation error message',
                'cent must be greater than or equal to 0', errors['errors'][0])
    }

    @Test
    void testPutChequeWithBadCent() {
        final def cheque = ChequeTest.createCheque(20, 130, 'sam', '2016-05-14')

        def response = resource.client().target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        assertEquals('Status should be UNPROCESSABLE_ENTITY.', 422, response.getStatus())

        def errors = response.readEntity(Map.class)
        assertEquals('Cent validation error message',
                'cent must be less than or equal to 99', errors['errors'][0])
    }

    @Test
    void testPutZeroAmount() {
        final def cheque = ChequeTest.createCheque(0, 0, 'sam', '2016-08-23')

        def response = resource.client().target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        assertEquals('Status should be UNPROCESSABLE_ENTITY.', 422, response.getStatus())
    }

    @Test
    void testPutChequeWithNullRecipient() {
        final def cheque = ChequeTest.createCheque(20, 30, 'NULL', '2016-05-14')
        final def jsonString = MAPPER.writeValueAsString(cheque).replace('"NULL"', 'null')

        def response = resource.client().target('/cheque/service/put')
                .request().put(Entity.json(jsonString))

        assertEquals('Status should be UNPROCESSABLE_ENTITY.', 422, response.getStatus())
    }

    @Test
    void testPutChequeWithEmptyRecipient() {
        final def cheque = ChequeTest.createCheque(20, 30, '', '2016-05-14')

        def response = resource.client().target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        assertEquals('Status should be UNPROCESSABLE_ENTITY.', 422, response.getStatus())
    }

    @Test
    void testPutChequeWithNullPaymentDate() {
        final def cheque = ChequeTest.createCheque(20, 30, 'Sam', '2016-06-14')
        final def jsonString = MAPPER.writeValueAsString(cheque).replace("[2016,6,14]", 'null')

        def response = resource.client().target('/cheque/service/put')
                .request().put(Entity.json(jsonString))

        assertEquals('Status should be UNPROCESSABLE_ENTITY.', 422, response.getStatus())
    }

    @Test
    void testPutChequeWithInvalidPaymentDate() {
        final def cheque = ChequeTest.createCheque(20, 30, 'Sam', '2016-06-14')
        final def jsonString = MAPPER.writeValueAsString(cheque).replace("[2016,6,14]", '[dasfa]')

        def response = resource.client().target('/cheque/service/put')
                .request().put(Entity.json(jsonString))

        assertEquals('Status should be BAD_REQUEST.', Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus())
    }

    @Test
    void testPutValidCheque() {
        final def cheque = ChequeTest.createCheque(20, 30, 'Sam', '2016-06-14')
        final def chequeID = 1l

        when(dao.insertCheque(cheque)).thenReturn(chequeID)

        def response = resource.client().target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        assertEquals('Status should be CREATED.', Response.Status.CREATED.getStatusCode(), response.getStatus())

        assertTrue('Location in response should point to the new cheque.',
                response.getLocation().toString().contains('/cheque/service/id/' + chequeID))
    }

    @Test
    void testPutValidChequeBadInsert() {
        final def cheque = ChequeTest.createCheque(20, 30, 'Sam', '2016-06-14')
        final def chequeID = 0l

        when(dao.insertCheque(cheque)).thenReturn(chequeID)

        def response = resource.client().target('/cheque/service/put')
                .request().put(Entity.json(cheque))

        assertEquals('Status should be NOT_MODIFIED.',
                Response.Status.NOT_MODIFIED.getStatusCode(),
                response.getStatus())
    }

    @Test
    void testPutChequeWithInsertException() {
        final cheque = ChequeTest.createCheque(1, 0, 'Sam','2016-05-13')

        when(dao.insertCheque(cheque)).thenThrow(RuntimeException.class)

        def response = resource.client().target('/cheque/service/put').request().put(Entity.json(cheque))

        assertEquals('Exception when insert into database.',
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                response.getStatus())
    }
}
