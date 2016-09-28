package com.kyl.cheque

import com.kyl.cheque.core.ICUMoneyFormatter
import com.kyl.cheque.db.MySQLChequeDAO
import com.kyl.cheque.resources.ChequeViewResources
import com.kyl.cheque.resources.ChequesResources
import com.kyl.cheque.resources.ChequesViewResources
import com.kyl.cheque.resources.HomepageViewResources
import com.kyl.cheque.resources.MoneySpellerResources
import io.dropwizard.Application
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.flyway.FlywayBundle
import io.dropwizard.flyway.FlywayFactory
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.views.ViewBundle
import org.skife.jdbi.v2.DBI

/**
 * Created on 2016-04-20.
 */
class ChequeApplication extends Application<ChequeConfiguration> {
    Bootstrap<ChequeConfiguration> bootstrap

    public static void main(String[] args) {
        new ChequeApplication().run(args)
    }

    void initialize(Bootstrap<ChequeConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/assets/"))
        bootstrap.addBundle(new ViewBundle<ChequeConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(ChequeConfiguration config) {
                return config.getViewRendererConfiguration()
            }
        })

        bootstrap.addBundle(new FlywayBundle<ChequeConfiguration>() {
            @Override
            DataSourceFactory getDataSourceFactory(ChequeConfiguration chequeConfiguration) {
                return chequeConfiguration.getDataSourceFactory()
            }

            @Override
            FlywayFactory getFlywayFactory(ChequeConfiguration chequeConfiguration) {
                return chequeConfiguration.getFlywayFactory()
            }
        })

        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor()))

        this.bootstrap = bootstrap
    }

    void migrateDatabase(ChequeConfiguration chequesConfiguration, Bootstrap<ChequeConfiguration> bootstrap) {
        def dataSource = chequesConfiguration.getDataSourceFactory().build(bootstrap.getMetricRegistry(), 'Flyway')
        def flyway = chequesConfiguration.getFlywayFactory().build(dataSource)
        flyway.migrate()
    }

    @Override
    void run(ChequeConfiguration chequesConfiguration, Environment environment) throws Exception {
        migrateDatabase(chequesConfiguration, this.bootstrap)

        def moneyFormatter = new ICUMoneyFormatter()
        environment.jersey().register(new MoneySpellerResources(moneyFormatter))

        final DBIFactory factory = new DBIFactory()
        final DBI jdbi = factory.build(environment, chequesConfiguration.getDataSourceFactory(), "mysql")

        final MySQLChequeDAO mySQLChequeDAO = jdbi.onDemand(MySQLChequeDAO.class)

        environment.jersey().register(new HomepageViewResources())
        environment.jersey().register(new ChequesResources(mySQLChequeDAO, moneyFormatter))
        environment.jersey().register(new ChequesViewResources(mySQLChequeDAO))
        environment.jersey().register(new ChequeViewResources(mySQLChequeDAO))
    }
}
