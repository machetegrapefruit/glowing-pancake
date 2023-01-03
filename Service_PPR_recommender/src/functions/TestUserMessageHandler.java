package functions;

public class TestUserMessageHandler extends UserMessageHandler {

	public TestUserMessageHandler(String userID, String firstname, String botName) {
		super(userID, firstname, botName, "", "");
		super.logService = new FakeLog();
	}

	@Override
	public void setMessage(String messageID, String text, String timeStamp) throws InvalidMessageException {
		// TODO Auto-generated method stub
		super.messageID = messageID;
		super.text = text;
		super.timeStamp = timeStamp;
	}

}
