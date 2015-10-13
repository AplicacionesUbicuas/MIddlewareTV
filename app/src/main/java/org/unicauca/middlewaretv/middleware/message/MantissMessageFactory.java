package org.unicauca.middlewaretv.middleware.message;

import android.util.Log;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;


public class MantissMessageFactory {
	
	private static final String TAG = MantissMessageFactory.class.getSimpleName();
	private ObjectMapper objectMapper = new ObjectMapper();
	private static MantissMessageFactory instance;
	
	private MantissMessageFactory(){
		instance = this;
	}
	
	public static String serializeMotionEvent(MantissMotionEvent motionEvent){
		String result = "";
		if(MantissMessageFactory.instance == null){
			MantissMessageFactory.instance = new MantissMessageFactory();
		}
		
		try {
			result = MantissMessageFactory.instance.objectMapper.writeValueAsString(motionEvent);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static MantissMotionEvent deserializeMotionEvent(String mantissMotionEventString){
		MantissMotionEvent result = null;
		
		if(MantissMessageFactory.instance == null){
			MantissMessageFactory.instance = new MantissMessageFactory();
		}
		
		try {
			result = MantissMessageFactory.instance.objectMapper.readValue(mantissMotionEventString, MantissMotionEvent.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return result;
	}
	
	
	public static MantissMessage getInstanceMove(MantissMotionEvent motionEvent){
		MantissMessage message = new MantissMessage();
		message.setType(MessageType.MOVE);
		message.setStartContent(serializeMotionEvent(motionEvent));
		Log.d(TAG, "created MantissMessage Move " + message);
		return message;
	}
	
	public static MantissMessage getInstancePress(MantissMotionEvent motionEvent){
		MantissMessage message = new MantissMessage();
		message.setType(MessageType.PRESS);
		message.setStartContent(serializeMotionEvent(motionEvent));
		Log.d(TAG, "created MantissMessage Press " + message);
		return message;
	}
	
	public static MantissMessage getInstanceLongPress(MantissMotionEvent motionEvent){
		MantissMessage message = new MantissMessage();
		message.setType(MessageType.LONG_PRESS);
		message.setStartContent(serializeMotionEvent(motionEvent));
		Log.d(TAG, "created MantissMessage LongPress " + message);
		return message;
	}
	
	public static MantissMessage getInstanceTap(MantissMotionEvent motionEvent){
		MantissMessage message = new MantissMessage();
		message.setType(MessageType.TAP);
		message.setStartContent(serializeMotionEvent(motionEvent));
		Log.d(TAG, "created MantissMessage Tap " + message);
		return message;
	}
	
	public static MantissMessage getInstanceDoubleTap(MantissMotionEvent motionEvent){
		MantissMessage message = new MantissMessage();
		message.setType(MessageType.DOUBLE_TAP);
		message.setStartContent(serializeMotionEvent(motionEvent));
		Log.d(TAG, "created MantissMessage DoubleTap " + message);
		return message;
	}
	
	public static MantissMessage getInstanceSwipe(MantissMotionEvent motionEventStart, MantissMotionEvent motionEventEnd){
		MantissMessage message = new MantissMessage();
		message.setType(MessageType.SWIPE);
		message.setStartContent(serializeMotionEvent(motionEventStart));
		message.setEndContent(serializeMotionEvent(motionEventEnd));
		Log.d(TAG, "created MantissMessage Swipe " + message);
		return message;
	}
	
	public static MantissMessage getInstanceDrag(MantissMotionEvent motionEvent){
		MantissMessage message = new MantissMessage();
		message.setType(MessageType.DRAG);
		message.setStartContent(serializeMotionEvent(motionEvent));
		Log.d(TAG, "created MantissMessage Drag " + message);
		return message;
	}
	
	public static MantissMessage getInstanceCustom(){
		MantissMessage message = new MantissMessage();
		message.setType(MessageType.CUSTOM);
		Log.d(TAG, "created MantissMessage Custom " + message);
		return message;
	}
	
	public static MantissMessage getInstanceSetup(String startContent, String endContent){
		MantissMessage message = new MantissMessage();
		message.setType(MessageType.SETUP);
		message.setStartContent(startContent);
		message.setEndContent(endContent);
		Log.d(TAG, "created MantissControlMessage " + message.toString());
		return message;
	}
	
	public static MantissMessage getInstancePing(String startContent, String endContent){
		MantissMessage message = new MantissMessage();
		message.setType(MessageType.PING);
		message.setStartContent(startContent);
		message.setEndContent(endContent);
		Log.d(TAG, "created MantissControlMessage " + message.toString());
		return message;
	}
	
	public static MantissMessage getInstanceBye(String startContent, String endContent){
		MantissMessage message = new MantissMessage();
		message.setType(MessageType.BYE);
		message.setStartContent(startContent);
		message.setEndContent(endContent);
		Log.d(TAG, "created MantissControlMessage " + message.toString());
		return message;
	}
	
	public static MantissMessage createMessage(MessageType messageType, String content, String extras){
		MantissMessage message = new MantissMessage(messageType, content, extras);
		return message;
	}

}
