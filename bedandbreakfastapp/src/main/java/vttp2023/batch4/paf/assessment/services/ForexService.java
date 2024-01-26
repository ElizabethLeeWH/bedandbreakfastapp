package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ForexService {

	public static final String URL = "https://api.frankfurter.app/latest";

	// TODO: Task 5
	public float convert(String from, String to, float amount) {
		String url = UriComponentsBuilder
				.fromUriString(URL)
				.queryParam("amount", amount)
				.queryParam("from", from)
				.queryParam("to", to)
				.toUriString();
		RestTemplate template = new RestTemplate();
		ResponseEntity<String> resp = template.getForEntity(url, String.class);
		JsonReader jr = Json.createReader(new StringReader(resp.getBody()));
		JsonObject jo = jr.readObject();
		JsonObject obj = jo.getJsonObject("rates");
		float ratesInSGD = Float.parseFloat(obj.get(to.toUpperCase()).toString());
		if (ratesInSGD > 0) {
			return ratesInSGD;
		}

		return -1000f;

	}
}
