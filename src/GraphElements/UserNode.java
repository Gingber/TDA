package GraphElements;



public class UserNode {
	private String username;
	private String id;
	private String text;
	private String created_at;
	
	
	public UserNode(String username, String created_at, String id, String text) {
		super();
		this.username = username;
		this.created_at = created_at;
		this.id = id;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public UserNode(String username){
		this.username = username;
	}
	
	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public UserNode(String username, String id) {
		super();
		this.username = username;
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean equals(Object un){
		if(un instanceof UserNode){
			return username.equals(((UserNode)un).username);
		}
		return false;
	}
	
	public String toString(){
		return username;
	}
	
	public int hashCode(){
		return username.hashCode();
	}
}
