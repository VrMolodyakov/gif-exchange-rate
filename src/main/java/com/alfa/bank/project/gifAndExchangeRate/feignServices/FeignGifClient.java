package com.alfa.bank.project.gifAndExchangeRate.feignServices;
import com.alfa.bank.project.gifAndExchangeRate.dto.GifUrlDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(name = "${giphy.name}", url =  "${giphy.baseURL}")
public interface FeignGifClient {

    @GetMapping("/random?api_key=" + "${giphy.appKey}" + "&"+"${giphy.limit}" + "&"+"${giphy.rating}" + "&"+"${giphy.richTag}")
    GifUrlDto getRandomRichGifByExchangeRates();

    @GetMapping("/random?api_key=" + "${giphy.appKey}" + "& ${giphy.limit}" + "& ${giphy.rating}" + "& ${giphy.brokeTag}")
    GifUrlDto getRandomBrokeGifByExchangeRates();

    @GetMapping("/random?api_key=" + "${giphy.appKey}" + "&"+"${giphy.limit}" + "&"+"${giphy.rating}" + "&"+"${giphy.equalCurrencyTag}")
    GifUrlDto getRandomEqualCurrencyGifByExchangeRates();
}
