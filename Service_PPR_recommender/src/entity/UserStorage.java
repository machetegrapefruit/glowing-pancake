package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public abstract class UserStorage {
	
	private static class UserInfo {
		
		private String entityToRecommend;
		private String[] top5Entities;
		private String entityToRate;
		private DeleteType deleteType;
		private Property propertyToRate;
		private List<String> messages;
		private int page;
		
		private UserInfo() {
			this.entityToRecommend = null;
			this.top5Entities = null;
			this.entityToRate = null;
			this.deleteType = DeleteType.NONE;
			this.propertyToRate = null;
			this.messages = new ArrayList<String>();
			this.page = 1;
		}

		private String getEntityToRecommend() {
			return entityToRecommend;
		}

		private void setEntityToRecommend(String entityToRecommend) {
			this.entityToRecommend = entityToRecommend;
		}
		
		private String[] getTop5Entities() {
			return top5Entities;
		}
		
		private void setTop5Entities(String[] top5Entities) {
			this.top5Entities = top5Entities;
		}

		private String getEntityToRate() {
			return entityToRate;
		}

		private void setEntityToRate(String entityToRate) {
			this.entityToRate = entityToRate;
		}
		
		private DeleteType getDeleteType() {
			return deleteType;
		}
		
		private void setDeleteType(DeleteType deleteType) {
			this.deleteType = deleteType;
		}
		
		private void setPropertyToRate(Property propertyToRate) {
			this.propertyToRate = propertyToRate;
		}
		
		private Property getPropertyToRate() {
			return propertyToRate;
		}
		
		private void addMessage(String messageID) {
			messages.add(messageID);
		}
		
		private boolean getMessage(String messageID) {
			return messages.contains(messageID);
		}
		
		private void putPage(int page) {
			this.page = page;
		}
		
		private int getPage() {
			return page;
		}
	}
	
	private static Map<String, UserInfo> info;
	
	static {
		info = new HashMap<String, UserInfo>();
	}
	
//	public static void putEntityToRecommend(String userID, String entity) {
//		if (info.containsKey(userID)) {
//			UserInfo existing = info.get(userID);
//			existing.setEntityToRecommend(entity);
//			System.out.println("User " + userID + " already has an entity to recommend.");
//			System.out.println("It was replaced with " + entity);
//		} else {
//			UserInfo userInfo = new UserInfo();
//			userInfo.setEntityToRecommend(entity);
//			info.put(userID, userInfo);
//			System.out.println("User " + userID + " didn't have an entity to recommend.");
//			System.out.println("It has set to " + entity);
//		}
//	}
	
//	public static void putTop5Entities(String userID, String[] top5Entities) {
//		if (info.containsKey(userID)) {
//			info.get(userID).setTop5Entities(top5Entities);
//		} else {
//			UserInfo userInfo = new UserInfo();
//			userInfo.setTop5Entities(top5Entities);
//			info.put(userID, userInfo);
//		}
//	}
	
//	public static String[] getTop5Entities(String userID) {
//		String[] result = null;
//		if (info.containsKey(userID)) {
//			result = info.get(userID).getTop5Entities();
//		}
//		if (result == null) {
//			System.out.println("User " + userID + " has no top 5 entities");
//		}
//		return result;
//	}
	
//	public static void putEntityToRate(String userID, String entity) {
//		if (info.containsKey(userID)) {
//			UserInfo existing = info.get(userID);
//			existing.setEntityToRate(entity);
//			System.out.println("User " + userID + " already has an entity to rate.");
//			System.out.println("It was replaced with " + entity);
//		} else {
//			UserInfo userInfo = new UserInfo();
//			userInfo.setEntityToRate(entity);
//			info.put(userID, userInfo);
//			System.out.println("User " + userID + " entity to rate set to: " + entity);
//		}
//	}
	
//	public static String getEntityToRecommend(String userID) {
//		String result = null;
//		if (info.containsKey(userID)) {
//			UserInfo userInfo = info.get(userID);
//			result = userInfo.getEntityToRecommend();
//			if (result != null) {
//				System.out.println("User " + userID + " has to recommend: " + result);
//			}
//		}
//		return result;
//	}
	
//	public static String getEntityToRate(String userID) {
//		String result = null;
//		if (info.containsKey(userID)) {
//			UserInfo userInfo = info.get(userID);
//			result = userInfo.getEntityToRate();
//			if (result != null) {
//				System.out.println("User " + userID + " has to rate: " + result);
//			}
//		}
//		return result;
//	}
	
//	public static void putDeleteType(String userID, DeleteType type) {
//		if (info.containsKey(userID)) {
//			info.get(userID).setDeleteType(type);
//		} else {
//			UserInfo userInfo = new UserInfo();
//			userInfo.setDeleteType(type);
//			info.put(userID, userInfo);
//		}
//	}
	
//	public static DeleteType getDeleteType(String userID) {
//		
//		DeleteType type;
//		if (info.containsKey(userID)) {
//			type = info.get(userID).getDeleteType();
//		} else {
//			type = DeleteType.NONE;
//		}
//		return type;
//	}
	
//	public static void putPropertyToRate(String userID, Property property) {
//		if (info.containsKey(userID)) {
//			info.get(userID).setPropertyToRate(property);
//			System.out.println("User " + userID + " already has a property to rate.");
//			System.out.println("It was replaced with " + property);
//		} else {
//			UserInfo userInfo = new UserInfo();
//			userInfo.setPropertyToRate(property);
//			info.put(userID, userInfo);
//			System.out.println("User " + userID + " property to rate set to: " + property);
//		}
//	}
//	
//	public static Property getPropertyToRate(String userID) {
//		Property result = null;
//		if (info.containsKey(userID)) {
//			result = info.get(userID).getPropertyToRate();
//		}
//		return result;
//	}
	
	public static void addMessage(String userID, String messageID) {
		if (info.containsKey(userID)) {
			info.get(userID).addMessage(messageID);
			System.out.println("User " + userID + " already has messages");
			System.out.println("Message " + messageID + " was added.");
		} else {
			UserInfo userInfo = new UserInfo();
			userInfo.addMessage(messageID);
			info.put(userID, userInfo);
			System.out.println("User " + userID + " didn't exist");
			System.out.println("It has been created with message " + messageID);
		}
	}
	
	public static boolean hasMessage(String userID, String messageID) {
		boolean present = false;
		if (info.containsKey(userID)) {
			present = info.get(userID).getMessage(messageID);
		}
		return present;
		
	}

//	public static int getPage(String userID) {
//		int result = 1;
//		if (info.containsKey(userID)) {
//			result = info.get(userID).getPage();
//		} else {
//			result = 1;
//		}
//		return result;
//	}
//	
//	public static void putPage(String userID, int page) {
//		if (info.containsKey(userID)) {
//			info.get(userID).putPage(page);
//			System.out.println("Page " + page + " put for user " + userID);
//		} else {
//			UserInfo userInfo = new UserInfo();
//			userInfo.putPage(page);
//			info.put(userID, userInfo);
//			System.out.println("User " + userID + " created with page " + page);
//		}
//	}
}
