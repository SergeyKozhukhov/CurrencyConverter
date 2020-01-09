package ru.sergeykozhukhov.currencyconverter.domain;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import ru.sergeykozhukhov.currencyconverter.domain.model.Currency;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Юнит тесты на {@link CurrenciesInteractor}
 **/
public class CurrenciesInteractorTest {

    /**
     * Интерактор для загрузки списка валют
     */
    private CurrenciesInteractor mCurrenciesInteractor;

    /**
     * Репозиторий для загрузки списка валют
     */
    private ICurrenciesRepository mRepository;

    @Before
    public void setUp() {
        mRepository = mock(ICurrenciesRepository.class);
        mCurrenciesInteractor = new CurrenciesInteractor(mRepository);
    }

    /**
     * Проверка, что при загрузки данных, операции проходят нормально, без ошибок
     */
    @Test
    public void testLoadCurrencies_happyCase() throws IOException, LoadCurrenciesException {
        // arrange
        // noinspection unchecked
        List<Currency> currencies = mock(List.class); // список валют domain
        when(mRepository.loadCurrencies()).thenReturn(currencies); // результат в случае загрузки данных

        // act
        List<Currency> output = mCurrenciesInteractor.loadCurrencies();

        // assert
        assertThat(output, is(currencies));
    }

    /**
     * Проверка, что выбрасывается исключение при загрузке данных
     */
    @Test(expected = LoadCurrenciesException.class)
    public void testLoadCurrencies_repositoryThrowsException_exceptionIsWrappedIntoLoadCurrenciesException()
            throws IOException, LoadCurrenciesException {
        // arrange
         when(mRepository.loadCurrencies()).thenThrow(new IOException("mock")); // генерация исключения при загрузке данных

        // act
        mCurrenciesInteractor.loadCurrencies();
    }
}
