package org.unicauca.middlewaretv.middleware.message;

import android.view.MotionEvent;

public class MantissMotionEvent {
	
	private int id;
	private MessageAction action;
	float x;
	float y;
	long eventTime;
	long downTime;
	int source;
	
	
	public MantissMotionEvent() {
		super();
	}


	public MantissMotionEvent(int id, MessageAction action, int x, int y,
			long eventTime, long downTime, int source) {
		super();
		this.id = id;
		this.action = action;
		this.x = x;
		this.y = y;
		this.eventTime = eventTime;
		this.downTime = downTime;
		this.source = source;
	}
	
	public MantissMotionEvent(MotionEvent motionEvent){
		super();
		this.id = motionEvent.getDeviceId();
		
		switch(motionEvent.getAction()){
		case MotionEvent.ACTION_UP:
			this.action = MessageAction.ACTION_UP;
			break;
		case MotionEvent.ACTION_DOWN:
			this.action = MessageAction.ACTION_DOWN;
			break;
		case MotionEvent.ACTION_MOVE:
			this.action = MessageAction.ACTION_MOVE;
			break;
		case MotionEvent.ACTION_CANCEL:
			this.action = MessageAction.ACTION_CANCEL;
			break;
		}
		
		this.x = motionEvent.getX();
		this.y = motionEvent.getY();
		this.eventTime = motionEvent.getEventTime();
		this.downTime = motionEvent.getDownTime();
		this.source = motionEvent.getSource();
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public MessageAction getAction() {
		return action;
	}


	public void setAction(MessageAction action) {
		this.action = action;
	}


	public float getX() {
		return x;
	}


	public void setX(float x) {
		this.x = x;
	}


	public float getY() {
		return y;
	}


	public void setY(float y) {
		this.y = y;
	}


	public long getEventTime() {
		return eventTime;
	}


	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}


	public long getDownTime() {
		return downTime;
	}


	public void setDownTime(long downTime) {
		this.downTime = downTime;
	}


	public int getSource() {
		return source;
	}


	public void setSource(int source) {
		this.source = source;
	}

}
