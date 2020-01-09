package ru.sergeykozhukhov.currencyconverter.domain;

import androidx.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.sergeykozhukhov.currencyconverter.R;
import ru.sergeykozhukhov.currencyconverter.domain.model.Currency;
import ru.sergeykozhukhov.currencyconverter.presentation.utils.IResourceWrapper;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Юнит тесты на {@link ConversionInteractor}
 **/
public class ConversionInteractorTest {

    /**
     * Интерактор, отвечающий за операции, связанные с конвертацией валют
     */
    private ConversionInteractor mConversionInteractor;

    /**
     * Обёртка над ресурсами приложения
     */
    private IResourceWrapper mResourceWrapper;

    private String mCharCode1 = "charCode1";
    private BigDecimal mValue1 = BigDecimal.ONE;
    private String mCharCode2 = "charCode2";
    private BigDecimal mValue2 = BigDecimal.TEN;

    /**
     * Список валют domain слоя
     */
    private List<Currency> mCurrencies;

    @Before
    public void setUp(){
        mResourceWrapper = mock(IResourceWrapper.class);
        mConversionInteractor = new ConversionInteractor(mResourceWrapper);
        String id1 = "id1";
        long nominal1 = 10L;
        String name1 = "name1";
        String id2 = "id2";
        long nominal2 = 20L;
        String name2 = "name2";

        mCurrencies = Arrays.asList(
                new Currency(
                        id1,
                        mCharCode1,
                        nominal1,
                        name1,
                        mValue1
                ),

                new Currency(
                        id2,
                        mCharCode2,
                        nominal2,
                        name2,
                        mValue2
                )
        );
    }

    /**
     * Проверка на правильность работы конвертера валют при получении курса конвертации
     */
    @Test
    public void testFormatConversionRate() {
        testFormatConversionRate(null, 0, 0, null); // список - null
        testFormatConversionRate(new ArrayList<Currency>(), 0, 0, null); // нет значений в списке
        testFormatConversionRate(mCurrencies, 4, 6, null); // выход за пределы размера списка

        String expectedResult = "expectedResult";
        when(mResourceWrapper.getString(R.string.conversion_rate, "0.2", mCharCode1, mCharCode2))
                .thenReturn(expectedResult);
        testFormatConversionRate(mCurrencies, 0, 1, expectedResult);
    }

    /**
     * Проверка на правильность работы конвертера валют при получении итогового результата
     */
    @Test
    public void testConverter(){
        testConvert(null, 0, 0, "10", null); // список - null
        testConvert(new ArrayList<Currency>(), 0, 0, "10", null); // нет значений в списке
        testConvert(mCurrencies, 4, 6, "10", null); // выход за пределы размера списка
        testConvert(mCurrencies, 0, 1, "incorrect input", null); // некорректный ввод количеста валюты

        String expectedResult = "expectedResult";
        when(mResourceWrapper.getString(R.string.you_will_get, "2", mCharCode2))
                .thenReturn(expectedResult); // формирование результата при конвертации
        testConvert(mCurrencies, 0, 1, "10", expectedResult);
    }

    /**
     * Проврека, что результат вычисления курса конвертации равен expectedOutput
     */
    private void testFormatConversionRate(@Nullable List<Currency> currencies,
                             int fromCurrencyWithIndex,
                             int toCurrencyWithIndex,
                             @Nullable String expectedOutput){
        // act
        String output = mConversionInteractor.formatConversionRate(currencies, fromCurrencyWithIndex, toCurrencyWithIndex);

        // assert
        assertThat(output, is(expectedOutput));
    }


    /**
     * Проверка, что результат конвертации равен передаваемому expectedOutput
     */
    private void testConvert(@Nullable List<Currency> currencies,
                             int fromCurrencyWithIndex,
                             int toCurrencyWithIndex,
                             @Nullable String amount,
                             @Nullable String expectedOutput){
        // act
        String output = mConversionInteractor.convert(currencies,
                fromCurrencyWithIndex,
                toCurrencyWithIndex,
                amount);

        // assert
        assertThat(output, is(expectedOutput));

    }


}
