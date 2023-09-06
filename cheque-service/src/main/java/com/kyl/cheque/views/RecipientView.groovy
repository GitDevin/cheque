package com.kyl.cheque.views

import com.kyl.cheque.core.Cheque
import io.dropwizard.views.common.View

/**
 * Created on 2016-04-21.
 */
class RecipientView extends View {
    List<Cheque> cheques
    String recipient

    protected RecipientView(List<Cheque> cheques) {
        super('recipient.ftl')
        this.cheques = cheques

        if (this.cheques.isEmpty()) {
            this.recipient = ""
        } else {
            this.recipient = this.cheques.first().getRecipient()
        }
    }
}
