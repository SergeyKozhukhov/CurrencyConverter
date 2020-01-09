package ru.sergeykozhukhov.currencyconverter.data;

import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.stream.InputNode;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Юнит тесты на {@link BigDecimalConverter}
 **/
public class BigDecimalConverterTest {

    /**
     * mBigDecimalConverter - преобразователь из String в BigDecimal при десериализации
     */
    private BigDecimalConverter mBigDecimalConverter;

    @Before
    public void setUp(){
        mBigDecimalConverter = new BigDecimalConverter();
    }

    /**
     * Проверка на правильность конвертации из String в BigDecimal
     */
    @Test
    public void testRead() throws Exception{
        // arrange
        InputNode input = mock(InputNode.class); // InputNode - итератор для элементов узла
        String value = "45,678"; // результат, возвращаемый при обращении к курсу валюты обозначенного узла
        when(
                input.getValue()
        ).thenReturn(value); // обозначение возвращаемого результата от данного узла

        BigDecimal expectedOutput = new BigDecimal("45.678"); // ожидаемый результат при конвертации в BigDecimal из String

        // act
        BigDecimal output = mBigDecimalConverter.read(input); // процесс конвертации в BigDecimal

        // assert

        // Hamcrest помогает писать тесты.
        // Не надо путать его с полнофункциональными фреймворками для тестирования, такими как JUnit.
        // Hamcrest – это всего лишь библиотека matcher-ов,
        // которая используется в паре с JUnit или другим аналогичным фреймворком для тестирования.
        // Matcher – это такое выражение, тестирующее на совпадение с определенным условием.
        // В Hamcrest составлять проверки удобнее, а главное, они более читаемые.
        // Возвращаемые тексты ошибок тоже скажут больше о проблеме.
        // предикат - интерфейс с функцией, проверяющей состояние объекта и возвращающий boolean значение
        // is(...) - кратная запись is(equalTo(...))
        assertThat(output, is(expectedOutput)); // проверка output на равенство expectedOutput
    }
}
