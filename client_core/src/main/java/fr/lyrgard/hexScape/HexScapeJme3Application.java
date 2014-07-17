package fr.lyrgard.hexScape;


import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppState;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.shadow.DirectionalLightShadowRenderer;

import fr.lyrgard.hexScape.camera.RotatingAroundCameraAppState;
import fr.lyrgard.hexScape.control.PieceControlerAppState;
import fr.lyrgard.hexScape.service.MapManager;

public class HexScapeJme3Application extends SimpleApplication {
	
	private MapManager scene;
	
	private RotatingAroundCameraAppState rotatingAroundCameraAppState = new RotatingAroundCameraAppState();
	
	private PieceControlerAppState pieceControlerAppState = new PieceControlerAppState();
	
	private boolean ready;
	
	public HexScapeJme3Application() {
		super(new AppState[] {new StatsAppState()});
		
		stateManager.attach(rotatingAroundCameraAppState);
		stateManager.attach(pieceControlerAppState);
		pieceControlerAppState.setEnabled(false);
	}

	@Override
	public void simpleInitApp() {
		
		assetManager.registerLocator("", FileLocator.class);
		
		cam.setLocation(new Vector3f(0, 10, 10));
		cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
	
		rotatingAroundCameraAppState.setRotateAroundNode(null);
		
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White.mult(0.5f));
		sun.setDirection(new Vector3f(1,-1, 0.5f).normalizeLocal());
		rootNode.addLight(sun);
		
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(0.8f));
		rootNode.addLight(al);
		
		final int SHADOWMAP_SIZE=1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(sun);
        dlsr.setShadowIntensity(0.3f);
        viewPort.addProcessor(dlsr);
        
        rootNode.setShadowMode(ShadowMode.CastAndReceive);
        setPauseOnLostFocus(false);
        
        ready = true;
	}

	public MapManager getScene() {
		return scene;
	}
	
	

	public void setScene(MapManager scene) {
		if (this.scene != null) {
			rootNode.detachChild(this.scene.getSpatial());
		}
		
		this.scene = scene;
		
		if (scene != null) {
			rootNode.attachChild(scene.getSpatial());
			rotatingAroundCameraAppState.setRotateAroundNode(scene.getSpatial());
			pieceControlerAppState.setEnabled(true);
		} else {
			rotatingAroundCameraAppState.setRotateAroundNode(null);
		}
	}

	public PieceControlerAppState getPieceControlerAppState() {
		return pieceControlerAppState;
	}

	public ViewPort getViewPort() {
		return viewPort;
	}

	public boolean isReady() {
		return ready;
	}

}
