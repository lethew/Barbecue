package com.thunisoft.znbq.bbq.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.model Barbecue
 * @date 2020/8/13 0013 11:44
 */
@Entity
@Table(name = "t_normalcode", schema = "normalcode")
@Data
@Accessors(chain = true)
public class NormalCode {
    @Transient
    public static final NormalCode NONE = new NormalCode();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String innerKey;
    private String outerKey;
    private String name;
    /**
     * 顺序索引
     */
    private Integer idx;
    private Integer version;
    private String typeId;
    private String sysId;
}
