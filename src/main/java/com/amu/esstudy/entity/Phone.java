package com.amu.esstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("phone")
public class Phone implements Serializable {
    private static final long serialVersionUID = -5087658155687251393L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    // 名称
    private String name;

    // 颜色，用英文分号；分隔
    private String colors;

    // 卖点，用英文分号；分隔
    @TableField("selling_points")
    private String sellingPoints;

    // 价格
    private String price;

    // 产量
    private String yield;

    // 销售量
    private Integer sale;

    // 上市时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField("market_time")
    private Date marketTime;

    // 数据抓取时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField("create_time")
    private Date createTime;
}
