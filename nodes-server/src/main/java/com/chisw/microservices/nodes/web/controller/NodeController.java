package com.chisw.microservices.nodes.web.controller;

import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import com.chisw.microservices.nodes.service.NodeService;
import com.chisw.microservices.nodes.web.dto.NodeDto;
import com.chisw.microservices.nodes.web.dto.NodeResource;
import com.chisw.microservices.nodes.web.util.NodeWebResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import static com.chisw.microservices.nodes.web.util.NodeWebResourceUtils.toResourceWithSelfLink;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * A RESTFul web for accessing node information.
 */
@RestController
public class NodeController {

    private static final Logger LOG = LoggerFactory.getLogger(NodeController.class);
    public static final String ID = "id";

    private NodeService nodeService;

    @Autowired
    public NodeController(NodeService nodeService) {

        this.nodeService = nodeService;
    }

    @RequestMapping(value = "/node/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<NodeResource> node(@PathVariable("id") String id) {

        LOG.info(format("get /node/%s", id));

        Node node = nodeService.getById(id);
        ;

        return new ResponseEntity<>(toResourceWithSelfLink(node), OK);
    }

    @RequestMapping(value = "/node/{id}/ancestors", method = GET)
    @ResponseBody
    public Page<NodeResource> ancestors(
            @PathVariable("id") String id,
            @RequestParam(value = "fields", required = false) String fields,
            Pageable pageable) {

        LOG.info(format("get /node/%s/ancestors", id));

        if(ID.equals(fields)) {
            return nodeService.getAncestorsIds(id, pageable).map(NodeWebResourceUtils::toResourceWithSelfLink);
        }

        return nodeService.getAncestors(id, pageable).map(NodeWebResourceUtils::toResourceWithSelfLink);
    }


    @RequestMapping(value = "/node/{id}/descendants", method = GET)
    @ResponseBody
    public Page<NodeResource> descendants(@PathVariable("id") String id, Pageable pageable) {

        LOG.info(format("get /node/%s/descendants", id));

        return nodeService.getDescendants(id, pageable).map(NodeWebResourceUtils::toResourceWithSelfLink);
    }

    @RequestMapping(value = "/node", method = POST)
    @ResponseBody
    public ResponseEntity<NodeResource> findOrCreate(@RequestBody NodeDto nodeDto) {

        LOG.info(format("post /node \n %s", nodeDto));

        Node node = nodeService.findOrCreate(nodeDto.getId(), nodeDto.getParentId());

        return new ResponseEntity<>(toResourceWithSelfLink(node), OK);
    }

    @RequestMapping(value = "/node/{id}", method = PUT)
    @ResponseBody
    public ResponseEntity<NodeResource> update(@PathVariable String id, @RequestBody NodeDto nodeDto) {

        LOG.info(format("put /node/%s \n %s", id, nodeDto));

        Node node = nodeService.update(id, nodeDto.getId());

        return new ResponseEntity<>(toResourceWithSelfLink(node), OK);
    }

    @RequestMapping(value = "/node/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity<NodeResource> deleteBranch(@PathVariable("id") String id) {

        LOG.info(format("delete /node/%s", id));

        Node node = nodeService.deleteBranch(id);

        return new ResponseEntity<>(toResourceWithSelfLink(node), OK);

    }
}
