package com.chisw.microservices.nodes.service;

import com.chisw.microservices.nodes.config.IntegrationTestsConfiguration;
import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import com.chisw.microservices.nodes.persistence.jpa.repository.NodeRepository;
import com.chisw.microservices.nodes.service.NodeService;
import org.apache.commons.collections.IteratorUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.chisw.microservices.nodes.testutil.TestUtils.assertNodeEquals;
import static com.chisw.microservices.nodes.testutil.TestUtils.node;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * Integration test for {@link NodeService} which uses running embedded postgresql database
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestsConfiguration.class)
@TestPropertySource(locations = "classpath:nodes-server-test.yml")
@SuppressWarnings("unchecked")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NodeServiceImplIntegrationTest {


    private static final String POPULATE_DATA_SCRIPT = "classpath:add-test-data.sql";
    private static final String DELETE_DATA_SCRIPT = "classpath:delete-test-data.sql";
    private static final String CREATE_SCHEMA_SCRIPT = "classpath:create-test-schema.sql";

    @Autowired
    private NodeService nodeService;

    @Autowired
    private NodeRepository nodeRepository;


    //this test's method name should be the first lexicographically among all other tests's names
    //because this test is dedicated to create schema before other tests are executed
    @Test
    @Sql({CREATE_SCHEMA_SCRIPT})
    public void createSchema() {
        //this test exists only to create schema
    }


    @Test
    @Sql({DELETE_DATA_SCRIPT, POPULATE_DATA_SCRIPT})
    public void getDescendants_success() throws Exception {

        Page<Node> firstPage = nodeService.getDescendants("1", new PageRequest(0, 2));
        Page<Node> secondPage = nodeService.getDescendants("1", new PageRequest(1, 2));

        List<Node> nodesSorted = Stream.of(firstPage.getContent(), secondPage.getContent())
                .flatMap(Collection::stream)
                .sorted(comparing(Node::getPath))
                .collect(toList());

        assertEquals(3, nodesSorted.size());
        assertNodeEquals(node("1", "1"), nodesSorted.get(0));
        assertNodeEquals(node("2", "1.2"), nodesSorted.get(1));
        assertNodeEquals(node("3", "1.3"), nodesSorted.get(2));

    }

    @Test
    @Sql({DELETE_DATA_SCRIPT, POPULATE_DATA_SCRIPT})
    public void getAncestors_success() throws Exception {

        Page<Node> firstPage = nodeService.getAncestors("2", new PageRequest(0, 1));
        Page<Node> secondPage = nodeService.getAncestors("2", new PageRequest(1, 1));

        List<Node> nodesSorted = Stream.of(firstPage.getContent(), secondPage.getContent())
                .flatMap(Collection::stream)
                .sorted(comparing(Node::getPath))
                .collect(toList());

        assertEquals(2, nodesSorted.size());
        assertNodeEquals(node("1", "1"), nodesSorted.get(0));
        assertNodeEquals(node("2", "1.2"), nodesSorted.get(1));
    }


    //       1        5
    //      / \  =>  / \
    //     2  3     2   3
    @Test
    @Sql({DELETE_DATA_SCRIPT, POPULATE_DATA_SCRIPT})
    public void updateNodeId_success() throws Exception {

        nodeService.update("1", "5");

        List<Node> nodes = IteratorUtils.toList(nodeRepository.findAll().iterator());

        assertEquals(3, nodes.size());

        nodes.sort(comparing(Node::getPath));

        assertNodeEquals(node("5", "5"), nodes.get(0));
        assertNodeEquals(node("2", "5.2"), nodes.get(1));
        assertNodeEquals(node("3", "5.3"), nodes.get(2));

    }


    //       1        1
    //      / \  =>  / \
    //     2  3     2   3
    //                  \
    //                   4
    @Test
    @Sql({DELETE_DATA_SCRIPT, POPULATE_DATA_SCRIPT})
    public void findOrCreate_success() throws Exception {

        nodeService.findOrCreate("4", "3");

        List<Node> nodes = IteratorUtils.toList(nodeRepository.findAll().iterator());

        assertEquals(4, nodes.size());

        nodes.sort(comparing(Node::getPath));

        assertNodeEquals(node("1", "1"), nodes.get(0));
        assertNodeEquals(node("2", "1.2"), nodes.get(1));
        assertNodeEquals(node("3", "1.3"), nodes.get(2));
        assertNodeEquals(node("4", "1.3.4"), nodes.get(3));

    }

    @Test
    @Sql({DELETE_DATA_SCRIPT, POPULATE_DATA_SCRIPT})
    public void findOrCreate_already_exists() throws Exception {

        nodeService.findOrCreate("3", "1");

        List<Node> nodes = IteratorUtils.toList(nodeRepository.findAll().iterator());

        assertEquals(3, nodes.size());

        nodes.sort(comparing(Node::getPath));

        assertNodeEquals(node("1", "1"), nodes.get(0));
        assertNodeEquals(node("2", "1.2"), nodes.get(1));
        assertNodeEquals(node("3", "1.3"), nodes.get(2));
    }

    //         1        1
    //        / \  =>    \
    //       2  3         3
    //      / \
    //     4   5
    @Test
    @Sql({DELETE_DATA_SCRIPT, POPULATE_DATA_SCRIPT})
    public void delete_branch_success() throws Exception {

        //add new children 4 and 5 to 2 parent node
        nodeRepository.save(node("4", "1.2.4"));
        nodeRepository.save(node("5", "1.2.5"));


        nodeService.deleteBranch("2");

        List<Node> nodes = IteratorUtils.toList(nodeRepository.findAll().iterator());

        assertEquals(2, nodes.size());

        nodes.sort(comparing(Node::getPath));

        assertNodeEquals(node("1", "1"), nodes.get(0));
        assertNodeEquals(node("3", "1.3"), nodes.get(1));


    }

    @Test
    @Sql({DELETE_DATA_SCRIPT, POPULATE_DATA_SCRIPT})
    public void getById_success() throws Exception {

        assertNodeEquals(node("1", "1"), nodeService.getById("1"));
        assertNodeEquals(node("2", "1.2"), nodeService.getById("2"));
        assertNodeEquals(node("3", "1.3"), nodeService.getById("3"));

    }

}