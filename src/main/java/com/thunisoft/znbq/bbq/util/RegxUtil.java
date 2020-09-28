package com.thunisoft.znbq.bbq.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

/**
 * TODO:
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.util Barbecue
 * @date 2020/9/28 0028 19:49
 */
@UtilityClass
public class RegxUtil {
    private static final Pattern EN_STR = Pattern.compile("^[a-zA-Z0-9_]+$");

    public boolean isEnStr(String src) {
        return EN_STR.matcher(src).find();
    }
}
