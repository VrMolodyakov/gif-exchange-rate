package com.alfa.bank.project.gifAndExchangeRate.exchangeServices;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeService {
    Optional<BigDecimal> getTodayAndYesterdayCurrencyRate(String code);
}
