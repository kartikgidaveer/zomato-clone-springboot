package foodapp.exception;

@SuppressWarnings("serial")
public class NoFoodsAssignedException extends RuntimeException{
	public NoFoodsAssignedException(String message) {
		super(message);
	}

}
