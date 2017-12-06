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
    public final String channelToListen = ""; //The Channel you want t the bot to output in it's console (Preferably the one you'll be controlling it in)

    /**Website Definitions**/
    // You shouldn't have to mess with these, its really just something to make my life a bit easier
    public final String toaLive = "http://theorangealliance.org/home";
    public final String toaLiveAPIV2 = "http://theorangealliance.org:8009/apiv2";
    public final String toaBeta = "http://beta.theorangealliance.org/home";
    public final String toaBetaAPI = "http://beta.theorangealliance.org/apiv2";
}
