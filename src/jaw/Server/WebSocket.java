package jaw.Server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Base64;

public class WebSocket {

	public WebSocket(NanoHttpd.IHTTPSession session) {

		String upgrade = session.getHeaders().get("upgrade");

		if (upgrade == null || !upgrade.equals("websocket")) {
			return;
		}

		this.session = session;
	}

	public NanoHttpd.Response setHeaders(NanoHttpd.Response response) throws Exception {

		if (getSession() == null) {
			return response;
		}

		String securityKey = getSession().getHeaders().get("sec-websocket-key");

		if (securityKey != null) {
			response.addHeader("Sec-WebSocket-Accept", getAcceptKey(securityKey));
		}

		response.addHeader("Upgrade", "websocket");
		response.addHeader("Connection", "upgrade");
		response.addHeader("WebSocket-Idle-Timeout", "-1");

		response.setStatus(NanoHttpd.Response.Status.SWITCH_PROTOCOL);
		response.setData(createResponse("Hello from server"));

		return response;
	}

	public String parseResponse(InputStream stream) throws Exception {

		int len;
		byte[] b = new byte[stream.available()];

		if ((len = stream.read(b)) <= 0) {
			return null;
		}

		byte rLength;
		int rMaskIndex = 2;
		int rDataStart;

		byte data = b[1];
		byte op = (byte) 127;

		rLength = (byte) (data & op);

		if (rLength == (byte) 126) {
			rMaskIndex = 4;
		}

		if (rLength == (byte) 127) {
			rMaskIndex = 10;
		}

		byte[] masks = new byte[4];

		for(int i = rMaskIndex, j = 0; i < rMaskIndex + 4; i++){
			masks[j++] = b[i];
		}
		rDataStart = rMaskIndex + 4;

		int messLen = len - rDataStart;
		byte[] message = new byte[messLen];

		for(int i = rDataStart, j = 0; i < len; i++, j++){
			message[j] = (byte) (b[i] ^ masks[j % 4]);
		}

		return new String(message);
	}

	public InputStream createResponse(String mess) throws Exception {

		byte[] rawData = mess.getBytes();
		int frameCount;
		byte[] frame = new byte[10];

		frame[0] = (byte) 129;

		if(rawData.length <= 125) {
			frame[1] = (byte) rawData.length;
			frameCount = 2;
		} else if(rawData.length >= 126 && rawData.length <= 65535) {
			frame[1] = (byte) 126;
			int len = rawData.length;
			frame[2] = (byte)((len >> 8));
			frame[3] = (byte)(len);
			frameCount = 4;
		} else {
			frame[1] = (byte) 127;
			long len = rawData.length;
			frame[2] = (byte)((len >> 56));
			frame[3] = (byte)((len >> 48));
			frame[4] = (byte)((len >> 40));
			frame[5] = (byte)((len >> 32));
			frame[6] = (byte)((len >> 24));
			frame[7] = (byte)((len >> 16));
			frame[8] = (byte)((len >> 8));
			frame[9] = (byte)(len);
			frameCount = 10;
		}

		int bLength = frameCount + rawData.length;
		byte[] reply = new byte[bLength];
		int bLim = 0;

		for(int i=0; i<frameCount;i++){
			reply[bLim++] = frame[i];
		}

		for (byte aRawData : rawData) {
			reply[bLim++] = aRawData;
		}

		return new ByteArrayInputStream(reply);
	}

	private static String getAcceptKey(String key) throws Exception {

		MessageDigest md = MessageDigest.getInstance("SHA-1");
		String text = key + GUUI;

		md.update(text.getBytes(), 0, text.length());

		return Base64.getEncoder().encodeToString(
			md.digest()
		);
	}

	private final static String GUUI
		= "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

	public NanoHttpd.IHTTPSession getSession() {
		return session;
	}

	private NanoHttpd.IHTTPSession session = null;
}
