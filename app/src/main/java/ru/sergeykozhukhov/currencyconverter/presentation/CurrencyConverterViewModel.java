package ru.sergeykozhukhov.currencyconverter.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executor;

import ru.sergeykozhukhov.currencyconverter.R;
import ru.sergeykozhukhov.currencyconverter.domain.ConversionInteractor;
import ru.sergeykozhukhov.currencyconverter.domain.CurrenciesInteractor;
import ru.sergeykozhukhov.currencyconverter.domain.LoadCurrenciesException;
import ru.sergeykozhukhov.currencyconverter.domain.SingleLiveEvent;
import ru.sergeykozhukhov.currencyconverter.domain.model.Currency;
import ru.sergeykozhukhov.currencyconverter.presentation.utils.IResourceWrapper;

/**
 * Вью модель конвертера валют
 **/
// ViewModel - абстрактный класс, предназначенный для хранения и управления данными.
// Избавляет от таких проблем, как, например, поворот экрана.
// ViewModel способен поддерживать свое состояние пока activity не закроется окончательно.
// В отличии от onSavedInstanceState не способна сохранить данные после экстренного закрытия activity системой.
class CurrencyConverterViewModel extends ViewModel {

    /*
    * LiveData - хранилище данных, работающее по принципу паттерна observer.
    * Имеется возможность поместить в него какой-либо объект, подписаться на него и тогда получать объекты, которые в него помещают.
    *
    * MutableLiveData - LiveData, предоставляющее setValue(T) и postValue(T) методы.
    *
    * SingleLiveEvent -это LiveData, который не будет слать последнее значение новым слушателям при их подключении.
    * В основном это полезно при поворотах экрана, чтобы не было повторных срабатываний при переподключении слушателей.
    * Например, чтобы повторно не показывался Toast или SnackBar, когда View после пересоздания снова подключается к LiveData.
    * */

    /**
     * Интерактор для загрузки списка валют
     */
    private final CurrenciesInteractor mCurrenciesInteractor;

    /**
     * Интерфейс для создания и запуска потоков
     */
    private final Executor mExecutor;

    /**
     * Список валют (LiveData)
     */
    private final MutableLiveData<List<Currency>> mCurrencies = new MutableLiveData<>();

    /**
     * Строка с информацией о конвертации (когда нажали "конвертировать") (LiveData)
     */
    private final MutableLiveData<String> mConvertedText = new MutableLiveData<>();

    /**
     * Строка с курсом конвертации (LiveData)
     */
    private final MutableLiveData<String> mConversionRate = new MutableLiveData<>();

    /**
     * Строка с ошибкой работы
     */
    private final SingleLiveEvent<String> mErrors = new SingleLiveEvent<>();

    /**
     * Производиться ли в данным момент загрузка данных (LiveData)
     */
    private final MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    /**
     * Обертка над ресурсами приложения
     */
    private final IResourceWrapper mResourceWrapper;

    /**
     * Интерактор, отвечающий за операции, связанные с конвертацией вал
     */
    private final ConversionInteractor mConversionInteractor;

    /**
     * Информация по валюте "рубли"
     */
    private final Currency mRub;

    CurrencyConverterViewModel(
            @NonNull CurrenciesInteractor currenciesInteractor,
            @NonNull Executor executor,
            @NonNull IResourceWrapper resourceWrapper,
            @NonNull ConversionInteractor conversionInteractor) {
        mCurrenciesInteractor = currenciesInteractor;
        mExecutor = executor;
        mResourceWrapper = resourceWrapper;
        mConversionInteractor = conversionInteractor;
        mIsLoading.setValue(false);
        mRub = new Currency(
                "rub_id",
                "RUB",
                1,
                mResourceWrapper.getString(R.string.russian_ruble),
                BigDecimal.ONE
        );
    }

    /**
     * Загружает список валют с добавление на начальную позицию валюты Rub
     */
    void loadCurrencies() {
        mIsLoading.setValue(true);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Currency> currencies = mCurrenciesInteractor.loadCurrencies();
                    if (!currencies.contains(mRub)) {
                        currencies.add(0, mRub);
                    }
                    mCurrencies.postValue(currencies);
                } catch (LoadCurrenciesException e) {
                    mErrors.postValue(mResourceWrapper.getString(R.string.error_loading_currencies));
                }
                mIsLoading.postValue(false); //
            }
        });
    }

    /**
     * Список валют
     */
    @NonNull
    LiveData<List<Currency>> getCurrencies() {
        return mCurrencies;
    }

    /**
     * Курс конверсии
     */
    @NonNull
    LiveData<String> getConversionRate() {
        return mConversionRate;
    }

    /**
     * Строка с информацией о конвертации (когда нажали "конвертировать")
     */
    @NonNull
    LiveData<String> getConvertedText() {
        return mConvertedText;
    }

    /**
     * Идёт ли загрузка
     */
    @NonNull
    LiveData<Boolean> isLoading() {
        return mIsLoading;
    }

    /**
     * Ошибки
     */
    @NonNull
    LiveData<String> getErrors() {
        return mErrors;
    }

    /**
     * Обновляет информацию о курсе валют
     * Итоговое значение записывается в виде форматированной строки в параметр класса
     *
     * @param fromCurrencyWithIndex индекс базовой валюты
     * @param toCurrencyWithIndex   индекс котируемой валюты
     */
    void updateConversionRate(int fromCurrencyWithIndex, int toCurrencyWithIndex) {
        String updatedConversionRate = mConversionInteractor
                .formatConversionRate(mCurrencies.getValue(), fromCurrencyWithIndex, toCurrencyWithIndex);
        if (updatedConversionRate != null) {
            mConversionRate.setValue(updatedConversionRate);
        }
    }

    /**
     * Выполняет конвертацию между выбранными валютами и введённой суммой
     * Итоговое значение записывается в виде форматированной строки в параметр класса
     *
     * @param fromCurrencyWithIndex индекс базовой валюты
     * @param toCurrencyWithIndex   индекс котируемой валюты
     * @param amount                сумма, введённая пользователем as is (ввод может быть некорректным)
     */
    void convert(int fromCurrencyWithIndex, int toCurrencyWithIndex, @Nullable String amount) {
        List<Currency> currencies = mCurrencies.getValue();
        String converted = mConversionInteractor.convert(currencies, fromCurrencyWithIndex, toCurrencyWithIndex, amount);
        if (converted == null) {
            mErrors.setValue(mResourceWrapper.getString(R.string.conversion_error));
        } else {
            mConvertedText.setValue(converted);
        }
    }
}