package com.enigma.wmb_api.util;

import org.springframework.data.domain.Sort;

public class SortUtil {
    public static Sort parseSortFromQueryParam(String sortQueryParam) {
        if (sortQueryParam == null) {
            return null;
        }
        if (sortQueryParam.startsWith("-")) {
            System.out.println("sortQueryParam.substring(1)" + sortQueryParam.substring(1));
            return Sort.by(Sort.Order.desc(sortQueryParam.substring(1)).ignoreCase());
        }
        return Sort.by(Sort.Order.asc(sortQueryParam).ignoreCase());
    }
}
