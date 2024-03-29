package com.kyl.cheque

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.kyl.cheque.core.Cheque
import com.kyl.cheque.core.ChequeTest
import io.dropwizard.testing.ConfigOverride
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


/**
 * Created on 2016-09-06.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
class ApplicationIT {
    static final String DB_URL = "jdbc:h2:mem:test;MODE=ORACLE"
    static final String DB_USER = "cheque-user"
    static final String DB_PASSWORD = "cheque-p4ssw0rd"

    static JacksonJsonProvider JACKSON_JSON_PROVIDER = new JacksonJaxbJsonProvider()

    private static DropwizardAppExtension<ChequeConfiguration> EXT = new DropwizardAppExtension<>(
            ChequeApplication.class,
            ResourceHelpers.resourceFilePath("cheque.yml"),
            ConfigOverride.config("database.driverClass", "org.h2.Driver"),
            ConfigOverride.config("database.url", DB_URL),
            ConfigOverride.config("database.user", DB_USER),
            ConfigOverride.config("database.password", DB_PASSWORD),
            ConfigOverride.config("flyway.locations", "filesystem:src/test/resources/db/migration"),
            ConfigOverride.config("flyway.schemas", "FINANCE"),
            ConfigOverride.config("flyway.locations", "filesystem:src/test/resources/db/migration")
    )

    static Client client
    static Flyway flyway

    @BeforeAll
    static void setUpClass() throws Exception {
        JACKSON_JSON_PROVIDER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        JACKSON_JSON_PROVIDER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

        JACKSON_JSON_PROVIDER.locateMapper(Object.class, MediaType.APPLICATION_JSON_TYPE)
                .registerModule(new JavaTimeModule())
    }

    @BeforeEach
    public void setUp() {
        client = EXT.client().register(JACKSON_JSON_PROVIDER)
    }

    @AfterEach
    public void tearDownEach() {
        def dataSource = EXT.getConfiguration().getDataSourceFactory().build(EXT.getApplication().bootstrap.getMetricRegistry(), 'Flyway')
        flyway = EXT.getConfiguration().getFlywayFactory().build(dataSource)
        flyway.clean()
        flyway.migrate()
    }

    @AfterAll
    public static void tearDown() {
        client?.close()
        flyway?.clean()
    }

    @Test
    public void testGetAllCheque() {
        final def url = "http://localhost:${EXT.getPort(0)}/cheque/service/all"

        def response = client.target(url).request().get()

        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), 'Status should be OK.')

        def cheques = response.readEntity(new GenericType<List<Cheque>>(){})

        def expect = [ChequeTest.createCheque(20, 30, 'Sam', '2016-06-12'),
                      ChequeTest.createCheque(44, 89, 'Tom', '2016-06-17'),
                      ChequeTest.createCheque(66, 87, 'Sam', '2016-06-20')]

        Assertions.assertEquals(expect, cheques, 'Cheques should equal to expected.')
    }

    @Test
    public void testGetCheque() {
        final def chequeID = 3
        final def url = "http://localhost:${EXT.getPort(0)}/cheque/service/id/${chequeID}"

        def response = client.target(url).request().get()

        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), 'Status should be OK.')

        def cheque = response.readEntity(Cheque.class)

        def expect = ChequeTest.createCheque(66, 87, 'Sam', '2016-06-20')

        Assertions.assertEquals(expect, cheque, 'Cheque should equal to expected.')
    }

    @Test
    public void testGetChequesPaidTo() {
        final def recipient = 'Sam'
        final def url = "http://localhost:${EXT.getPort(0)}/cheque/service/recipient/${recipient}"

        def response = client.target(url).request().get()

        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), 'Status should be OK.')

        def cheques = response.readEntity(new GenericType<List<Cheque>>(){})

        def expect = [ChequeTest.createCheque(20, 30, 'Sam', '2016-06-12'),
                      ChequeTest.createCheque(66, 87, 'Sam', '2016-06-20')]

        Assertions.assertEquals(expect, cheques, 'Cheques should equal to expected.')
    }

    @Test
    public void testPutCheque() {
        final def url = "http://localhost:${EXT.getPort(0)}/cheque/service/put"
        final def cheque = ChequeTest.createCheque(29, 83, 'Linda', '2016-08-23')

        def response = client.target(url).request().put(Entity.json(cheque))
        def result = response.readEntity(new GenericType<Map<String, String>>(){})

        Assertions.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus(), 'Status should be CREATED.')

        Assertions.assertTrue(response.getLocation().toString().endsWith('/cheque/service/id/4'), 'Location should contain expected URI.')
    }
}
