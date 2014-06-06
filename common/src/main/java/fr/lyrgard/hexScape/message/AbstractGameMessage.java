package fr.lyrgard.hexScape.message;

public abstract class AbstractGameMessage extends AbstractUserMessage {

	private String gameId;

	public AbstractGameMessage(String playerId, String gameId) {
		super(playerId);
		this.gameId = gameId;
	}

	public String getGameId() {
		return gameId;
	}
	
	
}
