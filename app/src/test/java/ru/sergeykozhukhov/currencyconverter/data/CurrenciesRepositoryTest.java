package ru.sergeykozhukhov.currencyconverter.data;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.FieldSetter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.sergeykozhukhov.currencyconverter.data.model.CurrenciesData;
import ru.sergeykozhukhov.currencyconverter.data.model.CurrencyData;
import ru.sergeykozhukhov.currencyconverter.domain.model.Currency;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Юнит тесты на {@link CurrenciesRepository}
 **/
public class CurrenciesRepositoryTest {

    /**
     * Репозитория для загрузки списка валют
     */
    private CurrenciesRepository mCurrenciesRepository;

    /**
     * Конвертер список валют из data сущностей в domain
     */
    private CurrencyConverter mCurrencyConverter;

    /**
     * Интерфейс, определяющий возможные http операции
     */
    private IRatesService mRatesApi;

    @Before
    public void setUp() throws Exception{

        mCurrencyConverter = mock(CurrencyConverter.class);
        mRatesApi = mock(IRatesService.class);

        mCurrenciesRepository = new CurrenciesRepository(mCurrencyConverter);
        // установка значения private полю через mockito c помощью рефлексии
        // mCurrenciesRepository - класс, в котором производиться установка значения private параметра
        // CurrenciesRepository.class.getDeclaredField("mRatesApi") - получение доступа к полю по его имени
        // mRatesApi - значение для установки
        FieldSetter.setField(mCurrenciesRepository, CurrenciesRepository.class.getDeclaredField("mRatesApi"), mRatesApi);
    }

    /**
     * Проверка, что при загрузки данных о валютах с сервера, все работает и не генерируются ошибки
     */
    // @SuppressWarnings  указывает на то, что предупреждение (warning) компилятора должно быть подавлено
    // Термин "unchecked" говорит, что не имеется достаточно информации о типе,
    // чтобы выполнить все проверки, которые были бы необходимы для обеспечения безопасности
    // наиболее распространенным источником таких предупреждения является raw типы
    @Test
    @SuppressWarnings("unchecked")
    public void testLoadCurrencies_happyCase() throws IOException{

        // arrange
        List<CurrencyData> currencies = Arrays.asList(mock(CurrencyData.class), mock(CurrencyData.class)); // список валют data слоя
        CurrenciesData currenciesData = mock(CurrenciesData.class); // информация о валютах и курсах конвертации
        when(currenciesData.getCurrencies()).thenReturn(currencies); // получение mock списка валют
        Response<CurrenciesData> response = Response.success(currenciesData); // формирование успешного ответа c десериализованной информацией о валютах
        Call<CurrenciesData> call = mock(Call.class); // создание запроса к серверу
        when(call.execute()).thenReturn(response); // формирование ответа от сервера
        when(mRatesApi.loadCurrencies()).thenReturn(call); // формирование загрузки валют с сервера
        List<Currency> expectedOutput = Arrays.asList(mock(Currency.class), mock(Currency.class)); // список ожидаемых валют domain
        when(mCurrencyConverter.convert(currencies)).thenReturn(expectedOutput); // формирование преобразования валют из data в domain

        // act
        List<Currency> output = mCurrenciesRepository.loadCurrencies(); // загрузка валют data слоя

        // assert
        assertThat(output, is(expectedOutput)); // проверка на равенство

    }

    /**
     * Проверка, что генерируется исключение в случае пустого ответа от сервера
     */
    // JUnit предоставляет возможность отслеживать исключения, а также проверяет генерирует ли код ожидаемое исключение или нет
    @Test (expected = IOException.class)  // проверка что код способен сгенерировать исключение
    @SuppressWarnings("unchecked")
    public void testLoadCurrencies_bodyIsNull_throwException() throws IOException{

        // arrange
        List<CurrencyData> currencies = Arrays.asList(mock(CurrencyData.class), mock(CurrencyData.class));
        CurrenciesData currenciesData = mock(CurrenciesData.class);
        when(currenciesData.getCurrencies()).thenReturn(currencies);
        Response<CurrenciesData> response = Response.success(null);
        Call<CurrenciesData> call = mock(Call.class);
        when(call.execute()).thenReturn(response);
        when(mRatesApi.loadCurrencies()).thenReturn(call);

        // act
        mCurrenciesRepository.loadCurrencies();

    }

    /**
     * Проверка, что генерируется исключение в случае ошибки в ответе от сервера
     */
    @Test(expected = IOException.class)
    @SuppressWarnings("unchecked")
    public void testLoadCurrencies_errorBodyIsNotNull_throwsException() throws IOException{

        // arrange
        List<CurrencyData> currencies = Arrays.asList(mock(CurrencyData.class), mock(CurrencyData.class));
        CurrenciesData currenciesData = mock(CurrenciesData.class);
        when(currenciesData.getCurrencies()).thenReturn(currencies);
        ResponseBody errorBody = mock(ResponseBody.class);
        Response<CurrenciesData> response = Response.error(404, errorBody); // error - формирование ошибки в ответе от сервера
        Call<CurrenciesData> call = mock(Call.class);
        when(call.execute()).thenReturn(response);
        when(mRatesApi.loadCurrencies()).thenReturn(call);

        // act
        mCurrenciesRepository.loadCurrencies();

    }




}
