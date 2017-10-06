package com.example.rowin.microbrewerysearcher.model;

/**
 * Created by Rowin on 9/29/2017.
 */

public class Beer {
    private String beerName;
    private String style;
    private Integer volume;
    private Integer alcohol;
    private String keg;

    public Beer(String beerName, String style, Integer volume, Integer alcohol, String keg){
        this.beerName = beerName;
        this.style = style;
        this.volume = volume;
        this.alcohol = alcohol;
        this.keg = keg;
    }

    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Integer getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(Integer alcohol) {
        this.alcohol = alcohol;
    }

    public String getKeg() {
        return keg;
    }

    public void setKeg(String keg) {
        this.keg = keg;
    }
}
