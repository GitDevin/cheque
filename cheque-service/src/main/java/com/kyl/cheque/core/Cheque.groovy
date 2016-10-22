package com.kyl.cheque.core

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.MoreObjects
import io.dropwizard.validation.ValidationMethod
import org.hibernate.validator.constraints.NotEmpty

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
    public String getAmount() {
        return "${dollar}.${cent}"
    }

    boolean equals(o) {
        if (this.is(o)) return true

        Cheque cheque = (Cheque) o

        return Objects.equals(getClass(), o.class)  &&
                Objects.equals(cent, cheque.cent) &&
                Objects.equals(dollar, cheque.dollar) &&
                Objects.equals(paymentDate, cheque.paymentDate) &&
                Objects.equals(recipient, cheque.recipient)
    }

    String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ID", chequeId)
                .add("Recipient", recipient)
                .add("Amount", amount)
                .add("PaymentDate", paymentDate.toString())
                .add("Description", amountDesc)
                .toString()
    }

    int hashCode() {
        return Objects.hash(dollar, cent, recipient, paymentDate);
    }

    @ValidationMethod(message='Invalid cheque object')
    @JsonIgnore
    public boolean isNotValidCheque() {
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
