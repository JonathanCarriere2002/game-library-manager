/***********************************************
 *** Author:    Jonathan Carrière            ***
 *** Date:      2024-08-20                   ***
 *** File:      MySQLiteOpenHelper.java      ***
 *** Project:   PlayList                     ***
 ***********************************************/

package com.jonathan.playlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Class pertaining to the management of the database queries
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PlayList.db";
    private static final int DATABASE_VERSION = 1;

    // Video game table columns
    private static final String TABLE_VIDEO_GAMES = "video_games";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_PLATFORM = "platform";
    private static final String COLUMN_PUBLISHER = "publisher";
    private static final String COLUMN_RELEASE_DATE = "release_date";
    private static final String COLUMN_COMPLETION_DATE = "completion_date";
    private static final String COLUMN_PLAYTIME = "playtime";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IS_BACKLOG = "is_backlog";
    private static final String COLUMN_IS_COLLECTION = "is_collection";
    private static final String COLUMN_IS_COMPLETION = "is_completion";
    private static final String COLUMN_IS_WISHLIST = "is_wishlist";
    private static final String COLUMN_IMAGE_PATH = "image_path";

    /**
     * Constructor for MySQLiteOpenHelper with parameters
     * @param context Context of the applications used in the management of the database
     */
    MySQLiteOpenHelper(@Nullable Context context) {

        // Initialization of the MySQLiteOpenHelper
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * Methode executed upon creation of the database allowing to create the necessary tables
     * @param db SQLite database in which the tables will be created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Query allowing to create the video game table
        String query =
                "CREATE TABLE " + TABLE_VIDEO_GAMES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_TITLE + " VARCHAR(100) NOT NULL, " +
                COLUMN_PLATFORM + " VARCHAR(50) NOT NULL, " +
                COLUMN_PUBLISHER + " VARCHAR(50) NOT NULL, " +
                COLUMN_RELEASE_DATE + " DATE NOT NULL, " +
                COLUMN_COMPLETION_DATE + " DATE, " +
                COLUMN_PLAYTIME + " INTEGER CHECK (" + COLUMN_PLAYTIME + " <= 10000), " +
                COLUMN_PRICE + " REAL CHECK (" + COLUMN_PRICE + " <= 10000) NOT NULL, " +
                COLUMN_IS_BACKLOG + " INTEGER NOT NULL, " +
                COLUMN_IS_COLLECTION + " INTEGER NOT NULL, " +
                COLUMN_IS_COMPLETION + " INTEGER NOT NULL, " +
                COLUMN_IS_WISHLIST + " INTEGER NOT NULL, " +
                COLUMN_IMAGE_PATH + " TEXT);";
        db.execSQL(query);

    }

    /**
     * Method executed upon upgrading the database in order to drop any existing tables and recreate them
     * @param db SQLite database in which the tables will be created
     * @param oldVersionNumber Number associated to the previous version of the database
     * @param newVersionNumber Number associated to the new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersionNumber, int newVersionNumber) {

        // Query allowing to drop all tables and recreate them
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO_GAMES);
        onCreate(db);

    }

    /**
     * Method allowing to read a specific video game from the database
     * @param id Id of the video game to read
     * @return ArrayList containing the specified video game from the database
     */
    ArrayList<VideoGame> readOneVideoGame(int id) {

        // Query allowing to read a specific video game from the database
        String query = "SELECT * FROM " + TABLE_VIDEO_GAMES + " WHERE " + COLUMN_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor containing the specified video game from the database
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }

        // Return ArrayList containing the specified video game from the database
        return mapCursorToVideoGames(cursor);

    }

    /**
     * Method allowing to read the amount of video games from the database
     * @return Amount of video games from the database
     */
    int readVideoGameCount() {

        // Query allowing to read all video games from the database
        String query = "SELECT * FROM " + TABLE_VIDEO_GAMES;
        SQLiteDatabase db = this.getReadableDatabase();

        // Initialize the count
        int videoGameCount = 0;

        // Cursor containing all video games from the database
        Cursor cursor;
        if (db != null) {
            cursor = db.rawQuery(query, null);
            // Calculate the count of video games from the cursor
            if (cursor != null && cursor.moveToFirst()) {
                videoGameCount = cursor.getInt(0);
            }
            assert cursor != null;
            cursor.close();
        }

        // Return the amount of video games in the database
        return videoGameCount;

    }

    /**
     * Method allowing to read all video games within the backlog from the database
     * @param sortColumn Column by which the video games will be sorted
     * @param sortOrder Order in which the video games will be sorted
     * @return ArrayList containing all video games within the backlog from the database
     */
    ArrayList<VideoGame> readAllVideoGamesBacklog(String sortColumn, String sortOrder) {

        // Query allowing to read all video games within the backlog from the database
        String query = "SELECT * FROM " + TABLE_VIDEO_GAMES + " WHERE " + COLUMN_IS_BACKLOG + " = 1" + " ORDER BY " + sortColumn + sortOrder + ", title ASC";
        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor containing all video games within the backlog from the database
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }

        // Return ArrayList containing all video games within the backlog from the database
        return mapCursorToVideoGames(cursor);

    }

    /**
     * Method allowing to read all video games within the collection from the database
     * @param sortColumn Column by which the video games will be sorted
     * @param sortOrder Order in which the video games will be sorted
     * @return ArrayList containing all video games within the collection from the database
     */
    ArrayList<VideoGame> readAllVideoGamesCollection(String sortColumn, String sortOrder) {

        // Query allowing to read all video games within the collection from the database
        String query = "SELECT * FROM " + TABLE_VIDEO_GAMES + " WHERE " + COLUMN_IS_COLLECTION + " = 1" + " ORDER BY " + sortColumn + sortOrder + ", title ASC";
        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor containing all video games within the collection from the database
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }

        // Return ArrayList containing all video games within the collection from the database
        return mapCursorToVideoGames(cursor);

    }

    /**
     * Method allowing to read all video games within the completion list from the database
     * @param sortColumn Column by which the video games will be sorted
     * @param sortOrder Order in which the video games will be sorted
     * @return ArrayList containing all video games within the completion list from the database
     */
    ArrayList<VideoGame> readAllVideoGamesCompletion(String sortColumn, String sortOrder) {

        // Query allowing to read all video games within the completion list from the database
        String query = "SELECT * FROM " + TABLE_VIDEO_GAMES + " WHERE " + COLUMN_IS_COMPLETION + " = 1" + " ORDER BY " + sortColumn + sortOrder + ", title ASC";
        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor containing all video games within the completion list from the database
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }

        // Return ArrayList containing all video games within the completion list from the database
        return mapCursorToVideoGames(cursor);

    }

    /**
     * Method allowing to read all video games within the wishlist from the database
     * @param sortColumn Column by which the video games will be sorted
     * @param sortOrder Order in which the video games will be sorted
     * @return ArrayList containing all video games within the wishlist from the database
     */
    ArrayList<VideoGame> readAllVideoGamesWishlist(String sortColumn, String sortOrder) {

        // Query allowing to read all video games within the wishlist from the database
        String query = "SELECT * FROM " + TABLE_VIDEO_GAMES + " WHERE " + COLUMN_IS_WISHLIST + " = 1" + " ORDER BY " + sortColumn + sortOrder + ", title ASC";
        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor containing all video games within the wishlist from the database
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }

        // Return ArrayList containing all video games within the wishlist from the database
        return mapCursorToVideoGames(cursor);

    }

    /**
     * Method that calculates the total number of categories in which a specific video game is present
     * @param id Id of the video game that will be checked
     * @return Total number of categories in which the video game is present
     */
    public int getCategoryStatusTotal(int id) {

        // Obtain the database in which the new video game will be inserted and initialize the total
        SQLiteDatabase db = this.getReadableDatabase();
        int total = 0;

        // Query allowing to calculate the total number of categories in which a specific video game is present
        String query = "SELECT " + COLUMN_IS_BACKLOG + ", " + COLUMN_IS_COLLECTION + ", " + COLUMN_IS_COMPLETION + ", " + COLUMN_IS_WISHLIST + " FROM " + TABLE_VIDEO_GAMES + " WHERE " + COLUMN_ID + " = ?";

        // Cursor containing the total number of categories in which a specific video game is present
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor != null) {

            // Calculate the total number of categories in which a specific video game is present
            if (cursor.moveToFirst()) {
                total = cursor.getInt(0) + cursor.getInt(1) + cursor.getInt(2) + cursor.getInt(3);
            }

            // Close the cursor
            cursor.close();

        }

        // Return the total number of categories in which a specific video game is present
        return total;

    }

    /**
     * Method allowing to create a new video game within the database
     * @param title Title of the new video game
     * @param platform Platform of the new video game
     * @param publisher Publisher of the new video game
     * @param releaseDate Release date of the new video game
     * @param completionDate Completion date of the new video game
     * @param playtime Playtime of the new video game
     * @param price Price of the new video game
     * @param isBacklog Is the new video game in the backlog?
     * @param isCollection Is the new video game in the collection?
     * @param isCompletion Is the new video game in the completion list?
     * @param isWishlist Is the new video game in the wishlist?
     * @param imagePath Image path of the new video game
     * @return Boolean indicating the success of the insertion of the new video game
     */
    Boolean addVideoGame(String title, String platform, String publisher, String releaseDate, String completionDate, int playtime, double price, boolean isBacklog, boolean isCollection, boolean isCompletion, boolean isWishlist, String imagePath){

        // Obtain the database in which the new video game will be inserted
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Associating the values received as parameters to the content values
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_PLATFORM, platform);
        cv.put(COLUMN_PUBLISHER, publisher);
        cv.put(COLUMN_RELEASE_DATE, releaseDate);
        cv.put(COLUMN_COMPLETION_DATE, completionDate);
        cv.put(COLUMN_PLAYTIME, playtime);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_IS_BACKLOG, isBacklog ? 1 : 0);
        cv.put(COLUMN_IS_COLLECTION, isCollection ? 1 : 0);
        cv.put(COLUMN_IS_COMPLETION, isCompletion ? 1 : 0);
        cv.put(COLUMN_IS_WISHLIST, isWishlist ? 1 : 0);
        cv.put(COLUMN_IMAGE_PATH, imagePath);

        // Inserting the new video game within the database and returning the appropriate result
        long result = db.insert(TABLE_VIDEO_GAMES,null, cv);
        return result != -1;

    }

    /**
     * Method allowing to update an existing video game within the database
     * @param id Id of the video game to update
     * @param title Title of the video game to update
     * @param platform Platform of the video game to update
     * @param publisher Publisher of the video game to update
     * @param releaseDate Release date of the video game to update
     * @param completionDate Completion date of the video game to update
     * @param playtime Playtime of the video game to update
     * @param price Price of the video game to update
     * @param isBacklog Is the video game in the backlog?
     * @param isCollection Is the video game in the collection?
     * @param isCompletion Is the video game in the completion list?
     * @param isWishlist Is the video game in the wishlist?
     * @param imagePath Image path of the video game to update
     * @return Boolean indicating the success of the update of the video game
     */
    Boolean updateVideoGame(int id, String title, String platform, String publisher, String releaseDate, String completionDate, int playtime, double price, boolean isBacklog, boolean isCollection, boolean isCompletion, boolean isWishlist, String imagePath){

        // Obtain the database in which the video game will be updated
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Associating the values received as parameters to the content values
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_PLATFORM, platform);
        cv.put(COLUMN_PUBLISHER, publisher);
        cv.put(COLUMN_RELEASE_DATE, releaseDate);
        cv.put(COLUMN_COMPLETION_DATE, completionDate);
        cv.put(COLUMN_PLAYTIME, playtime);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_IS_BACKLOG, isBacklog ? 1 : 0);
        cv.put(COLUMN_IS_COLLECTION, isCollection ? 1 : 0);
        cv.put(COLUMN_IS_COMPLETION, isCompletion ? 1 : 0);
        cv.put(COLUMN_IS_WISHLIST, isWishlist ? 1 : 0);
        cv.put(COLUMN_IMAGE_PATH, imagePath);

        // Updating the video game within the database and returning the appropriate result
        long result = db.update(TABLE_VIDEO_GAMES, cv, "_id=?", new String[]{String.valueOf(id)});
        return result != -1;

    }

    /**
     * Method allowing to add or remove a video game from a specific category within the database
     * @param category Category from which the game will be added or removed
     * @param id Id of the video game to be added or removed
     * @param status Indicates if the game is already or is not already in the category
     * @return Status of the category update for the video game
     */
    Boolean updateCategoryStatus(String category, int id, int status) {

        // Obtain the database in which the video game will be updated
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Update the status of the video game in the specified category using a switch case statement
        switch (category) {
            case "backlog":
                cv.put(COLUMN_IS_BACKLOG, status);
                break;
            case "collection":
                cv.put(COLUMN_IS_COLLECTION, status);
                break;
            case "completion":
                cv.put(COLUMN_IS_COMPLETION, status);
                break;
            case "wishlist":
                cv.put(COLUMN_IS_WISHLIST, status);
                break;
        }

        // Update the video game within the database and return the appropriate result
        long result = db.update(TABLE_VIDEO_GAMES, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return result != -1;

    }

    /**
     * Method allowing to delete a specific video game from the database
     * @param id Id of the video game that will be deleted
     * @return Boolean indicating the success of the deletion of the video game
     */
    Boolean deleteOneVideoGame(int id) {

        // Obtain the database in which the video game will be deleted
        SQLiteDatabase db = this.getWritableDatabase();

        // Deleting the specified video game from the database and returning the appropriate result
        long result = db.delete(TABLE_VIDEO_GAMES, "_id=?", new String[]{String.valueOf(id)});
        return result != -1;

    }

    /**
     * Method allowing to delete all video games from the database
     * @return Boolean indicating the success of the deletion of all video games
     */
    Boolean deleteAllVideoGames() {

        // Obtain the database in which all video games will be deleted
        SQLiteDatabase db = this.getWritableDatabase();

        // Query allowing to delete all video games from the database
        db.execSQL("DELETE FROM " + TABLE_VIDEO_GAMES);

        // Verify that all video games were successfully deleted and return the appropriate value
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_VIDEO_GAMES, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int rowCount = cursor.getInt(0);
            cursor.close();
            return rowCount == 0;
        }
        return false;

    }

    /**
     * Method allowing to map a Cursor object to an Arraylist of video games
     * @param cursor Cursor containing the video games to be mapped
     * @return ArrayList containing the mapped video games
     */
    public ArrayList<VideoGame> mapCursorToVideoGames(Cursor cursor) {

        // Initialize an ArrayList to store the mapped video games
        ArrayList<VideoGame> videoGames = new ArrayList<>();

        // Verify that the cursor is not null and move it to the first position
        if (cursor != null && cursor.moveToFirst()) {

            // Iterate through the cursor and add each video game to the ArrayList
            do {

                // Extract the values from the cursor and create a new VideoGame object
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String platform = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLATFORM));
                String publisher = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISHER));
                String releaseDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RELEASE_DATE));
                LocalDate releaseDate = releaseDateStr != null ? LocalDate.parse(releaseDateStr) : null;
                String completionDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPLETION_DATE));
                LocalDate completionDate = completionDateStr != null ? LocalDate.parse(completionDateStr) : null;
                int playtime = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAYTIME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                boolean isBacklog = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_BACKLOG)) > 0;
                boolean isCollection = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COLLECTION)) > 0;
                boolean isCompletion = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETION)) > 0;
                boolean isWishlist = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_WISHLIST)) > 0;
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH));

                // Initialize the new VideoGame object and add it to the ArrayList
                VideoGame videoGame = new VideoGame(id, title, platform, publisher, releaseDate, completionDate, playtime, price, isBacklog, isCollection, isCompletion, isWishlist, imagePath);
                videoGames.add(videoGame);

            }

            // Continue iterating trough the cursor until moveToNext() returns false
            while (cursor.moveToNext());

            // Close the cursor at the end of the loop
            cursor.close();

        }

        // Return the ArrayList of video games
        return videoGames;

    }

}
