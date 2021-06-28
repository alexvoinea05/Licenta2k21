package com.localgrower.service.dto.custom;

import java.util.Arrays;

public class CertificateGrowerDTO {

    private Long id;

    private String login;

    private byte[] certificate;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "CertificateGrowerDTO{" + "id=" + id + ", login='" + login + '\'' + ", certificate=" + Arrays.toString(certificate) + '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }

    public CertificateGrowerDTO() {}
}
