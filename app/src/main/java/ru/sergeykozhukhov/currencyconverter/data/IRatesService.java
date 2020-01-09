package ru.sergeykozhukhov.currencyconverter.data;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.sergeykozhukhov.currencyconverter.data.model.CurrenciesData;

/**
 * Web-api для списка курсов валют
 * Интерфейс, определяющий возможные http операции
 **/
public interface IRatesService {

    /**
     * Загружает курсы валют
     */
    // @GET - get запрос для базового адреса.
    // Каждый метод интерфейса представляет собой один из возможных вызовов API.
    // Он должен иметь HTTP аннотацию (GET, POST и т. д.), чтобы указать тип запроса и относительный URL.
    // Команда комбинируется с базовым адресом сайта (baseUrl()) и получается полный путь к странице.
    @GET("scripts/XML_daily.asp")
    // Метод retrofit, который отправляет запрос на сервер и возвращает ответ.
    // CurrenciesData - тип ответа/результата в случае успешного выполнения запроса.
    // Возвращаемое значение завершает ответ в Call-объекте с типом ожидаемого результата.
    Call<CurrenciesData> loadCurrencies();
}
