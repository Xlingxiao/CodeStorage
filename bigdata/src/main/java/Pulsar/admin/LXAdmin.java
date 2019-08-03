package Pulsar.admin;

import dataDistribution.mybatis.pojo.NamespaceStatus;
import dataDistribution.mybatis.pojo.TopicStatus;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.common.policies.data.*;
import org.json.JSONArray;
import org.json.JSONObject;
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
    private Set<String> allowClusters;

    public LXAdmin(PropertiesUtil propertiesUtil) throws IOException {
        this.properties = propertiesUtil.getProperties("pulsar/pulsar.properties");
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
        //获得集群名称
        String rowClusters = properties.getProperty("clusters");
        if (rowClusters != null && rowClusters.contains(",")) {
            String[] clusters = rowClusters.split(",");
            allowClusters = new HashSet<>(Arrays.asList(clusters));
        } else {
            allowClusters = new HashSet<String>() {{
                add("pulsar-cluster-1");
            }};
        }
        /*ClientConfigurationData config = new ClientConfigurationData();
        config.setTlsAllowInsecureConnection(false);
        config.setTlsHostnameVerificationEnable(Boolean.parseBoolean(properties.getProperty("useTls")));
        config.setTlsTrustCertsFilePath(properties.getProperty("tlsCertFile"));
        admin = new PulsarAdmin(properties.getProperty("tlsRestUrl"), config);*/
    }

    /*打印一些信息测试集群权限等问题*/
    public void printSome() throws PulsarAdminException {
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
            System.out.println(String.format("租户 [%s] 信息：%s", tenantName, (tenantName)));
        }
        //创建tenant
        /*tenant创建时需要*/
        String tenantName = "qfdagdffa";
        System.out.println(String.format("创建租户：%s", tenantName));
        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("admin");
        adminRoles.add("public");
        adminRoles.add(tenantName);
        createTenant(tenantName, adminRoles);
        list = admin.tenants().getTenants();
        System.out.println(String.format("租户列表：%s", list));
        System.out.println(String.format("新建的租户信息：%s", getTenantInfoToMap(tenantName)));
        //修改租户信息
        System.out.println(String.format("修改租户信息：%s", tenantName));
        adminRoles.add("lx");
        updateTenant(tenantName, adminRoles);
        System.out.println(String.format("[%s] 租户信息：%s", tenantName, getTenantInfoToMap(tenantName)));

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
        NamespaceStatus namespaceStatus = new NamespaceStatus();
        namespaceStatus.setName(namespaceName);
        System.out.println(String.format("创建namespace：%s", namespaceName));
        Set<String> producers = new HashSet<>();
        producers.add("admin");
        producers.add(tenantName);
        producers.add("zx");
        createNamespace(namespaceStatus, producers);
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
        TopicStatus topic = new TopicStatus();
        topic.setName(topicName);
        topic.setPartitions(5);
        createTopic(topic);
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
        deleteTopic(topic.getName());
        list = admin.namespaces().getTopics(namespaceName);
        System.out.println(String.format("namespace: [%s] topic list : %s", namespaceName, list));
        //删除namespace
        System.out.println(String.format("删除namespace:%s", namespaceName));
        admin.namespaces().deleteNamespace(namespaceName);
        System.out.println(String.format("namespace List:%s", admin.namespaces().getNamespaces(tenantName)));
        // 删除tenant
        System.out.println(String.format("删除租户：%s", tenantName));
        admin.tenants().deleteTenant(tenantName);
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

    /*创建tenant 默认的管理role就是tenant名称*/
    private Set<String> adminRoles = new HashSet<>();

    public void createTenant(String tenantName) throws PulsarAdminException {
        adminRoles.clear();
        adminRoles.add(tenantName);
        createTenant(tenantName, adminRoles);
    }

    /*创建tenant，创建需要指定管理员名称以及集群名称*/
    public void createTenant(String tenantName, Set<String> adminRoles) throws PulsarAdminException {
        String rowRoles = properties.getProperty("defaultAdmin");
        if (rowRoles != null && rowRoles.contains(",")) {
            String[] roles = rowRoles.split(",");
            adminRoles.addAll(Arrays.asList(roles));
        }
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setAdminRoles(adminRoles);
        tenantInfo.setAllowedClusters(allowClusters);
        admin.tenants().createTenant(tenantName, tenantInfo);
    }

    /*根据角色和管理的租户列表更新角色管理列表*/
    public void updateRolePermission(String role, List<String> adminTenants) throws PulsarAdminException {
        //用户是admin或者lx的话就不用修改权限，保证admin一直拥有所有tenant的管理权限
        //因为admin是超级管理员，授不授权都可以管理tenant
        if (role.equals("admin") || role.equals("lx")) return;
        List<String> tenants = admin.tenants().getTenants();
        //遍历所有租户
        for (String tenant : tenants) {
            // 获得租户管理角色列表
            Set<String> adminRoles = getTenantAdminRoles(tenant);
            //遍历要更新的租户列表
            for (String adminTenant : adminTenants) {
                //如果要更新的租户和租户相同 将role添加进管理员中
                if (adminTenant.equals(tenant)) {
                    adminRoles.add(role);
                    updateTenant(tenant, adminRoles);
                    break;
                } else {
                    // 如果要更新的租户和租户不同但是租户的管理员中又包含了当前role就将其删除
                    if (adminRoles.contains(role)) {
                        adminRoles.remove(role);
                        updateTenant(tenant, adminRoles);
                        break;
                    }
                }
            }
        }
    }

    /*修改tenant信息*/
    public void updateTenant(String tenantName, Set<String> adminRoles) throws PulsarAdminException {
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setAdminRoles(adminRoles);
        tenantInfo.setAllowedClusters(allowClusters);
        admin.tenants().updateTenant(tenantName, tenantInfo);
    }

    /*为tenant添加一个管理角色*/
    public void addRoleForTenant(String tenant, String role) throws PulsarAdminException {
        TenantInfo tenantInfo = admin.tenants().getTenantInfo(tenant);
        Set<String> adminRoles = tenantInfo.getAdminRoles();
        adminRoles.add(role);
        tenantInfo.setAdminRoles(adminRoles);
        admin.tenants().updateTenant(tenant, tenantInfo);
    }

    /*为tenant删除一个管理角色*/
    public void delRoleForTenant(String tenant, String role) throws PulsarAdminException {
        TenantInfo tenantInfo = admin.tenants().getTenantInfo(tenant);
        Set<String> adminRoles = tenantInfo.getAdminRoles();
        adminRoles.remove(tenant);
        tenantInfo.setAdminRoles(adminRoles);
        admin.tenants().updateTenant(tenant, tenantInfo);
    }

    /*递归删除tenant*/
    public void recursiveDeleteTenant(String tenant) throws PulsarAdminException {
        List<String> namespaces = admin.namespaces().getNamespaces(tenant);
        for (String namespace : namespaces) {
            recursiveDeleteNamespace(namespace);
        }
        admin.tenants().deleteTenant(tenant);
    }

    /*将tenant状态转为map形式*/
    public Map<String, Set<?>> getTenantInfoToMap(String tenant) throws PulsarAdminException {
        TenantInfo tenantInfo = admin.tenants().getTenantInfo(tenant);
        Map<String, Set<?>> map = new HashMap<>();
        Set<String> adminRoles = tenantInfo.getAdminRoles();
        Set<String> cluster = tenantInfo.getAllowedClusters();
        for (String adminRole : adminRoles) map.put(adminRole, cluster);
        return map;
    }

    /*获得tenant的权限列表*/
    public Set<String> getTenantAdminRoles(String tenant) throws PulsarAdminException {
        TenantInfo tenantInfo = admin.tenants().getTenantInfo(tenant);
        return tenantInfo.getAdminRoles();
    }

    /*获取所有租户*/
    public List<String> getTenants() throws PulsarAdminException {
        return admin.tenants().getTenants();
    }

    /*根据租户获取下面所有namespace状态*/
    public List<NamespaceStatus> getNamespaces(String tenant) throws PulsarAdminException {
        List<String> namespaces = admin.namespaces().getNamespaces(tenant);
        List<NamespaceStatus> statuses = new LinkedList<>();
        for (String namespace : namespaces) {
            NamespaceStatus status = getNamespaceStatus(namespace);
            statuses.add(status);
        }
        return statuses;
    }

    /*根据租户列表返回所有namespace 状态*/
    public Set<NamespaceStatus> getNamespaces(List<String> tenants) throws PulsarAdminException {
        Set<NamespaceStatus> statuses = new HashSet<>();
        for (String tenant : tenants) {
            try {
                statuses.addAll(getNamespaces(tenant));
            } catch (PulsarAdminException.NotFoundException e) {
                //TODO
            }
        }
        return statuses;
    }

    /*获取所有namespace状态*/
    public Set<NamespaceStatus> getNamespaces() throws PulsarAdminException {
        List<String> tenants = admin.tenants().getTenants();
        Set<NamespaceStatus> statuses = new HashSet<>();
        for (String tenant : tenants) {
            statuses.addAll(getNamespaces(tenant));
        }
        return statuses;
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
    public void createNamespace(NamespaceStatus namespace, Set<String> adminRoles) throws PulsarAdminException {
        String name = namespace.getTenant() + "/" + namespace.getName();
        admin.namespaces().createNamespace(name);
        /*授权*/
        for (String role : adminRoles) {
            admin.namespaces().grantPermissionOnNamespace(name, role, allAuthSet);
        }
        /*设置留存参数*/
        setNamespace(name, namespace.getRetentionSizeInMB(), namespace.getRetentionTimeInMinutes());
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

    /*递归删除namespace*/
    public void recursiveDeleteNamespace(String namespace) throws PulsarAdminException {
        List<String> topics = admin.namespaces().getTopics(namespace);
        for (String topic : topics) admin.topics().deletePartitionedTopic(topic);
        topics = admin.topics().getPartitionedTopicList(namespace);
        for (String topic : topics) admin.topics().delete(topic);
        admin.namespaces().deleteNamespace(namespace);
    }

    /*获取namespace占用的总空间大小Byte*/
    public long getNamespaceUsedSpace(String namespace) throws PulsarAdminException {
        //List<String> topics = admin.namespaces().getTopics(namespace);
        List<String> topics = admin.topics().getPartitionedTopicList(namespace);
        long totalSize = 0;
        for (String topic : topics) totalSize += getTopicUsedSpace(topic);
        return totalSize;
    }

    /*获得一个namespace的状态*/
    public NamespaceStatus getNamespaceStatus(String namespace) throws PulsarAdminException {
        NamespaceStatus status = new NamespaceStatus();
        String[] tn = namespace.split("/");
        status.setTenant(tn[0]);
        status.setName(tn[1]);
        //获取消息留存策略
        RetentionPolicies map = admin.namespaces().getRetention(namespace);
        //设置namespace状态
        status.setRetentionSizeInMB(map.getRetentionSizeInMB());
        status.setRetentionTimeInMinutes(map.getRetentionTimeInMinutes());
        //获得topic使用的总空间 结果是byte转为MB
        long totalSize = getNamespaceUsedSpace(namespace);
        status.setUsedSpaceMB(totalSize);
        status.setTopics(admin.topics().getPartitionedTopicList(namespace).size());
        return status;
    }

    /*设置namespace的消息留存参数*/
    public void setNamespace(String namespace, long spaceLimit, long ttl) throws PulsarAdminException {
        int retentionTime = (int) ttl;
        int retentionSize = (int) spaceLimit;
        admin.namespaces().setRetention(namespace, new RetentionPolicies(retentionTime, retentionSize));
    }


    /*========================topic==============================*/

    /*根据namespace获取所有带分区的topic*/
    public List<String> getPartitionedTopicList(String namespace) throws PulsarAdminException {
        return admin.topics().getPartitionedTopicList(namespace);
    }

    /*创建多分区topic pulsar不支持创建单个分区的topic*/
    public void createTopic(TopicStatus topic) throws PulsarAdminException {
        if (topic.getPartitions() > 1) {
            admin.topics().createPartitionedTopic(topic.getName(),topic.getPartitions());
        }
    }

    /*更新topic的分区数,修改后应该比修改前分区更多 pulsar的java客户端暂时不能实现这个功能*/
    public void updatePartition(String topic, int partition) throws PulsarAdminException {
        int rowPartition = admin.topics().getPartitionedTopicMetadata(topic).partitions;
        if (rowPartition < partition) {
            admin.topics().updatePartitionedTopic(topic, partition);
        }
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
    public void deleteTopic(String topic) throws PulsarAdminException {
        int partitions = getPartitionsCount(topic);
        if (partitions <= 1) {
            admin.topics().delete(topic);
        } else {
            admin.topics().deletePartitionedTopic(topic);
        }
    }

    /*获取topic占用的空间Byte*/
    public long getTopicUsedSpace(String topic) throws PulsarAdminException {
        int partitions = getPartitionsCount(topic);
        long totalSize = 0;
        for (int i = 0; i < partitions; i++) {
            try {
                PersistentTopicInternalStats stats = admin.topics().getInternalStats(topic + "-partition-" + i);
                totalSize += stats.totalSize;
            } catch (PulsarAdminException.NotFoundException e) {
                return 0;
            }
        }
        return totalSize;
    }

    /*获取topic信息*/
    public Map<String, PersistentTopicInternalStats> getTopicInfo(String topic) throws PulsarAdminException {
        Map<String, PersistentTopicInternalStats> statsMap = new LinkedHashMap<>();
        int partitions = getPartitionsCount(topic);
        if (partitions < 2) statsMap.put(topic, admin.topics().getInternalStats(topic));
        else{
            for (int i = 0; i < partitions; i++) {
                String topicItem = topic + "-partition-" + i;
                PersistentTopicInternalStats item = null;
                try {
                    item = admin.topics().getInternalStats(topicItem);
                } catch (PulsarAdminException.NotFoundException e) {
                    System.out.println(String.format("这个topic [%s] 目前没有创建", topicItem));
                }
                statsMap.put(topicItem, item);
            }
        }
        return statsMap;
    }


    /*======================其他===========================*/

    /*获取所有topic状态*/
    public List<TopicStatus> getAllTopicStatus() throws PulsarAdminException {
        List<TopicStatus> statuses = new LinkedList<>();
        List<String> tenants = admin.tenants().getTenants();
        for (String tenant : tenants) {
            statuses.addAll(getTopicStatusByTenant(tenant));
        }
        return statuses;
    }

    /*根据tenants获取topic*/
    public List<TopicStatus> getTopicStatus(List<String> tenants) throws PulsarAdminException {
        List<TopicStatus> statuses = new LinkedList<>();
        for (String tenant : tenants) {
            try {
                statuses.addAll(getTopicStatusByTenant(tenant));
            } catch (PulsarAdminException.NotFoundException e) {
                //TODO
            }
        }
        return statuses;
    }

    /*根据tenant获得所有topic状态*/
    public List<TopicStatus> getTopicStatusByTenant(String tenant) throws PulsarAdminException {
        List<TopicStatus> topicStatuses = new LinkedList<>();
        List<String> namespaces = admin.namespaces().getNamespaces(tenant);
        for (String namespace : namespaces) {
            //List<String> topics = admin.namespaces().getTopics(namespace);
            List<String> topics = admin.topics().getPartitionedTopicList(namespace);
            for (String topic : topics) {
                TopicStatus tmp = getTopicStatus(topic);
                if (tmp != null) {
                    topicStatuses.add(tmp);
                }
            }
        }
        return topicStatuses;
    }

    /*获得topic状态，不论topic是否有多分区*/
    public TopicStatus getTopicStatus(String topic) throws PulsarAdminException {
        int partitions = getPartitionsCount(topic);
        if (partitions < 2) return getOneTopicStatus(topic);
        TopicStatus topicStatus = new TopicStatus();
        topicStatus.setName(topic);
        topicStatus.setPartitions(partitions);
        long totalSize = 0;
        long messageSize = 0;
        Map<String, Long> subCount = new HashMap<>();
        for (int i = 0; i < partitions; i++) {
            TopicStatus oneTopicStatus = getOneTopicStatus(topic + "-partition-" + i);
//            if (oneTopicStatus == null) return oneTopicStatus;
            totalSize += oneTopicStatus.getTotalSize();
            messageSize += oneTopicStatus.getMessageCount();
            Map<String, Long> oneSubCount = oneTopicStatus.getSubCount();
            if (oneSubCount != null) {
                for (Map.Entry<String, Long> entry : oneSubCount.entrySet()) {
                    long count = 0;
                    try {
                        count = subCount.get(entry.getKey());
                    } catch (NullPointerException e) {
                        /*e.printStackTrace();*/
                    }
                    subCount.put(entry.getKey(), count + entry.getValue());
                }
            }
        }
        topicStatus.setSubCount(subCount);
        return topicStatus;
    }

    /*获得topic的状态*/
    @SuppressWarnings("CatchMayIgnoreException")
    public TopicStatus getOneTopicStatus(String topic) throws PulsarAdminException {
        TopicStatus topicStatus = new TopicStatus();
        try {
            PersistentTopicInternalStats stats = admin.topics().getInternalStats(topic);
            //总空间占用
            long totalSize = stats.totalSize;

            //每个订阅组消费的数据条数
            Map<String, Long> messageCountMap = new LinkedHashMap<>();
            Map<String, PersistentTopicInternalStats.CursorStats> map = stats.cursors;
            for (Map.Entry<String, PersistentTopicInternalStats.CursorStats> subMap : map.entrySet()) {
                String subName = subMap.getKey();
                long count = 0;
                try {
                    //第一次取不出来会报空指针异常
                    count = messageCountMap.get(subName);
                } catch (NullPointerException e) {
                }
                long tmpCount = subMap.getValue().messagesConsumedCounter;
                messageCountMap.put(subName, count + tmpCount);
            }

            //获得消息总条数
            long messageCount = countTopicMessage(topic);

            //装载
            topicStatus.setName(topic);
            topicStatus.setMessageCount(messageCount);
            topicStatus.setSubCount(messageCountMap);
            topicStatus.setTotalSize(totalSize);
        } catch (PulsarAdminException.NotFoundException e) {
            return topicStatus;
        }
        return topicStatus;
    }

    /*获得topic中总共有多少条数据*/
    public long countTopicMessage(String rowTopic) throws PulsarAdminException {
        int partitionCount = getPartitionsCount(rowTopic);

        if (partitionCount <= 1) {
            return admin.topics().getInternalStats(rowTopic).entriesAddedCounter;
        }
        long count = 0;
        for (int i = 0; i < partitionCount; i++) {
            String topic = rowTopic + "-partition-" + i;
            count += admin.topics().getInternalStats(topic).entriesAddedCounter;
        }
        return count;
    }

    /*获得topic中的Partition数量*/
    public int getPartitionsCount(String topic) throws PulsarAdminException {
        return admin.topics().getPartitionedTopicMetadata(topic).partitions;
    }

    /*获得topic中consumer组消费了多少数据*/
    @SuppressWarnings("CatchMayIgnoreException")
    public Map<String, Long> getMessageCountBySub(String topic) throws PulsarAdminException {
        Map<String, Long> messageCountMap = new LinkedHashMap<>();
        Map<String, PersistentTopicInternalStats.CursorStats> map = admin.topics().getInternalStats(topic).cursors;
        for (Map.Entry<String, PersistentTopicInternalStats.CursorStats> subMap : map.entrySet()) {
            String subName = subMap.getKey();
            long count = 0;
            try {
                count = messageCountMap.get(subName);
            } catch (NullPointerException e) {

            }
            long tmpCount = subMap.getValue().messagesConsumedCounter;
            messageCountMap.put(subName, count + tmpCount);
        }
        return messageCountMap;
    }


    /*获得所有topic 树*/
    public String getAllTopicNode() throws PulsarAdminException {
        List<String> tenants = admin.tenants().getTenants();
        return new JSONArray(getTenantTopicNode(tenants)).toString();
    }

    /*获取指定租户列表的topic树*/
    public String getTenantTopicNode(List<String> tenants) throws PulsarAdminException {
        List<TopicNode> result = new LinkedList<>();
        for (String tenant : tenants){
            TopicNode topicNode = getTopicNode(tenant);
            if (topicNode == null) continue;
            result.add(topicNode);
        }

        return new JSONArray(result).toString();
    }

    /*根据租户获取topic树*/
    public TopicNode getTopicNode(String tenant) throws PulsarAdminException {
        TopicNode tenantNode = new TopicNode(tenant, tenant, tenant);
        try {
            tenantNode.setAdmin(getTenantInfoToMap(tenant));
        } catch (PulsarAdminException.NotFoundException e) {
            return null;
        }
        List<String> namespaces = admin.namespaces().getNamespaces(tenant);
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
                    partitionNode.setLeaf(true);
                    partitions.add(partitionNode);
                }
                topicNode.setChildren(partitions);
                topicsNode.add(topicNode);
            }
            namespaceNode.setChildren(topicsNode);
            namespacesNode.add(namespaceNode);
        }
        tenantNode.setChildren(namespacesNode);
        return tenantNode;
    }

    /*获取所有的namespace树*/
    public String getAllNamespaceTree() throws PulsarAdminException {
        List<String> tenants = admin.tenants().getTenants();
        return getTenantNamespaceTree(tenants);
    }

    /*获取指定租户的namespace树*/
    public String getTenantNamespaceTree(List<String> tenants) throws PulsarAdminException {
        Map<String, List<String>> namespaceTree = new LinkedHashMap<>();
        for (String tenant : tenants) {
            namespaceTree.put(tenant, new LinkedList<>());
            List<String> namespaces = namespaceTree.get(tenant);
            admin.namespaces().getNamespaces(tenant).forEach(namespace ->{
                namespace = namespace.split("/")[1];
                namespaces.add(namespace);
            });
        }
        return new JSONObject(namespaceTree).toString();
    }


    /*关闭Admin*/
    public void closeAdmin() {
        admin.close();
    }

}
