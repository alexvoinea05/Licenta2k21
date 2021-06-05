package com.localgrower.service.dto.custom;

public class CertificateGrowerDTO {

    private Long id;

    private String login;

    private String imageUrl;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "CertificateGrowerDTO{" + "id=" + id + ", login='" + login + '\'' + ", imageUrl='" + imageUrl + '\'' + '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CertificateGrowerDTO() {}
}
