package com.enigma.wmb_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * @NotNull
 * - Mememastikan nilai tidak null
 * - Tidak peduli apakah string kosong atau hanya whitespace " "
 * - Cocok untuk tipe data primitif dan object
 *
 * @NotBlank
 * - Khusus untuk String
 * - Memastikan nilai tidak null, tidak kosong, dan tidak hanya whitespace
 * - "" invalid
 * - "   " invalid
 *
 * @NotEmpty
 * - Memastikan nilai tidak null dan memastikan panjang > 0
 * */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuRequest {
    @NotBlank(message = "Menu name is required")
    private String name;

//    @Min(value = 1, message = "price must be greater equal than 1") bahasanya terlalu teknis
    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price cannot be zero or negative value") // bahasa lebih manusiawi
    private Long price;

    @NotBlank(message = "Category is required")
    @Pattern(regexp = "(?i)(MAKANAN|MINUMAN)", message = "Category must be either MAKANAN or MINUMAN")
    private String category;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative value")
    private Integer stock;
}
