package BigData.Pulsar.admin;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.common.policies.data.AuthAction;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.json.JSONArray;
import utils.PropertiesUtil;

import java.io.IOException;
import java.util.*;

/**
 * @Author: LX
 * @Date: 2019/4/9 13:40
 * @Version: 1.0
 */
@SuppressWarnings({"Duplicates", "WeakerAccess", "unused"})
public class LXAdmin {
    private PulsarAdmin admin;
    private Properties properties;


    public static void main(String[] args) throws IOException, PulsarAdminException {
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        Properties prop = propertiesUtil.getProperties("pulsar/pulsar.properties");
        LXAdmin lxAdmin = new LXAdmin(prop);
        System.out.println(lxAdmin.admin.tenants().getTenantInfo("zx"));
//        lxAdmin.printSome();
//        System.out.println(lxAdmin.getAllTopicNode());
//        System.out.println(lxAdmin.getTopicsByNamespace("lx/java"));
        /*展示topic树*/
        /*System.out.println(lxAdmin.getTopicTree());
        lxAdmin.admin.topics().getPartitionedTopicList(namespace);*/
        /*展示topic下总共有多少消息*/
        /*String topic = "persistent://lx/java/testN";
        int pa = lxAdmin.getPartitionsCount(topic);
        int co = lxAdmin.countTopicMessage(topic, pa);
        System.out.println(co);
        lxAdmin.updatePartition(topic, 10);*/
        /*获取namespace下所有的topic，目前不可用*/
//        System.out.println(lxAdmin.getAllTopicList(namespace));
        /*创建topic后删除，测试创建删除操作*/
        /*System.out.println(lxAdmin.getTopicList(namespace));
        lxAdmin.createTopic(topic, 3);
        System.out.println(lxAdmin.getTopicList(namespace));
        lxAdmin.deleteTopic(topic);
        System.out.println(lxAdmin.getTopicList(namespace));*/

        /*获取namespace的权限*/
        lxAdmin.closeAdmin();
    }

    public LXAdmin(Properties prop) throws IOException {
        this.properties = prop;
        initAdmin();
    }

    /*初始化Admin*/
    private void initAdmin() throws IOException {
        //tls全权限使用
        /*Map<String, String> map = new HashMap<>();
        map.put("tlsCertFile", properties.getProperty("tlsCertFile"));
        map.put("tlsKeyFile", properties.getProperty("tlsKeyFile"));*/
        admin = PulsarAdmin.builder()
                .serviceHttpUrl(properties.getProperty("tlsRestUrl"))
                .tlsTrustCertsFilePath(properties.getProperty("trustTlsCertFile"))
//                .authentication("org.apache.pulsar.client.impl.auth.AuthenticationTls",map)
                .authentication(AuthenticationFactory.token(properties.getProperty("adminToken")))
                .allowTlsInsecureConnection(false).build();
        /*ClientConfigurationData config = new ClientConfigurationData();
        config.setTlsAllowInsecureConnection(false);
        config.setTlsHostnameVerificationEnable(Boolean.parseBoolean(properties.getProperty("useTls")));
        config.setTlsTrustCertsFilePath(properties.getProperty("tlsCertFile"));
        admin = new PulsarAdmin(properties.getProperty("tlsRestUrl"), config);*/
    }

    /*打印一些信息测试集群权限等问题*/
    private void printSome() throws PulsarAdminException {
        /*打印集群相关*/
        System.out.println("===========集群============");
        List<String> list = admin.clusters().getClusters();
        System.out.println(String.format("集群列表：%s", list));
        for (String clusterName : list) {
            System.out.println(String.format("集群 [%s] 信息：%s", clusterName, admin.clusters().getCluster(clusterName)));
        }

        /*打印tenant相关*/
        System.out.println("==============租户============");
        list = admin.tenants().getTenants();
        System.out.println(String.format("租户列表：%s", list));
        for (String tenantName : list) {
            System.out.println(String.format("租户 [%s] 信息：%s", tenantName, getTenantInfo(tenantName)));
        }
        //创建tenant
        /*tenant创建时需要*/
        String tenantName = "zx";
        System.out.println(String.format("创建租户：%s", tenantName));
        Set<String> adminRoles = new HashSet<>();
        Set<String> allowClusters = new HashSet<>();
        adminRoles.add("admin");
        adminRoles.add("public");
        adminRoles.add(tenantName);
        allowClusters.add("pulsar-cluster-1");
        createTenant(tenantName, adminRoles, allowClusters);
        list = admin.tenants().getTenants();
        System.out.println(String.format("租户列表：%s", list));
        System.out.println(String.format("新建的租户信息：%s", getTenantInfo(tenantName)));
        //修改租户信息
        System.out.println(String.format("修改租户信息：%s", tenantName));
        adminRoles.add("lx");
        updateTenant(tenantName, adminRoles, allowClusters);
        System.out.println(String.format("[%s] 租户信息：%s", tenantName, getTenantInfo(tenantName)));

        /*打印namespace相关*/
        System.out.println("==============namespace=============");
        list = admin.namespaces().getNamespaces(tenantName);
        System.out.println(String.format("租户 [%s] 下的NameSpace 列表：%s", tenantName, list));
        for (String namespaceName : list) {
            System.out.println(String.format("[%s] 权限信息：%s",
                    namespaceName, getNamespacePermission(namespaceName)));
        }
        //创建namespace
        String namespaceName = tenantName + "/testN";
        System.out.println(String.format("创建namespace：%s", namespaceName));
        Set<String> producers = new HashSet<>();
        producers.add("admin");
        producers.add(tenantName);
        producers.add("zx");
        Set<String> consumers = new HashSet<>();
        consumers.add("admin");
        consumers.add(tenantName);
        createNamespace(namespaceName, producers, consumers);
        System.out.println(String.format("新建的namespace [%s] 权限信息：%s", namespaceName, getNamespacePermission(namespaceName)));
        //修改namespace中角色权限
        System.out.println(String.format("修改namespace: [%s] 的角色 [%s] 权限", namespaceName, "zx"));
        updateNamespace(namespaceName, "zx", false, false);
        System.out.println(String.format("修改的namespace [%s] 权限信息：%s", namespaceName, getNamespacePermission(namespaceName)));

        /*打印topic相关*/
        System.out.println("=================Topic================");
        list = admin.topics().getPartitionedTopicList(namespaceName);
        System.out.println(String.format("namespace: [%s] topic list : %s", namespaceName, list));
        list = admin.namespaces().getTopics(namespaceName);
        System.out.println(String.format("namespace: [%s] topic list : %s", namespaceName, list));
        //创建分区topic
        String topicName = "persistent://" + namespaceName + "/testN";
        createTopic(topicName, 5);
        System.out.println(String.format("新建的topic [%s] 权限信息: %s", topicName, admin.topics().getPermissions(topicName)));
        System.out.println(String.format("namespace: [%s] topic list : %s", namespaceName, list));
        // 修改topic权限
        updateTopicPermission(topicName, "zx", true, true);
        System.out.println(String.format("修改的topic [%s] 权限信息: %s", topicName, admin.topics().getPermissions(topicName)));
        updateTopicPermission(topicName, "zx", true, false);
        System.out.println(String.format("修改的topic [%s] 权限信息: %s", topicName, admin.topics().getPermissions(topicName)));
        // 修改topic分区数 目前不可用 pulsar在添加token认证之后重定向时不会带token导致没有权限处理更新
        /*updatePartition(topicName, 10);
        System.out.println(String.format("修改的topic [%s] 分区数: %d", topicName, admin.topics().getPartitionedTopicMetadata(topicName).partitions));*/
        System.out.println("=================删除==================");
        // 删除topic
        System.out.println(String.format("删除topic %s", topicName));
        deleteTopic(topicName);
        list = admin.namespaces().getTopics(namespaceName);
        System.out.println(String.format("namespace: [%s] topic list : %s", namespaceName, list));
        //删除namespace
        System.out.println(String.format("删除namespace:%s", namespaceName));
        deleteNamespace(namespaceName);
        System.out.println(String.format("namespace List:%s", admin.namespaces().getNamespaces(tenantName)));
        // 删除tenant
        System.out.println(String.format("删除租户：%s", tenantName));
        deleteTenant(tenantName);
        list = admin.tenants().getTenants();
        System.out.println(String.format("租户列表：%s", list));
    }

    /*展示Message ID*/
    public void displayMessageId(MessageId id) {
        String[] idInfo = id.toString().split(":");
        System.out.println(String.format("ledger id = %s", idInfo[0]));
        System.out.println(String.format("Entry Id = %s", idInfo[1]));
        System.out.println(String.format("Partition Index = %s", idInfo[2]));
    }

    /*=====================cluster===============*/


    /*==================tenant============================*/

    /*创建tenant，创建需要指定管理员名称以及集群名称*/
    public void createTenant(String tenantName, Set<String> adminRoles, Set<String> allowClusters) throws PulsarAdminException {
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setAdminRoles(adminRoles);
        tenantInfo.setAllowedClusters(allowClusters);
        admin.tenants().createTenant(tenantName, tenantInfo);
    }

    /*修改tenant信息*/
    public void updateTenant(String tenantName, Set<String> adminRoles, Set<String> allowClusters) throws PulsarAdminException {
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setAdminRoles(adminRoles);
        tenantInfo.setAllowedClusters(allowClusters);
        admin.tenants().updateTenant(tenantName, tenantInfo);
    }

    /*删除tenant*/
    public void deleteTenant(String tenantName) throws PulsarAdminException {
        admin.tenants().deleteTenant(tenantName);
    }

    /*查询tenant状态*/
    public Map<String, Set<?>> getTenantInfo(String tenantName) throws PulsarAdminException {
        Map<String, Set<?>> map = new HashMap<>();
        Set<String> adminRoles = admin.tenants().getTenantInfo(tenantName).getAdminRoles();
        Set<String> cluster = admin.tenants().getTenantInfo(tenantName).getAllowedClusters();
        for (String adminRole : adminRoles) map.put(adminRole, cluster);
        return map;
    }


    /*====================namespace==========================*/

    /*获得namespace的权限信息*/
    public Map<String, Set<AuthAction>> getNamespacePermission(String namespaceName) throws PulsarAdminException {
        return admin.namespaces().getPermissions(namespaceName);
    }

    private final Set<AuthAction> producerAuthSet = new HashSet<AuthAction>() {{
        add(AuthAction.produce);
    }};
    private final Set<AuthAction> consumerAuthSet = new HashSet<AuthAction>() {{
        add(AuthAction.consume);
    }};
    private final Set<AuthAction> allAuthSet = new HashSet<AuthAction>() {{
        add(AuthAction.produce);
        add(AuthAction.consume);
    }};

    /*创建namespace 并指定默认的角色拥有produce和consume的权限*/
    public void createNamespace(String namespaceName, Set<String> producers, Set<String> consumers) throws PulsarAdminException {
        admin.namespaces().createNamespace(namespaceName);
        for (String producer : producers) {
            if (consumers.contains(producer)) {
                admin.namespaces().grantPermissionOnNamespace(namespaceName, producer, allAuthSet);
            } else {
                admin.namespaces().grantPermissionOnNamespace(namespaceName, producer, producerAuthSet);
            }
        }
        for (String consumer : consumers) {
            if (producers.contains(consumer)) continue;
            admin.namespaces().grantPermissionOnNamespace(namespaceName, consumer, consumerAuthSet);
        }
    }

    /*修改namespace中一个角色权限*/
    public void updateNamespace(String namespaceName, String roleName, boolean produce, boolean consume) throws PulsarAdminException {
        if (produce && consume) {
            admin.namespaces().grantPermissionOnNamespace(namespaceName, roleName, allAuthSet);
        } else if (consume) {
            admin.namespaces().grantPermissionOnNamespace(namespaceName, roleName, consumerAuthSet);
        } else if (produce) {
            admin.namespaces().grantPermissionOnNamespace(namespaceName, roleName, producerAuthSet);
        } else {
            admin.namespaces().revokePermissionsOnNamespace(namespaceName, roleName);
        }
    }

    /*删除namespace*/
    public void deleteNamespace(String namespaceName) throws PulsarAdminException {
        admin.namespaces().deleteNamespace(namespaceName);
    }

    /*获取namespace下所有topic*/
    public List<String> getTopicsByNamespace(String namespace) throws PulsarAdminException {
        return admin.namespaces().getTopics(namespace);
    }


    /*========================topic==============================*/

    /*根据namespace获取topic列表*/
    public List<String> getTopicList(String namespace) throws PulsarAdminException {
        return admin.topics().getPartitionedTopicList(namespace);
    }

    /*创建topic两个及以上分区*/
    public void createTopic(String topic, int partitions) throws PulsarAdminException {
        admin.topics().createPartitionedTopic(topic, partitions);
    }

    /*更新topic的分区数,修改后应该比修改前分区更多 pulsar的java客户端暂时不能实现这个功能*/
    public void updatePartition(String topic, int partition) throws PulsarAdminException {
        int rowPartition = admin.topics().getPartitionedTopicMetadata(topic).partitions;
        if (rowPartition >= partition) {
            System.err.printf("更新后的分区数量都必须大于原本的分区数，原分区数：%d,目标分区：%d\n", rowPartition, partition);
            return;
        }
        admin.topics().updatePartitionedTopic(topic, partition);
    }

    /*修改topic中某个角色的权限*/
    public void updateTopicPermission(String topicName, String roleName, boolean produce, boolean consume) throws PulsarAdminException {
        if (produce && consume) {
            admin.topics().grantPermission(topicName, roleName, allAuthSet);
        } else if (produce) {
            admin.topics().grantPermission(topicName, roleName, producerAuthSet);
        } else if (consume) {
            admin.topics().grantPermission(topicName, roleName, consumerAuthSet);
        } else {
            admin.topics().grantPermission(topicName, roleName, null);
        }
    }

    /*删除分区主题*/
    public void deleteTopic(String topic) {
        try {
            admin.topics().deletePartitionedTopic(topic);
            System.out.printf("删除成功:%s\n", topic);
        } catch (PulsarAdminException e) {
            try {
                admin.topics().delete(topic);
                System.out.printf("删除成功:%s\n", topic);
                return;
            } catch (PulsarAdminException ex) {
                ex.printStackTrace();
                System.out.println("无法删除topic");
            }
            System.out.println(String.format("删除失败:%s", topic));
        }
    }


    private List<TopicNode> tenantNodes = null;

    /*更新topic层次关系 所有的 topic树 不包括集群，顶级是租户--》namespace--》topic*/
    public void updateTopicTree() throws PulsarAdminException {
        if(tenantNodes != null)
            tenantNodes.clear();
        List<String> tenants = admin.tenants().getTenants();
        /*遍历tenant*/
        for (String tenant : tenants) {
            TopicNode tenantNode = new TopicNode(tenant, tenant, tenant);
            tenantNode.setAdmin(getTenantInfo(tenant));
            List<String> namespaces = admin.namespaces().getNamespaces(tenant);
            if (namespaces.size() <= 0) break;
            List<TopicNode> namespacesNode = new LinkedList<>();
            /*遍历namespace*/
            for (String namespace : namespaces) {
                TopicNode namespaceNode = new TopicNode(namespace, namespace, namespace);
                namespaceNode.setAdmin(getNamespacePermission(namespace));
                List<String> topics = admin.topics().getPartitionedTopicList(namespace);
                if (topics.size() <= 0) break;
                List<TopicNode> topicsNode = new LinkedList<>();
                /*遍历topic*/
                for (String topic : topics) {
                    TopicNode topicNode = new TopicNode(topic, topic, topic);
                    topicNode.setAdmin(admin.topics().getPermissions(topic));
                    int partitionCount = admin.topics().getPartitionedTopicMetadata(topic).partitions;
                    if (partitionCount <= 0) break;
                    List<TopicNode> partitions = new LinkedList<>();
                    /*遍历partition*/
                    for (int i = 0; i < partitionCount; i++) {
                        String partitionName = topic + "-partition-" + i;
                        TopicNode partitionNode = new TopicNode(partitionName, partitionName, partitionName);
                        partitionNode.isLeaf = true;
                        partitions.add(partitionNode);
                    }
                    topicNode.children = partitions;
                    topicsNode.add(topicNode);
                }
                namespaceNode.children = topicsNode;
                namespacesNode.add(namespaceNode);
            }
            tenantNode.children = namespacesNode;
            tenantNodes.add(tenantNode);
        }
    }

    /*获得topic层次关系*/
    public String getAllTopicNode() throws PulsarAdminException {
        if (tenantNodes == null) {
            synchronized (this) {
                if (tenantNodes == null) {
                    //更新topic树
                    tenantNodes = new LinkedList<>();
                    updateTopicTree();
                }
            }
        }
        return new JSONArray(tenantNodes).toString();
    }

    /*获取指定租户的topic树*/
    public String getTenantTopicNode(String tenant) {
        String result = null;
        for (TopicNode tenantNode : tenantNodes) {
            if(tenantNode.value.equals(tenant))
                result = new JSONArray(tenantNode.children).toString();
        }
        return result;
    }


    /*======================其他===========================*/
    /*获得topic中总共有多少条数据*/
    public int countTopicMessage(String rowTopic, int partitionCount) throws PulsarAdminException {
        int count = 0;
        for (int i = 0; i < partitionCount; i++) {
            String topic = String.format("%s-partition-%d", rowTopic, i);
            MessageId id = admin.topics().getLastMessageId(topic);
            displayMessageId(id);
            int c = Integer.parseInt(id.toString().split(":")[1]);
            count += c;
        }
        return count + partitionCount;
    }

    /*获得topic中的Partition数量*/
    public int getPartitionsCount(String topic) throws PulsarAdminException {
        return admin.topics().getPartitionedTopicMetadata(topic).partitions;
    }

    public String getTopicInfo(String topic) throws PulsarAdminException {
        return String.valueOf(admin.topics().getInternalInfo(topic));
    }

    /*关闭Admin*/
    public void closeAdmin() {
        admin.close();
    }
}
