package com.kyl.cheque.core

/**
 * Created on 2016-04-22.
 */
class ICUMoneyFormatterTest extends GroovyTestCase {
    ICUMoneyFormatter formatter

    void setUp() {
        formatter = new ICUMoneyFormatter()
    }

    void testInteger() {
        def result = formatter.format(33, "en_AU")
        assert "thirty-three" == result
    }

    void testLargeValue() {
        def result = formatter.format(23468938477213, "en_AU")
        assert 'twenty-three trillion four hundred sixty-eight billion nine hundred thirty-eight million four hundred seventy-seven thousand two hundred thirteen' == result
    }

    void testMaxValue() {
        def result = formatter.format(Integer.MAX_VALUE, "en_AU")
        println(Integer.MAX_VALUE)
        assert "two billion one hundred forty-seven million four hundred eighty-three thousand six hundred forty-seven" == result
    }

    void testZeroMoney() {
        def money = new Money(dollar: 0, cent: 0)
        def result = formatter.formatMoney(money, "en_AU")
        assert "zero dollar and zero cent" == result
    }

    void testOneCent() {
        def money = new Money(dollar: 0, cent: 1)
        def result = formatter.formatMoney(money, "en_AU")
        assert "one cent" == result
    }

    void testOneDollar() {
        def money = new Money(dollar: 1, cent: 0)
        def result = formatter.formatMoney(money, "en_AU")
        assert "one dollar" == result
    }

    void testOneDollarAndSomeCents() {
        def money = new Money(dollar: 1, cent: 12)
        def result = formatter.formatMoney(money, "en_AU")
        assert "one dollar and twelve cents" == result
    }

    void testSomeDollarAndOneCent() {
        def money = new Money(dollar: 23, cent: 1)
        def result = formatter.formatMoney(money, "en_AU")
        assert "twenty-three dollars and one cent" == result
    }

    void testSomeDollarsSomeCents() {
        def money = new Money(dollar: 95, cent: 87)
        def result = formatter.formatMoney(money, "en_AU")
        assert "ninety-five dollars and eighty-seven cents" == result
    }

    void testLargeAmount() {
        def money = new Money(dollar: 9472642, cent: 82)
        def result = formatter.formatMoney(money, "en_AU")
        assertEquals(
                "nine million four hundred seventy-two thousand six hundred forty-two dollars and eighty-two cents",
                result)
    }

    void testOneHundredCents() {
        def money = new Money(dollar: 3942, cent: 139)
        shouldFail(IllegalArgumentException) {
            formatter.formatMoney(money, "en_AU")
        }
    }

    void testExceedMaxValue() {
        def money = new Money(dollar: Integer.MAX_VALUE, cent: 139)
        shouldFail(IllegalArgumentException) {
            formatter.formatMoney(money, "en_AU")
        }
    }
}
