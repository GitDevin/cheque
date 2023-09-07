INSERT INTO CHEQUE
  (DOLLAR, CENT, RECIPIENT, PAYMENT_DATE, AMOUNT_DESC, ROWADDDT, ROWUPDDT)
VALUES
  (20, 30, 'Sam', TO_TIMESTAMP('06/12/2016 10:45:55','mm/dd/yyyy hh24:mi.ss'), 'twenty dollars and thirty cents',
    CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
  (44, 89, 'Tom', TO_TIMESTAMP('06/17/2016 14:45:55','mm/dd/yyyy hh24:mi.ss'),
   'forty four dollars and eighty nine cents', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
  (66, 87, 'Sam', TO_TIMESTAMP('06/20/2016 20:45:55','mm/dd/yyyy hh24:mi.ss'),
   'sixty six dollars and eighty seven cents', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
