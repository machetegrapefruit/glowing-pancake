package functions;

@SuppressWarnings("serial")
public class InvalidMessageException extends Exception {

	public InvalidMessageException() {
		this("");
	}
	
	public InvalidMessageException(String message) {
		super("Message " + message + " was already set.");
	}
}
