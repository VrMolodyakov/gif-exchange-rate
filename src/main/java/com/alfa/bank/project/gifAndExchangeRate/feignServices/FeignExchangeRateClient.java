package com.alfa.bank.project.gifAndExchangeRate.feignServices;

import com.alfa.bank.project.gifAndExchangeRate.dto.CurrencyRateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "${openexchangerates.name}", url =  "${openexchangerates.baseURL}")
public interface FeignExchangeRateClient {

    @GetMapping("/latest.json?app_id=" + "${openexchangerates.appId}" + "&" + "${openexchangerates.base}")
    CurrencyRateDto getExchangeRates();

    @GetMapping("/historical/{date}.json?app_id=" + "${openexchangerates.appId}" + "&" + "${openexchangerates.base}")
    CurrencyRateDto getYesterdayExchangeRates(@PathVariable(name = "date") String yesterdayDate);

}
