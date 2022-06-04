package com.alfa.bank.project.gifAndExchangeRate.gifTest;

import com.alfa.bank.project.gifAndExchangeRate.dto.CurrencyRateDto;
import com.alfa.bank.project.gifAndExchangeRate.dto.GifUrlDto;
import com.alfa.bank.project.gifAndExchangeRate.exchangeServices.ExchangeService;
import com.alfa.bank.project.gifAndExchangeRate.feignServices.FeignExchangeRateClient;
import com.alfa.bank.project.gifAndExchangeRate.feignServices.FeignGifClient;
import com.alfa.bank.project.gifAndExchangeRate.gifService.GifService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class GifServiceTest {

    @Autowired
    private GifService gifService;
    @MockBean
    private FeignGifClient gifClient;

    private GifUrlDto richGifDto;
    private GifUrlDto brokeGifDto;
    private GifUrlDto equalCurrencyGifDto;

    @BeforeEach
    public void init() {
        richGifDto = new GifUrlDto("richUrl","richEmbedUrl");
        brokeGifDto = new GifUrlDto("brokeUrl","brokeEmbedUrl");
        equalCurrencyGifDto = new GifUrlDto("equalUrl","equalEmbedUrl");
    }

    @Test
    public void shouldReturnRichGifDto(){
        given(gifClient.getRandomRichGifByExchangeRates()).willReturn(richGifDto);
        GifUrlDto gifUrlByRate = gifService.getGifUrlByRate(BigDecimal.ONE);
        assertEquals(richGifDto, gifUrlByRate);
    }

    @Test
    public void shouldReturnBrokeGifDto(){
        given(gifClient.getRandomBrokeGifByExchangeRates()).willReturn(brokeGifDto);
        GifUrlDto gifUrlByRate = gifService.getGifUrlByRate(BigDecimal.valueOf(-1));
        assertEquals(brokeGifDto, gifUrlByRate);
    }

    @Test
    public void shouldReturnEqualGifDto(){
        given(gifClient.getRandomEqualCurrencyGifByExchangeRates()).willReturn(equalCurrencyGifDto);
        GifUrlDto gifUrlByRate = gifService.getGifUrlByRate(BigDecimal.ZERO);
        assertEquals(equalCurrencyGifDto, gifUrlByRate);
    }
}
