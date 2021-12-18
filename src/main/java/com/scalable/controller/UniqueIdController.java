package com.scalable.controller;

import com.scalable.service.UniqueIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class UniqueIdController {

  @Autowired
  UniqueIdService service;

  @RequestMapping(value = "/id", method = RequestMethod.GET, produces = "application/json")
  public long getId() {
    return service.nextId();
  }
}
