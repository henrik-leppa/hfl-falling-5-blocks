import java.awt.event.KeyEvent;

public class GameKey{
	private boolean pressed;
	private boolean locked;
	private long lastPressed;
	private int code;
	private int location;

	GameKey(){
		pressed = false;
		locked = false;
		lastPressed = 0;// 0 = NEVER
		location = 5;// 5 = UNDEFINED
	}
	GameKey(int keyConstant){
		pressed = false;
		locked = false;
		lastPressed = 0;// 0 = NEVER
		code = keyConstant;
		location = 5;// 5 = UNDEFINED
	}
	GameKey(int keyConstant,int keyLocation){
		pressed = false;
		locked = false;
		lastPressed = 0;// 0 = NEVER
		code = keyConstant;
		location = keyLocation;

		System.out.println("#Key assigned as: "+getCodeAsString());
	}
	void press(){
		pressed = true;
		lastPressed = System.currentTimeMillis();
	}
	void lock(){
		locked = true;
	}
	void release(){
		pressed = false;
		locked = false;
	}
	boolean isValidKey(){
		if(code == 0){
			return false;
		}
		return true;
	}
	boolean isPressed(){
		return pressed;
	}
	boolean isLocked(){
		return locked;
	}
	boolean isPressable(){
		if(pressed && !locked){
			System.out.println("#Key press recognized: "+getCodeAsString());
			return true;
		}
		else{
			return false;
		}
	}
	long getLastPressed(){
		return System.currentTimeMillis() - lastPressed;
	}
	int getCode(){
		return code;
	}
	String getCodeAsString(){
		String codetext = KeyEvent.getKeyText(code);
		switch(location){
			case 0: codetext += "(Unknown)";break;
			case 1: codetext += "(Standard)";break;
			case 2: codetext += "(Left)";break;
			case 3: codetext += "(Right)";break;
			case 4: codetext += "(Numpad)";break;
		}
		return codetext;
	}
	void setCode(int keyConstant){
		code = keyConstant;
	}
	void setCode(int keyConstant,int keyLocation){
		code = keyConstant;
		location = keyLocation;
	}
	void setCode(String preferenceLine){
		String ending = "";
		if(preferenceLine.endsWith("(Unknown)")){
			ending = "(Unknown)";
			location = 0;
		}
		if(preferenceLine.endsWith("(Standard)")){
			ending = "(Standard)";
			location = 1;
		}
		if(preferenceLine.endsWith("(Left)")){
			ending = "(Left)";
			location = 2;
		}
		if(preferenceLine.endsWith("(Right)")){
			ending = "(Right)";
			location = 3;
		}
		if(preferenceLine.endsWith("(Numpad)")){
			ending = "(Numpad)";
			location = 4;
		}

		for(int a=0;a<600;a++){
			if(preferenceLine.endsWith(KeyEvent.getKeyText(a) + ending)){
				code = a;
				break;
			}
		}
	}
	void setLocation(int keyLocation){
		location = keyLocation;
	}
	int getLocation(){
		return location;
	}
	void processKeyPress(KeyEvent e){
		if(e.getKeyCode()==code && e.getKeyLocation()==location){
			pressed = true;
			lastPressed = System.currentTimeMillis();
			System.out.println("#Key pressed: "+getCodeAsString());
		}
	}
	void processKeyReleased(KeyEvent e){
		if(e.getKeyCode()==code && e.getKeyLocation()==location){
			pressed = false;
			locked = false;
			System.out.println("#Key released: "+getCodeAsString());
		}
	}

	public static void main(String[]beans){
		System.out.println(	"GameKey.class version 1.1,\n"+
							"created by Henrik Leppä");
	}
}