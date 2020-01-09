package ru.sergeykozhukhov.currencyconverter.domain;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import ru.sergeykozhukhov.currencyconverter.domain.model.Currency;

/**
 * Репозиторий для загрузки списка валют
 **/
public interface ICurrenciesRepository {

    /**
     * Загружает список валют
     */
    @NonNull
    List<Currency> loadCurrencies() throws IOException;
}
