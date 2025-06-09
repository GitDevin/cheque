package com.kyl.cheque

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.jersey3.InstrumentedResourceMethodApplicationListener
import com.kyl.cheque.core.ICUMoneyFormatter
import com.kyl.cheque.db.MySQLChequeDAO
import com.kyl.cheque.resources.ChequeViewResources
import com.kyl.cheque.resources.ChequesResources
import com.kyl.cheque.resources.ChequesViewResources
import com.kyl.cheque.resources.HomepageViewResources
import com.kyl.cheque.resources.MoneySpellerResources
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.core.Application
import io.dropwizard.db.PooledDataSourceFactory
import io.dropwizard.flyway.FlywayBundle
import io.dropwizard.flyway.FlywayFactory
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment
import io.dropwizard.views.common.ViewBundle
import org.jdbi.v3.core.Jdbi

/**
 * Created on 2016-04-20.
 */
class ChequeApplication extends Application<ChequeConfiguration> {
    Bootstrap<ChequeConfiguration> bootstrap

    static void main(String[] args) {
        new ChequeApplication().run(args)
    }

    void initialize(Bootstrap<ChequeConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/assets/"))
        bootstrap.addBundle(new ViewBundle<ChequeConfiguration>() {
            @Override
            Map<String, Map<String, String>> getViewConfiguration(ChequeConfiguration config) {
                return config.getViewRendererConfiguration()
            }
        })

        bootstrap.addBundle(new FlywayBundle<ChequeConfiguration>() {
            @Override
            FlywayFactory getFlywayFactory(ChequeConfiguration chequeConfiguration) {
                return chequeConfiguration.getFlywayFactory()
            }

            @Override
            PooledDataSourceFactory getDataSourceFactory(ChequeConfiguration chequeConfiguration) {
                return chequeConfiguration.getDataSourceFactory()
            }
        })

        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor()))

        this.bootstrap = bootstrap
    }

    void migrateDatabase(ChequeConfiguration chequesConfiguration, MetricRegistry metricRegistry) {
        def dataSource = chequesConfiguration.getDataSourceFactory().build(metricRegistry, 'Flyway')
        def flyway = chequesConfiguration.getFlywayFactory().build(dataSource)
        flyway.migrate()
    }

    @Override
    void run(ChequeConfiguration chequesConfiguration, Environment environment) throws Exception {
        migrateDatabase(chequesConfiguration, this.bootstrap.getMetricRegistry())

        def moneyFormatter = new ICUMoneyFormatter()
        environment.jersey().register(new MoneySpellerResources(moneyFormatter))

        final JdbiFactory factory = new JdbiFactory()
        final Jdbi jdbi = factory.build(environment, chequesConfiguration.getDataSourceFactory(), "mysql")

        final MySQLChequeDAO mySQLChequeDAO = jdbi.onDemand(MySQLChequeDAO.class)

        environment.jersey().register(new InstrumentedResourceMethodApplicationListener(new MetricRegistry()))
        environment.jersey().register(new HomepageViewResources())
        environment.jersey().register(new ChequesResources(mySQLChequeDAO, moneyFormatter))
        environment.jersey().register(new ChequesViewResources(mySQLChequeDAO))
        environment.jersey().register(new ChequeViewResources(mySQLChequeDAO))
    }
}
