package com.ramiro.operacionfuego;

import com.ramiro.operacionfuego.model.Position;
import com.ramiro.operacionfuego.model.Satellite;
import com.ramiro.operacionfuego.model.SecretServiceRequest;
import com.ramiro.operacionfuego.services.MessageDecoderService;
import com.ramiro.operacionfuego.services.MessageService;
import com.ramiro.operacionfuego.services.MessageSplitService;
import com.ramiro.operacionfuego.services.SatelliteService;
import com.ramiro.operacionfuego.utils.OperacionFuegoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.HashMap;

@SpringBootTest
class OperacionfuegoApplicationTests {

	@Autowired
	MessageDecoderService decoderService;
	@Autowired
	SatelliteService satelliteService;
	@Autowired
	MessageService messageService;
	@Autowired
	OperacionFuegoUtils utils;
	@Autowired
	Environment environment;
	@Autowired
	MessageSplitService messageSplitService;

	@BeforeEach
	void setUp(){
		satelliteService.setSatellitesOnService(null);
		messageSplitService.resetValues();
	}

	//main functions
	@Test
	void getValidMessageNoOffset() throws Exception{
		ArrayList<String[]> msgs = new ArrayList<>();
		String[] msg1 = {"este","","","mensaje",""};
		String[] msg2 = {"","es","","","secreto"};
		String[] msg3 = {"este","","un","",""};
		msgs.add(msg1);
		msgs.add(msg2);
		msgs.add(msg3);
		String[] finalMsg = decoderService.normalizeMessages(msgs);
		assert finalMsg.length == 5 && finalMsg[0].equals("este") && finalMsg[1].equals("es") &&
				finalMsg[2].equals("un") && finalMsg[3].equals("mensaje")
				&& finalMsg[4].equals("secreto");
	}

	@Test
	void getValidMessageMultipleOffset() throws Exception{
		ArrayList<String[]> msgs = new ArrayList<>();
		String[] msg1 = {"","","este","","","mensaje",""};
		String[] msg2 = {"","","","es","","","secreto"};
		String[] msg3 = {"","este","","un","",""};
		msgs.add(msg1);
		msgs.add(msg2);
		msgs.add(msg3);
		String[] finalMsg = decoderService.normalizeMessages(msgs);
		assert finalMsg.length == 5 && finalMsg[0].equals("este") && finalMsg[1].equals("es") &&
				finalMsg[2].equals("un") && finalMsg[3].equals("mensaje")
				&& finalMsg[4].equals("secreto");
	}

	@Test
	void getErrorInvalidMessage() {
		ArrayList<String[]> msgs = new ArrayList<>();
		String[] msg1 = {"","","este","","","mensaje",""};
		String[] msg2 = {"","","","es","","","secreto"};
		String[] msg3 = {"","este","","","",""};
		msgs.add(msg1);
		msgs.add(msg2);
		msgs.add(msg3);
		Exception ex = Assertions.assertThrows(Exception.class, () -> decoderService.normalizeMessages(msgs));
		assert ex.getMessage().equals(OperacionFuegoConstants.MESSAGE_NOT_FOUND);
	}

	@Test
	void getFinalMessage() throws Exception{
		ArrayList<String[]> msgs = new ArrayList<>();
		String[] msg1 = {"","","este","","","mensaje",""};
		String[] msg2 = {"","","","es","","","secreto"};
		String[] msg3 = {"","este","","un","",""};
		msgs.add(msg1);
		msgs.add(msg2);
		msgs.add(msg3);
		String finalMessage = messageService.getMessage(msgs);
		assert finalMessage.equals("este es un mensaje secreto");
	}

	@Test
	void getPositionFromDistances() throws Exception{
		HashMap<double[], Double> positionDistances = new HashMap<>();
		double[] pos1 = {1.0,1.0};
		double[] pos2 = {3.0,1.0};
		double[] pos3 = {2.0,2.0};
		positionDistances.put(pos1,1.0);
		positionDistances.put(pos2,1.0);
		positionDistances.put(pos3,1.0);
		Position posEmisor = messageService.getLocation(positionDistances);
		assert Double.compare(posEmisor.getX(), 2.0) == 0 && Double.compare(posEmisor.getY(), 1.0) == 0;
	}

	@Test
	void getErrorNotEnoughData() {
		HashMap<double[], Double> positionDistances = new HashMap<>();
		double[] pos1 = {1.0,1.0};
		double[] pos2 = {3.0,1.0};
		positionDistances.put(pos1,1.0);
		positionDistances.put(pos2,1.0);
		Exception ex = Assertions.assertThrows(Exception.class, () -> messageService.getLocation(positionDistances));
		assert ex.getMessage().equals(OperacionFuegoConstants.ERROR_NOT_ENOUGH_INPUT_DATA +
				satelliteService.getSatellitesOnService().size());
	}

	@Test
	void addPartialRequest() throws Exception{
		String[] msg = {"","algo","queda"};
		SecretServiceRequest request = new SecretServiceRequest(100.0, "sato", msg);
		messageSplitService.addRequestToValues(request);
		assert messageSplitService.getAllVaues().get(request.getName()).equals(request);
	}

	@Test
	void partialRequestForNonRegisteredSatellite(){
		String[] msg = {"","algo","queda"};
		SecretServiceRequest request = new SecretServiceRequest(100.0, "luke", msg);
		Exception ex = Assertions.assertThrows(Exception.class, () -> messageSplitService.addRequestToValues(request));
		assert ex.getMessage().equals(OperacionFuegoConstants.ERROR_NO_SATELLITES_ON_SERVICE +
				satelliteService.getSatellitesOnServiceNames());
	}

	@Test
	void getResponseWithNotEnoughValues() throws Exception{
		String[] msg = {"","algo","queda"};
		SecretServiceRequest request = new SecretServiceRequest(100.0, "sato", msg);
		messageSplitService.addRequestToValues(request);
		Exception ex = Assertions.assertThrows(Exception.class, () -> messageSplitService.getActualVaues());
		assert ex.getMessage().equals(OperacionFuegoConstants.ERROR_NOT_ENOUGH_INPUT_DATA +
				satelliteService.getSatellitesOnService().size());
	}

	@Test
	void getResponseWithNoSetValues() throws Exception{
		Exception ex = Assertions.assertThrows(Exception.class, () -> messageSplitService.getActualVaues());
		assert ex.getMessage().equals(OperacionFuegoConstants.ERROR_NOT_ENOUGH_INPUT_DATA +
				satelliteService.getSatellitesOnService().size());
	}

	@Test
	void recordEmptyAfterSuccessfulGet() throws Exception{
		String[] msg = {"","algo","queda"};
		SecretServiceRequest request1 = new SecretServiceRequest(100.0, "kenobi", msg);
		SecretServiceRequest request2 = new SecretServiceRequest(100.0, "skywalker", msg);
		SecretServiceRequest request3 = new SecretServiceRequest(100.0, "sato", msg);
		messageSplitService.addRequestToValues(request1);
		messageSplitService.addRequestToValues(request2);
		messageSplitService.addRequestToValues(request3);
		assert messageSplitService.getActualVaues().getSatellites().size() == 3 &&
				messageSplitService.getAllVaues().isEmpty();
	}

	//Utils functions

	@Test
	void arrayToString(){
		String[] msg = {"Luke", "soy", "tu", "padre"};
		String phrase = utils.stringArrayToString(msg, ' ');
		assert phrase.equals("Luke soy tu padre");
	}

	@Test
	void distanceWithInvalidSatIsNotAdded() throws Exception {
		ArrayList<SecretServiceRequest> requestSats = new ArrayList<>();
		SecretServiceRequest reqSat1 = new SecretServiceRequest(123.2,"dark",null);
		SecretServiceRequest reqSat2 = new SecretServiceRequest(500.2,"kenobi",null);
		SecretServiceRequest reqSat3 = new SecretServiceRequest(70.2,"sato",null);
		requestSats.add(reqSat1);
		requestSats.add(reqSat2);
		requestSats.add(reqSat3);
		satelliteService.getSatellitesOnService();
		Assertions.assertThrows(Exception.class, () -> utils.getDistancesFromRequest(requestSats));
	}

	@Test
	void getRequestMsgs(){
		ArrayList<SecretServiceRequest> requestSats = new ArrayList<>();
		String[] msg1 = {"","","este","","","mensaje",""};
		String[] msg2 = {"","","","es","","","secreto"};
		String[] msg3 = {"","este","","un","",""};
		SecretServiceRequest reqSat1 = new SecretServiceRequest(0.0,"kenobi",msg1);
		SecretServiceRequest reqSat2 = new SecretServiceRequest(0.0,"skywalker",msg2);
		SecretServiceRequest reqSat3 = new SecretServiceRequest(0.0,"sato",msg3);
		requestSats.add(reqSat1);
		requestSats.add(reqSat2);
		requestSats.add(reqSat3);
		ArrayList<String[]> messages = utils.getMessagesFromRequest(requestSats);
		assert  messages.size() == 3 && messages.get(0)[2].equals("este");
	}

	@Test
	void validateSatelliteLoad(){
		ArrayList<Satellite> satellites = satelliteService.getSatellitesOnService();
		assert satellites.size() == environment.getProperty("satellites").split(",").length;
	}

	@Test
	void getSatellitesNames(){
		ArrayList<Satellite> satellites = new ArrayList<>();
		Satellite satK = new Satellite(new Position(1.0, 1.0), "han");
		Satellite satSk = new Satellite(new Position(3.0, 1.0), "chuwaca");
		Satellite satSa = new Satellite(new Position(2.0, 2.0), "lord sidth");
		satellites.add(satK);
		satellites.add(satSk);
		satellites.add(satSa);
		satelliteService.setSatellitesOnService(satellites);
		assert satelliteService.getSatellitesOnServiceNames().equals("[han,chuwaca,lord sidth]");
	}

	@Test
	void getPosAndDist(){
		double[] distances = {1.0,12.0,3.0};
		double[][] positions = {{1.0,1.0},{3.0,1.0},{2.0,2.0}};
		HashMap<double[], Double> positionDistances = new HashMap<>();
		positionDistances.put(positions[0],distances[0]);
		positionDistances.put(positions[1],distances[1]);
		positionDistances.put(positions[2],distances[2]);
		assert utils.getDistances(positionDistances).length ==
				utils.getPositions(positionDistances).length;
	}

}
