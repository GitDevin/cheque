CREATE TABLE CHEQUE (
  CHEQUE_ID INT UNSIGNED NOT NULL AUTO_INCREMENT,
  DOLLAR INTEGER NOT NULL,
  CENT INTEGER NOT NULL,
  RECIPIENT VARCHAR(255) NOT NULL,
  PAYMENT_DATE DATE NOT NULL,
  AMOUNT_DESC VARCHAR(255),
  ROWADDDT TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
  ROWUPDDT TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (CHEQUE_ID));

CREATE INDEX IDX_RECIPIENT ON CHEQUE(RECIPIENT(64));

