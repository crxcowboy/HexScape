package fr.lyrgard.hexScape.gui.desktop.components.cardComponent;


import java.awt.EventQueue;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.google.common.eventbus.Subscribe;

import fr.lyrgard.hexScape.HexScapeCore;
import fr.lyrgard.hexScape.bus.GuiMessageBus;
import fr.lyrgard.hexScape.message.ArmyLoadedMessage;
import fr.lyrgard.hexScape.model.Universe;
import fr.lyrgard.hexScape.model.card.Army;
import fr.lyrgard.hexScape.model.player.Player;

public class ArmiesTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 7018446148678245684L;
	
	private ArmyPanel yourArmyPanel = new ArmyPanel();
	
	public ArmiesTabbedPane() {
		addTab("Your army", yourArmyPanel);
		GuiMessageBus.register(this);
	}

	@Subscribe public void onArmyLoaded(final ArmyLoadedMessage message) {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				String playerId = message.getPlayerId();
				Army army = message.getArmy();
				
				Player player = Universe.getInstance().getPlayersByIds().get(playerId);
				
				if (player != null) {
					if (playerId.equals(HexScapeCore.getInstance().getPlayerId())) {
						yourArmyPanel.setArmy(army, playerId);
					} else {
						ArmyPanel otherPlayerArmyPanel = new ArmyPanel(playerId, army);
						addTab(player.getName(), otherPlayerArmyPanel);
					}
				}
			}
		});
	}

}
