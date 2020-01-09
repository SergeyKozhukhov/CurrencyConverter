package ru.sergeykozhukhov.currencyconverter.data;

import androidx.annotation.NonNull;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import ru.sergeykozhukhov.currencyconverter.data.model.CurrenciesData;
import ru.sergeykozhukhov.currencyconverter.data.model.CurrencyData;
import ru.sergeykozhukhov.currencyconverter.domain.ICurrenciesRepository;
import ru.sergeykozhukhov.currencyconverter.domain.model.Currency;

/**
 * Реализация репозитория для загрузки списка валют
 **/
public class CurrenciesRepository implements ICurrenciesRepository {

    /**
     * Базовый адрес
     */
    private static final String BASE_URL = "http://www.cbr.ru";

    /**
     * Web-api для списка курсов валют
     */
    private final IRatesService mRatesApi;

    /**
     * Конвертер списка валют из data сущностей в domain
     */
    private final CurrencyConverter mCurrencyConverter;

    /**
     * Constructor
     * @param currencyConverter используется для конвертации загруженных валют в domain entity
     */
    public CurrenciesRepository(@NonNull CurrencyConverter currencyConverter) {
        mCurrencyConverter = currencyConverter;
        // Strategy - интерфейс, представляющий способы для загрузки объектов, подвергающихся сериализации.
        // Непосредственно в самой реализации данного интерейса используется предоставленный перечень атрибутов,
        // для получения тех данный, которые требуются в каждом конкретном случае.
        // AnnotationStrategy используется для того, чтобы в процессе сериализации/десериализации
        // преобразовать данные с помощью пользовательних настроек.
        // Данные способ (AnnotationStrategy) использует аннотацию convert, которая относится к полю mValue,
        // для того, чтобы в процессе десериализации изменять строку числа с "," на строку с ".", и на ее основе получать BigDecimal.
        Strategy strategy = new AnnotationStrategy();
        // Serializer - интерфейс, используемый для определения методов, которые позволяют производить процесс сериализации xml.
        // Он предоставляет некоторые методы для чтения и записи.
        // В реализации интерфейса может свободно использоваться любой предпочитаемый способ синтаксического анализа xml.
        // Persister реализует интерфейс Serializer и использует Filter объекты для для замены исходных переменных в предоставленном xml.
        Serializer serializer = new Persister(strategy);
        // Создание нового объектра Retrofit.
        // noinspection deprecation
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // определение основного адреса
                // добавление "фабрики" с serializer для сериализации/десериализации xml
                // на данный момент стоит отдавать предпочтение JAXB converter
                .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                .build(); // создание нового экземпляра на основе обозначенных параметров
        // реализация интерфейса с возможными http операциями
        // относительные пути для итоговых ссылок получаются из аннотаций
        // по умолчанию методы возвращают Call, которыке по сути, являются http запросами.
        // тип Call обрабатывается для итогового результата с помощью экземпляра "фабрики"
        mRatesApi = retrofit.create(IRatesService.class);
    }

    /**
     * Загрузка списка валют
     * @return список валют в domain представлении
     */
    @NonNull
    @Override
    public List<Currency> loadCurrencies() throws IOException {
        // http ответ
        Response<CurrenciesData> response = mRatesApi.loadCurrencies()
                .execute(); // execute - синхронный запрос
        if (response.body() == null || response.errorBody() != null) {
            throw new IOException("Не удалось загрузить список валют");
        }
        List<CurrencyData> currencies = response.body().getCurrencies();
        // конвертация в объект domain сущности
        return mCurrencyConverter.convert(currencies);
    }
}
