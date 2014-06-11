package fr.lyrgard.hexScape.gui.desktop.view.home;


import javax.swing.JButton;

import fr.lyrgard.hexScape.gui.desktop.action.ConnectToServerAction;
import fr.lyrgard.hexScape.gui.desktop.action.OpenNewGameDialogAction;
import fr.lyrgard.hexScape.gui.desktop.view.AbstractView;

public class HomeView extends AbstractView {

	private static final long serialVersionUID = 7669212340835857265L;

	public HomeView() {
		JButton soloGame = new JButton(new OpenNewGameDialogAction(false));
		soloGame.setText("Solo game");
//		soloGame.addActionListener(new ActionListener() {
//			
//			public void actionPerformed(ActionEvent e) {
//				HexScapeFrame.getInstance().showView(ViewEnum.GAME);
//			}
//		});
		add(soloGame);
		
		JButton multiplayer = new JButton(new ConnectToServerAction());
//		multiplayer.addActionListener(new ActionListener() {
//			
//			public void actionPerformed(ActionEvent e) {
//				ConnectToServerAction action = new ConnectToServerAction();
//				action.actionPerformed(e);
//			}
//		});
		add(multiplayer);

		
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
}
