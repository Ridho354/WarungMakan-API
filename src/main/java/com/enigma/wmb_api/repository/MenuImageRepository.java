package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.MenuImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MenuImageRepository extends JpaRepository<MenuImage, String> {
    List<MenuImage> findByMenuId(String menuId);
}
