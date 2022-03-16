package com.ramiro.operacionfuego.utils;

import com.ramiro.operacionfuego.OperacionFuegoConstants;
import com.ramiro.operacionfuego.model.Satellite;
import com.ramiro.operacionfuego.model.SecretServiceRequest;
import com.ramiro.operacionfuego.services.SatelliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class OperacionFuegoUtils {

    @Autowired
    private SatelliteService satelliteService;

    public String stringArrayToString(String[] arr, char concatChar){
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < arr.length; index++){
            sb.append(arr[index]);
            if(index+1!=arr.length){
                sb.append(concatChar);
            }
        }
        return sb.toString();
    }

    public HashMap<double[], Double> getDistancesFromRequest(ArrayList<SecretServiceRequest> incomingData) throws Exception {
        ArrayList<Satellite> satellites = satelliteService.getSatellitesOnService();
        HashMap<double[], Double> posDistance = new HashMap<>();
        for (SecretServiceRequest request: incomingData){
            Satellite satFound = satellites.stream().filter(sat ->
                    sat.getName().equals(request.getName().toLowerCase(Locale.ROOT))).findAny().orElse(null);
            if(satFound != null){
                double[] pos = new double[2];
                pos[0]= satFound.getPosition().getX();
                pos[1]= satFound.getPosition().getY();
                posDistance.put(pos, request.getDistance());
            } else {
                throw new Exception(OperacionFuegoConstants.ERROR_NO_SATELLITES_ON_SERVICE +
                        satelliteService.getSatellitesOnServiceNames());
            }
        }
        return posDistance;
    }

    public double[][] getPositions(HashMap<double[], Double> posDist){
        Set<double[]> posSet = posDist.keySet();
        double[][] positions = new double[posSet.size()][2];
        int index = 0;
        for(double[] pos : posSet){
            positions[index] = pos;
            index++;
        }
        return positions;
    }

    public double[] getDistances(HashMap<double[], Double> posDist){
        ArrayList<Double> distList = new ArrayList<>(posDist.values());
        double[] distances = new double[distList.size()];
        int index = 0;
        for(Double dist : distList){
            distances[index] = dist;
            index++;
        }
        return distances;
    }

    public ArrayList<String[]> getMessagesFromRequest(ArrayList<SecretServiceRequest> incomingData){
        ArrayList<String[]> messages = new ArrayList<>();
        for(int index = 0; index < incomingData.size(); index++){
            messages.add(incomingData.get(index).getMessage());
        }
        return messages;
    }
}
