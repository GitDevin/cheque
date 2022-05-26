package com.kyl.cheque.db

import com.kyl.cheque.core.Cheque

/**
 * Created on 2016-04-20.
 */
interface ChequeDAO {
    List<Cheque> getAllCheques()
    Cheque getCheque(long chequeId)
    long insertCheque(Cheque cheque)
    List<Cheque> getAllChequesPaidTo(String recipient)
}
