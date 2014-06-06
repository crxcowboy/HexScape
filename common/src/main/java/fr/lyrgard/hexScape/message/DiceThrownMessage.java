package fr.lyrgard.hexScape.message;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiceThrownMessage extends AbstractUserMessage {

	private String diceTypeId;
	
	private List<String> results;

	
	public DiceThrownMessage(
			@JsonProperty("playerId") String playerId, 
			@JsonProperty("diceTypeId") String diceTypeId,
			@JsonProperty("results") List<String> results) {
		super(playerId);
		this.diceTypeId = diceTypeId;
		this.results = results;
	}

	public String getDiceTypeId() {
		return diceTypeId;
	}

	public List<String> getResults() {
		return results;
	}
	
	
}