package com.chisw.microservices.nodes.web.controller;

import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import com.chisw.microservices.nodes.service.NodeService;
import com.chisw.microservices.nodes.web.dto.NodeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

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

    private static NodeDto toDto(Node node) {

        NodeDto dto = new NodeDto();
        dto.setId(node.getId());
        dto.setPath(node.getPath());
        return dto;
    }

    @RequestMapping(value = "/node/{id}", method = GET)
    @ResponseBody
    public NodeDto node(@PathVariable("id") String id) {

        logger.info(format("get /node/%s", id));

        return toDto(nodeService.getById(id));
    }

    @RequestMapping(value = "/node/{id}/ancestors", method = GET)
    @ResponseBody
    public Page<NodeDto> ancestors(@PathVariable("id") String id, Pageable pageable) {

        logger.info(format("get /node/%s/ancestors", id));

        return nodeService.getAncestors(id, pageable).map(NodeController::toDto);
    }

    @RequestMapping(value = "/node/{id}/descendants", method = GET)
    @ResponseBody
    public Page<NodeDto> descendants(@PathVariable("id") String id, Pageable pageable) {

        logger.info(format("get /node/%s/descendants", id));

        return nodeService.getDescendants(id, pageable).map(NodeController::toDto);
    }

    @RequestMapping(value = "/node", method = POST)
    @ResponseStatus(OK)
    @ResponseBody
    public NodeDto findOrCreate(@RequestBody NodeDto nodeDto) {

        logger.info(format("post /node \n %s", nodeDto));

        return toDto(nodeService.findOrCreate(nodeDto.getId(), nodeDto.getParentId()));
    }

    @RequestMapping(value = "/node/{id}", method = PUT)
    @ResponseStatus(OK)
    @ResponseBody
    public NodeDto update(@PathVariable String id, @RequestBody NodeDto nodeDto) {

        logger.info(format("put /node/%s \n %s", id, nodeDto));

        return toDto(nodeService.update(id, nodeDto.getId()));
    }

    @RequestMapping(value = "/node/{id}", method = DELETE)
    @ResponseStatus(OK)
    @ResponseBody
    public NodeDto deleteBranch(@PathVariable("id") String id) {

        logger.info(format("delete /node/%s", id));

        return toDto(nodeService.deleteBranch(id));
    }


}
