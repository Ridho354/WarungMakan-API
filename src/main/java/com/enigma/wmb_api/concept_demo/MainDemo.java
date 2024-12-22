package com.enigma.wmb_api.concept_demo;

import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.repository.MenuRepository;
import  org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class MainDemo {
    MenuRepository menuRepository;

    public static void main(String[] args) {
        SingletonDesignPatternDemo instance1 = SingletonDesignPatternDemo.getInstance();
        SingletonDesignPatternDemo instance2 = SingletonDesignPatternDemo.getInstance();

        SingletonDesignPatternDemo instance3 = new SingletonDesignPatternDemo();
        SingletonDesignPatternDemo instance4 = new SingletonDesignPatternDemo();


        System.out.println("instance1 == instance2 " + (instance1 == instance2));
        System.out.println("instance3 == instance4 " + (instance3 == instance4));

        GenericDemo genericDemo = new GenericDemo("String");
        GenericDemo genericDemo2 = new GenericDemo(10000);
        GenericDemo genericDemo3 = new GenericDemo(true);

        System.out.println("genericDemo" + genericDemo.getValue().getClass().getSimpleName());
        System.out.println("genericDemo2" + genericDemo2.getValue().getClass().getSimpleName());
        System.out.println("genericDemo3" + genericDemo3.getValue().getClass().getSimpleName());

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));

        Sort sort = Sort.by("name", "price");

        Sort descSort = Sort.by(Sort.Direction.DESC,"name", "price");

        Sort multipleSort = Sort.by(Sort.Order.desc("price"), Sort.Order.asc("name"));

        Sort parsedSort = parseSortFromQueryParam("-name");
        System.out.println(parsedSort.toString());
        Sort parsedSort2 = parseSortFromQueryParam("name");
        System.out.println(parsedSort2.toString());

//        Page<Menu> currentPage = menuRepository.findAll(pageable);

        // Page<Menu> currentPage adalah halaman hasil dari findAll dengan pageable tersebut

//        System.out.println(currentPage.getTotalPages()); // jadi punya informasi tentang jumlah/total halaman
//        System.out.println(currentPage.getNumber()); /// sekarang halaman keberapa



        Page<String> page = (Page<String>) new Object();
        System.out.println(page.getTotalPages()); // jadi punya informasi tentang jumlah/total halaman
    }

    public static Sort parseSortFromQueryParam(String sortQueryParam) {
        if (sortQueryParam != null && sortQueryParam.startsWith("-")) {
            System.out.println("sortQueryParam.substring(1)" + sortQueryParam.substring(1));
            return Sort.by(Sort.Direction.DESC, sortQueryParam.substring(1));
        }
            return Sort.by(Sort.Direction.ASC, sortQueryParam);
    }
}
