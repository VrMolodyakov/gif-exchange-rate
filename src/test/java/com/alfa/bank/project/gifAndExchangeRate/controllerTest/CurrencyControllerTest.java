package com.alfa.bank.project.gifAndExchangeRate.controllerTest;
import com.alfa.bank.project.gifAndExchangeRate.dto.GifUrlDto;
import com.alfa.bank.project.gifAndExchangeRate.exchangeServices.ExchangeService;
import com.alfa.bank.project.gifAndExchangeRate.gifService.GifService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.math.BigDecimal;
import java.util.Optional;


@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyControllerTest {

    @MockBean
    private ExchangeService exchangeService;
    @MockBean
    private GifService GifService;
    @Autowired
    private MockMvc mvc;
    private GifUrlDto richGifDto;
    private final String existCurrencyCode = "BOB";
    private final String notExistCurrencyCode = "null";
    @BeforeEach
    public void setup() {
        richGifDto = new GifUrlDto("richUrl","richEmbedUrl");
    }

    @Test
    public void existCodeAndShouldReturn200Status() throws Exception {

        given(exchangeService.getTodayAndYesterdayCurrencyRate(existCurrencyCode))
                .willReturn(Optional.of(BigDecimal.ONE));

        given(GifService.getGifUrlByRate(BigDecimal.ONE))
                .willReturn(richGifDto);

        MockHttpServletResponse response = mvc.perform(
                get("/currencies/" + existCurrencyCode).accept(MediaType.TEXT_HTML)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void unknownCodeAndShouldReturn200Status() throws Exception {

        given(exchangeService.getTodayAndYesterdayCurrencyRate(notExistCurrencyCode))
                .willReturn(Optional.empty());

        given(GifService.getGifUrlByRate(BigDecimal.ONE))
                .willReturn(richGifDto);

        MockHttpServletResponse response = mvc.perform(
                get("/currencies/" + notExistCurrencyCode).accept(MediaType.TEXT_HTML)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
