package com.ramiro.operacionfuego.services;


import com.ramiro.operacionfuego.OperacionFuegoConstants;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class MessageDecoderService {

    public String[]  normalizeMessages(ArrayList<String []> originalInput) throws Exception {
        int minLength = originalInput.stream().map(msg -> msg.length).min(Integer::compare).get();
        int offset;
        int missingWords = minLength;
        int msgSatQuantity = originalInput.size();
        String[] finalMessage = new String[minLength];
        Arrays.fill(finalMessage, "");
        for(int index = 0; index < minLength; index++){
            for(int actualMsg = 0; actualMsg<msgSatQuantity; actualMsg++){
                String[] actualMsgText = originalInput.get(actualMsg);
                offset = actualMsgText.length - minLength;
                if(actualMsgText[index+offset] != ""){
                    finalMessage[index] = actualMsgText[index+offset];
                    missingWords--;
                    if (missingWords == 0){
                        break;
                    }
                }
                if(actualMsg+1==msgSatQuantity && missingWords<minLength && finalMessage[index].equals("")){
                    //Ya se recorrieron 3 posiciones validas y no se econtro palabra
                    throw new Exception(OperacionFuegoConstants.MESSAGE_NOT_FOUND);
                }
            }
        }
        finalMessage = Arrays.stream(finalMessage).filter(val -> val != "").toArray(String[]::new);
        return finalMessage;
    }


}
