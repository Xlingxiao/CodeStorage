//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.PrivateKey;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import cfca.sadk.algorithm.common.Mechanism;
//import cfca.sadk.lib.crypto.JCrypto;
//import cfca.sadk.lib.crypto.Session;
//import cfca.sadk.util.CertUtil;
//import cfca.sadk.util.KeyUtil;
//import cfca.sadk.util.Signature;
//import cfca.sadk.x509.certificate.X509Cert;
//import connector.HttpConnector;
//
//
///**
// *  
// *
// * @author LX    
// * @description     
// * @date 2020/4/7 12:34  
// */
//public class CFCA {
//    public static void main(String[] args) {
//
//        // 一级保全存证/保全接口服务地址
//        String url = "http://192.168.113.110:38080/csserver/evidence.preserve";
//        // 二级保全存证/保全接口服务地址
////        String url = "http://192.168.113.110:8181/ess-customer/evidence.preserve";
//        // 连接超时时间
//        int connectTimeout = 3000;
//        // 读超时时间
//        int readTimeout = 15000;
//
//        String certPath = "./server.jks";
//        String password = "11111111";
//        String certAlias = "local ra@ess测试@112312312@1 (cfca test oca1)";
//
////        String url = "https://cstest.baoquanonline.com/csserver/evidence.preserve";
////      String certPath = "./cstest.jks";
////      String password = "cfca1234";                                          //（测试）
////      String certAlias = "ess application admin (cfca acs test oca31)";
//
////        String certPath = "./client.jks";
////        String password = "11111111";
////        String certAlias = "sysid@hehe@z9qeywacf@1 (cfca test oca11)";
//        try {
//            // https连接,创建httpConnector后执行initSSL方法初始化SSL环境，之后多次调用可使用同一httpConnector对象
////             HttpConnector httpConnector = new HttpConnector(url, connectTimeout, readTimeout);
////             httpConnector.initSSL(certPath, password.toCharArray(), certPath, password.toCharArray());
//
//            // http连接
//            HttpConnector httpConnector = new HttpConnector(url, connectTimeout, readTimeout);
//
//            // 模板中标签的值只能为String类型！ 模板中标签的值只能为String类型！ 模板中标签的值只能为String类型！
//            Map<String, String> sourceMap = new HashMap<String, String>();
//            sourceMap.put("txCode", "1101");
//
//            // 二级保全
////            sourceMap.put("branchCode", "0000");
////            sourceMap.put("applicationCode", "0001");
////            sourceMap.put("templateCode", "000A");
//
//            // 一级保全
//            sourceMap.put("branchCode", "0002");
//            sourceMap.put("applicationCode", "0005");
//            sourceMap.put("templateCode", "0014");
//
//            //对外测试
////          sourceMap.put("branchCode", "0001");
////          sourceMap.put("applicationCode", "0001");
////          sourceMap.put("templateCode", "007A");
//
//            sourceMap.put("test", "测试测试测试测试测试");
//            sourceMap.put("userName", "测试测试测试测试测试");
////             sourceMap.put("operationTime", "2019-03-18 14:53:29");
//
////             sourceMap.put("jftj_customer01", "顾华");
////             sourceMap.put("jftj_customer02", "中国建设银行股份有限公司上海江湾支行");
////             sourceMap.put("jftj_signtime", "2019年3月14日 14:10:21");
////             sourceMap.put("jftj_scene", "顾华名下建设银行储蓄卡被盗刷");
////             sourceMap.put("jftj_preservationtime", "2019年3月14日14:11:13");
////             sourceMap.put("jftj_wechat01", "13166213469");
////             sourceMap.put("jftj_initiationtime", "2019年3月5日 11:45:21");
////             sourceMap.put("jftj_customerinfo02", "中国建设银行股份有限公司上海江湾支行，负责人：竺璇");
////             sourceMap.put("jftj_wechat02", "13918516640");
////             sourceMap.put("jftj_mediator", "石婉琴");
////             sourceMap.put("jftj_mediatorid", "shiwanqin");
////             sourceMap.put("jftj_room", "604747487");
////             sourceMap.put("jftj_notice", "邮件");
////             sourceMap.put("jftj_frequencyhash", "FDEBBA6BCB8137F16D564EC025B1AC8A455CFC25");
////             sourceMap.put("jftj_platform", "银行业一站式纠纷调解平台");
////             sourceMap.put("jftj_protocolhash01", "14236F322A3460C4343B9CD96E0056A7414C3B23");
////             sourceMap.put("jftj_protocolhash02", "73660FDE242F003825BE8D300CFD9F665C0FE351");
//
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            // 多文件上传
////            List<File> fileList = new ArrayList<>();
////            fileList.add(new File("E:\\CuteFTP8.rar"));
////            fileList.add(new File("E:\\sq造数据l.txt"));
//
//            Signature signKit = new Signature();
//            byte[] signedData = null;
//            JCrypto.getInstance().initialize(JCrypto.JSOFT_LIB, null);
//            Session session = JCrypto.getInstance().openSession(JCrypto.JSOFT_LIB);
//
////            StringBuilder fileBase64Hash = new StringBuilder();
////            for (File file : fileList) {
////                byte[] hashData = null;
////                hashData = HashUtil.RSAHashData(readFile(file), new Mechanism(Mechanism.SHA256), session, false);
////                fileBase64Hash.append(new String(Base64.encode(hashData), "UTF-8"));
////            }
////            sourceMap.put("fileBase64Hash", fileBase64Hash.toString());
//
//            String request = objectMapper.writeValueAsString(sourceMap);
//            System.out.println(request);
//
//            PrivateKey privateKey = KeyUtil.getPrivateKeyFromJKS(certPath, password, certAlias);
//            X509Cert x509Cert = CertUtil.getCertFromJKS(certPath, password, certAlias);
//            signedData = signKit.p7SignMessageDetach(Mechanism.SHA256_RSA, request.getBytes("UTF-8"), privateKey, x509Cert, session);
//
//            // List<String> queryCodeList = new ArrayList<>();
//            // queryCodeList.add("testCode你好");
//            // queryCodeList.add("testCode123");
//
//            // String queryCode = objectMapper.writeValueAsString(queryCodeList);
//
//            // String response = httpConnector.post(request, new String(signedData, "UTF-8"), null, null);
//
//            for (int i = 0; i < 1; i++) {
//                String response = httpConnector.deal(request, new String(signedData, "UTF-8"), null, null);
//                System.out.println(response);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static byte[] readFile(File file) {
//        try {
//            return readFile(new FileInputStream(file));
//        } catch (FileNotFoundException e) {
//            return null;
//        }
//    }
//
//    public static byte[] readFile(InputStream fis) {
//        ByteArrayOutputStream baos = null;
//        try {
//            baos = new ByteArrayOutputStream();
//            byte[] buff = new byte[1024];
//            int r = 0;
//            while ((r = fis.read(buff)) != -1) {
//                baos.write(buff, 0, r);
//            }
//            return baos.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                }
//            }
//            if (baos != null) {
//                try {
//                    baos.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//    }
//}
