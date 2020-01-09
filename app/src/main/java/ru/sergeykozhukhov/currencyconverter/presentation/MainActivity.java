package ru.sergeykozhukhov.currencyconverter.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.sergeykozhukhov.currencyconverter.R;
import ru.sergeykozhukhov.currencyconverter.domain.model.Currency;

public class MainActivity extends AppCompatActivity {

    /**
     * Начальная позиция выбранного элемента спиннера итоговой валюты
     */
    private static final int SECOND_ITEM = 1;

    /**
     * ViewModel конвертера валют с LiveData полями
     */
    private CurrencyConverterViewModel mViewModel;

    /**
     * Список валют со значением исходной валюты
     */
    private Spinner mSpinnerFrom;

    /**
     * Список валют со значением итоговой валюты
     */
    private Spinner mSpinnerTo;

    /**
     * Количество исходной валюты, подвергающееся конвертации
     */
    private EditText mFromAmount;

    /**
     * Значение сконвертированной валюты
     */
    private TextView mConvertedText;

    /**
     * Курс валют
     */
    private TextView mConversionRate;

    /**
     * Процесс загрузки данных
     */
    private View mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupMvvm();
    }

    /**
     * Инициализация комопнентов
     */
    private void initViews() {
        mSpinnerFrom = findViewById(R.id.spinnerFrom);
        mSpinnerTo = findViewById(R.id.spinnerTo);
        // обработка нажатия на кнопку конвертации
        findViewById(R.id.convert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // конвертация валют
                mViewModel.convert(
                        mSpinnerFrom.getSelectedItemPosition(), // индекс исходной валюты
                        mSpinnerTo.getSelectedItemPosition(), // индекс итоговой валюты
                        mFromAmount.getText().toString() // колличество исходной валюты
                );
            }
        });
        mFromAmount = findViewById(R.id.fromAmount);
        mConvertedText = findViewById(R.id.convertedText);
        mLoadingView = findViewById(R.id.loading_view);
        mConversionRate = findViewById(R.id.conversionRate);
        mSpinnerFrom.setOnItemSelectedListener(new OnCurrencySelectedListener()); // установка listener для обновления информации о курсе валют
        mSpinnerTo.setOnItemSelectedListener(new OnCurrencySelectedListener()); // установка listener для обновления информации о курсе валют
    }

    /**
     * Настройка ViewModel и добавление наблюдателей к LiveData
     */
    private void setupMvvm() {
        // ViewModelProviders - класс, содержащий методы для ViewModelStore
        // Он использует передаваемый параметром ViewModelProvider.Factory для создания новых ViewModels.
        // ViewModelStore - класс для хранения ViewModels (под капотом HashMap c ViewModel)
        // создание нового ViewModelProvirer для сохранения нового ViewModel
        mViewModel = ViewModelProviders.of(
                this, // activity, в которой должны сохраниться ViewModels
                new CurrencyViewModelFactory(this) // фабрика для создания новых ViewModels
        ) // возвращение ViewModelProvider
                .get(CurrencyConverterViewModel.class); // создание новой ViewModel в данном провайдере
                                                        // или возвращение существующего экземпляра
                                                        // CurrencyConverterViewModel.class - класс ViewModel для создания экземпляра
                                                        // возвращет экземпляр ViewModel

        // observe - добавление наблюдателя к LiveData
        // параметры:
        // LifecycleOwner - класс, содержащий жизненный цикл activity
        // метод данного класса getLifecycle возвращает состояние жизненного цикла
        // Observer - наблюдатель, который будет получать события от LiveData
        // new Observer - создание интерфейса для внесения изменения при изменениях в LiveData
        // параметр Observer - тип данных подвергаемых изменениям
        mViewModel.getCurrencies().observe(this, new Observer<List<Currency>>(){
            //onChanged - метод, вызываемый при изменении данных
            @Override
            public void onChanged(List<Currency> currencies) {
                // обновление списков валют
                mSpinnerFrom.setAdapter(new CurrencyAdapter(currencies));
                mSpinnerTo.setAdapter(new CurrencyAdapter(currencies));
                mSpinnerTo.setSelection(SECOND_ITEM); // выставление в спиннере для итоговой валюты валюты, следующей за rub
            }
        });
        mViewModel.getConvertedText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String convertedText) {
                // обновление значения сконвертированной валюты
                mConvertedText.setText(convertedText);
            }
        });
        mViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                // отображение процесса загрузки
                mLoadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
        mViewModel.getConversionRate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String rate) {
                // обновление информации о курсе валют
                mConversionRate.setText(rate);
            }
        });
        mViewModel.getErrors().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                // отображение ошибки
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
        mViewModel.loadCurrencies(); // загрузка списка валют
    }

    /**
     * Реализация обработчика выбора элемента спиннера
     */
    private class OnCurrencySelectedListener implements AdapterView.OnItemSelectedListener {
        /**
         * Обновлении информации о курсе валют
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mViewModel.updateConversionRate(
                    mSpinnerFrom.getSelectedItemPosition(), // индекс исходной валюты
                    mSpinnerTo.getSelectedItemPosition()); // индекс итоговой валюты
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}