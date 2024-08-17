package com.example.monitor.file;

import java.io.IOException;

public interface ProductFileWriter {

    void fileFolderInit() throws IOException;

    void writeProductInfo(ProductFileInfo productFileInfo);

}
