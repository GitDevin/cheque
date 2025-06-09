package com.kyl.cheque.db

import com.kyl.cheque.core.ChequeTest
import io.dropwizard.core.setup.Environment
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jdbi3.JdbiFactory
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.ClassicConfiguration
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class MySQLChequeDAOIT {
    private static final String DB_URL = "jdbc:h2:mem:test;MODE=ORACLE"
    static final String DB_USER = "cheque-user"
    static final String DB_PASSWORD = "cheque-p4ssw0rd"

    private static Jdbi jdbi
    private static Handle handle
    private static Flyway flyway

    private MySQLChequeDAO dao

    @BeforeAll
    static void setUpClass() throws Exception {
        def environment = new Environment("test-env")
        jdbi = new JdbiFactory().build(environment, getDataSourceFactory(), "test")
        handle = jdbi.open()

        def config = new ClassicConfiguration()
        config.setDataSource(DB_URL, DB_USER, DB_PASSWORD)
        config.setSchemas("FINANCE")
        config.setShouldCreateSchemas(true)
        config.setLocationsAsStrings("filesystem:src/test/resources/db/migration")
        config.setCleanDisabled(false)

        flyway = new Flyway(config)
        flyway.migrate()
    }

    @AfterAll
    static void tearDownClass() {
        handle?.close()
        flyway?.clean()
    }

    @BeforeEach
    void setUp() {
        dao = jdbi.onDemand(MySQLChequeDAO.class)
    }

    private static DataSourceFactory getDataSourceFactory() {
        def dataSourceFactory = new DataSourceFactory()
        dataSourceFactory.setDriverClass("org.h2.Driver")
        dataSourceFactory.setUrl(DB_URL)
        dataSourceFactory.setUser(DB_USER)
        dataSourceFactory.setPassword(DB_PASSWORD)
        return dataSourceFactory
    }

    @Test
    void testGetAllCheques() {
        def cheques = dao.getAllCheques()

        assertEquals("There should be 3 cheques in the db.", cheques.size(), 3)

        ChequeTest.assertCheque(cheques[0], 20, 30, 'Sam', '2016-06-12')
        ChequeTest.assertCheque(cheques[1], 44, 89, 'Tom', '2016-06-17')
        ChequeTest.assertCheque(cheques[2], 66, 87, 'Sam', '2016-06-20')
    }

    @Test
    void testGetCheque() {
        def cheque = dao.getCheque(2l)

        ChequeTest.assertCheque(cheque, 44, 89, 'Tom', '2016-06-17')
    }

    @Test
    void testGetAllChequesPaidTo() {
        def cheques = dao.getAllChequesPaidTo('Sam')

        assertEquals("There should be 2 cheques paid to Sam.", cheques.size(), 2)

        ChequeTest.assertCheque(cheques[0], 20, 30, 'Sam', '2016-06-12')
        ChequeTest.assertCheque(cheques[1], 66, 87, 'Sam', '2016-06-20')
    }

    @Test
    void testInsertCheque() {
        def cheque = ChequeTest.createCheque(23, 45, 'noone', '2016-06-25')

        int sizeBefore = dao.getAllCheques().size()
        long id = dao.insertCheque(cheque)
        int sizeAfter = dao.getAllCheques().size()

        assertTrue("New cheque ID should not be zero", id != 0)
        assertTrue("Cheques in the db should grow.", sizeAfter > sizeBefore)

        def resultCheque = dao.getCheque(id)

        ChequeTest.assertCheque(resultCheque, 23, 45, 'noone', '2016-06-25')
    }
}
