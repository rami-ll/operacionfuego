package com.ramiro.operacionfuego.services;
import com.ramiro.operacionfuego.model.Position;
import com.ramiro.operacionfuego.model.Satellite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Locale;

@Service
public class SatelliteService{

    private ArrayList<Satellite> satellitesOnService;

    @Autowired
    Environment environment;

    //Metodo solo usado para testear con satelites mock
    public void setSatellitesOnService(ArrayList<Satellite> satellites){
        this.satellitesOnService = satellites;
    }

    public ArrayList<Satellite> getSatellitesOnService() {
        if(this.satellitesOnService == null){
            this.satellitesOnService = new ArrayList<>();
            fillSatellitesList();
        }
        return this.satellitesOnService;
    }

    private void fillSatellitesList (){
        String[] satellitesNames = environment.getProperty("satellites").split(",");
        for(String sat : satellitesNames){
            String[] posStn = environment.getProperty("satellite." + sat).split(",");
            Position pos =  new Position(
                    Double.valueOf(posStn[0]),
                    Double.valueOf(posStn[1])
            );
            Satellite satellite = new Satellite(pos, sat.toLowerCase(Locale.ROOT));
            this.satellitesOnService.add(satellite);
        }
    }

    public String getSatellitesOnServiceNames(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Satellite sat : this.getSatellitesOnService()){
            sb.append(sat.getName());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }

}
