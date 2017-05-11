package com.blueraja.magicduelsimporter.utils;

import org.junit.Test;

import java.io.File;

import static com.blueraja.magicduelsimporter.utils.FileUtils.getBaseName;
import static org.assertj.core.api.Assertions.assertThat;

public class FileUtilsTest {

    @Test
    public void testGetBaseName() throws Exception {
        assertThat(getBaseName("fileWithoutExtension")).isEqualTo("fileWithoutExtension");
        assertThat(getBaseName("fileWithExtension.txt")).isEqualTo("fileWithExtension");
        assertThat(getBaseName("relative" + File.separator + "path" + File.separator + "file.txt")).isEqualTo("file");
        assertThat(getBaseName("C:\\relative" + File.separator + "path" + File.separator + "file.txt")).isEqualTo("file");
    }
}