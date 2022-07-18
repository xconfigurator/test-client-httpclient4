package liuyang.testclienthttpclient4.modules.apache.httpclient4;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import liuyang.testclienthttpclient4.common.utils.IdUtils;
import liuyang.testclienthttpclient4.common.utils.IsWsdlReachable;
import liuyang.testclienthttpclient4.modules.apache.httpclient4.dto.PDT370MHzRequestDTO;
import liuyang.testclienthttpclient4.modules.apache.httpclient4.dto.PDT370MHzResponseDTO;
import lombok.Data;
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
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import sun.plugin.dom.core.CDATASection;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        String AIM_PERMISSION_ID = "8a02805e8103c0d6018117c3db780db6";
        String url = "https://220.163.82.253:33312/jzpt/subs/login";
        String appId = "cba53e16f2144fada6d4498df5d48e52";
        String appCode = "c041ac1987df4fab8133395baa122d50";
        String token = "NGRjZDNiOTYtMjBkMC00YmM0LTg4MGItZTE5M2JjZDMyMmM4";
        // new
        // YzIxYmNkYTItNDkwMC00MGNkLWExNDgtODczNDg0YmM1MzYy
        // http://192.168.238.133/login-for-yunnan?token=MzI4YmY4ZDctZTQ0Ny00NDE4LTlhZjEtNDM0YWE2MTAxY2Q5

        HttpPost httpPost = new HttpPost(url);
        //httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=GBK");// 不设置也可, 但设置了就以这个为准。
        // /////////////////////////////////////////////////////////
        // 给Post对象设置参数 begin
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("appId", appId));
        nameValuePairs.add(new BasicNameValuePair("appCode", appCode));
        //nameValuePairs.add(new BasicNameValuePair("token", "Y2M4YWY2NmUtNDgyNi00YzkwLWFiY2YtOThlNDFkZWI3Mjk2"));
        //nameValuePairs.add(new BasicNameValuePair("token", "ZjcwYTUwMzMtMTk4YS00MGUzLTk2ZDgtNThjMjI1NDNmOTRh"));// 20220627 新token ZjcwYTUwMzMtMTk4YS00MGUzLTk2ZDgtNThjMjI1NDNmOTRh
        nameValuePairs.add(new BasicNameValuePair("token", token));// 20220628 新token YzI1MGUwNTItMDAzYS00MDA2LWFjMWMtYzU1ZjIzMDc2YWYx
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
            EntityUtils.consume(entity);// 确保流关闭。
            log.info("验证结果报文返回正常 entityStr = {}", entityStr);
            // 验证成功：
            // entityStr = {"status":true,"message":"验证成功","data":{"user":{"userId":"8a0280527912dcb101791775cd99032c","email":"11","loginName":"王兵","organizationId":"1530000000001","phone":"11","qyId":null,"zjId":null,"sex":"0","trueName":"王兵","userIdcard":"2","userStart":"1","userType":"xt_user","sfqzgmm":"1","userLock":"1","qydj":null},"organization":{"organizationId":"1530000000001","organizationLevel":"1","organizationName":"云南省应急管理厅/厅领导","organizationParentId":"1530000000000","organizationParentName":null,"organizationType":"1","organizationTypeName":"安监局","organizationXzqhDm":"53","organizationXzqhDmName":"云南省"},"role":[{"roleId":"8a02805e8103c0d6018117c485ec0db8","roleName":"370MHz集群系统管理员"}],"permission":[{"permissionId":"8a02805e8103c0d6018117c3db780db6","permissionName":"370MHz集群系统管理员","urls":null}],"menu":[],"mapMenu":null,"token":null,"login":true,"validate":true}}
            // 验证失败：
            // entityStr = {"status":false,"message":"token验证错误","data":""}

            // ////////////////////////////////////////////////////////////////
            // 测试数据集 begin
            // 测试用 成功的 ok 202206281533 测试分支通过
            //entityStr = "{\"status\":true,\"message\":\"验证成功\",\"data\":{\"user\":{\"userId\":\"8a0280527912dcb101791775cd99032c\",\"email\":\"11\",\"loginName\":\"王兵\",\"organizationId\":\"1530000000001\",\"phone\":\"11\",\"qyId\":null,\"zjId\":null,\"sex\":\"0\",\"trueName\":\"王兵\",\"userIdcard\":\"2\",\"userStart\":\"1\",\"userType\":\"xt_user\",\"sfqzgmm\":\"1\",\"userLock\":\"1\",\"qydj\":null},\"organization\":{\"organizationId\":\"1530000000001\",\"organizationLevel\":\"1\",\"organizationName\":\"云南省应急管理厅/厅领导\",\"organizationParentId\":\"1530000000000\",\"organizationParentName\":null,\"organizationType\":\"1\",\"organizationTypeName\":\"安监局\",\"organizationXzqhDm\":\"53\",\"organizationXzqhDmName\":\"云南省\"},\"role\":[{\"roleId\":\"8a02805e8103c0d6018117c485ec0db8\",\"roleName\":\"370MHz集群系统管理员\"}],\"permission\":[{\"permissionId\":\"8a02805e8103c0d6018117c3db780db6\",\"permissionName\":\"370MHz集群系统管理员\",\"urls\":null}],\"menu\":[],\"mapMenu\":null,\"token\":null,\"login\":true,\"validate\":true}}";

            // 测试异常情况0 报文返回status就是false ok 202206281556 测试分支通过
            //entityStr = "{\"status\":false,\"message\":\"token验证错误\",\"data\":\"\"}";

            // 测试异常情况1 返回没有data。 ok 202206281546 测试分支通过
            //entityStr = "{\"status\":true,\"message\":\"验证成功\"}";

            // 测试异常情况2 返回有data，但没有permission ok 202206281541 测试分支通过
            // 注：逻辑上包含了有data但data是“”的情况
            //entityStr = "{\"status\":true,\"message\":\"验证成功\",\"data\":{\"user\":{\"userId\":\"8a0280527912dcb101791775cd99032c\",\"email\":\"11\",\"loginName\":\"王兵\",\"organizationId\":\"1530000000001\",\"phone\":\"11\",\"qyId\":null,\"zjId\":null,\"sex\":\"0\",\"trueName\":\"王兵\",\"userIdcard\":\"2\",\"userStart\":\"1\",\"userType\":\"xt_user\",\"sfqzgmm\":\"1\",\"userLock\":\"1\",\"qydj\":null},\"organization\":{\"organizationId\":\"1530000000001\",\"organizationLevel\":\"1\",\"organizationName\":\"云南省应急管理厅/厅领导\",\"organizationParentId\":\"1530000000000\",\"organizationParentName\":null,\"organizationType\":\"1\",\"organizationTypeName\":\"安监局\",\"organizationXzqhDm\":\"53\",\"organizationXzqhDmName\":\"云南省\"},\"role\":[{\"roleId\":\"8a02805e8103c0d6018117c485ec0db8\",\"roleName\":\"370MHz集群系统管理员\"}],\"menu\":[],\"mapMenu\":null,\"token\":null,\"login\":true,\"validate\":true}}";

            // 测试异常情况3 返回有data，有permission，但permission数组为空 ok 202206281539 测试分支通过
            //entityStr = "{\"status\":true,\"message\":\"验证成功\",\"data\":{\"user\":{\"userId\":\"8a0280527912dcb101791775cd99032c\",\"email\":\"11\",\"loginName\":\"王兵\",\"organizationId\":\"1530000000001\",\"phone\":\"11\",\"qyId\":null,\"zjId\":null,\"sex\":\"0\",\"trueName\":\"王兵\",\"userIdcard\":\"2\",\"userStart\":\"1\",\"userType\":\"xt_user\",\"sfqzgmm\":\"1\",\"userLock\":\"1\",\"qydj\":null},\"organization\":{\"organizationId\":\"1530000000001\",\"organizationLevel\":\"1\",\"organizationName\":\"云南省应急管理厅/厅领导\",\"organizationParentId\":\"1530000000000\",\"organizationParentName\":null,\"organizationType\":\"1\",\"organizationTypeName\":\"安监局\",\"organizationXzqhDm\":\"53\",\"organizationXzqhDmName\":\"云南省\"},\"role\":[{\"roleId\":\"8a02805e8103c0d6018117c485ec0db8\",\"roleName\":\"370MHz集群系统管理员\"}],\"permission\":[],\"menu\":[],\"mapMenu\":null,\"token\":null,\"login\":true,\"validate\":true}}";

            // 测试用4 返回有data，有permission，但permission数组不为空，但不包含指定的 ok 202206281534 测试分支通过
            //entityStr = "{\"status\":true,\"message\":\"验证成功\",\"data\":{\"user\":{\"userId\":\"8a0280527912dcb101791775cd99032c\",\"email\":\"11\",\"loginName\":\"王兵\",\"organizationId\":\"1530000000001\",\"phone\":\"11\",\"qyId\":null,\"zjId\":null,\"sex\":\"0\",\"trueName\":\"王兵\",\"userIdcard\":\"2\",\"userStart\":\"1\",\"userType\":\"xt_user\",\"sfqzgmm\":\"1\",\"userLock\":\"1\",\"qydj\":null},\"organization\":{\"organizationId\":\"1530000000001\",\"organizationLevel\":\"1\",\"organizationName\":\"云南省应急管理厅/厅领导\",\"organizationParentId\":\"1530000000000\",\"organizationParentName\":null,\"organizationType\":\"1\",\"organizationTypeName\":\"安监局\",\"organizationXzqhDm\":\"53\",\"organizationXzqhDmName\":\"云南省\"},\"role\":[{\"roleId\":\"8a02805e8103c0d6018117c485ec0db8\",\"roleName\":\"370MHz集群系统管理员\"}],\"permission\":[{\"permissionId\":\"8a02805e8103c0d6018117c3db780db6XXXXXXX\",\"permissionName\":\"370MHz集群系统管理员\",\"urls\":null}],\"menu\":[],\"mapMenu\":null,\"token\":null,\"login\":true,\"validate\":true}}";
            // 测试数据集 end
            // ////////////////////////////////////////////////////////////////

            // 解析返回的对象
            // 验证成功报文结构：
            // entityStr = {"status":true,"message":"验证成功","data":{"user":{"userId":"8a0280527912dcb101791775cd99032c","email":"11","loginName":"王兵","organizationId":"1530000000001","phone":"11","qyId":null,"zjId":null,"sex":"0","trueName":"王兵","userIdcard":"2","userStart":"1","userType":"xt_user","sfqzgmm":"1","userLock":"1","qydj":null},"organization":{"organizationId":"1530000000001","organizationLevel":"1","organizationName":"云南省应急管理厅/厅领导","organizationParentId":"1530000000000","organizationParentName":null,"organizationType":"1","organizationTypeName":"安监局","organizationXzqhDm":"53","organizationXzqhDmName":"云南省"},"role":[{"roleId":"8a02805e8103c0d6018117c485ec0db8","roleName":"370MHz集群系统管理员"}],"permission":[{"permissionId":"8a02805e8103c0d6018117c3db780db6","permissionName":"370MHz集群系统管理员","urls":null}],"menu":[],"mapMenu":null,"token":null,"login":true,"validate":true}}
            // 验证失败报文结构：
            // entityStr = {"status":false,"message":"token验证错误","data":""}
            // 当返回报文status=true且permission数组中包含（注意是包含）permissionId = 8a02805e8103c0d6018117c3db780db6 就合法了。
            JSONObject jsonObject = JSON.parseObject(entityStr);
            boolean status = (boolean)jsonObject.get("status");
            log.info("status = {}", status);
            String message = (String) jsonObject.get("message");
            log.info("message = {}", message);

            if (status == true) {
                // 验data
                Object data = jsonObject.get("data");
                log.debug("data = {}", data);
                if (null == data) {
                    log.error("验证结果报文错误 没有data");// 202206281533 测试分支通过
                    // TODO 编写错误返回信息
                    // return Result.error();
                    return;
                }

                // 验permission
                Object permission = jsonObject.getJSONObject("data").get("permission");
                log.debug("permission = {}", permission);
                if (null == permission) {
                    log.error("验证结果报文错误 没有permission");// 202206281541 测试分支通过
                    // TODO 编写错误返回信息
                    // return Result.error();
                    return;
                }
                JSONArray permissionJsonArray = jsonObject.getJSONObject("data").getJSONArray("permission");
                List<Object> permissionList = Arrays.asList(permissionJsonArray.stream().toArray());
                if (null == permissionList || permissionList.size() == 0) {
                    log.error("验证结果报文错误 permission下没有包含任何信息");// 202206281539 测试分支通过
                    // TODO 编写错误返回信息
                    // return Result.error();
                    return;
                }

                // 验permissionId
                Optional<Object> permissionId = permissionList.stream()
                        .filter(obj ->
                                AIM_PERMISSION_ID.equals(
                                        ((JSONObject) obj).get("permissionId")
                                )
                        )
                        .findFirst();
                if (permissionId.isPresent()) {
                    log.info("token验证成功 permissionId验证成功");// 202206281533 测试分支通过
                    // TODO 编写成功信息
                    // return Result.ok();
                    return;
                } else {
                    log.error("验证结果报文错误 授权报文中未包含指定的permissionId");// 202206281534 测试分支通过
                    // TODO 编写错误返回信息
                    // return Result.error();
                    return;
                }


                // @Deprecated
                /*
                //log.info("data = {}", jsonObject.getJSONObject("data").getJSONObject("permission"));
                PDT370MHzResponseDTO response = JSON.parseObject(entityStr, PDT370MHzResponseDTO.class);
                log.info("status = {}", response.getStatus());
                // 只要数组中有permissionId = 8a02805e8103c0d6018117c3db780db6 就合法了。 不一定是第0个元素。
                if (response.getData().getPermission() != null) {
                    log.info("permissionId = {}", response.getData().getPermission()[0].getPermissionId());
                    log.info("permissionName = {}", response.getData().getPermission()[0].getPermissionName());
                }
                 */

                // TODO 编写返回正确信息
                // return Result.ok();
            } else {
                // 验证报文直接返回失败 一般是"token验证错误", 但不排除有其他message
                log.info("验证失败 {}", message);// 202206281556 测试分支通过
                // TODO 编写返回错误信息
                // return Result.error();
            }

            //EntityUtils.consume(entity);// 确保流关闭。
        } catch(Exception e) {
            log.error("验证异常, 通信异常或者是返回报文结构发生改变。");// 202206281401 测试分支通过
            log.error(e.getMessage(), e);
            // TODO 编写返回错误信息
            // return Result.error();
        }
    }

    @Data
    class PermissionDTO {
        String permissionId;
        String permissionName;
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
