package com.thunisoft.znbq.bbq.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.model.dto Barbecue
 * @date 2020/8/13 0013 13:59
 */
@Entity
@Table(name = "t_normalcode_system", schema = "normalcode")
@Data
@Accessors(chain = true)
public class NormalCodeSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    private String desc;
    private Date createTime;
    private String importerId;
}
