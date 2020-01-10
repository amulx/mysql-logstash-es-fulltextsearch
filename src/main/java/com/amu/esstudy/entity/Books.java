package com.amu.esstudy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("books")
public class Books {
    private Integer id;
    private String title;
    private Double price;
    @TableField("publishDate")
    private Timestamp publishDate;
}
