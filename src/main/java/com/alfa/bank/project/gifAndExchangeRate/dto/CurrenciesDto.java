package com.alfa.bank.project.gifAndExchangeRate.dto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonDeserialize(using = CurrenciesDeserializer.class)
@ToString
public class CurrenciesDto  {
    private List<String> currencies;

}
