package org.unicauca.middlewaretv.middleware.client;

import android.util.Log;

import com.flurry.android.FlurryAgent;

import org.unicauca.middlewaretv.middleware.handler.MantissConnectionHandler;
import org.unicauca.middlewaretv.middleware.handler.MantissControlHandler;
import org.unicauca.middlewaretv.middleware.handler.MantissEventHandler;
import org.unicauca.middlewaretv.middleware.message.MantissMessage;

import java.util.HashMap;
import java.util.Map;

import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.WampConnection;



public class MantissClient {

	private static final String TAG = MantissClient.class.getSimpleName();

	private MantissConnectionHandler connectionHandler;
	private MantissEventHandler eventHandler;
	private MantissControlHandler controlHandler;
	private String wsEndpoint;
	private WampConnection wampConnection;

	private static final String DEFAULT_TOPIC = "http://mantiss.com/display";
	private static final String DEFAULT_CONTROL = "http://mantiss.com/control";

	public MantissClient(String webSocketEndpoint, MantissConnectionHandler connectionHandler, MantissEventHandler eventHandler, MantissControlHandler controlHandler) {
		this.wsEndpoint = webSocketEndpoint;
		this.connectionHandler = connectionHandler;
		this.eventHandler = eventHandler;
		this.controlHandler = controlHandler;
		wampConnection = new WampConnection();
	}



	public boolean isConnected(){
		return wampConnection.isConnected();
	}


	public void connect() {
		connect(new HashMap<String, String>());
	}

	public void connect(Map<String, String> flurryParams) {
		Log.i(TAG, "Connecting to WebSocket endpoint " + wsEndpoint);
		wampConnection.connect(wsEndpoint, autobahnConnectionHandler);

		flurryParams.put("WebSocketEndpoint", wsEndpoint);
		FlurryAgent.logEvent("Connect", flurryParams);
	}


	public void disconnect() {
		disconnect(new HashMap<String, String>());
	}

	public void disconnect(Map<String, String> flurryParams) {
		Log.i(TAG, "Disconnected from WebSocket endpoint");
		wampConnection.disconnect();

		flurryParams.put("WebSocketEndpoint", wsEndpoint);
		FlurryAgent.logEvent("Disconnect", flurryParams);
	}

	public void subscribe() {
		subscribe(DEFAULT_TOPIC, new HashMap<String, String>());
	}

	public void subscribeControl() {
		subscribe(DEFAULT_CONTROL, new HashMap<String, String>());
	}

	public void subscribe(Map<String, String> flurryParams) {
		subscribe(DEFAULT_TOPIC, new HashMap<String, String>());
	}

	public void subscribe(String topicUri) {
		subscribe(topicUri, new HashMap<String, String>());
	}

	public void subscribe(String topicUri, Map<String, String> flurryParams) {
		Log.i(TAG, "Subscribing to  topic " + topicUri);
		wampConnection.subscribe(topicUri, MantissMessage.class, autobahnEventHandler);

		flurryParams.put("Topic", topicUri);
		FlurryAgent.logEvent("Subscribe", flurryParams);
	}

	public void unsubscribe() {
		unsubscribe(DEFAULT_TOPIC, new HashMap<String, String>());
	}

	public void unsubscribe(Map<String, String> flurryParams) {
		unsubscribe(DEFAULT_TOPIC, flurryParams);
	}

	public void unsubscribe(String topicUri) {
		unsubscribe(topicUri);
	}

	public void unsubscribe(String topicUri, Map<String, String> flurryParams) {
		Log.i(TAG, "Unubscribing from topic " + topicUri);
		wampConnection.unsubscribe(topicUri);

		flurryParams.put("Topic", topicUri);
		FlurryAgent.logEvent("Unsubscribe", flurryParams);
	}

	public void unsubscribeAll() {
		unsubscribe(new HashMap<String, String>());
	}

	public void unsubscribeAll(Map<String, String> flurryParams) {
		Log.i(TAG, "Unubscribing from all topics ");
		wampConnection.unsubscribe();

		FlurryAgent.logEvent("UnsubscribeAll", flurryParams);
	}

	public void publish(MantissMessage message ) {
		publish(message, DEFAULT_TOPIC, new HashMap<String, String>());
	}

	public void publishControl(MantissMessage message ) {
		publish(message, DEFAULT_CONTROL, new HashMap<String, String>());
	}

	public void publish(MantissMessage message, String topicUri) {
		publish(message, topicUri, new HashMap<String, String>());
	}


	public void publish( MantissMessage message, String topicUri, Map<String, String> flurryParams ) {
		Log.i(TAG, "Publishing to "+topicUri+" message " + message.toString());
		wampConnection.publish(topicUri, message);

		flurryParams.put("type", message.getType().toString());

		switch(message.getType()){
			case MOVE:
				//flurryParams.put("startContent", message.getStartContent());
				FlurryAgent.logEvent("MOVE", flurryParams);
				break;

			case PRESS:
				//flurryParams.put("startContent", message.getStartContent());
				FlurryAgent.logEvent("PRESS", flurryParams);
				break;

			case LONG_PRESS:
				//flurryParams.put("startContent", message.getStartContent());
				FlurryAgent.logEvent("LONG_PRESS", flurryParams);
				break;

			case TAP:
				//flurryParams.put("startContent", message.getStartContent());
				FlurryAgent.logEvent("TAP", flurryParams);
				break;

			case DOUBLE_TAP:
				//flurryParams.put("startContent", message.getStartContent());
				FlurryAgent.logEvent("DOUBLE_TAP", flurryParams);
				break;

			case SWIPE:
				//flurryParams.put("startContent", message.getStartContent());
				//flurryParams.put("endContent", message.getEndContent());
				FlurryAgent.logEvent("SWIPE", flurryParams);
				break;

			case DRAG:
				//flurryParams.put("startContent", message.getStartContent());
				FlurryAgent.logEvent("DRAG", flurryParams);
				break;

			case CUSTOM:
				//flurryParams.put("startContent", message.getStartContent());
				FlurryAgent.logEvent("CUSTOM", flurryParams);
				break;

			default:
				//flurryParams.put("startContent", message.getStartContent());
				FlurryAgent.logEvent("Publish", flurryParams);
				break;

			case SETUP:
				//flurryParams.put("startContent", message.getStartContent());
				//flurryParams.put("endContent", message.getEndContent());
				FlurryAgent.logEvent("SETUP", flurryParams);
				break;

			case PING:
				//flurryParams.put("startContent", message.getStartContent());
				//flurryParams.put("endContent", message.getEndContent());
				FlurryAgent.logEvent("PING", flurryParams);
				break;

			case BYE:
				//flurryParams.put("startContent", message.getStartContent());
				//flurryParams.put("endContent", message.getEndContent());
				FlurryAgent.logEvent("BYE", flurryParams);
				break;
		}

	}


	private Wamp.EventHandler autobahnEventHandler = new Wamp.EventHandler() {

		@Override
		public void onEvent(String topicUri, Object object) {
			MantissMessage message = (MantissMessage) object;

			Log.d(TAG, "onEvent topic " + topicUri + " Message " + message.toString());
			if(eventHandler != null){
					switch(message.getType()){
					case MOVE:
						eventHandler.onMove(topicUri, message);
						break;

					case PRESS:
						eventHandler.onPress(topicUri, message);
						break;

					case LONG_PRESS:
						eventHandler.onLongPress(topicUri, message);
						break;

					case TAP:
						eventHandler.onTap(topicUri, message);
						break;

					case DOUBLE_TAP:
						eventHandler.onDoubleTap(topicUri, message);
						break;

					case SWIPE:
						eventHandler.onSwipe(topicUri, message);
						break;

					case DRAG:
						eventHandler.onDrag(topicUri, message);
						break;

					case CUSTOM:
						eventHandler.onCustom(topicUri, message);
						break;

					default:
						break;

				}
			}

			if(controlHandler != null) {
				switch(message.getType()){
				case SETUP:
					controlHandler.onSetup(topicUri, message);
					break;

				case PING:
					controlHandler.onPing(topicUri, message);
					break;

				case BYE:
					controlHandler.onPing(topicUri, message);
					break;

				default:
					break;
				}
			}
		}
	};

	private Wamp.ConnectionHandler autobahnConnectionHandler = new Wamp.ConnectionHandler() {

		@Override
		public void onOpen() {
			Log.d(TAG, "onOpen " + wampConnection.isConnected());

			if (connectionHandler != null) {
				connectionHandler.onOpen();
			}
		}

		@Override
		public void onClose(int code, String reason) {
			Log.d(TAG, "onClose code " + code + " reason " + reason);

			if (connectionHandler != null) {
				connectionHandler.onClose(code, reason);
			}
		}
	};
}
