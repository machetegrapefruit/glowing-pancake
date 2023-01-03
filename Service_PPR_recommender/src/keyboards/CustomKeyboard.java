package keyboards;

public class CustomKeyboard implements Keyboard {

	private String[][] options;

	public CustomKeyboard(String[][] options) {
		this.options = options;
	}
	
	public CustomKeyboard(String[] options) {
		this.options = new String[options.length][1];
		for (int i = 0; i < options.length; i++) {
			this.options[i][0] = options[i];
		}
	}
	
	@Override
	public String[][] getOptions() {
		return options;
	}
}
