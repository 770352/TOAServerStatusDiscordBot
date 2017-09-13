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
    private boolean TOABetaOnline = true;
    private boolean TOABetaAPIOnline = true;
    private boolean TOAYelOnline = true;
    private boolean updateGithub = true;
    private String APIDLv = null;
    private String BDUPLv = null;
    private String WEBAPv = null;
    MessageChannel postingChannel = null;
    private loop loop1 = new loop();
    Thread thread = new Thread(loop1);
    /**IDK Why I have to do this...**/
    secrets secret = new secrets();
    static secrets secretStatic = new secrets();

    public static void main(String[] args) {
        try
        {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(secretStatic.botKey)  //Bot Key
                    .addEventListener(new Main())  //An instance of a class that will handle events.
                    .buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.
        }
        catch (LoginException e)
        {
            //If anything goes wrong in terms of authentication, this is the exception that will represent it
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            //Due to the fact that buildBlocking is a blocking method, one which waits until JDA is fully loaded,
            // the waiting can be interrupted. This is the exception that would fire in that situation.
            //As a note: in this extremely simplified example this will never occur. In fact, this will never occur unless
            // you use buildBlocking in a thread that has the possibility of being interrupted (async thread usage and interrupts)
            e.printStackTrace();
        }
        catch (RateLimitedException e)
        {
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
                    if(checkOrangeBetaStatus() != TOABetaOnline){
                        update();
                        System.out.println("Updated because of TOA Beta Web");
                    }else if(checkOrangeStatus() != TOAOrgOnline){
                        update();
                        System.out.println("Updated because of TOA Live Web");
                    }else if(checkYellowStatus() != TOAYelOnline){
                        update();
                        System.out.println("Updated because of TYA Dev Web");
                    }else if(checkAPIBool("http://beta.theorangealliance.org/api") != TOABetaAPIOnline){
                        update();
                        System.out.println("Updated because of TOA Beta API");
                    }else if (checkAPIBool("http://www.theorangealliance.org/api" ) != TOAAPIOrgOnline){
                        update();
                        System.out.println("Updated because of TOA Live API");
                    }else if (updateGithub){
                        update();
                        System.out.println("Updated because of Github");
                    }
                    try {
                        Thread.sleep(waitTime * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        postingChannel.sendMessage("Loop stopped via an InterruptedException. Check log for more details").complete();
                    }
                }
            }
        }

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //These are provided with every event in JDA
        JDA jda = event.getJDA();                       //JDA, the core of the api.
        long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.

        //Event specific information
        User author = event.getAuthor();                //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        MessageChannel channel = event.getChannel();
        //This is the MessageChannel that the message was sent to.
        //  This could be a TextChannel, PrivateChannel, or Group!

        String msg = message.getContent();              //This returns a human readable version of the Message. Similar to what you would see in the client.

        boolean bot = author.isBot();                    //This boolean is useful to determine if the User that sent the Message is a BOT or not!

        if (event.isFromType(ChannelType.TEXT) && /*!bot &&*/ channel.getId().equals("347007802419707917")){ //If this message was sent to a Guild TextChannel

            Guild guild = event.getGuild();             //The Guild that this message was sent in. (note, in the API, Guilds are Servers)
            TextChannel textChannel = event.getTextChannel(); //The TextChannel that this message was sent to.
            Member member = event.getMember();          //This Member that sent the message. Contains Guild specific information about the User!

            String name;
            if (message.isWebhookMessage()) {
                name = author.getName();                //If this is a Webhook message, then there is no Member associated
            }                                           // with the User, thus we default to the author for name.
            else {
                name = member.getEffectiveName();       //This will either use the Member's nickname if they have one,
            }                                           // otherwise it will default to their username. (User#getName())

            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
            System.out.println(message.getAuthor().getId());
        }


        if(checkClientID(message.getAuthor().getId())){
            message.delete().complete();
            if(msg.equals("!stop stats")){
                channel.sendMessage(":wave:").complete();
                System.exit(0);

            }else if(msg.equals("!post here")){
                postingChannel = channel;
                String delete = postingChannel.sendMessage("Channel set to <#" + postingChannel.getId() + ">").complete().getId();
                postingChannel.deleteMessageById(delete).queueAfter(10, TimeUnit.SECONDS);

            }else if(postingChannel != null){

                if (msg.equals("!check")) {
                    update();
                }else if(msg.equals("!github") && checkClientID(message.getAuthor().getId())){
                    String delete = channel.sendMessage("Next manual `!check` or auto check will update the versions for all of the programs.").complete().getId();
                    channel.deleteMessageById(delete).queueAfter(10, TimeUnit.SECONDS);
                    updateGithub = true;

                }else if(msg.contains("!loop ")){
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

                }else if(msg.contains("!stop loop")){
                    if(loop1.runLoop){
                        loop1.runLoop = false;
                        String delete = postingChannel.sendMessage("Check Loop Stopped").complete().getId();
                        postingChannel.deleteMessageById(delete).queueAfter(10, TimeUnit.SECONDS);
                    }else{
                        String delete = postingChannel.sendMessage("Check Loop Not Running!  Type `!loop [Check Interval}` to start.").complete().getId();
                        postingChannel.deleteMessageById(delete).queueAfter(10, TimeUnit.SECONDS);
                    }
                }
            }else if(msg.contains("!loop ") || msg.equals("!github") || msg.equals("!stop loop")){
                String delete = channel.sendMessage("Posting Channel not set. Type `!post here` in your desired channel to set.").complete().getId();
                channel.deleteMessageById(delete).queueAfter(10, TimeUnit.SECONDS);
            }
        }

    }

    boolean checkYellowStatus(){
        boolean connectionSuccess = false;
         try {
             final URLConnection connection = new URL("http://dev.theyellowalliance.com/home").openConnection();
             connection.connect();
             connectionSuccess = true;
         } catch (final MalformedURLException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
         return connectionSuccess;
     }

    boolean checkOrangeBetaStatus(){
        boolean connectionSuccess = false;
        try {
            final URLConnection connection = new URL("http://beta.theorangealliance.org/home").openConnection();
            connection.connect();
            connectionSuccess = true;
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connectionSuccess;
    }

    boolean checkOrangeStatus(){
        boolean connectionSuccess = false;
        try {
            final URLConnection connection = new URL("http://theorangealliance.org/home").openConnection();
            connection.connect();
            connectionSuccess = true;
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connectionSuccess;
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

    boolean checkAPIBool(String sUrl) {
        boolean returnCode = false;
        try {
            URL url = new URL(sUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            con.setRequestProperty("X-TOA-Key", secret.apiKey);
            con.setRequestProperty("X-Application-Origin", "TOA Discord Status Bot");

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                returnCode = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
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

    private boolean checkClientID(String client){
        for(String admin : secret.admins){
            if(client.equals(admin)){
                return true;
            }
        }
        return false;
    }

    private void forceUpdateGithubVariables(){
        APIDLv = checkVersionAPIDownloader();
        BDUPLv = checkVersionDataUploader();
        WEBAPv = checkWebAPIVersion();
        updateGithub = false;
    }

    private void update(){
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

        if(checkOrangeStatus()){
            stats.add("theorangealliance.org - :white_check_mark:");
            if(!TOAOrgOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Main Orange Alliance Web Server is back online.");
            }
            TOAOrgOnline = true;
        }else{
            stats.add("theorangealliance.org - :x:");
            if(TOAOrgOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Main Orange Alliance Web Server is offline.  <@&" + secret.toaAdminRoleID + "> are investigating the issue now.");
            }else{
                postMessage.add("The Main Orange Alliance Web Server is still offline.  Investigation in progress.");
            }
            TOAOrgOnline = false;
        }

        List live = checkAPI("http://www.theorangealliance.org/api");
        if(live.get(0).equals("true")){
            stats.add("theorangealliance.org/api - :white_check_mark:");
            if(!TOAAPIOrgOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Main Orange Alliance API is back online.");
            }
            TOAAPIOrgOnline = true;
        }else{
            stats.add("theorangealliance.org/api - :x: (Response Code: " + live.get(1) + ")");
            if(TOAAPIOrgOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Main Orange Alliance API is offline.  <@&" + secret.toaAdminRoleID + "> are investigating the issue now.");
            }else{
                postMessage.add("The Main Orange Alliance API is still offline.  Investigation in progress.");
            }
            TOAAPIOrgOnline = false;
        }

        if(checkOrangeBetaStatus()){
            stats.add("beta.theorangealliance.org - :white_check_mark:");
            if(!TOABetaOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Beta Orange Alliance Web Server is back online.");
            }
            TOABetaOnline = true;
        }else{
            stats.add("beta.theorangealliance.org - :x:");
            if(TOABetaOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Beta Orange Alliance Web Server is offline.  <@&" + secret.toaAdminRoleID + "> are investigating the issue now.");
            }else{
                postMessage.add("The Beta Orange Alliance Web Server is still offline.  Investigation in progress.");
            }
            TOABetaOnline = false;
        }

        List beta = checkAPI("http://beta.theorangealliance.org/api");
        if(beta.get(0).equals("true")){
            stats.add("beta.theorangealliance.org/api - :white_check_mark:");
            if(!TOABetaAPIOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Beta Orange Alliance API is back online.");
            }
            TOABetaAPIOnline = true;
        }else{
            stats.add("beta.theorangealliance.org/api - :x: (Response Code: " + beta.get(1) + ")");
            if(TOABetaAPIOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Beta Orange Alliance API is offline.  <@&" + secret.toaAdminRoleID + "> are investigating the issue now.");
            }else{
                postMessage.add("The Beta Orange Alliance API is still offline.  Investigation in progress.");
            }
            TOABetaAPIOnline = false;
        }

        if(checkYellowStatus()){
            stats.add("dev.theyellowalliance.com - :white_check_mark:");
            if(!TOAYelOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Dev Orange Alliance Web Server is back online.");
            }
            TOAYelOnline = true;
        }else{
            stats.add("dev.theyellowalliance.com - :x:");
            if(TOAYelOnline){
                postMessage.add("<@&" + secret.serverStatusRoleID + "> the Dev Orange Alliance Web Server is offline.  <@&" + secret.toaAdminRoleID + "> are investigating the issue now.");
            }else{
                postMessage.add("The Dev Orange Alliance Web Server is still offline.  Investigation in progress.");
            }
            TOAYelOnline = false;
        }
        if(updateGithub){
            forceUpdateGithubVariables();
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
}