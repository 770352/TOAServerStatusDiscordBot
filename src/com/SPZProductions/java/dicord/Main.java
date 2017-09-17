package com.SPZProductions.java.dicord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * THIS IS A TEMPLATE MADE BY https://github.com/DV8FromTheWorld/JDA
 * NOT BY SPZ PRODUCTIONS
 * I JUST CHANGED IT A LITTLE
 * THANKS GUYS
 */

public class Main extends ListenerAdapter{

    private String lastMessageID = null;
    private boolean TOAOrgOnline = true;
    private boolean TOAAPIOrgOnline = true;
    private boolean TOAAPIv2OrgOnline = true;
    private boolean TOABetaOnline = true;
    private boolean TOABetaAPIOnline = true;
    private boolean TOAYelOnline = true;
    private boolean updateGithub = true;
    private String APIDLv = null;
    private String BDUPLv = null;
    private String WEBAPv = null;
    private MessageChannel postingChannel = null;
    private loop loop1 = new loop();
    private Thread thread = new Thread(loop1);
    /**IDK Why I have to do this...**/
    private secrets secret = new secrets();
    private static secrets secretStatic = new secrets();

    public static void main(String[] args) {
        try {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(secretStatic.botKey)  //Bot Key
                    .addEventListener(new Main())  //An instance of a class that will handle events.
                    .buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.
        } catch (LoginException e) {
            e.printStackTrace(); //If anything goes wrong in terms of authentication, this is the exception that will represent it
        }
        catch (InterruptedException e) {
            //Due to the fact that buildBlocking is a blocking method, one which waits until JDA is fully loaded,
            // the waiting can be interrupted. This is the exception that would fire in that situation.
            //As a note: in this extremely simplified example this will never occur. In fact, this will never occur unless
            // you use buildBlocking in a thread that has the possibility of being interrupted (async thread usage and interrupts)
            e.printStackTrace();
        } catch (RateLimitedException e) {
            //The login process is one which can be ratelimited. If you attempt to login in multiple times, in rapid succession
            // (multiple times a second), you would hit the ratelimit, and would see this exception.
            //As a note: It is highly unlikely that you will ever see the exception here due to how infrequent login is.
            e.printStackTrace();
        }
    }

    public class loop implements Runnable {

        public boolean runLoop = false;
        public int waitTime = 60;

        @Override
        public void run() {
            while(true){
                if(runLoop){
                    testForUp();
                    try {
                        Thread.sleep(waitTime * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        postingChannel.sendMessage("Loop stopped via an InterruptedException. Check log for more details").complete();
                    }
                }
            }
        }

        private void testForUp(){
            if(Boolean.valueOf(checkWeb(secret.toaBeta).get(0)) != TOABetaOnline){
                update("");
                System.out.println("Updated because of TOA Beta Web");
            }else if(Boolean.valueOf(checkWeb(secret.toaLive).get(0)) != TOAOrgOnline){
                update("");
                System.out.println("Updated because of TOA Live Web");
            }else if(Boolean.valueOf(checkWeb(secret.toaDeve).get(0)) != TOAYelOnline){
                update("");
                System.out.println("Updated because of TYA Dev Web");
            }else if(Boolean.valueOf(checkAPI(secret.toaBetaAPI).get(0)) != TOABetaAPIOnline){
                update("");
                System.out.println("Updated because of TOA Beta API");
            }else if (Boolean.valueOf(checkAPI(secret.toaLiveAPI).get(0)) != TOAAPIOrgOnline){
                update("");
                System.out.println("Updated because of TOA Live API");
            }else if (Boolean.valueOf(checkAPI(secret.toaLiveAPIV2).get(0)) != TOAAPIv2OrgOnline){
                update("");
                System.out.println("Updated because of TOA Live APIv2");
            }else if (updateGithub){
                update("");
                updateGithub = false;
                System.out.println("Updated because of Github");
            }
        }

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //These are provided with every event in JDA
        JDA jda = event.getJDA();                       //JDA, the core of the api.

        //Event specific information
        User author = event.getAuthor();                //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.
        Guild guild = event.getGuild();                 //The Guild that this message was sent in. (note, in the API, Guilds are Servers)

        String msg = message.getContent();              //This returns a human readable version of the Message. Similar to what you would see in the client.

        if (event.isFromType(ChannelType.TEXT) && channel.getId().equals(secret.channelToListen)){ //If this message was sent to a Guild TextChannel
            TextChannel textChannel = event.getTextChannel(); //The TextChannel that this message was sent to.
            Member member = event.getMember();          //This Member that sent the message. Contains Guild specific information about the User!
            String name = member.getEffectiveName();       //This will either use the Member's nickname if they have one, otherwise it will default to their username. (User#getName())
            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
        }

        if(checkClientID(guild, jda, message) && msg.startsWith("!") && channel.getId().equals(secret.channelToListen)){
            message.delete().complete();

            if(msg.equals("!stop stats")){
                stopProgram(channel);

            }else if(msg.equals("!post here")){
                setPostChannel(channel);

            }else if(postingChannel != null){
                if (msg.startsWith("!check")) {
                    update(msg);

                }else if(msg.equals("!github")){
                    githubCommand(channel);

                }else if(msg.contains("!loop ")){
                    loopCommand(msg);

                }else if(msg.contains("!stop loop")){
                    stopLoop();
                }
            }else if(msg.contains("!loop ") || msg.equals("!github") || msg.equals("!stop loop")){
                postNotSet(channel);
            }
        }

    }

    List<String> checkWeb(String sUrl){
        List<String> returnValue = new ArrayList<>();
        try {
            HttpURLConnection.setFollowRedirects(false);
            //HttpURLConnection.setInstanceFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(sUrl)
                    .openConnection();
            con.setRequestMethod("HEAD");
            if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
                returnValue.add("true");
                returnValue.add("200");
            }else{
                returnValue.add("false");
                returnValue.add(con.getResponseCode() + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    List<String> checkAPI(String sUrl) {
        List<String> returnValue = new ArrayList<>();
        try {
            URL url = new URL(sUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            con.setRequestProperty("X-TOA-Key", secret.apiKey);//API Key
            con.setRequestProperty("X-Application-Origin", "TOA Discord Status Bot");

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                returnValue.add("true");
                returnValue.add("200");
            }else{
                returnValue.add("false");
                returnValue.add(responseCode + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    private String checkVersionAPIDownloader(){
        String sUrl = "https://api.github.com/repos/orange-alliance/TOAApiDowloader/releases/latest?client_id=" + secret.githubClientID + "&client_secret=" + secret.githubClientSecret;
        String version = null;
        try {
            version = returnVersion(new URL(sUrl));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    private String checkVersionDataUploader(){
        String sUrl = "https://api.github.com/repos/orange-alliance/TOADataImporter/releases/latest?client_id=" + secret.githubClientID + "&client_secret=" + secret.githubClientSecret;
        String version = null;
        try {
            version = returnVersion(new URL(sUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    private String returnVersion(URL url) throws IOException {
        BufferedReader in = in(url);

        String output;
        StringBuilder response = new StringBuilder();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();

        JSONObject json = new JSONObject(response + "");
        return json.get("tag_name").toString();
    }

    private String checkWebAPIVersion() {
        StringBuilder response = new StringBuilder();
        try {
            BufferedReader in = in(new URL("https://api.github.com/repos/orange-alliance/the-orange-alliance/milestones?client_id=" + secret.githubClientID + "&client_secret=" + secret.githubClientSecret));

            String output;

            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray json = new JSONArray(response + "");
        int size = json.length();
        size--;
        size--;

        return json.getJSONObject(size).get("title").toString();
    }

    private BufferedReader in(URL url) throws IOException {

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("token", secret.githubClientSecret);
        con.setRequestProperty("X-OAuth-Scopes", "the-orange-alliance, orange-alliance");
        con.setRequestProperty("X-Accepted-OAuth-Scopes", secret.githubClientID);
        con.setRequestProperty("user", secret.githubUsername);

        return new BufferedReader(new InputStreamReader(con.getInputStream()));
    }

    private boolean checkClientID(Guild guild, JDA jda, Message message){
        return guild.getMembersWithRoles(jda.getRolesByName(secret.roleAllowed, true)).contains(message.getMember());
    }

    private void forceUpdateGithubVariables(){
        APIDLv = checkVersionAPIDownloader();
        BDUPLv = checkVersionDataUploader();
        WEBAPv = checkWebAPIVersion();
        updateGithub = false;
    }

    private void update(String msg){
        if(lastMessageID != null && postingChannel != null){
            try{
                postingChannel.deleteMessageById(lastMessageID).complete();
            }catch(ErrorResponseException e){
                System.out.println("Couldn't Delete the Last Status Message. Skipping...");
            }
        }

        List<String> stats = new ArrayList<>();
        List<String> postMessage = new ArrayList<>();
        StringBuilder statistics = new StringBuilder();

        stats.add("-----------SERVER STATUS-----------");

        if(checkWeb(secret.toaLive).get(0).equals("true")){
            stats.add("theorangealliance.org - :white_check_mark:");
            if(!TOAOrgOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Main Orange Alliance Web Server is back online.");
            }
            TOAOrgOnline = true;
        }else{
            stats.add("theorangealliance.org - :x: (Response Code: " + checkWeb(secret.toaLive).get(1) + ")" );
            if(TOAOrgOnline && !msg.contains("--noping")){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Main Orange Alliance Web Server is offline.  <@&" + secret.toaAdminRoleID + "> are investigating the issue now.");
            }else{
                postMessage.add("The Main Orange Alliance Web Server is still offline.  Investigation in progress.");
            }
            TOAOrgOnline = false;
        }

        if(checkAPI(secret.toaLiveAPI).get(0).equals("true")){
            stats.add("theorangealliance.org/api - :white_check_mark:");
            if(!TOAAPIOrgOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Main Orange Alliance API is back online.");
            }
            TOAAPIOrgOnline = true;
        }else{
            stats.add("theorangealliance.org/api - :x: (Response Code: " + checkWeb("http://theorangealliance.org/api").get(1) + ")");
            if(TOAAPIOrgOnline && !msg.contains("--noping")){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Main Orange Alliance API is offline.  <@&" + secret.toaAdminRoleID + "> are investigating the issue now.");
            }else{
                postMessage.add("The Main Orange Alliance API is still offline.  Investigation in progress.");
            }
            TOAAPIOrgOnline = false;
        }

        if(checkAPI(secret.toaLiveAPIV2).get(0).equals("true")){
            stats.add("theorangealliance.org/apiv2 - :white_check_mark:");
            if(!TOAAPIv2OrgOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Main Orange Alliance APIv2 is back online.");
            }
            TOAAPIv2OrgOnline = true;
        }else{
            stats.add("theorangealliance.org/apiv2 - :x: (Response Code: " + checkWeb(secret.toaLiveAPIV2).get(1) + ")");
            if(TOAAPIv2OrgOnline && !msg.contains("--noping")){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Main Orange Alliance APIv2 is offline.  <@&" + secret.toaAdminRoleID + "> are investigating the issue now.");
            }else{
                postMessage.add("The Main Orange Alliance APIv2 is still offline.  Investigation in progress.");
            }
            TOAAPIv2OrgOnline = false;
        }

        if(checkWeb(secret.toaBeta).get(0).equals("true")){
            stats.add("beta.theorangealliance.org - :white_check_mark:");
            if(!TOABetaOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Beta Orange Alliance Web Server is back online.");
            }
            TOABetaOnline = true;
        }else{
            stats.add("beta.theorangealliance.org - :x: (Response Code: " + checkWeb(secret.toaBeta).get(1) + ")" );
            if(TOABetaOnline && !msg.contains("--noping")){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Beta Orange Alliance Web Server is offline.  <@&" + secret.toaAdminRoleID + "> are investigating the issue now.");
            }else{
                postMessage.add("The Beta Orange Alliance Web Server is still offline.  Investigation in progress.");
            }
            TOABetaOnline = false;
        }

        if(checkAPI(secret.toaBetaAPI).get(0).equals("true")){
            stats.add("beta.theorangealliance.org/api - :white_check_mark:");
            if(!TOABetaAPIOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Beta Orange Alliance API is back online.");
            }
            TOABetaAPIOnline = true;
        }else{
            stats.add("beta.theorangealliance.org/api - :x: (Response Code: " + checkWeb(secret.toaBetaAPI).get(1) + ")");
            if(TOABetaAPIOnline && !msg.contains("--noping")){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Beta Orange Alliance API is offline.  <@&" + secret.toaAdminRoleID + "> are investigating the issue now.");
            }else{
                postMessage.add("The Beta Orange Alliance API is still offline.  Investigation in progress.");
            }
            TOABetaAPIOnline = false;
        }

        if(checkWeb(secret.toaDeve).get(0).equals("true")){
            stats.add("dev.theyellowalliance.com - :white_check_mark:");
            if(!TOAYelOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Dev Orange Alliance Web Server is back online.");
            }
            TOAYelOnline = true;
        }else{
            stats.add("dev.theyellowalliance.com - :x: (Response Code: " + checkWeb(secret.toaDeve).get(1) + ")");
            if(TOAYelOnline && !msg.contains("--noping")){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Dev Orange Alliance Web Server is offline.  <@&" + secret.toaAdminRoleID + "> are investigating the issue now.");
            }else{
                postMessage.add("The Dev Orange Alliance Web Server is still offline.  Investigation in progress.");
            }
            TOAYelOnline = false;
        }
        if(updateGithub){
            forceUpdateGithubVariables();
            updateGithub = false;
        }
        stats.add("--------VERSION TRACKING--------");
        String ver = "`v" + WEBAPv.substring(8) + "`";
        stats.add("Live Web App: " + ver);
        stats.add("Live API: " + ver);
        stats.add("TOA API Downloader: `v" + APIDLv + "`");
        stats.add("TOA Data Uploader: `v" + BDUPLv + "`");

        postMessage.add("To be notified about server outages, do `?giveme Server Alerting` in <#" + secret.spamChannelID + ">");


        for(String stat : stats){
            statistics.append(stat);
            statistics.append("\n");
        }

        for(String pm : postMessage){
            statistics.append(pm);
            statistics.append("\n");
        }

        if(postingChannel != null){
            lastMessageID = postingChannel.sendMessage(statistics.toString()).complete().getId();
        }
    }

    private void stopProgram(MessageChannel channel){
        channel.sendMessage(":wave:").complete();
        System.exit(0);
    }

    private void setPostChannel(MessageChannel channel){
        postingChannel = channel;
        String delete = postingChannel.sendMessage("Channel set to <#" + postingChannel.getId() + ">").complete().getId();
        postingChannel.deleteMessageById(delete).queueAfter(10, TimeUnit.SECONDS);
    }

    private void githubCommand(MessageChannel channel){
        String delete = channel.sendMessage("Next manual `!check` or auto check will update the versions for all of the programs.").complete().getId();
        channel.deleteMessageById(delete).queueAfter(10, TimeUnit.SECONDS);
        updateGithub = true;
    }

    private void loopCommand(String msg){
        update("");
        try{
            if(!thread.isAlive()){
                thread.start();
            }
            loop1.waitTime = 60;
            loop1.waitTime = Integer.parseInt(msg.substring(6));
        }catch(Exception e){
            e.printStackTrace();
        }
        loop1.runLoop = true;
    }

    private void stopLoop(){
        if(loop1.runLoop){
            loop1.runLoop = false;
            String delete = postingChannel.sendMessage("Check Loop Stopped").complete().getId();
            postingChannel.deleteMessageById(delete).queueAfter(10, TimeUnit.SECONDS);
        }else{
            String delete = postingChannel.sendMessage("Check Loop Not Running!  Type `!loop [Check Interval}` to start.").complete().getId();
            postingChannel.deleteMessageById(delete).queueAfter(10, TimeUnit.SECONDS);
        }
    }

    private void postNotSet(MessageChannel channel){
        String delete = channel.sendMessage("Posting Channel not set. Type `!post here` in your desired channel to set.").complete().getId();
        channel.deleteMessageById(delete).queueAfter(10, TimeUnit.SECONDS);
    }

    private void playWithEmbeds(){

    }
}
