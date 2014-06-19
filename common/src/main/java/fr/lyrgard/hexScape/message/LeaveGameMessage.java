package fr.lyrgard.hexScape.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LeaveGameMessage extends AbstractUserMessage {

	private String gameId;

	@JsonCreator
	public LeaveGameMessage(
			@JsonProperty("playerId") String playerId, 
			@JsonProperty("gameId") String gameId) {
		super(playerId);
		this.gameId = gameId;
	}

	public String getGameId() {
		return gameId;
	}
	
}
