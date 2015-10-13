package org.unicauca.middlewaretv.middleware.message;

public class MantissMessage {
	
	private MessageType type;
	private String startContent;
	private String endContent;
	private String extras;
	
	public MantissMessage(){
		
	}
	
	public MantissMessage(MessageType messageType, String startContent, String extras){
		this.type = messageType;
		this.startContent = startContent;
		this.extras = extras;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getStartContent() {
		return startContent;
	}

	public void setStartContent(String content) {
		this.startContent = content;
	}
	
	public String getEndContent() {
		return endContent;
	}

	public void setEndContent(String endContent) {
		this.endContent = endContent;
	}

	public String getExtras() {
		return extras;
	}

	public void setExtras(String extras) {
		this.extras = extras;
	}

	@Override
	public String toString(){
		return "[type: "+type+", startContent:"+startContent+", endContent:"+endContent+", extras:"+extras+"]";
	}
	
}
