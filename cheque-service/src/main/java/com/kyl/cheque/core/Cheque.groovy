package com.kyl.cheque.core

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.validation.ValidationMethod
import javax.validation.constraints.NotEmpty

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Created on 2016-04-20.
 */
class Cheque {
    @Min(0l)
    @JsonProperty('dollar')
    int dollar

    @Min(0l)
    @Max(99l)
    @JsonProperty('cent')
    int cent

    @NotEmpty
    @JsonProperty('recipient')
    String recipient

    @NotNull
    @JsonProperty('paymentDate')
    LocalDate paymentDate

    @JsonIgnore
    String amountDesc
    @JsonIgnore
    long chequeId
    @JsonIgnore
    LocalDateTime addedTime
    @JsonIgnore
    LocalDateTime updatedTime

    @JsonIgnore
    String getAmount() {
        return "${dollar}.${cent}"
    }

    String toString() {
        return "${chequeId} ${recipient} ${getAmount()} ${paymentDate} ${amountDesc}"
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Cheque cheque = (Cheque) o

        if (cent != cheque.cent) return false
        if (dollar != cheque.dollar) return false
        if (paymentDate != cheque.paymentDate) return false
        if (recipient != cheque.recipient) return false

        return true
    }

    int hashCode() {
        int result
        result = dollar
        result = 31 * result + cent
        result = 31 * result + (recipient != null ? recipient.hashCode() : 0)
        result = 31 * result + (paymentDate != null ? paymentDate.hashCode() : 0)
        return result
    }

    @ValidationMethod(message='Invalid cheque object')
    @JsonIgnore
    boolean isNotValidCheque() {
        if (dollar == 0 && cent == 0) {
            return false
        }
        if (recipient == null) {
            return false
        }
        if (paymentDate == null) {
            return false
        }
        return true
    }
}
