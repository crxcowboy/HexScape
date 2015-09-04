package fr.lyrgard.hexScape.message;

import fr.lyrgard.hexScape.model.map.Map;

public class DisplayMapMessage extends AbstractMessage {
	
	private Map map;

	public DisplayMapMessage(Map map) {
		super();
		this.map = map;
	}

	public Map getMap() {
		return map;
	}
}
