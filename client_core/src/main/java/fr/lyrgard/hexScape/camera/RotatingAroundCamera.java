package fr.lyrgard.hexScape.camera;

import com.jme3.bounding.BoundingBox;
import com.jme3.input.InputManager;
import com.jme3.input.awt.AwtKeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

public class RotatingAroundCamera implements AnalogListener, ActionListener {

	public static final String ROTATINGCAM_MouseLeft = "ROTATINGCAM_MouseLeft";
	public static final String ROTATINGCAM_MouseRight = "ROTATINGCAM_MouseRight";
	public static final String ROTATINGCAM_MouseUp = "ROTATINGCAM_MouseUp";
	public static final String ROTATINGCAM_MouseDown = "ROTATINGCAM_MouseDown";
	public static final String ROTATINGCAM_RotateDrag = "ROTATINGCAM_RotateDrag";
	public static final String ROTATINGCAM_Left = "ROTATINGCAM_Left";
	public static final String ROTATINGCAM_Right = "ROTATINGCAM_Right";
	public static final String ROTATINGCAM_Up = "ROTATINGCAM_Up";
	public static final String ROTATINGCAM_Down = "ROTATINGCAM_Down";
	public static final String ROTATINGCAM_ZoomIn = "ROTATINGCAM_ZoomIn";
	public static final String ROTATINGCAM_ZoomOut = "ROTATINGCAM_ZoomOut";
	
	private static String[] mappings = new String[]{
			ROTATINGCAM_MouseLeft,
			ROTATINGCAM_MouseRight,
			ROTATINGCAM_MouseUp,
			ROTATINGCAM_MouseDown,
			ROTATINGCAM_RotateDrag,
			ROTATINGCAM_Left,
			ROTATINGCAM_Right,
			ROTATINGCAM_Up,
			ROTATINGCAM_Down,
			ROTATINGCAM_ZoomIn,
			ROTATINGCAM_ZoomOut,
	};
	
	protected Camera cam;
	
	protected Vector3f initialUpVec;
	
	protected Vector3f centerPos = Vector3f.ZERO;
	
	protected float distance;
	
	private Quaternion rot = new Quaternion();
	
	protected InputManager inputManager;
	
	private boolean draging = false;
	
	protected float rotationSpeed = 1.0F;
	protected float zoomSpeed = 20F;
	
	public RotatingAroundCamera(Camera cam) {
		this.cam = cam;
		this.initialUpVec = cam.getUp().clone();
	}
	
	/**
     * Registers the FlyByCamera to recieve input events from the provided
     * Dispatcher.
     * @param dispacher
     */
    public void registerWithInput(InputManager inputManager){
        this.inputManager = inputManager;

        // both mouse and button - rotation of cam
        inputManager.addMapping(ROTATINGCAM_MouseLeft, new MouseAxisTrigger(0, true));
        inputManager.addMapping(ROTATINGCAM_MouseRight, new MouseAxisTrigger(0, false));
        inputManager.addMapping(ROTATINGCAM_MouseUp, new MouseAxisTrigger(1, false));
        inputManager.addMapping(ROTATINGCAM_MouseDown, new MouseAxisTrigger(1, true));
        inputManager.addMapping(ROTATINGCAM_RotateDrag, new MouseButtonTrigger(1));
        inputManager.addMapping(ROTATINGCAM_Left, new KeyTrigger(AwtKeyInput.KEY_LEFT));
        inputManager.addMapping(ROTATINGCAM_Right, new KeyTrigger(AwtKeyInput.KEY_RIGHT));
        inputManager.addMapping(ROTATINGCAM_Up, new KeyTrigger(AwtKeyInput.KEY_UP));
        inputManager.addMapping(ROTATINGCAM_Down, new KeyTrigger(AwtKeyInput.KEY_DOWN));
        inputManager.addMapping(ROTATINGCAM_ZoomIn, new KeyTrigger(AwtKeyInput.KEY_ADD));
        inputManager.addMapping(ROTATINGCAM_ZoomOut, new KeyTrigger(AwtKeyInput.KEY_SUBTRACT));
        inputManager.addListener(this, mappings);
        inputManager.setCursorVisible(true);
    }
    
    public void unregisterInput()
    {
    	if (this.inputManager == null) {
    		return;
    	}
    	
    	for (String s : mappings) {
    		if (this.inputManager.hasMapping(s)) {
    			this.inputManager.deleteMapping(s);
    		}
    	}
    	this.inputManager.removeListener(this);
    }

	
    public void onAnalog(String name, float value, float tpf) {
    	if (name.equals(ROTATINGCAM_MouseLeft) && draging){
            rotateCamera(value, true);
        }else if (name.equals(ROTATINGCAM_MouseRight) && draging){
            rotateCamera(-value, true);
        }else if (name.equals(ROTATINGCAM_MouseUp) && draging){
            rotateCamera(-value, false);
        }else if (name.equals(ROTATINGCAM_MouseDown) && draging){
            rotateCamera(value, false);
        }else if (name.equals(ROTATINGCAM_Left)){
            rotateCamera(-value, true);
        }else if (name.equals(ROTATINGCAM_Right)){
            rotateCamera(value, true);
        }else if (name.equals(ROTATINGCAM_Up)){
            rotateCamera(value, false);
        }else if (name.equals(ROTATINGCAM_Down)){
            rotateCamera(-value, false);
        }else if (name.equals(ROTATINGCAM_ZoomIn)){
            zoomCamera(-value);
        }else if (name.equals(ROTATINGCAM_ZoomOut)){
            zoomCamera(value);
        }
    }
    
    public void onAction(String name, boolean value, float tpf) {
        if (name.equals(ROTATINGCAM_RotateDrag)){
        	draging = value;
            inputManager.setCursorVisible(!value);
        }
    }

    private void rotateCamera(float value, boolean sideways) {
    	if (sideways) {
    		Vector3f camPos = cam.getLocation(); // So now we have current position and center
    		rot.fromAngles(0,value * rotationSpeed, 0);
    		Vector3f difference = camPos.subtract(centerPos);
    		rot.multLocal(difference);
    		cam.setLocation(centerPos.add(difference));
    		cam.lookAt(centerPos, Vector3f.UNIT_Y);
    	} else {
    		Vector3f v = new Vector3f();
    		cam.getUp(v);
    		Vector3f pos = cam.getLocation();
    		Vector3f newPos = pos.add(v.mult(distance * value * 1f));
    		boolean goOverVertical = ((pos.x - centerPos.x) * (newPos.x -  centerPos.x) < 0) || ((pos.z - centerPos.z) * (newPos.z - centerPos.z) < 0);
    		//float distanceToVertical = Math.abs(newPos.x - centerPos.x) + Math.abs(newPos.z - centerPos.z);
    		if (newPos.y > centerPos.y && !goOverVertical ) {
    			newPos = centerPos.add(newPos.subtract(centerPos).normalize().mult(distance));
    			cam.setLocation(newPos);
    			cam.lookAt(centerPos, Vector3f.UNIT_Y);
    		}
    	}
    }

    private void zoomCamera(float value){
    	
    	float newDistance = distance + value * zoomSpeed;
    	if (newDistance > 1) {
    		distance = newDistance;
    		cam.setLocation(centerPos.add(cam.getLocation().subtract(centerPos).normalize().mult(distance)));
        	cam.lookAt(centerPos, Vector3f.UNIT_Y);
    	}
    }

	public Vector3f getCenterPos() {
		return centerPos;
	}


	public void setRotateAroundNode(Spatial rotateAroundNode, boolean resetPosition) {
		if (rotateAroundNode != null) {
			BoundingBox volume = (BoundingBox)rotateAroundNode.getWorldBound();
			centerPos = volume.getCenter();
			
			if (resetPosition) {
				distance = (float)(Math.sqrt(4 * volume.getXExtent() * volume.getXExtent() +  4 * volume.getYExtent() * volume.getYExtent()));
			} else {
				distance = cam.getLocation().distance(centerPos);
			}
		} else {
			centerPos = Vector3f.ZERO;
			distance = 20f;
		}
		
		if (resetPosition) {
			cam.setLocation(centerPos.add(new Vector3f(1, 1, 0).normalize().mult(distance)));
		}
		cam.lookAt(centerPos, Vector3f.UNIT_Y);
	}

}
