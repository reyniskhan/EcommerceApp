package com.example.ecommerceapp.Model;

public class Products {

    private String name, descripton, price, image, category, pid;

    public Products(){

    }

    public Products(String name, String descripton, String price, String image, String category, String pid) {
        this.name = name;
        this.descripton = descripton;
        this.price = price;
        this.image = image;
        this.category = category;
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripton() {
        return descripton;
    }

    public void setDescripton(String descripton) {
        this.descripton = descripton;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
