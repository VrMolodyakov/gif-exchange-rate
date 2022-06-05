package com.alfa.bank.project.gifAndExchangeRate.exchangeServices;
import com.alfa.bank.project.gifAndExchangeRate.dto.CurrenciesDto;
import com.alfa.bank.project.gifAndExchangeRate.dto.CurrencyRateDto;
import com.alfa.bank.project.gifAndExchangeRate.feignServices.FeignExchangeRateClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExchangeServiceImpl.class);
    private CurrencyRateDto currentRates;
    private CurrencyRateDto yesterdayRates;
    private CurrenciesDto currencyCodes;
    private final ReadWriteLock todayCallLock = new ReentrantReadWriteLock();
    private final ReadWriteLock yesterdayCallLock = new ReentrantReadWriteLock();
    private final ReadWriteLock allCurrencyCodesLock = new ReentrantReadWriteLock();
    @Autowired
    private FeignExchangeRateClient exchangeRateService;

    @Override
    public Optional<BigDecimal> getTodayAndYesterdayCurrencyRate(String code) {
        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);
        CurrencyRateDto currentRates = getCurrentExchangeRate(currentTime);
        CurrencyRateDto yesterdayRates = getYesterdayExchangeRate(currentTime);
        if(codeIsExist(currentRates,code)) {
            BigDecimal todayRate = currentRates.getRates().get(code);
            BigDecimal yesterdayRate = yesterdayRates.getRates().get(code);
            LOGGER.info("today rate {} and yesterday rate {}", todayRate, yesterdayRate);
            return Optional.of(todayRate.subtract(yesterdayRate));
        }
        return Optional.empty();
    }

    @Override
    public List<String> geAllCurrencyCodes() {
        Predicate<CurrenciesDto> codesIsNull = codes -> {
            if (codes == null ) {
                return true;
            }
            return false;
        };
        Supplier<CurrenciesDto> allCurrencyCodesSupplier = () -> exchangeRateService.getCodes();
        return updateCurrencyCodesIf(codesIsNull,allCurrencyCodesSupplier);
    }

    public CurrencyRateDto getCurrentExchangeRate(LocalDateTime currentTime) {
        LOGGER.debug("call today rates with time {}",currentTime);
        Predicate<LocalDateTime> rateNullOrOld = time -> {
            if (currentRates == null ) {
                return true;
            }
            LocalDateTime lastUpdateTime =
                    LocalDateTime.ofInstant(Instant.ofEpochSecond(currentRates.getTimestamp()), ZoneOffset.UTC);
            Duration timeBetween = Duration.between(lastUpdateTime, currentTime);
            long hoursBetween = timeBetween.minusDays(timeBetween.toDays()).toHoursPart();
            LOGGER.debug("time between {} for current",hoursBetween);
            if (hoursBetween != 0 ) {
                return true;
            }
            return false;
        };
        Supplier<CurrencyRateDto> currencyRateSupplier = () -> exchangeRateService.getExchangeRates();
        return updateCurrentRateIf(rateNullOrOld,currencyRateSupplier,currentTime);
    }


    public CurrencyRateDto getYesterdayExchangeRate(LocalDateTime currentTime) {
        LOGGER.debug("call yesterday rates with time {}",currentTime);
        String yesterday = LocalDate.now(ZoneOffset.UTC).minusDays(1L).toString();
        Predicate<LocalDateTime> yesterdayRateNullOrOld = time -> {
            if(yesterdayRates == null) {
                return true;
            }
            LocalDateTime lastUpdateTime = LocalDateTime
                    .ofInstant(Instant.ofEpochSecond(yesterdayRates.getTimestamp()), ZoneOffset.UTC);
            Duration timeBetween = Duration.between(lastUpdateTime,currentTime.minusDays(1L));
            long daysBetween = timeBetween.toDays();
            LOGGER.debug("days between yesterday {}",daysBetween);
            if(daysBetween != 0){
                return true;
            }
            return false;
        };
        Supplier<CurrencyRateDto> currencyRateSupplier = () -> exchangeRateService.getYesterdayExchangeRates(yesterday);
        return updateYesterdayRateIf(yesterdayRateNullOrOld,currencyRateSupplier,currentTime);
    }

    private CurrencyRateDto updateCurrentRateIf(Predicate<LocalDateTime> condition,
                                                Supplier<CurrencyRateDto> currencyRateRetriever,
                                                LocalDateTime currentTime){
        todayCallLock.readLock().lock();
        try {
            if(!condition.test(currentTime)){
                return currentRates;
            }
        }finally {
            todayCallLock.readLock().unlock();
        }
        todayCallLock.writeLock().lock();
        try {
            if(condition.test(currentTime)){
                LOGGER.debug("current rates was refreshed");
                currentRates = currencyRateRetriever.get();
            }
            return currentRates;
        }finally {
            todayCallLock.writeLock().unlock();
        }
    }

    private CurrencyRateDto updateYesterdayRateIf(Predicate<LocalDateTime> condition,
                                                  Supplier<CurrencyRateDto> currencyRateRetriever,
                                                  LocalDateTime currentTime){
        yesterdayCallLock.readLock().lock();
        try {
            if(!condition.test(currentTime)){
                return yesterdayRates;
            }
        }finally {
            yesterdayCallLock.readLock().unlock();
        }
        yesterdayCallLock.writeLock().lock();
        try {
            if(condition.test(currentTime)){
                LOGGER.debug("yesterday rates was refreshed");
                yesterdayRates = currencyRateRetriever.get();
            }
            return yesterdayRates;
        }finally {
            yesterdayCallLock.writeLock().unlock();
        }
    }

    private List<String> updateCurrencyCodesIf(Predicate<CurrenciesDto> condition,
                                               Supplier<CurrenciesDto> currencyRateRetriever){
        allCurrencyCodesLock.readLock().lock();
        try {
            if(!condition.test(currencyCodes)){
                return currencyCodes.getCurrencies();
            }
        }finally {
            allCurrencyCodesLock.readLock().unlock();
        }
        allCurrencyCodesLock.writeLock().lock();
        try {
            if(condition.test(currencyCodes)){
                LOGGER.debug("refresh currency codes list");
                currencyCodes = currencyRateRetriever.get();
            }
            return currencyCodes.getCurrencies();
        }finally {
            allCurrencyCodesLock.writeLock().unlock();
        }
    }

    private boolean codeIsExist(CurrencyRateDto currencyRateDto, String code){
        return currencyRateDto.getRates().containsKey(code);
    }
}
