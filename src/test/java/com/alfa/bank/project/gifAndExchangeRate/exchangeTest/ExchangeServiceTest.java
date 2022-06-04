package com.alfa.bank.project.gifAndExchangeRate.exchangeTest;

import com.alfa.bank.project.gifAndExchangeRate.dto.CurrencyRateDto;
import com.alfa.bank.project.gifAndExchangeRate.exchangeServices.ExchangeService;
import com.alfa.bank.project.gifAndExchangeRate.feignServices.FeignExchangeRateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ExchangeServiceTest {

    @Autowired
    private ExchangeService currencyExchangeRateService;
    @MockBean
    private FeignExchangeRateClient FeignExchangeRatesClient;

    private CurrencyRateDto currentRates;
    private CurrencyRateDto yesterdayRates;
    private CurrencyRateDto todayThreadRates;
    private CurrencyRateDto yesterdayThreadRates;
    private final String CODE = "USD";
    private LocalDateTime yesterdayThreadDate = LocalDateTime.now().minusDays(1);

    @BeforeEach
    public void init() {
        Map<String, BigDecimal> currentCurrencyMap = new HashMap<>();
        currentCurrencyMap.put("firstCase",BigDecimal.valueOf(1));
        currentCurrencyMap.put("secondCase",BigDecimal.valueOf(2));
        currentCurrencyMap.put("thirdCase",BigDecimal.valueOf(0));
        long currentTime = 1654293608;
        currentRates = new CurrencyRateDto("1","1", currentTime,CODE,currentCurrencyMap);

        Map<String, BigDecimal> yesterdayCurrencyMap = new HashMap<>();
        yesterdayCurrencyMap.put("firstCase",BigDecimal.ZERO);
        yesterdayCurrencyMap.put("secondCase",BigDecimal.valueOf(3));
        yesterdayCurrencyMap.put("thirdCase",BigDecimal.valueOf(0));
        long yesterdayTime = 1654214399;
        yesterdayRates = new CurrencyRateDto("2","2", yesterdayTime,CODE,yesterdayCurrencyMap);

        Map<String, BigDecimal> threadCurrencyMap = new HashMap<>();
        threadCurrencyMap.put("firstCase",BigDecimal.valueOf(1));
        threadCurrencyMap.put("secondCase",BigDecimal.valueOf(2));
        threadCurrencyMap.put("thirdCase",BigDecimal.valueOf(0));
        ZoneId zoneId = ZoneId.systemDefault();
        long timestampRightNow = LocalDateTime.now().atZone(zoneId).toEpochSecond();
        todayThreadRates = new CurrencyRateDto("3","3", timestampRightNow,CODE,threadCurrencyMap);


        long timestampYesterday = yesterdayThreadDate.atZone(zoneId).toEpochSecond();
        yesterdayThreadRates = new CurrencyRateDto("4","4",timestampYesterday,CODE,threadCurrencyMap);
    }

    @Test
    public void currencyExchangeRateShouldIncreaseAndReturn1(){
        given(FeignExchangeRatesClient.getExchangeRates()).willReturn(currentRates);
        given(FeignExchangeRatesClient.getYesterdayExchangeRates(any())).willReturn(yesterdayRates);
        Optional<BigDecimal> rate = currencyExchangeRateService.getTodayAndYesterdayCurrencyRate("firstCase");
        assertEquals(BigDecimal.ONE, rate.get());
    }

    @Test
    public void currencyExchangeRateShouldDecreaseAndReturnMinus1(){
        given(FeignExchangeRatesClient.getExchangeRates()).willReturn(currentRates);
        given(FeignExchangeRatesClient.getYesterdayExchangeRates(any())).willReturn(yesterdayRates);
        Optional<BigDecimal> rate = currencyExchangeRateService.getTodayAndYesterdayCurrencyRate("secondCase");
        assertEquals(BigDecimal.valueOf(-1), rate.get());
    }

    @Test
    public void currencyExchangeRateShouldBeEqualAndReturn0(){
        given(FeignExchangeRatesClient.getExchangeRates()).willReturn(currentRates);
        given(FeignExchangeRatesClient.getYesterdayExchangeRates(any())).willReturn(yesterdayRates);
        Optional<BigDecimal> rate = currencyExchangeRateService.getTodayAndYesterdayCurrencyRate("thirdCase");
        assertEquals(BigDecimal.ZERO, rate.get());
    }

    @Test
    public void unknownCurrencyAndShouldReturnEmptyOptional(){
        given(FeignExchangeRatesClient.getExchangeRates()).willReturn(currentRates);
        given(FeignExchangeRatesClient.getYesterdayExchangeRates(any())).willReturn(yesterdayRates);
        Optional<BigDecimal> rate = currencyExchangeRateService.getTodayAndYesterdayCurrencyRate("null");
        assertEquals(Optional.empty(), rate);
    }

    @Test
    public void shouldCallFeignClientOneTimeOnFirstAccessWithMultipleThreads() throws Exception{
        given(FeignExchangeRatesClient.getExchangeRates()).willReturn(todayThreadRates);
        given(FeignExchangeRatesClient.getYesterdayExchangeRates(any())).willReturn(yesterdayThreadRates);
        final var latch = new CountDownLatch(1);

        final Runnable runnable = () -> {
            try {
                latch.await();
                Optional<BigDecimal> rate = currencyExchangeRateService.getTodayAndYesterdayCurrencyRate("firstCase");
            } catch (InterruptedException ex) {
                throw new AssertionError(ex);
            }
        };

        final Thread[] threads = Stream.generate(() -> new Thread(runnable))
                .limit(5)
                .peek(Thread::start)
                .toArray(Thread[]::new);

        latch.countDown();

        for (Thread thread : threads) {
            thread.join();
        }

        verify(FeignExchangeRatesClient).getExchangeRates();
        verify(FeignExchangeRatesClient).getYesterdayExchangeRates(any());
    }


}
