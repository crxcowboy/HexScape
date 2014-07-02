package fr.lyrgard.hexScape.model.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import fr.lyrgard.hexScape.model.map.Map;
import fr.lyrgard.hexScape.model.marker.MarkerInfo;

public class Game {
	
	private String id;
	
	private String name;
	
	private Map map;
	
	private int playerNumber;

	private Collection<String> playersIds;
	
	private Collection<String> observersIds;
	
	private java.util.Map<String, MarkerInfo> markersById = new HashMap<>();

	public Collection<String> getPlayersIds() {
		if (playersIds == null) {
			playersIds = new ArrayList<>();
		}
		return playersIds;
	}

	public Collection<String> getObserversIds() {
		if (observersIds == null) {
			observersIds = new ArrayList<>();
		}
		return observersIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public java.util.Map<String, MarkerInfo> getMarkersById() {
		return markersById;
	}

	
}
