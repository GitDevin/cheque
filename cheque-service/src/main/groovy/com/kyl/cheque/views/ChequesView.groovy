package com.kyl.cheque.views

import com.kyl.cheque.core.Cheque
import io.dropwizard.views.View

/**
 * Created on 2016-04-20.
 */
class ChequesView extends View {
    List<Cheque> cheques
    protected ChequesView(List<Cheque> cheques) {
        super('cheques.ftl')
        this.cheques = cheques
    }

    public List<Cheque> getCheques() {
        return this.cheques
    }
}
