package com.thunisoft.znbq.bbq.model.vo;

import java.util.List;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.model.vo Barbecue
 * @date 2020/8/13 0013 13:58
 */
public class NormalCodeTypeNode {
    private String id;
    private String innerKey;
    private String outerKey;
    private String name;
    private Integer version;
    private List<NormalCodeNode> codeNodes;
    private NormalCodeTypeNode mappingTypeNode;
}
