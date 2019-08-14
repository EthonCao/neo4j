package com.cao.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4JDBManager {

	private static String neo4jDataBasePath;
	
	private GraphDatabaseService graphDatabaseDB;
	
	
	static {
		Properties props = new Properties();
		InputStream inputStream = Neo4JDBManager.class.getClassLoader().getResourceAsStream("application.properties");
		try {
			props.load(inputStream);
			neo4jDataBasePath = props.getProperty("neo4j.path");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	
	@Bean
	public GraphDatabaseService getDB() {
        if (graphDatabaseDB == null) {
            synchronized (Neo4JDBManager.class) {
                graphDatabaseDB = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File(neo4jDataBasePath))
                		.setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                		.setConfig(GraphDatabaseSettings.string_block_size, "60")
                		.setConfig(GraphDatabaseSettings.array_block_size, "300")
                		.newGraphDatabase();
                
                registerShutDownHook(graphDatabaseDB);
            }
        }
        return graphDatabaseDB;
    }
	
	private void registerShutDownHook(final GraphDatabaseService db) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                db.shutdown();
            }
        });
    }

	public GraphDatabaseService getGraphDatabaseDB() {
		return graphDatabaseDB;
	}

	public void setGraphDatabaseDB(GraphDatabaseService graphDatabaseDB) {
		this.graphDatabaseDB = graphDatabaseDB;
	}
	
	
	
}
