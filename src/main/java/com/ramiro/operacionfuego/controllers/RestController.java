package com.ramiro.operacionfuego.controllers;

import com.ramiro.operacionfuego.model.ErrorResponse;
import com.ramiro.operacionfuego.model.Position;
import com.ramiro.operacionfuego.model.SecretServiceRequest;
import com.ramiro.operacionfuego.model.SecretServiceRequestWrapper;
import com.ramiro.operacionfuego.model.SecretServiceResponse;
import com.ramiro.operacionfuego.services.MessageService;
import com.ramiro.operacionfuego.services.MessageSplitService;
import com.ramiro.operacionfuego.utils.OperacionFuegoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Locale;

@Controller
public class RestController {

    @Autowired
    MessageService messageService;
    @Autowired
    MessageSplitService messageSplitService;
    @Autowired
    OperacionFuegoUtils utils;

    @RequestMapping(
            value = "/topsecret",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity topSecret(@RequestBody SecretServiceRequestWrapper inputInformation){
        try {
            return messageService.processRequest(inputInformation);
        } catch (Exception ex){
            ErrorResponse response = new ErrorResponse(ex.getMessage(), "404");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @RequestMapping(
            value = "/topsecret_split",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getSplitResult(){
        try {
            SecretServiceRequestWrapper requestSaved = messageSplitService.getActualVaues();
            return messageService.processRequest(requestSaved);
        } catch (Exception ex){
            ErrorResponse response = new ErrorResponse(ex.getMessage(), "404");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }

    @RequestMapping(
            value = "/topsecret_split/{sat_name}",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity addPartialMessage(@PathVariable String sat_name, @RequestBody SecretServiceRequest inputData){
        try{
            inputData.setName(sat_name.toLowerCase(Locale.ROOT));
            messageSplitService.addRequestToValues(inputData);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception ex){
            ErrorResponse response = new ErrorResponse(ex.getMessage(), "404");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


}
