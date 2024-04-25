package com.example.monitor.infra.converter.controller;

import com.example.monitor.infra.converter.dto.ConvertProduct;

import java.util.List;

public interface IConverterFacade {
    public void convertProduct(List<ConvertProduct> convertProduct);
}
