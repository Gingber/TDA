/**
 * 
 */
package utils;

/**
 * @author Gingber
 *
 */
public class RetweetObject {
	private String FromUserName;
	private String FromMsgId;
	private String orignalText;
	private String OriginalDate;
	private String ToUserName;
	private String ToMsgId;
	private String RetweetText;
	private String RetweetDate;

	
	public RetweetObject(String fromUserName, String originalDate,
			String toUserName, String retweetDate) {
		super();
		FromUserName = fromUserName;
		OriginalDate = originalDate;
		ToUserName = toUserName;
		RetweetDate = retweetDate;
	}

	public RetweetObject(String fromUserName, String originalDate, String fromMsgId, String orignalText,  
			String toUserName, String retweetDate, String toMsgId, String retweetText) {
		super();
		this.FromUserName = fromUserName;
		this.OriginalDate = originalDate;
		this.FromMsgId = fromMsgId;
		this.orignalText = orignalText;
		this.ToUserName = toUserName;
		this.RetweetDate = retweetDate;
		this.ToMsgId = toMsgId;
		this.RetweetText = retweetText;	
	}

	public String getOrignalText() {
		return orignalText;
	}

	public void setOrignalText(String orignalText) {
		this.orignalText = orignalText;
	}

	public String getRetweetText() {
		return RetweetText;
	}

	public void setRetweetText(String retweetText) {
		RetweetText = retweetText;
	}



	public String getOriginalDate() {
		return OriginalDate;
	}

	public void setOriginalDate(String originalDate) {
		OriginalDate = originalDate;
	}

	public String getRetweetDate() {
		return RetweetDate;
	}

	public void setRetweetDate(String retweetDate) {
		RetweetDate = retweetDate;
	}

	/**
	 * @return the fromUserName
	 */
	public String getFromUserName() {
		return FromUserName;
	}

	/**
	 * @param fromUserName the fromUserName to set
	 */
	public void setFromUserName(String fromUserName) {
		this.FromUserName = fromUserName;
	}

	/**
	 * @return the fromMsgId
	 */
	public String getFromMsgId() {
		return FromMsgId;
	}

	/**
	 * @param fromMsgId the fromMsgId to set
	 */
	public void setFromMsgId(String fromMsgId) {
		this.FromMsgId = fromMsgId;
	}

	/**
	 * @return the toUserName
	 */
	public String getToUserName() {
		return ToUserName;
	}

	/**
	 * @param toUserName the toUserName to set
	 */
	public void setToUserName(String toUserName) {
		this.ToUserName = toUserName;
	}

	/**
	 * @return the toMsgId
	 */
	public String getToMsgId() {
		return ToMsgId;
	}

	/**
	 * @param toMsgId the toMsgId to set
	 */
	public void setToMsgId(String toMsgId) {
		this.ToMsgId = toMsgId;
	}

	/**
	 * 
	 */
	public RetweetObject() {
		// TODO Auto-generated constructor stub
	}

}
