package com.recognize.common.vo;


import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {

    private Long total;

    private Integer page;

    private Integer size;

    private List<T> items;
}