package ru.sergeykozhukhov.currencyconverter.data;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import ru.sergeykozhukhov.currencyconverter.data.model.CurrencyData;
import ru.sergeykozhukhov.currencyconverter.domain.model.Currency;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Юнит тесты на {@link CurrencyConverter}
 **/
public class CurrencyConverterTest {

    /**
     * Конвертер валют из data сущностей в domain
     */
    private CurrencyConverter mCurrencyConverter;

    @Before
    public void setUp(){
        mCurrencyConverter = new CurrencyConverter();
    }

    /**
     * Проверка на правильность конвертации валют из data сущностей в domain
     */
    @Test
    public void testConverter(){

        // arrange
        String id = "id";
        int numCode = 15;
        String charCode = "charCode";
        long nominal = 10L;
        String name = "Ruble";
        BigDecimal value = BigDecimal.ONE; // BigDecimal.ONE - заранее определенное значение BigDecimal в виде "1"

        // инициализация входного значения data слоя
        // singletonList - создание неизменяемого списка с одним элементом
        List<CurrencyData> input = Collections.singletonList(new CurrencyData(
                id,
                numCode,
                charCode,
                nominal,
                name,
                value
        ));

        // инициализация итогового значения domain слоя
        List<Currency> expectedOutput = Collections.singletonList(new Currency(
                id,
                charCode,
                nominal,
                name,
                value
        ));

        // act
        List <Currency> output = mCurrencyConverter.convert(input);

        // assert
        assertThat(output, is(expectedOutput)); // сравнение объектов на равенство

    }
}
