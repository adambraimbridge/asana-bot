package com.ft.backup.csv;

import com.ft.asanaapi.model.AsanaEntity;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class EntityCellFormatter extends CellProcessorAdaptor implements StringCellProcessor {

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);
        validateInputClass(value, context);

        String formattedValue = formatValue(value);
        return next.execute(formattedValue, context);
    }

    private String formatValue(Object value) {
        AsanaEntity asanaEntity = (AsanaEntity) value;
        return asanaEntity.getName();
    }

    private void validateInputClass(Object value, CsvContext context) {
        if (!(value instanceof AsanaEntity)) {
            throw new SuperCsvCellProcessorException(AsanaEntity.class, value, context, this);
        }
    }
}
