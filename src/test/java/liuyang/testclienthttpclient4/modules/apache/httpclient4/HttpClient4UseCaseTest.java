package liuyang.testclienthttpclient4.modules.apache.httpclient4;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import liuyang.testclienthttpclient4.common.utils.IdUtils;
import liuyang.testclienthttpclient4.common.utils.IsWsdlReachable;
import liuyang.testclienthttpclient4.modules.apache.httpclient4.dto.PDT370MHzRequestDTO;
import liuyang.testclienthttpclient4.modules.apache.httpclient4.dto.PDT370MHzResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyang(wx)
 * @since 2022/4/20
 */
@Slf4j
public class HttpClient4UseCaseTest {

    @Test
    void test() {
        String entityStr = "{\"status\":true,\"message\":\"验证成功\",\"data\":{\"user\":{\"userId\":\"8a0280527912dcb101791775cd99032c\",\"email\":\"11\",\"loginName\":\"王兵\",\"organizationId\":\"1530000000030\",\"phone\":\"17313160980\",\"qyId\":null,\"zjId\":null,\"sex\":\"0\",\"trueName\":\"王兵\",\"userIdcard\":\"2\",\"userStart\":\"1\",\"userType\":\"xt_user\",\"sfqzgmm\":\"1\",\"userLock\":\"1\",\"qydj\":null},\"organization\":{\"organizationId\":\"1530000000030\",\"organizationLevel\":\"3\",\"organizationName\":\"云南省应急管理厅/应急指挥中心\",\"organizationParentId\":\"1530000000000\",\"organizationParentName\":null,\"organizationType\":\"1\",\"organizationTypeName\":\"安监局\",\"organizationXzqhDm\":\"53\",\"organizationXzqhDmName\":\"云南省\"},\"role\":[{\"roleId\":\"8a02805e8103c0d6018117c485ec0db8\",\"roleName\":\"370MHz集群系统管理员\"}],\"permission\":[{\"permissionId\":\"8a02805e8103c0d6018117c3db780db6\",\"permissionName\":\"370MHz集群系统管理员\",\"urls\":null}],\"menu\":[],\"mapMenu\":null,\"token\":null,\"login\":true,\"validate\":true}}";
        JSONObject jsonObject = JSON.parseObject(entityStr);
        log.info("data = {}", jsonObject.get("data"));
        //log.info("data = {}", jsonObject.getJSONObject("data").getJSONObject("permission"));
        PDT370MHzResponseDTO response = JSON.parseObject(entityStr, PDT370MHzResponseDTO.class);
        log.info("permissionId = {}", response.getData().getPermission()[0].getPermissionId());
        log.info("permissionName = {}", response.getData().getPermission()[0].getPermissionName());
    }


    // 370系统对接统一认证平台需求
    @Test
    void test202205311010PDT370MHzClusterPrototype() {
        /*
        PDT370MHzRequestDTO req = new PDT370MHzRequestDTO();
        req.setAppId("cba53e16f2144fada6d4498df5d48e52");
        req.setAppCode("c041ac1987df4fab8133395baa122d50");
        req.setToken("Y2M4YWY2NmUtNDgyNi00YzkwLWFiY2YtOThlNDFkZWI3Mjk2");
         */

        String url = "https://220.163.82.253:33312/jzpt/subs/login";
        HttpPost httpPost = new HttpPost(url);
        //httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=GBK");// 不设置也可, 但设置了就以这个为准。
        // /////////////////////////////////////////////////////////
        // 给Post对象设置参数 begin
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("appId", "cba53e16f2144fada6d4498df5d48e52"));
        nameValuePairs.add(new BasicNameValuePair("appCode", "c041ac1987df4fab8133395baa122d50"));
        nameValuePairs.add(new BasicNameValuePair("token", "Y2M4YWY2NmUtNDgyNi00YzkwLWFiY2YtOThlNDFkZWI3Mjk2"));
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8);
        httpPost.setEntity(urlEncodedFormEntity);
        // 给Post对象设置参数 end
        // /////////////////////////////////////////////////////////
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
        ){
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);

            // 解析返回的对象
            // 当返回报文status=true且permission数组中包含（注意是包含）permissionId = 8a02805e8103c0d6018117c3db780db6 就合法了。
            JSONObject jsonObject = JSON.parseObject(entityStr);
            log.info("data = {}", jsonObject.get("data"));
            //log.info("data = {}", jsonObject.getJSONObject("data").getJSONObject("permission"));
            PDT370MHzResponseDTO response = JSON.parseObject(entityStr, PDT370MHzResponseDTO.class);
            log.info("status = {}", response.getStatus());
            // 只要数组中有permissionId = 8a02805e8103c0d6018117c3db780db6 就合法了。 不一定是第0个元素。
            log.info("permissionId = {}", response.getData().getPermission()[0].getPermissionId());
            log.info("permissionName = {}", response.getData().getPermission()[0].getPermissionName());

            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    @Test
    void testIsWsdlReachable () {
        // 1. 正常
        //String url = "http://20.48.250.70:8080/pdt-nms/ProvinceTopService?wsdl";// 正常
        // 2. IP可达，端口无监听
        //String url = "http://20.48.250.70:8082/pdt-nms/ProvinceTopService?wsdl";
        // 3. IP不可达
        //String url = "http://1.2.3.4:8082/pdt-nms/ProvinceTopService?wsdl";
        // 4. IP可达，端口有监听，但服务不匹配
        //String url = "http://20.48.250.70:/pdt-nms/ProvinceTopService?wsdl";// SoapWs/CityAlmServiceWsdl.wsdl

        String url = "http://20.48.250.70:8080/pdt-nms/ProvinceTaskService?wsdl";
        boolean test = IsWsdlReachable.test(url);
        log.info("test = {}", test);
    }

    @Test
    void testAvailable() {
        long begin = System.currentTimeMillis();

        String url = "http://20.48.250.70:8080/pdt-nms/ProvinceTopService?wsdl";
        //String url = "http://20.48.250.70:8082/pdt-nms/ProvinceTopService?wsdl";
        //String url = "http://1.2.3.4:8082/pdt-nms/ProvinceTopService?wsdl";
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(RequestConfig.custom().setConnectTimeout(3000).setSocketTimeout(60000).build());
        try (
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        ) {
            HttpEntity entity = closeableHttpResponse.getEntity();
            String entityStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            log.info("entityStr = {}", entityStr);
            EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            long end = System.currentTimeMillis();
            log.info("耗时: {} ms", end - begin);
        }
    }
}
