package cn.fancychuan.neo4j.app;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;

/**
 * 1. 创建节点
 * 2. 创建关系，有三种方法
 *  2.1 实现RelationshipType接口
 *  2.2 使用枚举类
 *  2.3 使用动态类：DynamicRelationshipType
 *      使用场景：运行时才能知道关系类型。比如
 *          String runtimeString = func();
 *          Relationship rel = DynamicRelationshipType.withName(runtimeString)
 */
public class CreateGraphApp {
    private GraphDatabaseService graphDb;
    public CreateGraphApp() {
        File file = new File("E:\\JavaWorkshop\\graph-knowledge\\neo4j\\db\\");
        this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(file);
    }

    public static void main(String[] args) {
        CreateGraphApp app = new CreateGraphApp();
        app.createUserNode();
        app.createUserRelationship();
        app.setUserNodeProperty();
        app.createMovieNode();
        app.userMovieRelationship();
    }


    public void createUserNode() {
        try (Transaction tx = graphDb.beginTx()) {
            Node user1 = graphDb.createNode();
            System.out.println("created user: " + user1.getId());
            Node user2 = graphDb.createNode();
            System.out.println("created user: " + user2.getId());
            Node user3 = graphDb.createNode();
            System.out.println("created user: " + user3.getId());
            tx.success();
        }
    }

    /**
     * 2. 创建关系
     *      关系的创建需要运行事务
     */
    public void createUserRelationship() {
        try (Transaction tx = graphDb.beginTx()) {
            Node user1 = graphDb.getNodeById(0);
            Node user2 = graphDb.getNodeById(1);
            Node user3 = graphDb.getNodeById(2);
            user1.createRelationshipTo(user2, new IsFriendOf()); // user1是起始节点，user2是结束节点
            user1.createRelationshipTo(user3, MyRelationshipTypes.IS_FRIEND_OF);
            tx.success();
        }
    }
    /**
     * 3. 设置节点属性
     */
    public void setUserNodeProperty() {
        try (Transaction tx = graphDb.beginTx()) {
            Node user1 = graphDb.getNodeById(0);
            Node user2 = graphDb.getNodeById(1);
            Node user3 = graphDb.getNodeById(2);
            user1.setProperty("name", "fancy");
            user1.setProperty("age", 22);
            user2.setProperty("name", "chuan");
            user2.setProperty("locked", true);  // bool类型
            user3.setProperty("name", "fancyChuan");
            user3.setProperty("cars_owned", new String[]{"BMW", "Audi"}); // 数组类型
            // 2.0版本以上使用标签来区分节点类型，比如Node userX = graphDb.createNode("User");
            user1.addLabel(new UserLable());
            user2.addLabel(MyLabels.USERS);
            user3.addLabel(MyLabels.USERS);
            tx.success();
        }
    }
    /**
     * 创建电影节点
     *  从neo4j数据库的角度讲，user节点和movie节点没有本质的区别，都是节点
     */
    public void createMovieNode() {
        try (Transaction tx = graphDb.beginTx()) {
            Node movie1 = graphDb.createNode();
            Node movie2 = graphDb.createNode();
            Node movie3 = graphDb.createNode();
            movie1.setProperty("name", "Fargo");
            movie2.setProperty("name", "Alien");
            movie3.setProperty("name", "Heat");
            // 1.9版本及以下使用type属性来区分节点类型
            movie1.setProperty("type", "Movie");
            movie2.setProperty("type", "Movie");
            movie3.setProperty("type", "Movie");
            tx.success();
        }
    }

    /**
     * 创建用户和节点的关系，并且设置关系的属性
     */
    public void userMovieRelationship() {
        try (Transaction tx = graphDb.beginTx()) {
            Node user1 = graphDb.getNodeById(0);
            Node user2 = graphDb.getNodeById(1);
            Node user3 = graphDb.getNodeById(2);
            Node movie1 = graphDb.getNodeById(3);
            Node movie2 = graphDb.getNodeById(4);
            Node movie3 = graphDb.getNodeById(5);
            Relationship rel1 = user1.createRelationshipTo(movie1, MyRelationshipTypes.HAS_SEEN);
            rel1.setProperty("stars", 5);
            Relationship rel2 = user2.createRelationshipTo(movie3, MyRelationshipTypes.HAS_SEEN);
            rel2.setProperty("stars", 3);
            Relationship rel3 = user3.createRelationshipTo(movie2, MyRelationshipTypes.HAS_SEEN);
            rel3.setProperty("stars", 4);
            tx.success();
        }
    }

    void findNodeByLabelAndProperty() {

    }


    /**
     * 关系用接口RelationshipType接口定义
     */
    public class IsFriendOf implements RelationshipType {
        @Override
        public String name() {
            return "IS_FRIEND_OF";
        }
    }

    /**
     * 使用枚举类来自定义关系
     */
    public enum MyRelationshipTypes implements RelationshipType {
        IS_FRIEND_OF,
        HAS_SEEN
    }

    public class UserLable implements Label {
        @Override
        public String name() {
            return "USERS";
        }
    }
    public enum MyLabels implements Label {
        MOVIES, USERS
    }
}
