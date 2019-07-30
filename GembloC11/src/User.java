
public class User {
	private String name;
	private String where;
	
	public User() {
		
	}
	public User(String name, String where) {
		this.name = name; this.where = where;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String toString() {
		return String.format("이름:%s  위치:%s", name,where);
	}
}
