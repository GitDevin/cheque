package com.kyl.cheque.core

import com.ibm.icu.text.RuleBasedNumberFormat
import com.ibm.icu.util.ULocale

import static com.google.common.base.Preconditions.checkArgument

/**
 * Created on 2016-04-22.
 */
class ICUMoneyFormatter implements MoneyFormatter {
    public static int MAX_VALUE = Integer.MAX_VALUE

    String format(long number, String localeId) {
        def locale = new ULocale(localeId)
        def format = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT)
        return format.format(number)
    }

    String formatMoney(Money money, String localId) {
        def dollar = money.getDollar()
        def cent = money.getCent()

        this.formatMoney(dollar, cent, localId)
    }

    @Override
    String formatMoney(int dollar, int cent, String localId) {
        checkArgument(cent < 100, "Cent value ${cent} exceeds the maximum value.", cent)

        checkArgument(dollar < MAX_VALUE, "Dollar value ${dollar} exceeds the maximum supported values.", dollar)

        def result = new StringBuilder()
        if (dollar && cent) {
            result.append(this.format(dollar, localId))
            result.append(dollar == 1 ? " dollar and " : " dollars and ")
            result.append(this.format(cent, localId))
            result.append(cent == 1 ? " cent" : " cents")
        } else if (dollar == 0 && cent) {
            result.append(this.format(cent, localId))
            result.append(cent == 1 ? " cent" : " cents")
        } else if (dollar && cent == 0) {
            result.append(this.format(dollar, localId))
            result.append(dollar == 1 ? " dollar" : " dollars")
        } else {
            result.append("zero dollar and zero cent")
        }
        return result.toString()
    }
}
