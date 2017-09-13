package com.SPZProductions.java.dicord;

/**
 * Created by SPZ Productions on 9/12/2017.
 *
 * REFACTOR THIS AS 'secrets.java' IN YOUR MAIN DIRECTORY
 * AFTER PUTTING IN YOUR API/BOT/CHANNEL KEY IN,
 * BOT WILL NOT WORK WITHOUT IT
 * All fields are required in order for bot to work
 */


public class secrets_example {

    /**Auth Stuff**/
    //TOA Auth
    public final String apiKey = ""; //TOA API Key
    //Discord Auth
    public final String botKey = ""; //DISCORD Oath2 Token
    //Github Auth
    public final String githubClientID = ""; //Github Cliend ID
    public final String githubClientSecret = ""; //GitHub Oath2 token
    public final String githubUsername = "";//Github Username
    /**Discord Server Stuff**/
    public final String serverStatusRoleID = ""; //The role to @ when server status changes
    public final String toaAdminRoleID = ""; //The role to inform the admin when the server status changes
    public final String spamChannelID = ""; //The Channel in which the ?giveme command can/should be performed
    //TODO: Change this to role rather than specific user because this is kinda dumb
    public final String[] admins = {"", "", "", ""};//All people allowed to use the bot

}
