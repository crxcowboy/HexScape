package fr.lyrgard.hexScape.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PieceRemovedMessage extends AbstractPieceMessage {

	@JsonCreator
	public PieceRemovedMessage(
			@JsonProperty("playerId") String playerId, 
			@JsonProperty("cardInstanceId") String cardInstanceId, 
			@JsonProperty("pieceId") String pieceId) {
		super(playerId, cardInstanceId, pieceId);
	}



}
