package com.cao.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.cao.entity.Code;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;

/**
* <p>Title: Neo4jController</p>  
* <p>Description: </p>  
* @author Cao
* @date 2019年8月5日
 */
@Api(tags = "Neo4j访问方式: 通过REST的独立服务器")
@Controller("neo4jServerController")
@RequestMapping("/neoServer")
@Scope("prototype")
public class Neo4jServerController {
	
	static Driver driver = null;
	
	static {
		driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "root" ) );
	}
	
    @RequestMapping(value = "/add", method=RequestMethod.POST)
	public void add(@RequestParam("name") String name, HttpServletRequest request, HttpServletResponse response) {
		try{			
	        Session session = driver.session();
	        session.run( "CREATE (三路漫:CaoTest{name: {name}, title: {title}, age:{age}, sex:{sex}})",
	                parameters( "name", name, "title", "皇帝", "age", "100", "sex","神仙" ) );
	        session.close();
	        driver.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
    
    @RequestMapping(value = "/query", method=RequestMethod.GET)
	public JSONObject query(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject object = new JSONObject();
		try{			
	        Session session = driver.session();
	        session.run( "CREATE (三路漫:CaoTest{name: {name}, title: {title}, age:{age}, sex:{sex}})",
	                parameters( "name", "三路漫", "title", "皇帝", "age", "100", "sex","神仙" ) );
	        session.run( "CREATE (巫师:CaoTest{name: {name}, title: {title}, age:{age}, sex:{sex}})",
	                parameters( "name", "巫师", "title", "捉妖记", "age", "1010", "sex","牛鬼蛇神" ) );
	        
	        StatementResult SpecifiedNodes = session.run( "MATCH (a:CaoTest) WHERE a.name = {name} " +
                    "RETURN a.name AS name, a.title AS title, a.sex AS sex",
                    parameters( "name", "巫师" ) );
	        while ( SpecifiedNodes.hasNext() ) {
	            Record record = SpecifiedNodes.next();
	            System.out.println( record.get( "title" ).asString() + " " + record.get( "name" ).asString() + " " + record.get( "sex" ).asString() );
	        }
	        object.put("node1", SpecifiedNodes);
	        StatementResult allNodes = session.run( "MATCH (a:CaoTest)  " +
                    "RETURN a.name AS name, a.title AS title, a.sex AS sex" );
	        while ( allNodes.hasNext() ) {
	            Record record = allNodes.next();
	            System.out.println( record.get( "title" ).asString() + " " + record.get( "name" ).asString() + " " + record.get( "sex" ).asString() );
	        }
	        object.put("node2", allNodes);
	        session.close();
	        driver.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return object;
    }
    
    @RequestMapping(value="/queryById", method=RequestMethod.GET)
    public void queryById(@RequestParam("id") String id) {
    	Session session = driver.session();
    	StatementResult statementResult = session.run("MATCH(n) where id(n)=" + id + " RETURN n");
    	while (statementResult.hasNext()) {
    		Record record = statementResult.next();
			System.out.println(record.toString());
		}
    }
    
    @RequestMapping(value = "/deteleByName", method=RequestMethod.DELETE)
	public void deteleByName(@RequestParam("name") String name, HttpServletRequest request, HttpServletResponse response) {
		try{			
	        Session session = driver.session();
	        StatementResult SpecifiedNodes = session.run( "MATCH (a:CaoTest) WHERE a.name = {name} " +
                    " Delete a ",
                    parameters( "name", name) );
	        while ( SpecifiedNodes.hasNext() ) {
	            Record record = SpecifiedNodes.next();
	            System.out.println( record.get( "title" ).asString() + " " + record.get( "name" ).asString() + " " + record.get( "sex" ).asString() );
	        }
	        session.close();
	        driver.close();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    
    @RequestMapping(value = "deleteById", method=RequestMethod.DELETE)
	public void deleteById(HttpServletRequest request, HttpServletResponse response, @RequestBody Code code) {
		try{			
	        Session session = driver.session();
	        session.run( "match (n) where ID(n) = " + code.getId() +" detach delete n");
	        session.close();
	        driver.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    
    @RequestMapping(value = "/relate", method=RequestMethod.POST)
	public void relate(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Code code) {
		try{
	        Session session = driver.session();
	        session.run("MATCH (a:" + code.getNodeFromLabel() + "), (b:" + code.getNodeToLabel() + ") " +
	        		"WHERE ID(a) = " + code.getNodeFromId() + " AND ID(b) = " + code.getNodeToId()
	        		+ " CREATE (a)-[:" + code.getRelation() + "]->(b)");
	        session.close();
	        driver.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    
    @RequestMapping(value = "update", method=RequestMethod.PUT)
	public void update(HttpServletRequest request, HttpServletResponse response, @RequestBody Code code) {
		try{	
	        Session session = driver.session();
	        StatementResult result = session.run("MATCH (a:" + code.getLabel() + ") WHERE a." + code.getWhere() + " SET a." + code.getUpdate() + " return COUNT(a)");
	        while (result.hasNext()) {
	            Record record = result.next();
	            System.out.println(record.fields().get(0).value().toString());
	        }
	        session.close();
	        driver.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    
    /**
     * 查询共同乘机人关系中航班号为CZ3137的所有人
     */
    @RequestMapping(value = "/sameRelationsSameAttr", method=RequestMethod.GET)
    public void getFromSameRelationshipAndSpecifiedAttribute() {
    	String cql = "MATCH (a:UserFlight)-[:共同乘机人]->(m)<-[:共同乘机人]-(b:UserFlight) \r\n" + 
    			"where a.flightNumber=\"CZ3137\" and b.flightNumber=\"CZ3137\"\r\n" + 
    			"return a,m,b\r\n" + 
    			"limit 100";
    	try{			
	        Session session = driver.session();
	        StatementResult result = session.run(cql);
	        
	        List<Record> records = result.list();
	        for (int i = 0; i < records.size(); i++) {
	        	Record record = records.get(i);
	        	System.out.println(record.toString());
	        	Map<String, Object> map = record.asMap();
	        	System.out.println(map.toString());

	        }
	        session.close();
	        driver.close();
		}catch(Exception e){
			e.printStackTrace();
		}
    	
    }
    
    @RequestMapping(value = "deleteRelByName", method=RequestMethod.DELETE)
    public void deleteRelationshiopByRelationshipName() {
    	String cql = "Match p=()-[r:属于蜀国的人]-() Delete r";
    	try{	
	        Session session = driver.session();
	        StatementResult result = session.run(cql);
	        while (result.hasNext()) {
	            Record record = result.next();
	            System.out.println(record.fields().get(0).value().toString());
	        }
	        session.close();
	        driver.close();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    
    
    @RequestMapping(value = "findRelByName", method=RequestMethod.DELETE)
    public void findRelationshiopByRelationshipName() {
    	String cql = "MATCH p=()-[r:蜀国人物]->() RETURN p";
    	try{	
	        Session session = driver.session();
	        StatementResult result = session.run(cql);
	        while (result.hasNext()) {
	            Record record = result.next();
	            System.out.println(record.fields().get(0).value().toString());
	        }
	        session.close();
	        driver.close();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    
    @RequestMapping(value = "/tempTest", method=RequestMethod.POST)
	public void tempTest(@RequestParam("name") String name) {
    	String cql = "create  (civilAviation0:civilAviation{departureTime:\"10:50:00\",estimatedTime:\"13:35:00\",name:\"3U8212\"}) , (civilAviation1:civilAviation{departureTime:\"06:40:00\",estimatedTime:\"09:00:00\",name:\"CZ3137\"}) , (civilAviation2:civilAviation{departureTime:\"15:55:00\",estimatedTime:\"19:20:00\",name:\"HU7819\"}) , (UserFlight0:UserFlight{name:\"韩妍\",id_card:\"610302198803057972\",flightNumber:\"CZ3137\"}) , (UserFlight1:UserFlight{name:\"华二丫\",id_card:\"610117199012127904\",flightNumber:\"CZ3137\"}) , (UserFlight2:UserFlight{name:\"孔小蝶\",id_card:\"610112199408094028\",flightNumber:\"CZ3137\"}) ,  (UserFlight16:UserFlight{name:\"朱静\",id_card:\"610113197207127749\",flightNumber:\"HU7819\"}) , (UserFlight17:UserFlight{name:\"魏可\",id_card:\"610113197207127431\",flightNumber:\"KY8253\"}) , (UserFlight18:UserFlight{name:\"陈美丽\",id_card:\"610115198501010373\",flightNumber:\"CZ3137\"}) , (UserFlight19:UserFlight{name:\"吴大秀\",id_card:\"610101198301018261\",flightNumber:\"CZ3137\"}) , (UserFlight20:UserFlight{name:\"华沛文\",id_card:\"610115198501012700\",flightNumber:\"3U8212\"}) , (UserFlight25:UserFlight{name:\"许元珊\",id_card:\"610302198803058434\",flightNumber:\"3U8212\"}) , (UserFlight29:UserFlight{name:\"孔敏\",id_card:\"610113197207128405\",flightNumber:\"SC9883\"})   WITH true as pass MATCH (s:civilAviation),(e:UserFlight) WHERE s.name = e.flightNumber CREATE (s)-[r:乘客的航班关系]->(e) RETURN r ";
    	Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "root" ) );
    	Session session = driver.session();
    	try{			
    		StatementResult result = session.run(cql);
	        
	        List<Record> records = result.list();
	        for (int i = 0; i < records.size(); i++) {
	        	Record record = records.get(i);
	        	System.out.println(record.toString());
	        	Map<String, Object> map = record.asMap();
	        	System.out.println(map.toString());

	        }
	        /*    
	        ResultSummary summary = result.summary();
	        
	        StatementResult result2 = session.run("MATCH p=()-[r:`乘客的航班关系`]->() RETURN p LIMIT 25");
	        List<Record> records1 = result.list();
	        for (int i = 0; i < records1.size(); i++) {
	        	Record record = records1.get(i);
	        	System.out.println(record.toString());
	        	Map<String, Object> map = record.asMap();
	        	System.out.println(map.toString());

	        }
	        
	        ResultSummary summary1 = result.summary();*/
	        
	        StatementResult result3 = session.run("call db.relationshiptypes");
	        List<Record> records2 = result3.list();
	        for (int i = 0; i < records2.size(); i++) {
	        	Record record = records2.get(i);
	        	System.out.println(record.toString());
	        	Map<String, Object> map = record.asMap();
	        	System.out.println(map.toString());
	        }
	        
	        
	        session.close();
	        driver.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
 
}
