package com.tpe.cinetime.service.helpers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageableHelper {

    public Pageable getPageableWithProps(
            int page,
            int size,
            String sortBy,
            Sort.Direction order) {

        return PageRequest.of(page-1, size, Sort.by(order, sortBy));
    }
}
