package com.kyl.cheque.views

import com.kyl.cheque.core.Cheque
import io.dropwizard.views.common.View

/**
 * Created on 2016-04-21.
 */
class ChequeView extends View {

    Cheque cheque
    final boolean ifReadOnly

    ChequeView(Cheque cheque, boolean ifReadOnly) {
        super('cheque.ftl')
        this.cheque = cheque
        this.ifReadOnly = ifReadOnly
    }

    Cheque getCheque() {
        return this.cheque
    }

    boolean getIfReadOnly() {
        return this.ifReadOnly
    }
}
