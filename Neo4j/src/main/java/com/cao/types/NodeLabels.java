package com.cao.types;

import org.neo4j.graphdb.Label;

/**
 * 节点标签
 */
public enum NodeLabels implements Label {
    NODE,
    USER,
    MOVIE;
}
