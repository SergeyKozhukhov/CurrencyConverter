package ru.sergeykozhukhov.currencyconverter.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.sergeykozhukhov.currencyconverter.data.model.CurrencyData;
import ru.sergeykozhukhov.currencyconverter.domain.IConverter;
import ru.sergeykozhukhov.currencyconverter.domain.model.Currency;

/**
 * Конвертирует список валют из data сущностей в domain
 **/
public class CurrencyConverter
        implements IConverter<List<CurrencyData>, List<Currency>> {

    /**
     * Конвертация списка валют между обозначенными представлениями
     * @param currencies - список валют data представления
     * @return список валют domain представления
     */
    @NonNull
    @Override
    public List<Currency> convert(@NonNull List<CurrencyData> currencies) {
        List<Currency> result = new ArrayList<>();
        for (CurrencyData currency : currencies) {
            result.add(new Currency(
                    currency.getId(),
                    currency.getCharCode(),
                    currency.getNominal(),
                    currency.getName(),
                    currency.getValue()
            ));
        }
        return result;
    }
}
