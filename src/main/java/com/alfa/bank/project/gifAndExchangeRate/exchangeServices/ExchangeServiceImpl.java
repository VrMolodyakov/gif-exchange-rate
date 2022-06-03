package com.alfa.bank.project.gifAndExchangeRate.exchangeServices;

import com.alfa.bank.project.gifAndExchangeRate.dto.ExchangeRateDto;
import com.alfa.bank.project.gifAndExchangeRate.exchangeRateController.ExchangeRateController;
import com.alfa.bank.project.gifAndExchangeRate.feignServices.FeignExchangeRateClient;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.TimeZone;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExchangeServiceImpl.class);
    private final static String YESTERDAY = LocalDate.now(ZoneOffset.UTC).minusDays(1L).toString();
    private volatile ExchangeRateDto currentRates;
    private volatile ExchangeRateDto yesterdayRates;
    @Autowired
    private FeignExchangeRateClient exchangeRateService;

    @Override
    public ExchangeRateDto getCurrentExchangeRate(LocalDateTime currentTime) {
        LOGGER.info("call today rates with time {}",currentTime);
        if(currentExchangeRateIsNullOrOld(currentTime)){
            LOGGER.info("current rates was refreshed");
            currentRates = exchangeRateService.getExchangeRates();
        }
        return currentRates;

    }

    @Override
    public ExchangeRateDto getYesterdayExchangeRate(LocalDateTime currentTime) {
        LOGGER.info("call yesterday rates with time {}",currentTime);
        if(yesterdayExchangeRateIsNullOrOld(currentTime)){
            LOGGER.info("current rates was refreshed");
            yesterdayRates = exchangeRateService.getYesterdayExchangeRates(YESTERDAY);
        }
        return yesterdayRates;
    }

    private boolean currentExchangeRateIsNullOrOld(LocalDateTime currentTime){
        if( yesterdayRates == null) {
            return true;
        }
        LocalDateTime lastUpdateTime = LocalDateTime
                .ofInstant(Instant.ofEpochSecond(currentRates.getTimestamp()), ZoneOffset.UTC);
        Duration timeBetween = Duration.between(lastUpdateTime,currentTime);
        long hoursBetween = timeBetween.minusDays(timeBetween.toDays()).toHoursPart();
        if(hoursBetween != 0){
            return true;
        }
        return false;
    }

    private boolean yesterdayExchangeRateIsNullOrOld(LocalDateTime currentTime){
        if(yesterdayRates == null) {
            return true;
        }
        LocalDateTime lastUpdateTime = LocalDateTime
                .ofInstant(Instant.ofEpochSecond(currentRates.getTimestamp()), ZoneOffset.UTC);
        Duration timeBetween = Duration.between(lastUpdateTime,currentTime);
        long daysBetween = timeBetween.toDays();
        if(daysBetween != 0){
            return true;
        }
        return false;
    }


}
