package fr.lyrgard.hexScape.gui.desktop.controller.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.eventbus.Subscribe;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.TabGroup;
import de.lessvoid.nifty.controls.tabs.builder.TabBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import fr.lyrgard.hexScape.gui.desktop.controller.ImageButtonTextBuilder;
import fr.lyrgard.hexScape.message.ArmyLoadedMessage;
import fr.lyrgard.hexScape.model.CurrentUserInfo;
import fr.lyrgard.hexScape.model.card.Army;
import fr.lyrgard.hexScape.model.card.CardInstance;
import fr.lyrgard.hexScape.model.card.CardType;
import fr.lyrgard.hexScape.model.player.Player;
import fr.lyrgard.hexScape.service.CardService;

public class ArmiesPanelController {

	private Nifty nifty;
	private Screen screen;
	private Collection<Player> players;
	private TabGroup playersTabs;
	private Map<String, Element> armyContainers = new HashMap<>();

	public ArmiesPanelController(Nifty nifty, Screen screen) {
		this.nifty = nifty;
		this.screen = screen;
		playersTabs = screen.findNiftyControl("gameBottomPanelPlayerTabs", TabGroup.class);
	}

	public void setPlayers(Collection<Player> players) {
		this.players = players;
		
		Element tabsContentPanel = playersTabs.getElement().findElementByName("#tab-content-panel");

		// Remove all tabs
		for (int i = 0; i < playersTabs.getTabCount(); i++) {
			playersTabs.removeTab(0);
		}
		
		int currentPlayerIndex = 0;
		int i = 0;
		for (final Player player : players) {
			if (player.getId().equals(CurrentUserInfo.getInstance().getPlayerId())) {
				currentPlayerIndex = i;
			}
			i++;
			Element tab = new TabBuilder(player.getId() + "_armyTab", player.getDisplayName()) {{
			}}.build(nifty, screen, tabsContentPanel);
			
			new ControlBuilder("scrollPanel") {{
				parameter("vertical", "false");
				parameter("stepSizeX", "100");
				parameter("pageSizeX", "300");
				width("750px");
				height("177px");
				valign(VAlign.Top);
				panel(new PanelBuilder() {{
					id("#scrollPanelContainer");
					height("154px");
					paddingTop("2px");
					childLayoutHorizontal();
				}});
			}}.build(nifty, screen, tab);
			
			Element scrollPanelContainer = tab.findElementByName("#scrollPanelContainer");
			
			armyContainers.put(player.getId(), scrollPanelContainer);
			
			if (player.getArmy() == null) {
				Element panel = new PanelBuilder() {{
					width("740px");
					height("100%");
					childLayoutCenter();
				}}.build(nifty, screen, scrollPanelContainer);
				
				new ImageButtonTextBuilder() {{
					parameter("image", "gui/images/openFileWithText.png");
					parameter("imageHover", "gui/images/openFileWithText.png");
					parameter("imagePressed", "gui/images/openFileWithText.png");
					parameter("text", "${i18n.loadArmy}");
					parameter(ChooseArmyButtonController.PLAYER_ID_PROPERTY, player.getId());
					parameter("textWidth", "200px");
					width("240px");
					height("48px");
					alignCenter();
					controller(new ChooseArmyButtonController());
				}}.build(nifty, screen, panel);
			} else {
				displayArmy(player.getArmy(), player.getId());
			}
			
			playersTabs.addTab(tab);
		}
		
		playersTabs.setSelectedTabIndex(currentPlayerIndex);
	}
	
	private void displayArmy(Army army, final String playerId) {
		Element armyContainer = armyContainers.get(playerId);
		if (armyContainer != null) {
			for (Element child : armyContainer.getElements()) {
				child.markForRemoval();
			}
			for (final CardInstance card : army.getCards()) {
				final CardType cardType = CardService.getInstance().getCardInventory().getCardTypesById().get(card.getCardTypeId());
				new ArmyPanelBuilder(card) {{
					parameter("image", cardType.getIconPath());
					parameter(GameProperties.PLAYER_ID, playerId);
					parameter(GameProperties.CARD_ID, card.getId());
					marginLeft("2px");
					marginRight("2px");
				}}.build(nifty, screen, armyContainer);
			}
		}
	}
	
	@Subscribe
	public void onArmyLoaded(ArmyLoadedMessage message) {
		final String playerId = message.getPlayerId();
		Army army = message.getArmy();
		
		displayArmy(army, playerId);
	}
}
