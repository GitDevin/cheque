package com.kyl.cheque.core

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.MoreObjects

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

/**
 * Created on 2016-04-22.
 */
class Money {

    @Min(0l)
    @NotNull
    @JsonProperty("dollar")
    int dollar

    @Min(0l)
    @NotNull
    @JsonProperty("cent")
    int cent

    @JsonIgnore
    String getAmount() {
        return MoreObjects.toStringHelper(this).
                add("Dollar", dollar).
                add("Cent", cent).
                toString()
    }

    String toString() {
        return getAmount()
    }

    boolean equals(o) {
        if (this.is(o)) return true

        Money money = (Money) o

        return Objects.equals(getClass(), o.getClass()) &&
                Objects.equals(cent, money.cent) &&
                Objects.equals(dollar, money.dollar)
    }

    int hashCode() {
        return Objects.hash(dollar, cent)
    }
}
