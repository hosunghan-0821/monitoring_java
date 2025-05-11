package com.example.monitor.infra.converter.controller;

import com.example.monitor.infra.converter.dto.ConvertProduct;
import com.example.monitor.monitoring.global.MonitoringProduct;

import java.util.List;

public interface IConverterFacade {
    void convertProduct(List<ConvertProduct> convertProduct);

    void sendToSearchServer(List<ConvertProduct> convertProductList);

    void sendToAutoOrderServer(MonitoringProduct newProduct);
}
