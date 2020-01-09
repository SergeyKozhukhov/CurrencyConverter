package ru.sergeykozhukhov.currencyconverter.data.model;

import androidx.annotation.VisibleForTesting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import java.math.BigDecimal;

import ru.sergeykozhukhov.currencyconverter.data.BigDecimalConverter;

/**
 * Валюта (data-представление)
 **/
// @Root - обозначение корневого элемента xml (e.g. <Valute>). Данный класс определяется как сериализуемый.
@Root(name = "Valute")
public class CurrencyData {

    /**
     * id валюты
     */
    // @Attribute - обозначение аттрибута (e.g. <Valute ID="R01010">)
    // name - наименование аттрибута в xml документе. По умолчанию без использования name производится конвертация по имени поля.
    // Т.о. достаточно, чтобы имя поля класса и имя аттрибута xml были идентичными, чтобы name не указывать.
    @Attribute(name = "ID")
    private String mId;

    /**
     * Код
     */
    // @Element - внутреннее поле "Valute" (e.g. <NumCode>036</NumCode>)
    @Element(name = "NumCode")
    private int mNumCode;

    /**
     * Сокращенное название валюты
     */
    @Element(name = "CharCode")
    private String mCharCode;

    /**
     * Номинал
     */
    @Element(name = "Nominal")
    private long mNominal;

    /**
     * Название
     */
    @Element(name = "Name")
    private String mName;

    /**
     * Курс по отношению к рублю
     */
    // @Convert - внутренее поле требуется подвергнуть преобразованию при сериализации/десериализации.
    // Способ преобразования описывается в отдельном классе. В данном случае это BigDecimalConverter.
    @Element(name = "Value")
    @Convert(BigDecimalConverter.class)
    private BigDecimal mValue;

    /**
     * Используется при парсинге через SimpleXml
     */
    public CurrencyData() {
    }

    /**
     * Используется в юнит тестах
     */
    // @VisibleForTesting: предполагается, что этой аннотацией мы будем отмечать методы и поля,
    // которые, по идее, должны были быть private,
    // но для тестов были сделаны package-private или public
    @VisibleForTesting
    public CurrencyData(String id, int numCode, String charCode, long nominal, String name, BigDecimal value) {
        mId = id;
        mNumCode = numCode;
        mCharCode = charCode;
        mNominal = nominal;
        mName = name;
        mValue = value;
    }

    public String getId() {
        return mId;
    }

    public int getNumCode() {
        return mNumCode;
    }

    public String getCharCode() {
        return mCharCode;
    }

    public long getNominal() {
        return mNominal;
    }

    public String getName() {
        return mName;
    }

    public BigDecimal getValue() {
        return mValue;
    }
}
