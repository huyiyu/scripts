package com.huyiyu.excel.service;

import java.io.OutputStream;
import java.util.function.Consumer;

public interface StorageService {

    String upload(Consumer<OutputStream> oconsumer);
}
