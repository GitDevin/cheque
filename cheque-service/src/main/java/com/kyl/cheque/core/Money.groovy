package com.kyl.cheque.core

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

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
        return "${dollar}.${cent}"
    }

    String toString() {
        return getAmount()
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Money money = (Money) o

        if (cent != money.cent) return false
        if (dollar != money.dollar) return false

        return true
    }

    int hashCode() {
        int result
        result = dollar
        result = 31 * result + cent
        return result
    }
}
