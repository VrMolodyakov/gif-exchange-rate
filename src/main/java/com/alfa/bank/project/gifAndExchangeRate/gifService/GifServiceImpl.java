package com.alfa.bank.project.gifAndExchangeRate.gifService;

import com.alfa.bank.project.gifAndExchangeRate.dto.GifUrlDto;
import com.alfa.bank.project.gifAndExchangeRate.exchangeServices.ExchangeServiceImpl;
import com.alfa.bank.project.gifAndExchangeRate.feignServices.FeignGifClient;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class GifServiceImpl implements GifService {

    private final static Logger LOGGER = LoggerFactory.getLogger(GifServiceImpl.class);
    private FeignGifClient gifClient;
    @Override
    public GifUrlDto getGifUrlByRate(BigDecimal currencyExchangeRate) {
        LOGGER.debug("the result of subtraction for currency rate {} ",currencyExchangeRate);
        if(currencyExchangeRate.compareTo(BigDecimal.ZERO) > 0){
            return gifClient.getRandomRichGifByExchangeRates();
        }else if(currencyExchangeRate.compareTo(BigDecimal.ZERO) < 0){
            return gifClient.getRandomBrokeGifByExchangeRates();
        }
        return gifClient.getRandomEqualCurrencyGifByExchangeRates();
    }
}
