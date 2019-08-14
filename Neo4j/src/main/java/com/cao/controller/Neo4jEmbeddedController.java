package com.cao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.*;

import com.alibaba.fastjson.JSONObject;
import com.cao.config.Neo4JDBManager;
import com.cao.types.NodeLabels;
import com.cao.types.Relationships;

import io.swagger.annotations.Api;

/**
* <p>Title: Neo4jController</p>  
* <p>Description: </p>  
* @author Cao
* @date 2019年8月5日
 */
@Api(tags = "Neo4j访问方式: Java应用程序中的嵌入式数据库")
@RestController("neo4jEmbeddedController")
@RequestMapping("/neoEmbed")
@Scope("prototype")
public class Neo4jEmbeddedController {
	
	@Autowired
	private Neo4JDBManager neo4jdbManager;
	
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public JSONObject add() {
		JSONObject object = new JSONObject();
		//开启事务
		Transaction tx = null;
		try {
			tx = neo4jdbManager.getGraphDatabaseDB().beginTx();
            // Perform DB operations
            Node steve = neo4jdbManager.getGraphDatabaseDB().createNode(NodeLabels.USER);
            steve.setProperty("name", "Steve");
            steve.setProperty("title", "developer");
            steve.setProperty("age", 10);
            steve.addLabel(NodeLabels.USER);
            
            Node linda = neo4jdbManager.getGraphDatabaseDB().createNode(NodeLabels.USER);
            linda.setProperty("name", "Linda");
            steve.setProperty("title", "tester");
            steve.setProperty("age", 12);
            steve.addLabel(NodeLabels.USER);
            
            steve.createRelationshipTo( linda, Relationships.IS_FRIEND_OF );
            System.out.println("created node name is " + steve.getProperty("name"));
            tx.success();
            object.put("result", "success");
        } catch (Exception e) {
        	tx.failure();
        	object.put("result", "failed");
        	e.printStackTrace();
		} finally {
			tx.terminate();
		}
		 return object;
	}
	
	@RequestMapping(value="/queryByLabels", method=RequestMethod.GET)
	public JSONObject queryByLabels() {
		JSONObject object = new JSONObject();
		Transaction tx = null;
		try {
			tx = neo4jdbManager.getGraphDatabaseDB().beginTx();
			ResourceIterable<Node> users = neo4jdbManager.getGraphDatabaseDB().getAllNodes();
			List<Map<String, Object>> resulotList = new ArrayList<Map<String,Object>>();
			if (users.iterator().hasNext()) {
				Map<String, Object> map = new HashMap<>();
				//users.iterator();
				map.put("id", users.iterator().next().getId());
				map.put("name", users.iterator().next().getProperty("name"));
				//map.put("title", users.iterator().next().getProperty("title"));
				//map.put("age", users.iterator().next().getProperty("age"));
				resulotList.add(map);
			}
			object.put("users", users.iterator().next().getProperty("name"));
		} catch (Exception e) {
        	tx.failure();
        	object.put("users", "failed");
        	e.printStackTrace();
		} finally {
			tx.terminate();
		}
		return object;
	}
	
	@RequestMapping(value="/query", method=RequestMethod.GET)
	public void query() {
		//查询数据库
        String query ="match (n:USER) return n.name as name";
        Map<String, Object >parameters = new HashMap<String, Object>();
         try (Result result = neo4jdbManager.getGraphDatabaseDB().execute(query, parameters)) {
             while (result.hasNext()) {
                 Map<String, Object> row = result.next();
                 for (String key : result.columns()) {
                     System.out.printf( "%s = %s%n", key, row.get( key ) );
                 }
             }
         }
	}
	
}
