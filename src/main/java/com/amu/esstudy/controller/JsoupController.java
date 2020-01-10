package com.amu.esstudy.controller;

import com.alibaba.fastjson.JSON;
import com.amu.esstudy.entity.ColorModeBean;
import com.amu.esstudy.entity.HuaWeiPhoneBean;
import com.amu.esstudy.entity.Phone;
import com.amu.esstudy.mapper.BooksMapper;
import com.amu.esstudy.mapper.PhoneMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/jsoup")
public class JsoupController {

    @Autowired
    private PhoneMapper phoneMapper;

    @GetMapping("/huawei")
    public String huawei() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("https://consumer.huawei.com/cn/phones/?ic_medium=hwdc&ic_source=corp_header_consumer"); // 创建httpget实例
        CloseableHttpResponse response = httpclient.execute(httpget); // 执行get请求
        HttpEntity entity = response.getEntity(); // 获取返回实体
        String content = EntityUtils.toString(entity, "utf-8");
        response.close(); // 关闭流和释放系统资源

        Document document = Jsoup.parse(content);

        Elements elements = document.select("#content-v3-plp #pagehidedata .plphidedata");
        for (Element element : elements) {
            String jsonStr = element.text();
            List<HuaWeiPhoneBean> huaWeiPhoneBeanlist = JSON.parseArray(jsonStr, HuaWeiPhoneBean.class);
            for (HuaWeiPhoneBean bean : huaWeiPhoneBeanlist){
                String productName = bean.getProductName();
                List<ColorModeBean> colorModeBeanList = bean.getColorsItemMode();

                String colors = "";

                for (ColorModeBean colorModeBean : colorModeBeanList){
                    String colorName = colorModeBean.getColorName();
                    colors += colorName + ";";
                }

                List<String> sellingPointList = bean.getSellingPoints();
                String sellingPoints = "";
                for (String sellingPoint : sellingPointList) {
                    sellingPoints += sellingPoint+";";
                }
                System.out.println("产品名：" + productName);
                System.out.println("颜  色：" + colors);
                System.out.println("买  点：" + sellingPoints);
                Phone phone = new Phone();
                phone.setName(productName);
                phone.setColors(colors);
                phone.setSellingPoints(sellingPoints);
                phone.setCreateTime(new Date());
                phone.setMarketTime(new Date());
                phoneMapper.insert(phone);
            }
        }
        return content;
    }

    @GetMapping("/meizu")
    public void meizu() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault(); // 创建httpclient实例
        HttpGet httpget = new HttpGet("https://lists.meizu.com/page/list?categoryid=76"); // 创建httpget实例

        CloseableHttpResponse response = httpclient.execute(httpget); // 执行get请求
        HttpEntity entity=response.getEntity(); // 获取返回实体fsdf
        //System.out.println("网页内容："+ EntityUtils.toString(entity, "utf-8")); // 指定编码打印网页内容

        String content = EntityUtils.toString(entity, "utf-8");
        response.close(); // 关闭流和释放系统资

        Document document = Jsoup.parse(content);
        Elements names = document.select("#goodsListWrap .gl-item .gl-item-link .item-title");

        Elements cellingPoints = document.select("#goodsListWrap .gl-item .gl-item-link .item-desc");

        Elements colorsElements = document.select(".container .goods-list #goodsListWrap .gl-item .gl-item-link .item-slide");
        int i = 0;

        for (Element nameElement : names) {
            Phone phone = new Phone();
            phone.setName(nameElement.text());
            Elements elements = colorsElements.get(i).select(".item-slide-dot");
            String endcolors = "";
            for (Element color : elements){
                endcolors += color.attr("title") + ";";
            }
//            System.out.println(endcolors);
            phone.setSellingPoints(cellingPoints.get(i).text());
            phone.setColors(endcolors);
            phone.setCreateTime(new Date());
            phone.setMarketTime(new Date());
            phoneMapper.insert(phone);
        }
        return;
    }
}
