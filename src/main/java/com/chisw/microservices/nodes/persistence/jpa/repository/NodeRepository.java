package com.chisw.microservices.nodes.persistence.jpa.repository;

import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;


/**
 * Repository for {@link Node} data implemented using Spring Data JPA.
 */
public interface NodeRepository extends CrudRepository<Node, String>, JpaSpecificationExecutor<Node> {

    //This query updates node id along with all children references to the node
    //so that all children references point to the new id
    String UPDATE_NODE_ID =
            "  update nodes set path = (?2)\\:\\:ltree || \n" +
                    " case \n " +
                    "  when nlevel(path) > nlevel((?1)\\:\\:ltree ) then subpath(path, nlevel((?1)\\:\\:ltree ))\n " +
                    "  when nlevel(path) = nlevel((?1)\\:\\:ltree ) then '' \n " +
                    " end\n " +
                    " where path <@ (?1)\\:\\:ltree ;  \n " +
                    " update nodes set id = (?4) where id = (?3) \n ";

    @Modifying
    @Query("delete from Node where id in :ids")
    void deleteByNodeIds(@Param("ids") Set<String> ids);

    //This method updates id of a given parent Node along with all child nodes so that they refer to updated parent Node id
    //Implemented in native query, it gives better performance than similar operations via JPA
    @Modifying
    @Query(value = UPDATE_NODE_ID, nativeQuery = true)
    void updateNodeId(
            String oldPath,
            String newPath,
            String oldId,
            String newId
    );

}
