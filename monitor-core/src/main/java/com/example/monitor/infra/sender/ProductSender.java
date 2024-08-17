package com.example.monitor.infra.sender;

import java.util.List;

public interface ProductSender {
     void sendToSearchServer(List<SearchProduct> searchProductList);
}
