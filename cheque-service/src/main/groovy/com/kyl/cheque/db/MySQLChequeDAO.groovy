package com.kyl.cheque.db

import com.kyl.cheque.core.Cheque
import org.skife.jdbi.v2.sqlobject.*
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory
import org.skife.jdbi.v2.tweak.BeanMapperFactory

/**
 * Created on 2016-04-20.
 */
@RegisterMapperFactory(BeanMapperFactory.class)
interface MySQLChequeDAO extends ChequeDAO {

    @SqlQuery("""SELECT
                    CHEQUE_ID AS chequeId, DOLLAR, CENT, RECIPIENT, PAYMENT_DATE AS paymentDate,
                    AMOUNT_DESC AS amountDesc, ROWADDDT AS addedTime, ROWUPDDT AS updatedTime
                 FROM FINANCE.CHEQUE""")
    List<Cheque> getAllCheques()

    @SqlQuery("""SELECT
                    CHEQUE_ID AS chequeId, DOLLAR, CENT, RECIPIENT, PAYMENT_DATE AS paymentDate,
                    AMOUNT_DESC AS amountDesc, ROWADDDT AS addedTime, ROWUPDDT AS updatedTime
                 FROM FINANCE.CHEQUE
                 WHERE CHEQUE_ID = :cheque_Id""")
    Cheque getCheque(@Bind("cheque_Id") long chequeId)

    @SqlQuery("""SELECT
                    CHEQUE_ID AS chequeId, DOLLAR, CENT, RECIPIENT, PAYMENT_DATE AS paymentDate,
                    AMOUNT_DESC AS amountDesc, ROWADDDT AS addedTime, ROWUPDDT AS updatedTime
                 FROM FINANCE.CHEQUE
                 WHERE RECIPIENT = :recipient""")
    List<Cheque> getAllChequesPaidTo(@Bind("recipient") String recipient)

    @SqlUpdate("""INSERT INTO FINANCE.CHEQUE
                    (CHEQUE_ID, DOLLAR, CENT, RECIPIENT, PAYMENT_DATE, AMOUNT_DESC, ROWADDDT, ROWUPDDT)
                  VALUES
                    (NULL, :c.dollar, :c.cent, :c.recipient, :c.paymentDate, :c.amountDesc,
                    NOW(), NOW())""")
    @GetGeneratedKeys
    long insertCheque(@BindBean("c") Cheque cheque)
}
