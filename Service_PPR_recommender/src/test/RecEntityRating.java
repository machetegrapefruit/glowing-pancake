package test;

public class RecEntityRating implements Comparable<RecEntityRating> {
	private String userID;
	private String entityURI;
	private int numberRecommendationList;
	private boolean like;
	private boolean dislike;
	private long ratedAt;
	private int position;
	private int pagerankCycle;
	private boolean refine;
	private boolean refocus;
	private boolean details;
	private boolean why;
	private String botName;
	private String messageID;
	private long botTimestamp;
	private long responseTime;
	private String[] recommendationsList;
	public RecEntityRating(String userID, String entityURI, int numberRecommendationList, boolean like, boolean dislike,
			long ratedAt, int position, int pagerankCycle, boolean refine, boolean refocus, boolean details,
			boolean why, String botName, String messageID, long botTimestamp, long responseTime,
			String[] recommendationsList) {
		this(userID, entityURI, numberRecommendationList, like, dislike, ratedAt, position, pagerankCycle, refine, refocus, details,
				why, botName, messageID, botTimestamp, responseTime);
		this.recommendationsList = recommendationsList;
	}
	
	public RecEntityRating(String userID, String entityURI, int numberRecommendationList, boolean like, boolean dislike,
			long ratedAt, int position, int pagerankCycle, boolean refine, boolean refocus, boolean details,
			boolean why, String botName, String messageID, long botTimestamp, long responseTime) {
		super();
		this.userID = userID;
		this.entityURI = entityURI;
		this.numberRecommendationList = numberRecommendationList;
		this.like = like;
		this.dislike = dislike;
		this.ratedAt = ratedAt;
		this.position = position;
		this.pagerankCycle = pagerankCycle;
		this.refine = refine;
		this.refocus = refocus;
		this.details = details;
		this.why = why;
		this.botName = botName;
		this.messageID = messageID;
		this.botTimestamp = botTimestamp;
		this.responseTime = responseTime;
	}
	
	public RecEntityRating(String userID, String entityURI, int numberRecommendationList, boolean like, boolean dislike,
			long ratedAt, int position, int pagerankCycle, boolean refine, boolean refocus, boolean details,
			boolean why, String botName, String messageID, long botTimestamp, long responseTime,
			String recommendations) {
		this(userID, entityURI, numberRecommendationList, like, dislike, ratedAt, position, pagerankCycle, refine, refocus, details,
				why, botName, messageID, botTimestamp, responseTime);
		String[] recommendationsArray = recommendations.substring(1, recommendations.length() - 1).split(",");
		this.recommendationsList = recommendationsArray;

	}
	public String getUserID() {
		return userID;
	}
	public String getEntityURI() {
		return entityURI;
	}
	public int getNumberRecommendationList() {
		return numberRecommendationList;
	}
	public boolean isLike() {
		return like;
	}
	public boolean isDislike() {
		return dislike;
	}
	public long getRatedAt() {
		return ratedAt;
	}
	public int getPosition() {
		return position;
	}
	public int getPagerankCycle() {
		return pagerankCycle;
	}
	public boolean isRefine() {
		return refine;
	}
	public boolean isRefocus() {
		return refocus;
	}
	public boolean isDetails() {
		return details;
	}
	public boolean isWhy() {
		return why;
	}
	public String getBotName() {
		return botName;
	}
	public String getMessageID() {
		return messageID;
	}
	public long getBotTimestamp() {
		return botTimestamp;
	}
	public long getResponseTime() {
		return responseTime;
	}
	public String[] getRecommendationsList() {
		return recommendationsList;
	}

	@Override
	public int compareTo(RecEntityRating o) {
		// TODO Auto-generated method stub
		return this.numberRecommendationList - o.numberRecommendationList;
	}
	
}
