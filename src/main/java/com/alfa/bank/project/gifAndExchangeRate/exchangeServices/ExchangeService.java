package com.alfa.bank.project.gifAndExchangeRate.exchangeServices;

import com.alfa.bank.project.gifAndExchangeRate.dto.ExchangeRateDto;

import java.time.LocalDateTime;

public interface ExchangeService {
    ExchangeRateDto getCurrentExchangeRate(LocalDateTime currentTime);
    ExchangeRateDto getYesterdayExchangeRate(LocalDateTime currentTime);
}
