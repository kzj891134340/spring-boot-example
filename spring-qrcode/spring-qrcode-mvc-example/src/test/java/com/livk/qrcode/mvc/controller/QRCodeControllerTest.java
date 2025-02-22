package com.livk.qrcode.mvc.controller;

import com.livk.autoconfigure.qrcode.enums.PicType;
import com.livk.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * QRCodeControllerTest
 * </p>
 *
 * @author livk
 * @date 2022/11/4
 */
@SpringBootTest
@AutoConfigureMockMvc
class QRCodeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void hello() throws Exception {
        mockMvc.perform(get("/qrcode"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    ByteArrayInputStream in = new ByteArrayInputStream(result.getResponse().getContentAsByteArray());
                    FileUtils.testDownload(in, "hello." + PicType.JPG.name().toLowerCase());
                });
        File outFile = new File("hello." + PicType.JPG.name().toLowerCase());
        Assertions.assertTrue(outFile.exists());
        Assertions.assertTrue(outFile.delete());
    }

    @Test
    void json() throws Exception {
        mockMvc.perform(get("/qrcode/json"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    ByteArrayInputStream in = new ByteArrayInputStream(result.getResponse().getContentAsByteArray());
                    FileUtils.testDownload(in, "json." + PicType.JPG.name().toLowerCase());
                });
        File outFile = new File("json." + PicType.JPG.name().toLowerCase());
        Assertions.assertTrue(outFile.exists());
        Assertions.assertTrue(outFile.delete());
    }
}
