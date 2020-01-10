package com.amu.esstudy.mapper;

import com.amu.esstudy.entity.Books;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksMapper extends BaseMapper<Books> {

//    List<Books> SelectFullText(Wrapper<Books> queryWrapper);
}
