package com.kyl.cheque.db

import com.kyl.cheque.core.Cheque
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

/**
 * Created on 2016-04-20.
 */
interface MySQLChequeDAO extends ChequeDAO {

    @SqlQuery("""SELECT
                    CHEQUE_ID AS chequeId, DOLLAR, CENT, RECIPIENT, PAYMENT_DATE AS paymentDate,
                    AMOUNT_DESC AS amountDesc, ROWADDDT AS addedTime, ROWUPDDT AS updatedTime
                 FROM FINANCE.CHEQUE""")
    @RegisterBeanMapper(Cheque.class)
    List<Cheque> getAllCheques()

    @SqlQuery("""SELECT
                    CHEQUE_ID AS chequeId, DOLLAR, CENT, RECIPIENT, PAYMENT_DATE AS paymentDate,
                    AMOUNT_DESC AS amountDesc, ROWADDDT AS addedTime, ROWUPDDT AS updatedTime
                 FROM FINANCE.CHEQUE
                 WHERE CHEQUE_ID = :cheque_Id""")
    @RegisterBeanMapper(Cheque.class)
    Cheque getCheque(@Bind("cheque_Id") long chequeId)

    @SqlQuery("""SELECT
                    CHEQUE_ID AS chequeId, DOLLAR, CENT, RECIPIENT, PAYMENT_DATE AS paymentDate,
                    AMOUNT_DESC AS amountDesc, ROWADDDT AS addedTime, ROWUPDDT AS updatedTime
                 FROM FINANCE.CHEQUE
                 WHERE RECIPIENT = :recipient""")
    @RegisterBeanMapper(Cheque.class)
    List<Cheque> getAllChequesPaidTo(@Bind("recipient") String recipient)

    @SqlUpdate("""INSERT INTO FINANCE.CHEQUE
                    (DOLLAR, CENT, RECIPIENT, PAYMENT_DATE, AMOUNT_DESC, ROWADDDT, ROWUPDDT)
                  VALUES
                    (:c.dollar, :c.cent, :c.recipient, :c.paymentDate, :c.amountDesc,
                    NOW(), NOW())""")
    @GetGeneratedKeys
    long insertCheque(@BindBean("c") Cheque cheque)
}
