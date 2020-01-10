package com.amu.esstudy.controller;

import com.amu.esstudy.entity.Books;
import com.amu.esstudy.entity.Phone;
import com.amu.esstudy.mapper.BooksMapper;
import com.amu.esstudy.mapper.PhoneMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController {

    @Autowired
    private BooksMapper booksMapper;

    @Autowired
    private PhoneMapper phoneMapper;

    @GetMapping("/")
    public String Index(){
        QueryWrapper<Books> wrapper = new QueryWrapper();
        wrapper.like("title","大雨");
//        return booksMapper.SelectFullText(wrapper).toString();
        return booksMapper.selectList(wrapper).toString();
    }

    /**
     * 第一种分页方式
     * @param keyWord
     * @return
     */
    @GetMapping("/mysqlPhone")
    public Object mysqlPhone(String keyWord){
        // 1、设置条件
        QueryWrapper<Phone> wrapper = new QueryWrapper();
        wrapper.like("colors",keyWord).or();
        wrapper.like("selling_points",keyWord).or();
        wrapper.like("name",keyWord);
        // 2、设置分页
        Page<Phone> page = new Page<>(1,5);  // 查询第1页，每页返回5条
        IPage<Phone> iPage = phoneMapper.selectPage(page,wrapper);

        return iPage;

    }

    /**
     * 第二种分页方式
     * @param keyWord
     * @return
     */
    @GetMapping("/mysqlSelectPhone")
    public Object mysqlSelectPhone(String keyWord){
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> m = new HashMap<>();
        m.put("keyWord","%" + keyWord + "%");
        Page<Phone> page = new Page<>(1,5);
        Page<Phone> mapIPage = page.setRecords(phoneMapper.selectpage(m,page));
        return mapIPage;
    }
}
