package dialog;

public class DialogSingleton {
	private static ApiAiDialogController controller = new ApiAiDialogController();
	
	public static ApiAiDialogController getDialogController() {
		return controller;
	}
}
