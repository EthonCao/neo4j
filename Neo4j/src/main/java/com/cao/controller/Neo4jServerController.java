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
    
    @RequestMapping(value = "deleteById")
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
    
    @RequestMapping(value = "update")
	public void update(HttpServletRequest request, HttpServletResponse response, @RequestBody Code code) {
		try{	
	        Session session = driver.session();
	        
	        StatementResult result = session.run("MATCH (a:" + code.getLabel() + ") WHERE a." + code.getWhere() + " SET a." + code.getUpdate() + " return COUNT(a)");
	        
	        while (result.hasNext())
	        {
	            Record record = result.next();
	            System.out.println(record.fields().get(0).value().toString());
	        }
	        
	        session.close();
	        driver.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
 
}
