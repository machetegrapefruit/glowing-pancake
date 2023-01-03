package dialog;

import com.google.gson.JsonObject;

public interface Response {
	public void addSpeech(String speech);
	public String getSpeech();
	public JsonObject toJson();
	public boolean isFailure();
	public void setFailure(boolean failure);
}
