package com.alfa.bank.project.gifAndExchangeRate.dto;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CurrenciesDeserializer extends JsonDeserializer<CurrenciesDto> {
    @Override
    public CurrenciesDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {

        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);
        Iterator<String> fieldNames = node.fieldNames();
        List<String> currencies = new ArrayList<>();
        while(fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            currencies.add(fieldName);
        }
        return new CurrenciesDto(currencies);
    }

}
