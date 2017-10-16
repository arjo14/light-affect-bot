package me.aboullaite.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPhoto {

    private Integer id;
    private String url;
    private byte[] photo;
    private Integer productId;
    private String targetUrl;
}
