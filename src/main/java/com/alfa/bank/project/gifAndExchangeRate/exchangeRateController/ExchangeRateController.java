package com.alfa.bank.project.gifAndExchangeRate.exchangeRateController;
import com.alfa.bank.project.gifAndExchangeRate.dto.ExchangeRateDto;
import com.alfa.bank.project.gifAndExchangeRate.dto.GifUrlDto;
import com.alfa.bank.project.gifAndExchangeRate.feignServices.FeignExchangeRateClient;
import com.alfa.bank.project.gifAndExchangeRate.feignServices.FeignGifClient;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;


import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@AllArgsConstructor
public class ExchangeRateController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExchangeRateController.class);
    private final static String YESTERDAY = LocalDate.now().minusDays(1L).toString();
    private FeignExchangeRateClient exchangeRateService;
    private FeignGifClient gifClient;

    @RequestMapping(value = "/currency/{code}", method = GET)
    public String getGifByExchangeRate(@PathVariable String code, Model model){
        LOGGER.info("get rate for currency {} ",code);
        ExchangeRateDto currentRates = exchangeRateService.getExchangeRates();
        ExchangeRateDto yesterdayRates = exchangeRateService.getYesterdayExchangeRates(YESTERDAY);
        BigDecimal todayRate = currentRates.getRates().get(code);
        if(currencyIsExist(currentRates,code)) {
            BigDecimal yesterdayRate = yesterdayRates.getRates().get(code);
            GifUrlDto rateGif = getGifUrlByRate(todayRate.subtract(yesterdayRate));
            LOGGER.debug("today rate {} and yesterday rate {}",todayRate,yesterdayRate);
            String gifUrl = rateGif.getUrl();
            model.addAttribute("baseUrl",rateGif.getEmbedUrl());
            model.addAttribute("embedUrl",rateGif.getEmbedUrl());
            return "myHome";

        }
        return "error";
        //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    private GifUrlDto getGifUrlByRate(BigDecimal currencyRate){
        LOGGER.debug("the result of subtraction for currency rate {} ",currencyRate);
        if(currencyRate.compareTo(BigDecimal.ZERO) > 0){
            return gifClient.getRandomRichGifByExchangeRates();
        }else if(currencyRate.compareTo(BigDecimal.ZERO) < 0){
            return gifClient.getRandomBrokeGifByExchangeRates();
        }
        return gifClient.getRandomEqualCurrencyGifByExchangeRates();
    }

    private boolean currencyIsExist(ExchangeRateDto rateDto , String currencyCode){
        return rateDto.getRates().containsKey(currencyCode);
    }
}


