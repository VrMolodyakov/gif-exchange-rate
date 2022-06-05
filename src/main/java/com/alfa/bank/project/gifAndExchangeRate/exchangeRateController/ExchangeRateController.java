package com.alfa.bank.project.gifAndExchangeRate.exchangeRateController;
import com.alfa.bank.project.gifAndExchangeRate.dto.GifUrlDto;
import com.alfa.bank.project.gifAndExchangeRate.exchangeServices.ExchangeService;
import com.alfa.bank.project.gifAndExchangeRate.gifService.GifService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@AllArgsConstructor
public class ExchangeRateController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExchangeRateController.class);
    private final ExchangeService exchangeService;
    private final GifService GifByCurrencyExchangeRate;

    @RequestMapping(value = "/currencies/{code}", method = GET)
    public String getGifByExchangeRate(@PathVariable String code, Model model){
        LOGGER.info("get rate for currency {} ",code);
        Optional<BigDecimal> exchangeRateDifference = exchangeService.getTodayAndYesterdayCurrencyRate(code);
        if(exchangeRateDifference.isEmpty()){
            List<String> codes = exchangeService.geAllCurrencyCodes();
            model.addAttribute("codes",codes);
            return "error";
        }
        GifUrlDto rateGif = GifByCurrencyExchangeRate.getGifUrlByRate(exchangeRateDifference.get());
        String gifUrl = rateGif.getUrl();
        model.addAttribute("baseUrl",rateGif.getEmbedUrl());
        model.addAttribute("embedUrl",rateGif.getEmbedUrl());
        return "gifPage";
    }


}


