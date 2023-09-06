package com.kyl.cheque.core

import groovy.test.GroovyAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

/**
 * Created on 2016-04-22.
 */
class ICUMoneyFormatterTest {
    ICUMoneyFormatter formatter

    @BeforeEach
    void setUp() {
        formatter = new ICUMoneyFormatter()
    }

    @Test
    void testInteger() {
        def result = formatter.format(33, "en_AU")
        assert "thirty-three" == result
    }

    @Test
    void testLargeValue() {
        def result = formatter.format(23468938477213, "en_AU")
        assert 'twenty-three trillion four hundred sixty-eight billion nine hundred thirty-eight million four hundred seventy-seven thousand two hundred thirteen' == result
    }

    @Test
    void testMaxValue() {
        def result = formatter.format(Integer.MAX_VALUE, "en_AU")
        println(Integer.MAX_VALUE)
        assert "two billion one hundred forty-seven million four hundred eighty-three thousand six hundred forty-seven" == result
    }

    @Test
    void testZeroMoney() {
        def money = new Money(dollar: 0, cent: 0)
        def result = formatter.formatMoney(money, "en_AU")
        assert "zero dollar and zero cent" == result
    }

    @Test
    void testOneCent() {
        def money = new Money(dollar: 0, cent: 1)
        def result = formatter.formatMoney(money, "en_AU")
        assert "one cent" == result
    }

    @Test
    void testOneDollar() {
        def money = new Money(dollar: 1, cent: 0)
        def result = formatter.formatMoney(money, "en_AU")
        assert "one dollar" == result
    }

    @Test
    void testOneDollarAndSomeCents() {
        def money = new Money(dollar: 1, cent: 12)
        def result = formatter.formatMoney(money, "en_AU")
        assert "one dollar and twelve cents" == result
    }

    @Test
    void testSomeDollarAndOneCent() {
        def money = new Money(dollar: 23, cent: 1)
        def result = formatter.formatMoney(money, "en_AU")
        assert "twenty-three dollars and one cent" == result
    }

    @Test
    void testSomeDollarsSomeCents() {
        def money = new Money(dollar: 95, cent: 87)
        def result = formatter.formatMoney(money, "en_AU")
        assert "ninety-five dollars and eighty-seven cents" == result
    }

    @Test
    void testLargeAmount() {
        def money = new Money(dollar: 9472642, cent: 82)
        def result = formatter.formatMoney(money, "en_AU")
        Assertions.assertEquals(
                "nine million four hundred seventy-two thousand six hundred forty-two dollars and eighty-two cents",
                result)
    }

    @Test
    void testOneHundredCents() {
        def money = new Money(dollar: 3942, cent: 139)
        GroovyAssert.shouldFail(IllegalArgumentException) {
            formatter.formatMoney(money, "en_AU")
        }
    }

    @Test
    void testExceedMaxValue() {
        def money = new Money(dollar: Integer.MAX_VALUE, cent: 139)
        GroovyAssert.shouldFail(IllegalArgumentException) {
            formatter.formatMoney(money, "en_AU")
        }
    }
}
