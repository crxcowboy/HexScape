package fr.lyrgard.hexScape.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fr.lyrgard.hexScape.model.card.CardType;
import fr.lyrgard.hexScape.model.card.CardCollection;

public class CardService {

private static final File baseFolder = new File("asset/cards");
	
	

	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String FIGURES_3D = "3dFigures";
	
	private static final String cardPropertiesFilename = "card.properties";
	
	private static final CardService INSTANCE = new CardService();
	
	public static CardService getInstance() {
		return INSTANCE;
	}
	
	private CardCollection cardInventory;
	
	private CardService() {
		loadCardInventory(baseFolder);
	}

	public CardCollection loadCardInventory(File baseFolder) {
		if (baseFolder == null) {
			baseFolder = CardService.baseFolder;
		}
		cardInventory = new CardCollection();
		if (baseFolder.exists()) {
			for (File folder : baseFolder.listFiles()) {
				if (folder.exists() && folder.isDirectory()) {
					File cardPropertiesFile = new File(folder, cardPropertiesFilename);
					if (cardPropertiesFile.exists() && cardPropertiesFile.isFile() && cardPropertiesFile.canRead()) {
						CardType card = new CardType();
						Properties cardProperties = new Properties();
						InputStream input;
						try {
							input = new FileInputStream(cardPropertiesFile);
							cardProperties.load(input);
							
							String id =  cardProperties.getProperty(ID);
							card.setId(id);
							card.setName(cardProperties.getProperty(NAME));
							card.setFolder(folder);
							String figures3d = cardProperties.getProperty(FIGURES_3D);
							String[] figures = figures3d.split(",");
							for (String figureName : figures) {
								figureName = figureName.trim();
								card.getFigureNames().add(figureName);
							}
							cardInventory.addCard(id, card, null);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				}
			}
		}
		return cardInventory;
	}

	public CardType getCardByPieceId(String pieceId) {
		return cardInventory.getCardsByPieceId().get(pieceId);
	}

	public CardCollection getCardInventory() {
		return cardInventory;
	}

}
