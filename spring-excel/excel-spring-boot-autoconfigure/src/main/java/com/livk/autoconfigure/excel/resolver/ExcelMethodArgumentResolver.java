package com.livk.autoconfigure.excel.resolver;

import com.alibaba.excel.EasyExcel;
import com.livk.autoconfigure.excel.annotation.ExcelImport;
import com.livk.autoconfigure.excel.listener.ExcelReadListener;
import com.livk.util.BeanUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * <p>
 * ExcelMethodArgumentResolver
 * </p>
 *
 * @author livk
 * @date 2022/1/17
 */
public class ExcelMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        ExcelImport excelImport = parameter.getMethodAnnotation(ExcelImport.class);
        return excelImport != null && excelImport.paramName().equals(parameter.getParameterName());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        if (!Collection.class.isAssignableFrom(parameter.getParameterType())) {
            throw new IllegalArgumentException("Excel upload request resolver error, @ExcelData parameter is not Collection ");
        }
        ExcelImport importExcel = parameter.getMethodAnnotation(ExcelImport.class);
        if (Objects.nonNull(importExcel)) {
            ExcelReadListener<?> listener = BeanUtils.instantiateClass(importExcel.parse());
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            Class<?> excelModelClass = ResolvableType.forMethodParameter(parameter).resolveGeneric(0);
            EasyExcel.read(getInputStream(request, importExcel.fileName()), excelModelClass, listener)
                    .ignoreEmptyRow(importExcel.ignoreEmptyRow()).sheet().doRead();
            return listener.getCollectionData();
        }
        return Collections.emptyList();
    }

    private InputStream getInputStream(HttpServletRequest request, String fileName) {
        try {
            if (request instanceof MultipartRequest multipartRequest) {
                MultipartFile file = multipartRequest.getFile(fileName);
                Assert.notNull(file, "file not be null");
                return file.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
