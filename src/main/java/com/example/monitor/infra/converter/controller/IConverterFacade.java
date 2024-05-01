package com.example.monitor.infra.converter.controller;

import com.example.monitor.infra.converter.dto.ConvertProduct;
import com.example.monitor.infra.sender.SearchProduct;

import java.util.List;

public interface IConverterFacade {
    List<ConvertProduct> convertProduct(List<ConvertProduct> convertProduct);

     void sendToSearchServer(List<ConvertProduct> convertProductList);
}
