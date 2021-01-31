package com.example.reddrip;

public class Annonce {
    private String email;
    private String num_tel;
    private String nom;
    private String prenom;
    private String gs;
    private String adresse;
    private String type;
    private String sexe;
    private String description;
    private int age;

    public Annonce() {
    }

    public Annonce(String email, String num_tel, String nom, String prenom, String gs, String adresse, String type, String sexe, String description, int age) {
        this.email = email;
        this.num_tel = num_tel;
        this.nom = nom;
        this.prenom = prenom;
        this.gs = gs;
        this.adresse = adresse;
        this.type = type;
        this.sexe = sexe;
        this.description = description;
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNum_tel() {
        return num_tel;
    }

    public void setNum_tel(String num_tel) {
        this.num_tel = num_tel;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getGs() {
        return gs;
    }

    public void setGs(String gs) {
        this.gs = gs;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
