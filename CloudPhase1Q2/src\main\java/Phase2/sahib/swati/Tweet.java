package Phase2.sahib.swati;

public class Tweet {

	private String id_str;
	private String createdAt;
	private String tweet_text;
	private String userID;

	public Tweet(String id_str, String createdAt, String tweet_text,
			String userID) {
		this.id_str = id_str;
		this.createdAt = createdAt;
		this.tweet_text = tweet_text;
		this.userID = userID;

	}

	public void setIDSTR(String id_str) {
		this.id_str = id_str;
	}

	public String getIDSTR() {
		return this.id_str;
	}

	public void setcreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getcreatedAt() {
		return this.createdAt;
	}

	public void settweet_text(String tweet_text) {
		this.tweet_text = tweet_text;
	}

	public String gettweet_text() {
		return this.tweet_text;
	}

	public void setuserID(String userID) {
		this.userID = userID;
	}

	public String getuserID() {
		return this.userID;
	}
}
