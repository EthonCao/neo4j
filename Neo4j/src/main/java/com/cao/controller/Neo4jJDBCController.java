package com.cao.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
public class Neo4jJDBCController {
	
	public static Connection getNeo4jConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:neo4j:bolt://localhost:7687/","neo4j", "root");
        return conn;
    }
	
	public static void main(String[] args) throws SQLException {
	    Connection conn = getNeo4jConnection();
	    try{
	        Statement statement = conn.createStatement();
	        ResultSet resultSet = statement.executeQuery("MATCH p=()-[r:蜀国人物]->() RETURN p");
	        while (resultSet.next()){
	            System.out.println(resultSet.getString(1));
	        }
	    }catch (Exception e){
	        e.printStackTrace();
	    }
	}
 
}
