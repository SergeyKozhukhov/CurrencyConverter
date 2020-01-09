package ru.sergeykozhukhov.currencyconverter.data.model;

import androidx.annotation.NonNull;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Информация о валютах и курсах конвертации
 **/
// strict позволяет определить способ парсинга.
// При строгом (strict = true) все элементы класса должбы соответствовать элементам xml документа, иначе вызывается ошибка.
// При нестрогои (strict = false) все опущенные элементы xml в классе будет отбрасываться при парсинге.
@Root(name = "ValCurs", strict = false)
public class CurrenciesData {

    /**
     * Список валют data представления
     */
    // @ElementList - коллекция для хранения псследовательности записей xml документа.
    // Поле используется для хранения коллекции (в данном случае: List ) XML элементов с тем же именем.
    // Параметр inline показывает, где расположены элементы списка — прямо в текущем узле xml(true) или в отдельном (false).
    // Т.е. элементы содержатся прямо в ValCurs без использования промежуточного родительского элемента.
    // Или, другими словами, когда inline установлено значение true, это значит,
    // что элементы коллекции перечислены один за другим сразу внутри заданного элемента
    // и не имеют промежуточного родительского элемента.
    @ElementList(inline = true)
    private List<CurrencyData> mCurrencies;

    @NonNull
    public List<CurrencyData> getCurrencies() {
        return new ArrayList<>(mCurrencies);
    }
}
