package com.example.qrattendance.model;


public class CheckinRequest {
    private String qrCode;
    private String tokenId;

    public CheckinRequest(final String qrCode, final String tokenId) {
        this.qrCode = qrCode;
        this.tokenId = tokenId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(final String qrCode) {
        this.qrCode = qrCode;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(final String tokenId) {
        this.tokenId = tokenId;
    }
}
