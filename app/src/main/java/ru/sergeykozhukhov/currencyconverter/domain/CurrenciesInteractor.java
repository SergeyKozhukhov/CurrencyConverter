package ru.sergeykozhukhov.currencyconverter.domain;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import ru.sergeykozhukhov.currencyconverter.domain.model.Currency;

/**
 * Интерактор для загрузки списка валют
 **/
public class CurrenciesInteractor {

    /**
     * Репозиторий для загрузки списка валют
     */
    private final ICurrenciesRepository mCurrenciesRepository;

    public CurrenciesInteractor(@NonNull ICurrenciesRepository currenciesRepository) {
        mCurrenciesRepository = currenciesRepository;
    }

    /**
     * Загружает список валют
     */
    @NonNull
    public List<Currency> loadCurrencies() throws LoadCurrenciesException {
        try {
            return mCurrenciesRepository.loadCurrencies();
        } catch (IOException e) {
            throw new LoadCurrenciesException("Не удалось загрузить список валют", e);
        }
    }
}