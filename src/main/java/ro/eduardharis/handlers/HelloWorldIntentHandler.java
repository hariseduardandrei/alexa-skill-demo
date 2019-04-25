package ro.eduardharis.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.request.Predicates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

public class HelloWorldIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(Predicates.intentName("HelloWorldIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {

        String dayOfWeek = getDayOfWeekFrom(input);

        try {
            String response = getResponseFromServer(dayOfWeek);
            return input.getResponseBuilder()
                    .withSpeech(response)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input.getResponseBuilder()
                .withSpeech("Sorry, could not connect to external system")
                .build();
    }

    private String getDayOfWeekFrom(final HandlerInput input) {
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        Map<String, Slot> slots = intentRequest.getIntent().getSlots();
        return slots.get("dayOfWeek").getValue();
    }

    private String getResponseFromServer(final String dayOfWeek) throws IOException {
        URL url = new URL("http://orar-scolar.herokuapp.com/orar/dorel/" + dayOfWeek);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        return response.toString();
    }
}