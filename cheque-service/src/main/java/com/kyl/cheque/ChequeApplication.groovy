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
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.views.ViewBundle
import org.skife.jdbi.v2.DBI

/**
 * Created on 2016-04-20.
 */
class ChequeApplication extends Application<ChequeConfiguration> {

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
    }

    @Override
    void run(ChequeConfiguration chequesConfiguration, Environment environment) throws Exception {
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
