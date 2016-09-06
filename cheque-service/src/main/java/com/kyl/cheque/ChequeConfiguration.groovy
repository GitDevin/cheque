package com.kyl.cheque

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Created on 2016-04-20.
 */
class ChequeConfiguration extends Configuration {
    @NotNull
    private Map<String, Map<String, String>> viewRendererConfiguration = [:]

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory()

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database
    }

    public Map<String, Map<String, String>> getViewRendererConfiguration() {
        return viewRendererConfiguration
    }
}
