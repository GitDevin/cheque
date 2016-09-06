package com.kyl.cheque.views

import com.kyl.cheque.core.Cheque
import io.dropwizard.views.View

/**
 * Created on 2016-04-21.
 */
class ChequeView extends View {

    Cheque cheque
    final boolean ifReadOnly

    protected ChequeView(Cheque cheque, boolean ifReadOnly) {
        super('cheque.ftl')
        this.cheque = cheque
        this.ifReadOnly = ifReadOnly
    }

    public Cheque getCheque() {
        return this.cheque
    }

    public boolean getIfReadOnly() {
        return this.ifReadOnly
    }
}
