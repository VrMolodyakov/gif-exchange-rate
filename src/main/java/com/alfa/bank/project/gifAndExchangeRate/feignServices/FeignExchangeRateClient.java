package com.alfa.bank.project.gifAndExchangeRate.feignServices;

import com.alfa.bank.project.gifAndExchangeRate.dto.ExchangeRateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "${openexchangerates.name}", url =  "${openexchangerates.baseURL}")
public interface FeignExchangeRateClient {

    @GetMapping("/latest.json?app_id=" + "${openexchangerates.appId}" + "&" + "${openexchangerates.base}")
    ExchangeRateDto getExchangeRates();

    @GetMapping("/historical/{date}.json?app_id=" + "${openexchangerates.appId}" + "&" + "${openexchangerates.base}")
    ExchangeRateDto getYesterdayExchangeRates(@PathVariable(name = "date") String yesterdayDate);

}
