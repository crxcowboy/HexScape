package fr.lyrgard.hexScape.event;

import java.util.List;

import fr.lyrgard.hexScape.model.dice.DiceFace;
import fr.lyrgard.hexScape.model.dice.DiceType;
import fr.lyrgard.hexScape.model.player.Player;

public class DiceRolledEvent {

	private DiceType diceType;
	
	private List<DiceFace> result;
	
	private Player player;

	public DiceRolledEvent(DiceType diceType, List<DiceFace> result, Player player) {
		super();
		this.diceType = diceType;
		this.result = result;
		this.player = player;
	}

	public DiceType getDiceType() {
		return diceType;
	}

	public List<DiceFace> getResult() {
		return result;
	}

	public Player getPlayer() {
		return player;
	}
	
	
}
