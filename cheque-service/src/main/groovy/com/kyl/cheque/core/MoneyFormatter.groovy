package com.kyl.cheque.core

/**
 * Created on 2016-04-22.
 */
interface MoneyFormatter {
    String formatMoney(Money money, String localId)
    String formatMoney(int dollar, int cent, String localId)
}
