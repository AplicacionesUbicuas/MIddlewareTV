package org.unicauca.middlewaretv.middleware.handler;

import org.unicauca.middlewaretv.middleware.message.MantissMessage;

public interface MantissControlHandler {
	void onSetup(String topicUri, MantissMessage message);
	void onPing(String topicUri, MantissMessage message);
	void onBye(String topicUri, MantissMessage message);
}
