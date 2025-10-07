/***********************************************
 *** Author:    Jonathan Carrière            ***
 *** Date:      2024-08-20                   ***
 *** File:      VideoGame.java               ***
 *** Project:   PlayList                     ***
 ***********************************************/

package com.jonathan.playlist;

import java.time.LocalDate;

/**
 * Class representing a video game within one or many categories
 */
@SuppressWarnings("unused")
public class VideoGame {

    // ID of the video game
    private int id;

    // Title of the video game
    private String title;

    // Platform of the video game
    private String platform;

    // Price of the video game
    private double price;

    // Publisher of the video game
    private String publisher;

    // Release date of the video game
    private LocalDate releaseDate;

    // Completion date of the video game
    private LocalDate completionDate;

    // Playtime in hours of the video game
    private int playtime;

    // Category status of the video game in the backlog
    private boolean isBacklog;

    // Category status of the video game in the collection
    private boolean isCollection;

    // Category status of the video game in the completion list
    private boolean isCompletion;

    // Category status of the video game in the wishlist
    private boolean isWishlist;

    // Image path of the video game
    private String imagePath;

    /**
     * Constructor with parameters for the VideoGame class
     * @param id ID of the video game
     * @param title Title of the video game
     * @param platform Platform of the video game
     * @param price Price of the video game
     * @param publisher Publisher of the video game
     * @param releaseDate Release date of the video game
     * @param completionDate Completion date of the video game
     * @param playtime Playtime in hours of the video game
     * @param isBacklog Category status of the video game in the backlog
     * @param isCollection Category status of the video game in the collection
     * @param isCompletion Category status of the video game in the completion list
     * @param isWishlist Category status of the video game in the wishlist
     * @param imagePath Image path of the video game
     */
    public VideoGame(int id, String title, String platform, String publisher, LocalDate releaseDate, LocalDate completionDate, int playtime, double price, boolean isBacklog, boolean isCollection, boolean isCompletion, boolean isWishlist, String imagePath) {

        // Association between the parameters and the attributes of the VideoGame class
        this.id = id;
        this.title = title;
        this.platform = platform;
        this.publisher = publisher;
        this.releaseDate = releaseDate;
        this.completionDate = completionDate;
        this.playtime = playtime;
        this.price = price;
        this.isBacklog = isBacklog;
        this.isCollection = isCollection;
        this.isCompletion = isCompletion;
        this.isWishlist = isWishlist;
        this.imagePath = imagePath;

    }

    /**
     * Getter for the ID attribute
     * @return ID of the video game
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the ID attribute
     * @param id ID of the video game
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the title attribute
     * @return Title of the video game
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title attribute
     * @param title Title of the video game
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the platform attribute
     * @return Platform of the video game
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Setter for the platform attribute
     * @param platform Platform of the video game
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * Getter for the price attribute
     * @return Price of the video game
     */
    public double getPrice() {
        return price;
    }

    /**
     * Setter for the price attribute
     * @param price Price of the video game
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Getter for the publisher attribute
     * @return Publisher of the video game
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Setter for the publisher attribute
     * @param publisher Publisher of the video game
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Getter for the releaseDate attribute
     * @return Release date of the video game
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    /**
     * Setter for the releaseDate attribute
     * @param releaseDate Release date of the video game
     */
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Getter for the completionDate attribute
     * @return Completion date of the video game
     */
    public LocalDate getCompletionDate() {
        return completionDate;
    }

    /**
     * Setter for the completionDate attribute
     * @param completionDate Completion date of the video game
     */
    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    /**
     * Getter for the playtime attribute
     * @return Playtime in hours of the video game
     */
    public int getPlaytime() {
        return playtime;
    }

    /**
     * Setter for the playtime attribute
     * @param playtime Playtime in hours of the video game
     */
    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }

    /**
     * Getter for the isBacklog attribute
     * @return Category status of the video game in the backlog
     */
    public boolean isBacklog() {
        return isBacklog;
    }

    /**
     * Setter for the isBacklog attribute
     * @param isBacklog Category status of the video game in the backlog
     */
    public void setBacklog(boolean isBacklog) {
        this.isBacklog = isBacklog;
    }

    /**
     * Getter for the isCollection attribute
     * @return Category status of the video game in the collection
     */
    public boolean isCollection() {
        return isCollection;
    }

    /**
     * Setter for the isCollection attribute
     * @param isCollection Category status of the video game in the collection
     */
    public void setCollection(boolean isCollection) {
        this.isCollection = isCollection;
    }

    /**
     * Getter for the isCompletion attribute
     * @return Category status of the video game in the completion list
     */
    public boolean isCompletion() {
        return isCompletion;
    }

    /**
     * Setter for the isCompletion attribute
     * @param isCompletion Category status of the video game in the completion list
     */
    public void setCompletion(boolean isCompletion) {
        this.isCompletion = isCompletion;
    }

    /**
     * Getter for the isWishlist attribute
     * @return Category status of the video game in the wishlist
     */
    public boolean isWishlist() {
        return isWishlist;
    }

    /**
     * Setter for the isWishlist attribute
     * @param isWishlist Category status of the video game in the wishlist
     */
    public void setWishlist(boolean isWishlist) {
        this.isWishlist = isWishlist;
    }

    /**
     * Getter for the imagePath attribute
     * @return Image path of the video game
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Setter for the imagePath attribute
     * @param imagePath Image path of the video game
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
