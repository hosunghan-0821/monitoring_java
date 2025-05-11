package com.example.monitor.infra.sender;

import com.example.monitor.infra.sender.dto.AutoOrderDto;
import com.example.monitor.infra.sender.dto.SearchProduct;

import java.util.List;

public interface ProductSender {
     void sendToSearchServer(List<SearchProduct> searchProductList);

     void sendToAutoOrderServer(AutoOrderDto autoOrderDto);
}
