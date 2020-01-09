package ru.sergeykozhukhov.currencyconverter.domain;

import androidx.annotation.NonNull;

/**
 * Конвертер из одной произвольной сущности в другую
 **/
public interface IConverter<From, To> {

    /**
     * Выполняет конвертацию
     */
    @NonNull
    To convert(@NonNull From from);
}
