package com.cao.types;

import org.neo4j.graphdb.RelationshipType;

/**
 * 关系类别
 */
public enum Relationships implements RelationshipType {
    ARROW,
    IS_FRIEND_OF,
    HAS_SEEN;
}
