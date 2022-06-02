package com.alfa.bank.project.gifAndExchangeRate.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GifUrlDto {

    private String url;
    private String embedUrl;

    @JsonProperty("data")
    public void setUrl(Map<String,Object> data) {
        this.url =data.get("url").toString();
        this.embedUrl = data.get("embed_url").toString();
    }
}
