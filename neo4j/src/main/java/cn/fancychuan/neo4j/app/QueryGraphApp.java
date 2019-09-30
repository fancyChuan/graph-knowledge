package cn.fancychuan.neo4j.app;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.HashSet;

public class QueryGraphApp {
    private GraphDatabaseService graphDb;
    public QueryGraphApp() {
        this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File("E:\\JavaWorkshop\\graph-knowledge\\neo4j\\db\\"));
    }

    public static void main(String[] args) {
        QueryGraphApp app = new QueryGraphApp();
        app.findMovieByUserNodeId();
    }

    void findMovieByUserNodeId() {
        try (Transaction tx = graphDb.beginTx()) {
            Node userNode = graphDb.getNodeById(0);
            Iterable<Relationship> allRelations = userNode.getRelationships(); // 获取到所有起始节点或终止节点是userNode的所有关系 todo:为什么每个关系会重复一次？
            HashSet<Node> hasSeenSet = new HashSet<>();
            for (Relationship item : allRelations) {
                System.out.println("* " + item.getStartNode().getProperty("name") + " --"
                                + item.getType().name() + "--> " + item.getEndNode().getProperty("name"));
                if (item.getType().name().equalsIgnoreCase("HAS_SEEN")) {  // 关系的类型通过getType()获取
                    hasSeenSet.add(item.getEndNode());
                }
            }
            System.out.println("[user]" + userNode.getProperty("name"));
            for (Node node : hasSeenSet) {
                System.out.println("\thas seen: " + node.getProperty("name"));
            }
            tx.success();
        }
    }
}
