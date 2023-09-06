package com.kyl.cheque.db

import com.codahale.metrics.MetricRegistry
import com.kyl.cheque.core.Cheque
import com.kyl.cheque.core.ChequeTest
import io.dropwizard.core.setup.Environment
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jackson.Jackson
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.testing.junit5.DAOTestExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.flywaydb.core.Flyway
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * Created on 2016-09-03.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
class MySQLChequeDAOIT {
    private static final String DB_URL = "jdbc:h2:mem:FINANCE;MODE=MSSQLServer"
    private static final String DB_USER = "sa"
    private static final String DB_PASSWORD = "sa_password"

    private static Jdbi jdbi
    private static Handle handle
    private MySQLChequeDAO dao

    public DAOTestExtension database = DAOTestExtension.newBuilder().addEntityClass(Cheque.class).build();

    @BeforeAll
    static void setUpClass() throws Exception {
        def environment = new Environment("test-env")
        jdbi = new JdbiFactory().build(environment, getDataSourceFactory(), "test")
        handle = jdbi.open()

        def flyway = new Flyway()
        flyway.setDataSource(DB_URL, DB_USER, DB_PASSWORD)
        flyway.setSchemas("FINANCE")
        flyway.migrate()
    }

    @AfterAll
    static void tearDownClass() {
        handle.close()
    }

    @BeforeEach
    void setUp() {
        def factory = database.getSessionFactory()

//        this.dao = new MySQLChequeDAO(database.getSessionFactory())
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
    public void testGetCheque() {
        def cheque = dao.getCheque(2l)

        ChequeTest.assertCheque(cheque, 44, 89, 'Tom', '2016-06-17')
    }

    @Test
    public void testGetAllChequesPaidTo() {
        def cheques = dao.getAllChequesPaidTo('Sam')

        assertEquals("There should be 2 cheques paid to Sam.", cheques.size(), 2)

        ChequeTest.assertCheque(cheques[0], 20, 30, 'Sam', '2016-06-12')
        ChequeTest.assertCheque(cheques[1], 66, 87, 'Sam', '2016-06-20')
    }

    @Test
    public void testInsertCheque() {
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
