package com.amu.esstudy.controller;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/es")
public class EsController {

    /**
     * 创建索引
     * @return
     * @throws IOException
     */
    @GetMapping("indexApi")
    public String indexApi() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
//                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("172.16.18.18", 9200, "http")));

        // 方式一
        IndexRequest request = new IndexRequest(
                "posts",
                "doc",
                "2");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearchsdfdsddfdddfdfdfd\"" +
                "}";
        request.source(jsonString, XContentType.JSON);
        /*
        // 方式二
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest indexRequest = new IndexRequest("posts", "doc", "1")
                .source(jsonMap);
*/
        // 方式三
/*        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "kimchy");
            builder.timeField("postDate", new Date());
            builder.field("message", "trying out Elasticsearch");
        }
        builder.endObject();
        IndexRequest indexRequest2 = new IndexRequest("posts", "doc", "1")
                .source(builder);*/

        // 获取响应结果
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        String index = indexResponse.getIndex();
        String type = indexResponse.getType();
        String id = indexResponse.getId();
        long version = indexResponse.getVersion();
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {

        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {

        }
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {

        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure :
                    shardInfo.getFailures()) {
                String reason = failure.reason();
            }
        }
        client.close();
        return index + "  " + type + "  " +  id +  "  " + version;
    }

    /**
     * 删除单条数据
     * @return
     * @throws IOException
     */
    @GetMapping("delApi")
    public String delApi() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("172.16.18.18", 9200, "http")));
        DeleteRequest request = new DeleteRequest(
                "posts",
                "doc",
                "1");
        DeleteResponse deleteResponse = client.delete(
                request, RequestOptions.DEFAULT);
        client.close();
        String index = deleteResponse.getIndex();
        String type = deleteResponse.getType();
        String id = deleteResponse.getId();
        long version = deleteResponse.getVersion();
        ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {

        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure :
                    shardInfo.getFailures()) {
                String reason = failure.reason();
            }
        }
        return deleteResponse.toString();
    }

    /**
     * 修改
     * @return
     * @throws IOException
     */
    @GetMapping("updateApi")
    public String updateApi() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("172.16.18.18", 9200, "http")));

        UpdateRequest request = new UpdateRequest("posts", "doc", "1");
        String jsonString = "{" +
                "\"user\":\"xxxs\"," +
                "\"postDate\":\"2017-01-01\"," +
                "\"message\":\"daily update\"" +
                "}";
        request.doc(jsonString, XContentType.JSON);
        UpdateResponse updateResponse = client.update(
                request, RequestOptions.DEFAULT);
        client.close();
        return updateResponse.toString();
    }
    /**
     * 获取单条数据
     * @return
     * @throws IOException
     */
    @GetMapping("/getApi")
    public String getApi() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
//                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("172.16.18.18", 9200, "http")));
        try {
            GetRequest request = new GetRequest("posts", "doc", "1").version(2);
            GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);

            String index = getResponse.getIndex();
            String type = getResponse.getType();
            String id = getResponse.getId();
            if (getResponse.isExists()) {
                long version = getResponse.getVersion();
                String sourceAsString = getResponse.getSourceAsString();
                Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
                byte[] sourceAsBytes = getResponse.getSourceAsBytes();
                return sourceAsString;
            } else {
                return "数据不存在";
            }
        } catch (ElasticsearchException exception) {
            if (exception.status() == RestStatus.CONFLICT) {
                return null;
            }
        }

        client.close();
        return null;
    }


    /**
     * SearchSourceBuilder
     * @return
     * @throws IOException
     */
    @GetMapping("/search")
    public Object search() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("172.16.18.18", 9200, "http")));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("posts");// 设置 索引
        searchRequest.types("doc");
        // 方式一：
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("message", "Elastic")
                .fuzziness(Fuzziness.AUTO)
                .prefixLength(0)
                .maxExpansions(20);
        searchSourceBuilder.query(matchQueryBuilder); // 设置关键字
        searchSourceBuilder.from(0); // 偏移量
        searchSourceBuilder.size(10); // 每页显示数量
        searchRequest.source(searchSourceBuilder);
        // 方式二：
        /*
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);*/

        SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
        // 获取返回结果
        List<Map<String,Object>> result = dealResult(searchResponse.getHits());
        client.close();
        return result;
    }


    /**
     * SearchTemplateResponse 实现搜索
     * @return
     * @throws IOException
     */
    @GetMapping("queryTest")
    public Object queryTest() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("172.16.18.18", 9200, "http")));

        SearchTemplateRequest request = new SearchTemplateRequest();
        SearchRequest searchRequest = new SearchRequest("posts");
        searchRequest.types("doc");
        request.setRequest(searchRequest);

        request.setScriptType(ScriptType.INLINE);
        request.setScript(
                "{" +
                        "  \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } }," +
                        "  \"size\" : \"{{size}}\"" +
                        "}");

        Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("field", "message");
        scriptParams.put("value", "out");
        scriptParams.put("size", 5);
        request.setScriptParams(scriptParams);
        SearchTemplateResponse renderResponse = client.searchTemplate(request,RequestOptions.DEFAULT);
        client.close();
        return renderResponse.getResponse();
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
