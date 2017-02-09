package it.drwolf.push;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.PayloadBuilder;

public class Push {

	private String googlePushKey;
	private String applePushCert;

	private String applePushPassword;
	private String sender;

	public Push(String googlePushKey, String applePushCert, String applePushPassword, String sender) {
		this.googlePushKey = googlePushKey;
		this.applePushCert = applePushCert;
		this.applePushPassword = applePushPassword;
		this.sender = sender;
	}

	private ApnsService getAppleService() {
		ApnsService service = APNS.newService().withCert(this.applePushCert, this.applePushPassword)
				.withSandboxDestination().build();
		return service;
	}

	private void pushApple(final String token, String msg, Map<String, Object> attrib, Integer unread, String sound) {

		ApnsService service = this.getAppleService();

		PayloadBuilder payload = APNS.newPayload();

		payload.alertBody(msg);

		for (Entry<String, Object> entry : attrib.entrySet()) {
			payload.customField(entry.getKey(), entry.getValue().toString());
		}

		payload.alertTitle(this.sender);
		if (unread != null) {
			payload.badge(unread);
		}

		if (sound != null) {
			payload.sound("www/" + sound + ".wav");
		}

		service.push(token, payload.build());

	}

	private void pushGoogle(final String token, String msg, Map<String, Object> attrib, String sound) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		ObjectNode params = mapper.createObjectNode();
		ObjectNode params_data = params.putObject("data");
		params_data.put("title", this.sender);
		params_data.put("message", msg);
		if (sound != null) {
			params_data.put("soundname", sound);
		}

		for (Entry<String, Object> entry : attrib.entrySet()) {
			params_data.put(entry.getKey(), entry.getValue().toString());
		}
		ArrayNode devtok = params.putArray("registration_ids");
		devtok.add(token);
		String payload = params.toString();

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost("https://android.googleapis.com/gcm/send");

		request.addHeader("Authorization", "key=" + this.googlePushKey);
		request.addHeader("Content-Type", "application/json");
		request.setEntity(new ByteArrayEntity(payload.getBytes("UTF8")));

		client.execute(request);

	}

	public void send(String text, String token, Integer unread, String sound) throws Exception {

		if (token.matches("[0-9a-fA-F]+")) {
			this.pushApple(token, text, new HashMap<String, Object>(), unread, sound);
		} else {
			this.pushGoogle(token, text, new HashMap<String, Object>(), sound);
		}

	}

}
