package com.ramiro.operacionfuego.services;

import com.ramiro.operacionfuego.OperacionFuegoConstants;
import com.ramiro.operacionfuego.model.SecretServiceRequest;
import com.ramiro.operacionfuego.model.SecretServiceRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MessageSplitService {

    @Autowired
    private SatelliteService satelliteService;

    private HashMap<String, SecretServiceRequest> actualVaues;

    public HashMap<String, SecretServiceRequest> getAllVaues() {
        return  this.actualVaues;
    }

    public void resetValues(){
        if(this.actualVaues != null){
            this.actualVaues.clear();
        }
    }

    public SecretServiceRequestWrapper getActualVaues() throws Exception {
        if(actualVaues == null || actualVaues.isEmpty() || actualVaues.containsValue(null)){
            throw new Exception(OperacionFuegoConstants.ERROR_NOT_ENOUGH_INPUT_DATA +
                    satelliteService.getSatellitesOnService().size());
        }
        SecretServiceRequestWrapper wrapper = new SecretServiceRequestWrapper();
        wrapper.setSatellites(new ArrayList<>(actualVaues.values()));
        this.resetValues();
        return wrapper;
    }

    public void addRequestToValues(SecretServiceRequest request) throws Exception {
        if(actualVaues == null){
            actualVaues = new HashMap<>();
        }
        if(actualVaues.isEmpty()){
            satelliteService.getSatellitesOnService().stream().forEach(satellite -> {
                actualVaues.put(satellite.getName(), null);
            });
        }
        if(!actualVaues.containsKey(request.getName())){
            throw new Exception(OperacionFuegoConstants.ERROR_NO_SATELLITES_ON_SERVICE +
                    satelliteService.getSatellitesOnServiceNames());
        } else {
            actualVaues.replace(request.getName(), request);
        }
    }
}
