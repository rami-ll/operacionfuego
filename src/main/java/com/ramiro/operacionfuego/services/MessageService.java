package com.ramiro.operacionfuego.services;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import com.ramiro.operacionfuego.OperacionFuegoConstants;
import com.ramiro.operacionfuego.model.ErrorResponse;
import com.ramiro.operacionfuego.model.Position;
import com.ramiro.operacionfuego.model.SecretServiceRequest;
import com.ramiro.operacionfuego.model.SecretServiceRequestWrapper;
import com.ramiro.operacionfuego.model.SecretServiceResponse;
import com.ramiro.operacionfuego.utils.OperacionFuegoUtils;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class MessageService {

    @Autowired
    SatelliteService satelliteService;
    @Autowired
    MessageDecoderService decoderService;
    @Autowired
    OperacionFuegoUtils utils;

    public ResponseEntity processRequest (SecretServiceRequestWrapper inputInformation) throws Exception {
        SecretServiceResponse responseData = new SecretServiceResponse();
        Position position = getLocation(utils.getDistancesFromRequest(inputInformation.getSatellites()));
        String message = getMessage(utils.getMessagesFromRequest(inputInformation.getSatellites()));
        if(position != null && message != null){
            responseData.setMessage(message);
            responseData.setPosition(position);
            return ResponseEntity.status(HttpStatus.OK).body(responseData);
        } else {
            ErrorResponse response = new ErrorResponse("No fue posible procesar la solicitud", "404");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Se obtiene el mensaje original a partir de los mensajes entregados. Se entiende que existe un desfazaje en el inicio de
     * alguno o todos los mensajes
     * @param inputMessages
     * @return
     */

    public String getMessage(ArrayList<String[]> inputMessages) throws Exception {
            String[] message = decoderService.normalizeMessages(inputMessages);
            return utils.stringArrayToString(message, ' ');
    }

    /**
     * Calula la posision del emisior basado en las distancias enviadas. Las posiciones estan asociadas a
     * las distancias segun el request con su correspondiente satelite
     * @param positionDistances position linked to the distance
     * @return Posicion del emisor del mensaje
     */
    public Position getLocation(HashMap<double[], Double> positionDistances) throws Exception {
        if(positionDistances.size() < satelliteService.getSatellitesOnService().size()){
            throw new Exception(OperacionFuegoConstants.ERROR_NOT_ENOUGH_INPUT_DATA +
                    satelliteService.getSatellitesOnService().size());
        }
        TrilaterationFunction trilaterationFunction = new TrilaterationFunction(
                utils.getPositions(positionDistances),
                utils.getDistances(positionDistances)
        );
        NonLinearLeastSquaresSolver nonLinearSolver = new NonLinearLeastSquaresSolver(
                trilaterationFunction, new LevenbergMarquardtOptimizer());

        double[] emisorPosition = nonLinearSolver.solve().getPoint().toArray();
        return new Position(emisorPosition[0],emisorPosition[1]);
    }
}
