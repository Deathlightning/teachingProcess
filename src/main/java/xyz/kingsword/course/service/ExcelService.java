package xyz.kingsword.course.service;

import java.io.InputStream;
import java.util.List;

public interface ExcelService<T> {
    List<T> excelImport(InputStream inputStream);
}
