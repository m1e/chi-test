package com.chisw.microservices.nodes.service;

import com.chisw.microservices.nodes.exception.NoNodeContentException;
import com.chisw.microservices.nodes.exception.NodeAlreadyExistsException;
import com.chisw.microservices.nodes.exception.NodeNotFoundException;
import com.chisw.microservices.nodes.exception.RootAlreadyExistsException;
import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import com.chisw.microservices.nodes.persistence.jpa.repository.NodeRepository;
import com.chisw.microservices.nodes.service.NodeService;
import com.chisw.microservices.nodes.service.NodeServiceImpl;
import com.chisw.microservices.nodes.testutil.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import java.util.List;

import static com.chisw.microservices.nodes.testutil.TestUtils.*;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
@SuppressWarnings("unchecked")
public class NodeServiceImplUnitTest {

    private NodeRepository nodeRepository;
    private NodeService nodeService;
    private EntityManager entityManager;


    @Before
    public void init() {

        nodeRepository = mock(NodeRepository.class);
        entityManager = mock(EntityManager.class);
        nodeService = new NodeServiceImpl(nodeRepository, entityManager);
    }


    @Test
    public void getById_success() {

        Node node = node("id", "path");

        when(nodeRepository.findOne(eq(node.getId()))).thenReturn(node);

        Node result = nodeService.getById(node.getId());

        verify(nodeRepository, times(1)).findOne(eq("id"));

        assertNodeEquals(node, result);
    }

    @Test(expected = NodeNotFoundException.class)
    public void getById_not_found() {

        Node node = node("id", "path");

        when(nodeRepository.findOne(eq(node.getId()))).thenReturn(null);

        nodeService.getById(node.getId());

        verify(nodeRepository, times(1)).findOne(eq("id"));

    }

    @Test
    public void getAncestors_success() throws Exception {

        String id = "id";
        Pageable page = mock(Pageable.class);

        Node nodeOne = node("1", "1");
        Node nodeTwo = node("2", "2");

        when(nodeRepository.findOne(eq(id))).thenReturn(nodeOne);

        when(nodeRepository.findAll(any(), eq(page)))
                .thenReturn(new PageImpl<>(asList(nodeOne, nodeTwo)));

        Page<Node> result = nodeService.getAncestors(id, page);

        verify(nodeRepository, times(1)).findOne(eq(id));
        verify(nodeRepository, times(1)).findAll(any(), eq(page));

        List<Node> resultingNodes = result.getContent();

        assertNodeEquals(resultingNodes.get(0), nodeOne);
        assertNodeEquals(resultingNodes.get(1), nodeTwo);
    }

    @Test(expected = NodeNotFoundException.class)
    public void getAncestors_node_not_found() throws Exception {

        String id = "id";
        Pageable page = mock(Pageable.class);

        when(nodeRepository.findOne(eq(id))).thenReturn(null);

        nodeService.getAncestors(id, page);

    }

    @Test
    public void getDescendants() throws Exception {

        String id = "id";
        Pageable page = mock(Pageable.class);

        Node nodeOne = node("1", "1");
        Node nodeTwo = node("2", "2");

        when(nodeRepository.findOne(eq(id))).thenReturn(nodeOne);

        when(nodeRepository.findAll(any(), eq(page)))
                .thenReturn(new PageImpl<>(asList(nodeOne, nodeTwo)));

        Page<Node> result = nodeService.getDescendants(id, page);

        verify(nodeRepository, times(1)).findOne(eq(id));
        verify(nodeRepository, times(1)).findAll(any(), eq(page));

        List<Node> resultingNodes = result.getContent();

        assertNodeEquals(resultingNodes.get(0), nodeOne);
        assertNodeEquals(resultingNodes.get(1), nodeTwo);
    }

    @Test(expected = NodeNotFoundException.class)
    public void getDescendants_node_not_found() throws Exception {

        String id = "id";
        Pageable page = mock(Pageable.class);

        when(nodeRepository.findOne(eq(id))).thenReturn(null);

        nodeService.getDescendants(id, page);

    }

    @Test(expected = NodeNotFoundException.class)
    public void findOrCreate_node_not_found() throws Exception {

        String id = "id";
        String parentId = "parentId";

        when(nodeRepository.findOne(eq(id))).thenReturn(null);

        nodeService.findOrCreate(id, parentId);
    }

    @Test(expected = NodeNotFoundException.class)
    public void findOrCreate_parent_node_not_found() throws Exception {

        String id = "id";
        String parentId = "parentId";

        when(nodeRepository.findOne(eq(parentId))).thenReturn(null);

        nodeService.findOrCreate(id, parentId);
    }


    @Test
    public void findOrCreate_node_already_exists() throws Exception {

        Node node = node("id", "path");
        Node parentNode = node("parentId", "parentPath");

        when(nodeRepository.findOne(eq(node.getId()))).thenReturn(node);

        Node result = nodeService.findOrCreate(node.getId(), parentNode.getId());

        verify(nodeRepository, times(1)).findOne(anyString());

        assertNodeEquals(node, result);
    }


    @Test(expected = RootAlreadyExistsException.class)
    public void findOrCreate_root_already_exists() throws Exception {

        Node node = node("id", "id");

        when(nodeRepository.count(any(Specification.class))).thenReturn(1L);

        nodeService.findOrCreate(node.getId(), node.getPath());

    }


    @Test
    public void findOrCreate_node_created_success() throws Exception {

        Node node = node("id", "path");
        Node parentNode = node("parentId", "parentPath");

        when(nodeRepository.findOne(eq(node.getId()))).thenReturn(null);
        when(nodeRepository.findOne(eq(parentNode.getId()))).thenReturn(parentNode);

        Node result = nodeService.findOrCreate(node.getId(), parentNode.getId());

        verify(nodeRepository, times(1)).findOne(eq(node.getId()));
        verify(nodeRepository, times(1)).findOne(eq(parentNode.getId()));


        assertNodeEquals(node(node.getId(), parentNode.getPath() + "." + node.getId()), result);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteBranch() throws Exception {

        Node firstNode = node("1", "1");
        Node secondNode = node("2", "2");

        when(nodeRepository.findOne(eq(firstNode.getId()))).thenReturn(firstNode);

        when(nodeRepository.findAll(any(Specification.class))).thenReturn(asList(firstNode, secondNode));

        doNothing().when(nodeRepository).deleteByNodeIds(set(firstNode.getId(), secondNode.getId()));

        nodeService.deleteBranch(firstNode.getId());

        verify(nodeRepository, times(1)).findOne(eq(firstNode.getId()));
        verify(nodeRepository, times(1)).findAll(any(Specification.class));
        verify(nodeRepository, times(1)).deleteByNodeIds(eq(set(firstNode.getId(), secondNode.getId())));

    }

    @Test(expected = NoNodeContentException.class)
    public void deleteBranch_node_not_found() throws Exception {

        String id = "parentId";

        when(nodeRepository.findOne(eq(id))).thenReturn(null);

        nodeService.deleteBranch(id);
    }


    @Test(expected = NodeAlreadyExistsException.class)
    public void update_node_with_new_id_already_exists() throws Exception {

        String id = "id";
        String newId = "newId";

        when(nodeRepository.findOne(eq(newId))).thenReturn(mock(Node.class));

        nodeService.update(id, newId);
    }

    @Test(expected = NodeNotFoundException.class)
    public void update_target_node_does_not_exist() throws Exception {

        String id = "id";
        String newId = "newId";

        when(nodeRepository.findOne(eq(id))).thenReturn(null);

        nodeService.update(id, newId);
    }

    @Test
    public void update_success() throws Exception {

        Node targetImmutable = node("1", "0.1");
        Node target = node("1", "0.1");
        Node expected = node("newId", "0.newId");

        when(nodeRepository.findOne(eq(target.getId()))).thenReturn(target);
        when(nodeRepository.findOne(eq(expected.getId()))).thenReturn(null);

        doNothing().when(entityManager).detach(any());
        doNothing().when(nodeRepository).updateNodeId(any(), any(), any(), any());

        Node resulting = nodeService.update(target.getId(), expected.getId());

        verify(nodeRepository, times(1)).findOne(eq(targetImmutable.getId()));
        verify(nodeRepository, times(1)).findOne(eq(expected.getId()));
        verify(entityManager, times(1)).detach(eq(target));
        verify(nodeRepository, times(1))
                .updateNodeId(
                        eq(targetImmutable.getPath()),
                        eq(expected.getPath()),
                        eq(targetImmutable.getId()),
                        eq(expected.getId())
                );


        assertNodeEquals(expected, resulting);


    }


}