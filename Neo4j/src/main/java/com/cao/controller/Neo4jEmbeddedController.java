package com.cao.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cao.service.DBManager;

import io.swagger.annotations.Api;

/**
* <p>Title: Neo4jController</p>  
* <p>Description: </p>  
* @author Cao
* @date 2019年8月5日
 */
@Api(tags = "Neo4j访问方式: Java应用程序中的嵌入式数据库")
@Controller("neo4jEmbeddedController")
@RequestMapping("/neoEmbed")
@Scope("prototype")
public class Neo4jEmbeddedController {
	
	@Value("${neo4j.path}")
	private String neo4jDataBasePath;
	
	@RequestMapping(value="/createDB", method=RequestMethod.POST)
	public void createDataBase() {
			DBManager.getDB(neo4jDataBasePath);
	}
	
}
