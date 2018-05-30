package com.aron.ChatApp;

/**
 * 13. Model Class that represents an individual chat message
*/

public class InstantMessage {

	// 14. Member variables
	private String message;
	private String author;

	// 15. Constructor
    public InstantMessage(String message, String author) {
        this.message = message;
        this.author = author;
    }

    // 16. No argument contructor- Requirement by Firebase 
    public InstantMessage() {
    	
    }

    // 17. Generate Getters
    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
}
