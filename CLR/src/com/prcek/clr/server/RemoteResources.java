package com.prcek.clr.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RemoteResources {

	public static byte[] FONT;
	
	private byte[] getRes(String name) {
		InputStream is = getClass().getResourceAsStream(name);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		if (is==null) return null;
		byte[] buf = new byte[1024];
		int read;
		try {
			while ((read = is.read(buf)) != -1) {  
				os.write(buf, 0, read);  
			}  
		} catch (IOException ex) {
			return null;
		}
		return os.toByteArray();
	}
	
	public RemoteResources() {
		FONT = getRes("DejaVuSansMono.ttf");
	}
}
