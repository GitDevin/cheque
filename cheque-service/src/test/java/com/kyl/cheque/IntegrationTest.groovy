package com.kyl.cheque

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.kyl.cheque.core.Cheque
import com.kyl.cheque.core.ChequeTest
import io.dropwizard.testing.ConfigOverride
import io.dropwizard.testing.junit.DropwizardAppRule
import org.flywaydb.core.Flyway
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import static junit.framework.TestCase.assertTrue
import static org.junit.Assert.assertEquals

/**
 * Created on 2016-09-06.
 */
class IntegrationTest {
    static final String CONFIG_PATH = "src/test/resources/cheque.yml"

    static final String DB_URL = "jdbc:h2:mem:FINANCEMODE=MSSQLServer"
    static final String DB_USER = "sa"
    static final String DB_PASSWORD = "sa_password"

    static JacksonJsonProvider JACKSON_JSON_PROVIDER = new JacksonJaxbJsonProvider()

    Flyway flyway

    @ClassRule
    public static final DropwizardAppRule<ChequeConfiguration> RULE = new DropwizardAppRule<>(
            ChequeApplication.class, CONFIG_PATH,
            ConfigOverride.config("database.driverClass", "org.h2.Driver"),
            ConfigOverride.config("database.url", DB_URL),
            ConfigOverride.config("database.user", DB_USER),
            ConfigOverride.config("database.password", DB_PASSWORD)
    )

    Client client

    @BeforeClass
    static void setUpClass() throws Exception {
        JACKSON_JSON_PROVIDER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        JACKSON_JSON_PROVIDER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

        JACKSON_JSON_PROVIDER.locateMapper(Object.class, MediaType.APPLICATION_JSON_TYPE)
                .registerModule(new JavaTimeModule())
    }

    @Before
    void setUp() {
        client = ClientBuilder.newClient().register(JACKSON_JSON_PROVIDER)

        flyway = new Flyway()
        flyway.setDataSource(DB_URL, DB_USER, DB_PASSWORD)
        flyway.setSchemas("FINANCE")
        flyway.migrate()
    }

    @After
    void tearDown() {
        client.close()
        flyway.clean()
    }

    @Test
    void testGetAllCheque() {
        final def url = "http://localhost:${RULE.getLocalPort()}/cheque/service/all"

        def response = client.target(url).request().get()

        assertEquals('Status should be OK.', Response.Status.OK.getStatusCode(), response.getStatus())

        def cheques = response.readEntity(new GenericType<List<Cheque>>(){})

        def expect = [ChequeTest.createCheque(20, 30, 'Sam', '2016-06-12'),
                      ChequeTest.createCheque(44, 89, 'Tom', '2016-06-17'),
                      ChequeTest.createCheque(66, 87, 'Sam', '2016-06-20')]

        assertEquals('Cheques should equal to expected.', expect, cheques)
    }

    @Test
    void testGetCheque() {
        final def chequeID = 3
        final def url = "http://localhost:${RULE.getLocalPort()}/cheque/service/id/${chequeID}"

        def response = client.target(url).request().get()

        assertEquals('Status should be OK.', Response.Status.OK.getStatusCode(), response.getStatus())

        def cheque = response.readEntity(Cheque.class)

        def expect = ChequeTest.createCheque(66, 87, 'Sam', '2016-06-20')

        assertEquals('Cheque should equal to expected.', expect, cheque)
    }

    @Test
    void testGetChequesPaidTo() {
        final def recipient = 'Sam'
        final def url = "http://localhost:${RULE.getLocalPort()}/cheque/service/recipient/${recipient}"

        def response = client.target(url).request().get()

        assertEquals('Status should be OK.', Response.Status.OK.getStatusCode(), response.getStatus())

        def cheques = response.readEntity(new GenericType<List<Cheque>>(){})

        def expect = [ChequeTest.createCheque(20, 30, 'Sam', '2016-06-12'),
                      ChequeTest.createCheque(66, 87, 'Sam', '2016-06-20')]

        assertEquals('Cheques should equal to expected.', expect, cheques)
    }

    @Test
    void testPutCheque() {
        final def url = "http://localhost:${RULE.getLocalPort()}/cheque/service/put"
        final def cheque = ChequeTest.createCheque(29, 83, 'Linda', '2016-08-23')

        def response = client.target(url).request().put(Entity.json(cheque))

        assertEquals('Status should be CREATED.', Response.Status.CREATED.getStatusCode(), response.getStatus())

        assertTrue('Location should contain expected URI.',
                response.getLocation().toString().endsWith('/cheque/service/id/4'))
    }
}
