package com.amu.esstudy.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


@Data
@Accessors(chain = true)
public class HuaWeiPhoneBean implements Serializable {

    private static final long serialVersionUID = 8336475299715913053L;

    private String productName;

    private List<ColorModeBean> colorsItemMode;

    private List<String> sellingPoints;

}