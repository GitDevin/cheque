package com.kyl.cheque

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.core.Configuration
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.flyway.FlywayFactory

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

/**
 * Created on 2016-04-20.
 */
class ChequeConfiguration extends Configuration {
    @NotNull
    private Map<String, Map<String, String>> viewRendererConfiguration = [:]

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory()

    @Valid
    @NotNull
    private FlywayFactory flyway = new FlywayFactory()

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database
    }

    @JsonProperty("flyway")
    public FlywayFactory getFlywayFactory() {
        return flyway
    }

    @JsonProperty("flyway")
    public void setFlywayFactory(FlywayFactory flywayFactory) {
        this.flyway = flywayFactory
    }

    public Map<String, Map<String, String>> getViewRendererConfiguration() {
        return viewRendererConfiguration
    }
}
