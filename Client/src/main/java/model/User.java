package model;

import java.io.Serializable;

public class User implements Serializable{
	private long id;
	private String username;
	private String password;
	private String name;
	private String position;
	private String status;
        private float score;
	public User() {
            super();
	}

	public User(long id, String username, String name,float score, String status) {
		super();
		this.id = id;
                this.username = username;
		this.name = name;
                this.score = score;
                this.status = status;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
        public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
        
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
