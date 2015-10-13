package org.unicauca.middlewaretv.middleware.handler;

public interface MantissConnectionHandler {
	
	void onClose(int code, String reason);
	void onOpen();
}
