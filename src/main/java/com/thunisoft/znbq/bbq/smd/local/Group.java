package com.thunisoft.znbq.bbq.smd.local;

import lombok.Data;

import java.util.List;

/**
 * TODO:
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd Barbecue
 * @date 2020/9/28 0028 10:20
 */
@Data
public class Group {
    private String path;
    private List<Table> tables;
}
