package com.alfa.bank.project.gifAndExchangeRate.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExchangeRateDto {

    @JsonProperty("disclaimer")
    private String disclaimer;
    @JsonProperty("license")
    private String license;
    @JsonFormat(pattern="yyyy-MM-dd")
    @JsonProperty("timestamp")
    private Date timestamp;
    @JsonProperty("base")
    private String base;
    @JsonProperty("rates")
    private Map<String,BigDecimal> rates;


}
