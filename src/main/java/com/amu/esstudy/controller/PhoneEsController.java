package com.amu.esstudy.controller;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/phoneEs")
@RestController
public class PhoneEsController {

    @GetMapping("/search")
    public Object search(String keyWord,int offset,int pageSize) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("172.16.18.18", 9200, "http")));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("fulltext");// 设置 索引
        searchRequest.types("phone");
        // 方式一：
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", keyWord)
                .fuzziness(Fuzziness.AUTO)
                .prefixLength(0)
                .maxExpansions(20);
        searchSourceBuilder.query(matchQueryBuilder); // 设置关键字
        searchSourceBuilder.from(offset); // 偏移量
        searchSourceBuilder.size(pageSize); // 每页显示数量
        searchRequest.source(searchSourceBuilder);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 获取返回结果
        List<Map<String,Object>> result = dealResult(searchResponse.getHits());
        client.close();
        return result;
    }

    /**
     * 数据格式处理方法
     * @param hits
     * @return
     */
    private List<Map<String, Object>> dealResult(SearchHits hits){
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();
            result.add(map);
        }
        return result;
    }

}
