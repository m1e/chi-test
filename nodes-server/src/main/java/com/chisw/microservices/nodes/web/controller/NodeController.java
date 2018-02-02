package com.chisw.microservices.nodes.web.controller;

import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import com.chisw.microservices.nodes.service.NodeService;
import com.chisw.microservices.nodes.web.dto.NodeDto;
import com.chisw.microservices.nodes.web.dto.NodeResource;
import com.chisw.microservices.nodes.web.util.NodeWebResourceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

import static com.chisw.microservices.nodes.web.util.NodeWebResourceUtils.toResourceWithSelfLink;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * A RESTFul web for accessing node information.
 */
@RestController
public class NodeController {

    private Logger logger = Logger.getLogger(NodeController.class
            .getName());

    private NodeService nodeService;

    @Autowired
    public NodeController(NodeService nodeService) {

        this.nodeService = nodeService;
    }

    @RequestMapping(value = "/node/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<NodeResource> node(@PathVariable("id") String id) {

        logger.info(format("get /node/%s", id));

        Node node = nodeService.getById(id);
        ;

        return new ResponseEntity<>(toResourceWithSelfLink(node), OK);
    }

    @RequestMapping(value = "/node/{id}/ancestors", method = GET)
    @ResponseBody
    public Page<NodeResource> ancestors(@PathVariable("id") String id, Pageable pageable) {

        logger.info(format("get /node/%s/ancestors", id));

        return nodeService.getAncestors(id, pageable).map(NodeWebResourceUtils::toResourceWithSelfLink);
    }

    @RequestMapping(value = "/node/{id}/descendants", method = GET)
    @ResponseBody
    public Page<NodeResource> descendants(@PathVariable("id") String id, Pageable pageable) {

        logger.info(format("get /node/%s/descendants", id));

        return nodeService.getDescendants(id, pageable).map(NodeWebResourceUtils::toResourceWithSelfLink);
    }

    @RequestMapping(value = "/node", method = POST)
    @ResponseBody
    public ResponseEntity<NodeResource> findOrCreate(@RequestBody NodeDto nodeDto) {

        logger.info(format("post /node \n %s", nodeDto));

        Node node = nodeService.findOrCreate(nodeDto.getId(), nodeDto.getParentId());

        return new ResponseEntity<>(toResourceWithSelfLink(node), OK);
    }

    @RequestMapping(value = "/node/{id}", method = PUT)
    @ResponseBody
    public ResponseEntity<NodeResource> update(@PathVariable String id, @RequestBody NodeDto nodeDto) {

        logger.info(format("put /node/%s \n %s", id, nodeDto));

        Node node = nodeService.update(id, nodeDto.getId());

        return new ResponseEntity<>(toResourceWithSelfLink(node), OK);
    }

    @RequestMapping(value = "/node/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity<NodeResource> deleteBranch(@PathVariable("id") String id) {

        logger.info(format("delete /node/%s", id));

        Node node = nodeService.deleteBranch(id);

        return new ResponseEntity<>(toResourceWithSelfLink(node), OK);

    }
}