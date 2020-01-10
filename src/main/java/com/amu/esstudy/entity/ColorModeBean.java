package com.amu.esstudy.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ColorModeBean implements Serializable {
    private static final long serialVersionUID = -8082160955256532791L;
    private String colorName;
}