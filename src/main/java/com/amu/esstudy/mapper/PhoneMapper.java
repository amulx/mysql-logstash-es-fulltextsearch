package com.amu.esstudy.mapper;

import com.amu.esstudy.entity.Phone;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PhoneMapper extends BaseMapper<Phone> {

    @Select("SELECT id,name,colors,selling_points,price,yield,sale,market_time,create_time FROM phone WHERE (name LIKE  #{m.keyWord} OR selling_points LIKE #{m.keyWord} OR colors like #{m.keyWord})")
    List<Phone> selectpage(Map<String,Object> m, Page<Phone> page);
}
