package com.alfa.bank.project.gifAndExchangeRate.gifService;

import com.alfa.bank.project.gifAndExchangeRate.dto.GifUrlDto;

import java.math.BigDecimal;

public interface GifService {
    GifUrlDto getGifUrlByRate(BigDecimal currencyExchangeRate);
}
