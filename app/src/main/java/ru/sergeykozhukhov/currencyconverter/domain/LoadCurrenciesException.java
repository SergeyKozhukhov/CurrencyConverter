package ru.sergeykozhukhov.currencyconverter.domain;

/**
 * Ошибка доменного слоя, связанная с загрузкой списка валют
 **/
public class LoadCurrenciesException extends Exception {

    public LoadCurrenciesException(String message, Throwable cause) {
        super(message, cause);
    }
}
