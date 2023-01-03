package replies;

import entity.AuxAPI;
import entity.DeleteType;
import entity.Message;
import entity.ReplyMarkup;
import keyboards.Keyboard;
import utils.EmojiCodes;

public class ResetProfileReply implements Reply {

	Message[] messages;
	ReplyMarkup replyMarkup;
	
	public ResetProfileReply(DeleteType deleteType) {
		System.out.println("DEBUG");
		String preference = "";
		switch (deleteType) {
		case PROPERTIES:
			preference = "Properties";
			break;
		case ENTITIES:
			preference = "Entities";
			break;
		case ALL:
			preference = "Preferences";
			break;
		case NONE:
			System.err.println("E' stato passato DeleteType.NONE a ResetProfileReply!");
			System.exit(0);
			break;
		}
		String text = "...Warning! All your \"" + preference + "\" will be deleted.\n";
		text += "Please, confirm the choice";
		messages = new Message[] {
				new Message(text)
		};
		replyMarkup = new ReplyMarkup(new Keyboard() {

			@Override
			public String[][] getOptions() {
				String checkMarkCode = EmojiCodes.hexHtmlSurrogatePairs.get("check_mark");
				String prohibitedCode = EmojiCodes.hexHtmlSurrogatePairs.get("no_entry_sign");
				String gearCode = EmojiCodes.hexHtmlSurrogatePairs.get("gear");
				
				return new String[][] {
					new String[] {
							checkMarkCode + " Yes",
							prohibitedCode + " No"
					},
					new String[] {
							gearCode + " Profile"
					}
				};
			}
			
		});
		
		System.out.println(text);
		System.out.println(replyMarkup.getKeyboard().getOptions()[0][0]);
	}
	
	@Override
	public Message[] getMessages() {
		return messages;
	}

	@Override
	public ReplyMarkup getReplyMarkup() {
		return replyMarkup;
	}

	@Override
	public AuxAPI getAuxAPI() {
		return null;
	}

}
