package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

/* MenuRepository extends ke JpaRepository bertujuannya agar nanti implementasinya tergenerate
secara otomatis oleh Spring Data JPA, sehingga kita bisa menggunakan method-method yang telah
disediakan oleh implementasi Spring Data JPA tadi, untuk berkomunikasi langsung dengan database
dan secara otomatis nanti Objek/instance MenuRepository ini akan bisa di pakai oleh objek/ instance class lain
yang membutuhkan
MenuRepository akan secara otomatis dibuat sebagai Bean (jadi bisa dipakai oleh semua objek/instance class lain)
 */
public interface MenuRepository extends JpaRepository<Menu, String>, JpaSpecificationExecutor<Menu> {
    List<Menu> findByNameIgnoreCase(String name); // JPA query methods
    // https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
    List<Menu> findByPrice(double price);
    List<Menu> findByNameIgnoreCaseAndPrice(String name, double price);
    Page<Menu> findByNameIgnoreCaseAndPrice(Pageable page, String name, double price);
}
