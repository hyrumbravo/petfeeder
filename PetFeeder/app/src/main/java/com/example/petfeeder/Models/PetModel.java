package com.example.petfeeder.Models;

import android.media.Image;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PetModel {

    String Name, Breed, Sex, Birthdate, VetName, VetContact, Image, PetFinderID, Allergies, Medications;
    Integer Age, Weight, ID;

    public PetModel() {
        Image = "null";
    }

    public void nullify(){
        ID = null;
        Name = null;
        Breed = null;
        Sex = null;
        Birthdate = null;
        VetName = null;
        VetContact = null;
        Image = "null";
        Age = null;
        Weight = null;
        Allergies = null;
        Medications = null;
        PetFinderID = null;
    }

    public Integer getID() {
        return ID;
    }
    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getImage() {
        return Image;
    }
    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getBreed() {
        return Breed;
    }
    public void setBreed(String breed) {
        Breed = breed;
    }

    public String getSex() {
        return Sex;
    }
    public void setSex(String sex) {
        Sex = sex;
    }

    public String getBirthdate() {
        return Birthdate;
    }
    public void setBirthdate(String birthdate) {
        Birthdate = birthdate;
    }

    public String getVetName() {
        return alterReturn(VetName);
    }
    public void setVetName(String vetName) {
        VetName = vetName;
    }

    public String getVetContact() {
        return alterReturn(VetContact);
    }
    public void setVetContact(String vetContact) {
        VetContact = vetContact;
    }

    public Integer getAge() {
        return Age;
    }
    public void setAge(Integer age) {
        Age = age;
    }

    public Integer getWeight() {
        return Weight;
    }
    public void setWeight(Integer weight) {
        Weight = weight;
    }

    public String getAllergies() {
        return alterReturn(Allergies);
    }
    public void setAllergies(String allergies) {
        Allergies = allergies;
    }

    public String getMedications() {
        return alterReturn(Medications);
    }
    public void setMedications(String medications) {
        Medications = medications;
    }

    public String getPetFinderID() {
        return PetFinderID;
    }
    public void setPetFinderID(String petFinderID) {
        petFinderID = petFinderID;
    }

    private String alterReturn(@Nullable String value){
        return value==null?"":value=="null"?"":value;
    }
}
