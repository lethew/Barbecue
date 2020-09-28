package com.thunisoft.znbq.bbq.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.model Barbecue
 * @date 2020/8/13 0013 11:44
 */
@Entity
@Table(name = "t_normalcode_type", schema = "normalcode")
@Data
@Accessors(chain = true)
public class NormalCodeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String innerKey;
    private String outerKey;
    private String name;
    private Integer version;
    private String sysId;
}
