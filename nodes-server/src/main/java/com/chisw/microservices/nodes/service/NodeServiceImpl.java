package com.chisw.microservices.nodes.service;

import com.chisw.microservices.nodes.exception.NoNodeContentException;
import com.chisw.microservices.nodes.exception.NodeAlreadyExistsException;
import com.chisw.microservices.nodes.exception.NodeNotFoundException;
import com.chisw.microservices.nodes.exception.RootAlreadyExistsException;
import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import com.chisw.microservices.nodes.persistence.jpa.repository.NodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.chisw.microservices.nodes.persistence.jpa.specification.NodeSpecifications.*;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional
public class NodeServiceImpl implements NodeService {

    private static final String DESCENDANTS_REGEX = "*.%s.*";
    private static final Logger LOG = LoggerFactory.getLogger(NodeServiceImpl.class);
    private static final String SPLIT_ANCESTORS_REGEX = "\\.";

    private NodeRepository nodeRepository;
    private EntityManager entityManager;

    @Autowired
    public NodeServiceImpl(NodeRepository nodeRepository, EntityManager entityManager) {

        this.nodeRepository = checkNotNull(nodeRepository);
        this.entityManager = checkNotNull(entityManager);
    }

    @Override
    public Page<Node> getAncestors(String id, Pageable page) {

        checkNotNull(id);
        checkNotNull(page);

        getIfExistsOrThrow(id);

        LOG.info("getAncestors({},{}))", id, page);

        return nodeRepository.findAll(nodeAncestors(id), page);

    }

    /**
     * This method is a lightweight alternative to {@link NodeServiceImpl#getAncestors}.
     * It fetches only ancestors ids using {@link Node#path} instead of performing heavy query
     * that fetches actual node table rows
     */
    @Override
    public Page<String> getAncestorsIds(String id, Pageable pageable) {

        checkNotNull(id);
        checkNotNull(pageable);

        Node node = getIfExistsOrThrow(id);

        List<String> ancestors = asList(node.getPath().split(SPLIT_ANCESTORS_REGEX));

        if (pageable.getOffset() > ancestors.size()) {
            return new PageImpl<>(emptyList());
        }

        return new PageImpl<>(ancestors.subList(
                pageable.getOffset(),
                min(pageable.getOffset() + pageable.getPageSize(), ancestors.size())
        ));

    }

    @Override
    public Page<Node> getDescendants(String id, Pageable pageable) {

        checkNotNull(id);

        checkNotNull(pageable);

        getIfExistsOrThrow(id);

        LOG.info("getDescendants({},{}))", id, pageable);

        return nodeRepository.findAll(nodeDescendants(format(DESCENDANTS_REGEX, id)), pageable);
    }


    @Override
    public Node findOrCreate(String id, String parentId) {

        checkNotNull(id);
        checkNotNull(parentId);

        LOG.info("findOrCreate({},{}))", id, parentId);

        Node node = nodeRepository.findOne(id);

        if (node == null) {

            LOG.info("Creating Node(id={}, parentId={})", id, parentId);

            String pathToNode;

            //trying to create a root but root exists
            if (id.equals(parentId) && rootExists()) {
                LOG.info("Cant't create root Node with id = {}: root already exists", parentId);
                throw new RootAlreadyExistsException();

            }
            //trying to create a root and root does not exist
            else if (id.equals(parentId)) {
                pathToNode = id;
            }
            //trying to create a simple node
            else {
                pathToNode = getChildPath(getIfExistsOrThrow(parentId).getPath(), id);
            }

            node = new Node();
            node.setId(id);
            node.setPath(pathToNode);

            nodeRepository.save(node);

            LOG.info("Node(id={}, parentId={}) created", id, parentId);

        } else {

            LOG.info("Node with id = {} already exists", id);
        }

        return node;
    }

    @Override
    public Node update(String id, String newId) {

        checkNotNull(id);
        checkNotNull(newId);

        LOG.info("update({},{}))", id, newId);

        throwIfExists(newId);

        Node node = getIfExistsOrThrow(id);

        String originalPath = node.getPath();
        String newPath = originalPath.replace(node.getId(), newId);

        LOG.info("Updating node with id = {}, path = {} to newId = {}, newPath = {}", id, originalPath, newId, newPath);

        //detach Node as long as id is about to be changed
        entityManager.detach(node);

        nodeRepository.updateNodeId(originalPath, newPath, id, newId);

        node.setId(newId);
        node.setPath(newPath);

        LOG.info("Node updated");

        return node;
    }

    @Override
    public Node deleteBranch(String parentId) {

        checkNotNull(parentId);

        LOG.info("deleteBranch({}))", parentId);

        //return 204 and not 404 when there is no node
        Node targetNode = Optional.ofNullable(nodeRepository.findOne(parentId))
                .orElseThrow(NoNodeContentException::new);

        LOG.info("Deleting branch with parent node with id = {}", parentId);

        Set<String> descendantsIds = nodeRepository.findAll(nodeDescendants(format(DESCENDANTS_REGEX, parentId)))
                .stream()
                .map(Node::getId)
                .collect(toSet());

        nodeRepository.deleteByNodeIds(descendantsIds);

        LOG.info("Branch deleted");

        return targetNode;
    }

    @Override
    public Node getById(String id) {

        checkNotNull(id);

        LOG.info("getById({}))", id);

        return getIfExistsOrThrow(id);
    }

    private boolean rootExists() {

        return nodeRepository.count(root()) != 0L;
    }

    private Node getIfExistsOrThrow(String id) {

        return Optional
                .ofNullable(nodeRepository.findOne(id))
                .orElseThrow(() -> new NodeNotFoundException(id));
    }

    private void throwIfExists(String id) {

        if (nodeRepository.findOne(id) != null) {
            throw new NodeAlreadyExistsException(id);
        }
    }

    private String getChildPath(String parentPath, String childId) {

        return parentPath + "." + childId;
    }
}
