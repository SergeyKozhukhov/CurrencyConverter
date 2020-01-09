package ru.sergeykozhukhov.currencyconverter.data;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import java.math.BigDecimal;

/**
 * Читает строки вида "41,456" как {@link BigDecimal}
 **/
// Converter<BigDecimal> - способ обработки данных при сериализации/десериализации
// для получения требуемых итоговых результатов, отличных от исходных
public class BigDecimalConverter implements Converter<BigDecimal> {

    /**
     * Метод, используемый при десериализации
     * @param node - узел xml, подвергающийся обработке
     * @return BigDecimal из String
     */
    @Override
    public BigDecimal read(InputNode node) throws Exception {
        // получение объекта BigDecimal из xml узла со значением вида e.g. <Value>43,3835</Value>
        // для BigDecimal требуется наличие точки вместо запятой
        return new BigDecimal(node.getValue().replace(',', '.'));
    }

    /**
     * Метод, используемый при сериализации
     *
     * @param node - узел, в который преобразовываются данные
     * @param value - данные подвергающиеся преобразованию
     */
    // в данном случае в ее выполнении нет необходимсости в силу текущей задачи
    @Override
    public void write(OutputNode node, BigDecimal value) {
        throw new UnsupportedOperationException("Serialization is not supported");
    }
}
