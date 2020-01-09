package ru.sergeykozhukhov.currencyconverter.presentation;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.sergeykozhukhov.currencyconverter.data.CurrenciesRepository;
import ru.sergeykozhukhov.currencyconverter.data.CurrencyConverter;
import ru.sergeykozhukhov.currencyconverter.domain.ConversionInteractor;
import ru.sergeykozhukhov.currencyconverter.domain.CurrenciesInteractor;
import ru.sergeykozhukhov.currencyconverter.domain.ICurrenciesRepository;
import ru.sergeykozhukhov.currencyconverter.presentation.utils.ResourceWrapper;

/**
 * Фабрика вьюмоделей конвертера валют
 *
 * Используется для того, чтобы передать в конструктор CurrencyConverterViewModel параметры
 **/
public class CurrencyViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Context mApplicationContext;

    CurrencyViewModelFactory(@NonNull Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (CurrencyConverterViewModel.class.equals(modelClass)) {
            // создание объектов для конструктора CurrencyConverterViewModel
            ICurrenciesRepository currenciesRepository = new CurrenciesRepository(new CurrencyConverter());
            CurrenciesInteractor interactor = new CurrenciesInteractor(currenciesRepository);
            Executor executor = Executors.newSingleThreadExecutor();
            ResourceWrapper resourceWrapper = new ResourceWrapper(mApplicationContext.getResources());
            // возвращение CurrencyConverterViewModel(...)
            // noinspection unchecked
            return (T) new CurrencyConverterViewModel(
                    interactor,
                    executor,
                    resourceWrapper,
                    new ConversionInteractor(resourceWrapper));
        } else {
            // простая "фабрика", которая вызывает пустой конструктор переданного класса
            return super.create(modelClass);
        }
    }
}
