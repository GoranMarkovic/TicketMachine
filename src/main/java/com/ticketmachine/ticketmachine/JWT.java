package com.ticketmachine.ticketmachine;

public class JWT {
    private String token;
    private String refresh_token;
    private String subject;

    public JWT()
    {
        super();
    }

    public JWT(String token, String refresh_token, String subject) {
        this.token = token;
        this.refresh_token = refresh_token;
        this.subject = subject;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
