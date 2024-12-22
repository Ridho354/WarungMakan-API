package com.enigma.wmb_api.dto.response;

import com.enigma.wmb_api.constant.MenuCategory;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.MenuImage;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuResponse {
    private String id;
    private String name;
    private Long price;
    private Integer stock;
    private String category;
    private List<MenuImage> images;

//    public static MenuResponse menuEntityToMenuResponse(Menu menu) {
//          return MenuResponse.builder()
//                  .name(menu.getName())
//                  .price(menu.getPrice())
//                  .stock(menu.getStock())
//                  .category(menu.getCategory())
//                  .build();
//    }
}
