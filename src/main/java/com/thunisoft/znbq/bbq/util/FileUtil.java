package com.thunisoft.znbq.bbq.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.util Barbecue
 * @date 2020/9/28 0028 19:34
 */
@UtilityClass
public class FileUtil {
    public List<File> loadFiles(File root, FileFilter filter) {
        List<File> result = new LinkedList<>();
        if (root.isFile() && filter.accept(root)) {
            result.add(root);
        } else if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    result.addAll(loadFiles(file, filter));
                }
            }
        }
        return result;
    }
}
