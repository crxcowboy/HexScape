package fr.lyrgard.hexScape.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MarkerRevealedMessage extends AbstractMarkerMessage {

	@JsonCreator
	public MarkerRevealedMessage(
			@JsonProperty("playerId") String playerId, 
			@JsonProperty("gameId") String gameId, 
			@JsonProperty("cardId") String cardId, 
			@JsonProperty("markerId") String markerId, 
			@JsonProperty("number") int number) {
		super(playerId, gameId, cardId, markerId, number);
	}

}
