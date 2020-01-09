package ru.sergeykozhukhov.currencyconverter.presentation;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.sergeykozhukhov.currencyconverter.R;
import ru.sergeykozhukhov.currencyconverter.domain.ConversionInteractor;
import ru.sergeykozhukhov.currencyconverter.domain.CurrenciesInteractor;
import ru.sergeykozhukhov.currencyconverter.domain.LoadCurrenciesException;
import ru.sergeykozhukhov.currencyconverter.domain.model.Currency;
import ru.sergeykozhukhov.currencyconverter.presentation.utils.IResourceWrapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Юнит тесты на {@link CurrencyConverterViewModel}
 **/
public class CurrencyConverterViewModelTest {

    private static final String RUB = "Российский рубль";

    /**
     * Правило — это способ запуска кода до и после выполнения теста в JUnit.
     * InstantTaskExecutorRule — это правило JUnit, которое настраивает LiveData
     * для немедленной публикации в основном потоке во время выполнения теста.
     */
    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * ViewModel конвертера валют
     */
    private CurrencyConverterViewModel mViewModel;

    /**
     * Интерактор для загрузки списка валют
     */
    private CurrenciesInteractor mCurrenciesInteractor;

    /**
     * Обёртка над ресурсами приложения
     */
    private IResourceWrapper mResourceWrapper;

    /**
     * Интерактор, отвечающий за операции, связанные с конвертацией валют
     */
    private ConversionInteractor mConversionInteractor;

    /**
     * Валюта - рубли
     */
    private Currency mRub;

    /**
     * Список валют domain слоя
     */
    private List<Currency> mCurrencies;

    @Before
    public void setUp() {
        mCurrencies = new ArrayList<>(Arrays.asList(mock(Currency.class), mock(Currency.class)));
        mCurrenciesInteractor = mock(CurrenciesInteractor.class);
        mResourceWrapper = mock(IResourceWrapper.class);
        mConversionInteractor = mock(ConversionInteractor.class);
        when(mResourceWrapper.getString(R.string.russian_ruble)).thenReturn(RUB);

        // инициализация ViewModel
        mViewModel = new CurrencyConverterViewModel(
                mCurrenciesInteractor,
                new SynchronousExecutor(),
                mResourceWrapper,
                mConversionInteractor
        );
        // инициализация валюты (рубли)
        mRub = new Currency(
                "rub_id",
                "RUB",
                1,
                mResourceWrapper.getString(R.string.russian_ruble),
                BigDecimal.ONE
        );
    }

    @Test
    public void testLoadCurrencies_happyCase() throws LoadCurrenciesException {
        // arrange
        when(mCurrenciesInteractor.loadCurrencies()).thenReturn(mCurrencies); // формирование списка валют, получаемого при загрузе с сервера
        List<Currency> expectedCurrencies = new ArrayList<>(mCurrencies); // ожидаемый список валют от сервера
        expectedCurrencies.add(0, mRub); // добавление рублей

        // act
        mViewModel.loadCurrencies();

        // assert
        assertThat(mViewModel.getCurrencies().getValue(), is(expectedCurrencies)); // проверка, что данные совпадают
        assertThat(mViewModel.isLoading().getValue(), is(false)); // проверка, что загрузка данных прекращена
    }

    @Test
    public void testLoadCurrencies_interactorThrowsException() throws LoadCurrenciesException {
        // arrange
        when(mCurrenciesInteractor.loadCurrencies()).thenThrow(new LoadCurrenciesException("message", new Throwable())); // формирование ошибки при загрузке с сервера
        String errorLoadingCurrencies = "errorLoadingCurrencies"; // ошибка
        when(mResourceWrapper.getString(R.string.error_loading_currencies)).thenReturn(errorLoadingCurrencies); // формирование текста ошибки

        // act
        mViewModel.loadCurrencies();

        // assert
        assertThat(mViewModel.getErrors().getValue(), is(errorLoadingCurrencies)); // проверка на получение ошибки
        assertThat(mViewModel.isLoading().getValue(), is(false)); // проверка, что загрузка данных прекращена
    }

    @Test
    public void testUpdateConversionRate_happyCase() {
        // arrange
        ((MutableLiveData<List<Currency>>) mViewModel.getCurrencies()).setValue(mCurrencies); // установка списка валют в LiveData
        String formattedString = "formattedString";
        when(mConversionInteractor.formatConversionRate(mCurrencies, 0, 1))
                .thenReturn(formattedString); // формирование строки с курсом валют

        // act
        mViewModel.updateConversionRate(0, 1);

        // assert
        assertThat(mViewModel.getConversionRate().getValue(), is(formattedString)); // проверка на значение курса валют
    }

    @Test
    public void testUpdateConversionRate_errorCase() {
        // arrange
        when(mConversionInteractor.formatConversionRate(mCurrencies, 0, 1))
                .thenReturn(null);
        ((MutableLiveData<List<Currency>>) mViewModel.getCurrencies()).setValue(mCurrencies);

        // act
        mViewModel.updateConversionRate(0, 1);

        // assert
        // nullValue - создание матчера для обработки ситуации, когда значение равно null
        assertThat(mViewModel.getConversionRate().getValue(), is(nullValue())); // проверка на null
    }

    @Test
    public void testConvert_happyCase() {
        // arrange
        ((MutableLiveData<List<Currency>>) mViewModel.getCurrencies()).setValue(mCurrencies);
        String amount = "10";
        String formattedString = "formattedString";
        when(mConversionInteractor.convert(mCurrencies, 0, 1, amount))
                .thenReturn(formattedString);

        // act
        mViewModel.convert(0, 1, amount);

        // assert
        assertThat(mViewModel.getConvertedText().getValue(), is(formattedString));
    }

    @Test
    public void testConvert_errorCase() {
        // arrange
        ((MutableLiveData<List<Currency>>) mViewModel.getCurrencies()).setValue(mCurrencies);
        String error = "error";
        when(mResourceWrapper.getString(R.string.conversion_error)).thenReturn(error);
        String amount = "10";
        when(mConversionInteractor.convert(mCurrencies, 0, 1, amount))
                .thenReturn(null);

        // act
        mViewModel.convert(0, 1, amount);

        // assert
        assertThat(mViewModel.getErrors().getValue(), is(error));
    }
}