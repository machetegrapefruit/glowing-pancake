package entity;

@SuppressWarnings("unused")
public class Message {
	
	private String text;
	private String photo;
	private String link;
	
	public Message(String text) {
		this(text, null, null);
	}
	
	public Message(String text, String photo) {
		this(text, photo, null);
	}

	public Message(String text, String photo, String link) {
		this.text = text;
		this.photo = photo;
		this.link = link;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text; 
	}
	
	public String getPhoto() {
		return photo;
	}
	
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
}