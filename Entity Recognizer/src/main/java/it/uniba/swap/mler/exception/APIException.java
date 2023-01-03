package it.uniba.swap.mler.exception;

public class APIException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Exception inner;
	
	public APIException(Exception inner) {
		this.inner = inner;
	}
	
	@Override
	public void printStackTrace() {
		super.printStackTrace();
		System.err.print("Inner exception is: ");
		inner.printStackTrace();
	}
}
