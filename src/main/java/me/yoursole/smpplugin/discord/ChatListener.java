package me.yoursole.smpplugin.discord;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.quickchart.QuickChart;
import me.yoursole.smpplugin.SMPPlugin;
import me.yoursole.smpplugin.data.DataManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hypixel.api.HypixelAPI;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ChatListener extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e){

        if(!e.getChannel().getId().equalsIgnoreCase(Bot.t))
            return;
        if(e.getAuthor().isBot())
            return;
        if(e.getMessage().getContentRaw().length()>0 && e.getMessage().getContentRaw().toCharArray()[0] == '?'){
            try {
                processCommand(e.getMessage().getContentRaw(), e.getAuthor());
            } catch (IOException | ExecutionException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            return;
        }

        Bukkit.broadcastMessage(ChatColor.BLUE+ Objects.requireNonNull(e.getMember()).getNickname()+": "+e.getMessage().getContentRaw());
    }

    private void processCommand(String msg, User user) throws IOException, ExecutionException, InterruptedException {
        String[] words = msg.split(" ");

        switch (words[0]){
            case "?blockdata":{
                executeBlockData();
                break;
            }
            case "?link":{
                executeLink(user, words);
                break;
            }
            case "?unlink":{
                executeUnlink(user, words);
                break;
            }
            case "?stop":{
                if (checkShutdown(user)) break;
                Bot.sendMessage("Server is shutting down","");

                break;
            }
            case "?restart":{
                if (checkShutdown(user)) break;
                Bot.sendMessage("Server is restarting","");

                break;
            }
            case "?reload":{
                if (checkPermission(user)) break;
                Bot.sendMessage("Server is reloading", "");
                Bukkit.getServer().reload();
                break;

            }
            case "?kick":{
                if (words.length <= 1) break;
                if (checkPermission(user)) break;

                Player p = Bukkit.getPlayer(words[1]);
                if(p == null) {
                    Bot.sendMessage("Invalid Player","");
                    break;
                }

                Bukkit.getScheduler().runTask(SMPPlugin.getPlugin(),
                        () -> p.kickPlayer("You were kicked by the discord bot how unfortunate"));
                Bot.sendMessage("You successfully kicked "+p.getDisplayName(), "");
                break;

            }
            case "?ban":{
                if (words.length <= 1) break;
                if (checkPermission(user)) break;

                Player p = Bukkit.getPlayer(words[1]);
                if(p == null) {
                    OfflinePlayer p2 = Bukkit.getOfflinePlayer(words[1]);
                    Bukkit.getBanList(BanList.Type.NAME).addBan(Objects.requireNonNull(p2.getName()), "You were banned by the discord bot how unfortunate",getFutureDate(999999999),"Discord Bot");
                    Bot.sendMessage("You successfully banned "+p2.getName(), "");
                    break;
                }


                Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(), "You were banned by the discord bot how unfortunate", getFutureDate(999999999),"Discord Bot");
                Bukkit.getScheduler().runTask(SMPPlugin.getPlugin(),
                        () -> p.kickPlayer("You were kicked by the discord bot how unfortunate"));
                Bot.sendMessage("You successfully banned "+p.getDisplayName(), "");
                break;
            }
            case "?help":{

            }
        }
    }

    public static Date getFutureDate(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    private boolean checkPermission(User user) throws IOException {
        return check(user);
    }

    private boolean checkShutdown(User user) throws IOException {
        if (check(user)) return true;
        Bukkit.getServer().shutdown();
        return false;
    }

    private boolean check(User user) throws IOException {
        String uuid = DataManager.botData.getMinecraft(user.getAsTag());
        if(uuid == null) {
            Bot.sendMessage("Please link your account to perform this action", "");
            return true;
        }

        if(!Bukkit.getOfflinePlayer(UUID.fromString(insertDashes(uuid))).isOp()) {
            Bot.sendMessage("You do not have permission to perform this action", "");
            return true;
        }
        return false;
    }

    private String insertDashes(String uuid){
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");
        sb.insert(13, "-");
        sb.insert(18, "-");
        sb.insert(23, "-");
        return sb.toString();
    }

    private void executeUnlink(User user, String[] words) throws IOException {
        if(!(words.length>1))
            return;
        String minecraftIGN = words[1];
        String uuid = Bot.getUUID(minecraftIGN);
        String discord = user.getAsTag();

        if(DataManager.botData.getMinecraft(discord) != null && !DataManager.botData.getMinecraft(discord).equalsIgnoreCase(uuid)){
            Bot.sendMessage("This is not the minecraft account you are linked with!","");
            return;
        }

        DataManager.botData.removeEntry(discord);
        Bot.sendMessage("Your minecraft account has been unlinked","");
    }

    private void executeLink(User user, String[] words) throws IOException, ExecutionException, InterruptedException {
        if(!(words.length>1))
            return;
        String minecraftIGN = words[1];
        String uuid = Bot.getUUID(minecraftIGN);
        String discordHypixel = getDiscord(uuid);
        String discord = user.getAsTag();

        if(DataManager.botData.getMinecraft(discordHypixel)!=null) {
            Bot.sendMessage("You are already linked!  Do ?unlink if you wish to relink your account","");
            return;
        }

        if(!discord.equals(discordHypixel)) {
            Bot.sendMessage("Something went wrong -- Please make sure your account is linked" +
                    " on Hypixel!", "");
            return;
        }

        DataManager.botData.addEntry(discordHypixel, uuid);
        Bot.sendMessage("You have linked "+discordHypixel+" to "+minecraftIGN + " ("+uuid+")","");
        Bot.sendMessage("If you are curious what you can do with a linked account, run ?help", "");
        //get minecraft UUID
        //check if the name and discord ID match with hypixel API
        //if not explain how to link
        //else add discord ID and minecraft UUID to a list for future use

        //people can choose to use their discord name or minecraft name in all chats
    }

    public static String getDiscord(String uuid) throws ExecutionException, InterruptedException {
        final UUID API_UUID = UUID.fromString("cf5c2051-35d6-4d1a-88f9-57924b6ed9a4");
        HypixelAPI api = new HypixelAPI(API_UUID);
        JsonObject json;
        try{
            json = JsonParser.parseString(api.getPlayerByUuid(uuid).get().getPlayer().toString()).getAsJsonObject();
            if(json.get("socialMedia")!=null){
                JsonObject socialMedia = json.getAsJsonObject("socialMedia");
                if(socialMedia.get("links")!=null){
                    JsonObject links = socialMedia.getAsJsonObject("links");
                    if(links.get("DISCORD")!=null){
                        return links.get("DISCORD").toString().replace("\"","");
                    }
                }
            }
            return null;
        }catch(ExecutionException e){
            return null;
        }
    }


    private void executeBlockData() throws IOException {
        try{
            LinkedList<Map.Entry<String, Integer>> blockCounts = new LinkedList<>(DataManager.pluginData.getBlocksBroken().entrySet());

            LinkedList<Map.Entry<String, Integer>> sorted = new LinkedList<>();
            ArrayList<Integer> used = new ArrayList<>();

            for(int i = 0; i < (Math.min(blockCounts.size(), 10)); i++){
                int max = Integer.MIN_VALUE;
                Map.Entry<String, Integer> currBest = null;
                int index = -1;

                for(int j = 0; j < blockCounts.size(); j++){
                    if(used.contains(j))
                        continue;
                    Map.Entry<String, Integer> m = blockCounts.get(j);
                    int q = m.getValue();
                    if(q > max){
                        max = q;
                        currBest = m;
                        index = j;
                    }
                }

                if(currBest == null) {
                    Bot.sendMessage("something went wrong", "");
                    break;
                }

                used.add(index);
                sorted.add(currBest);
            }

            //sorted should be top 10 most common blocks now
            StringBuilder labels = new StringBuilder("[");
            for(Map.Entry<String, Integer> s : sorted){
                String label = s.getKey();
                labels.append("'").append(label).append("', ");
            }

            labels = new StringBuilder(labels.substring(0, labels.length() - 2));
            labels.append("]");

            StringBuilder data = new StringBuilder("[");
            for(Map.Entry<String, Integer> s : sorted){
                int value = s.getValue();
                data.append(value).append(", ");
            }
            data = new StringBuilder(data.substring(0, data.length() - 2));
            data.append("]");


            QuickChart chart = new QuickChart();
            chart.setWidth(500);
            chart.setHeight(300);
            chart.setConfig("{"
                    + "    type: 'bar',"
                    + "    data: {"
                    + "        labels: "+ labels +","
                    + "        datasets: [{"
                    + "            label: 'Top ten most broken blocks!',"
                    + "            data: "+ data +""
                    + "        }]"
                    + "    }"
                    + "}"
            );

            Bot.sendURL(chart.getUrl());
        }catch (Exception e){
            Bot.sendMessage("Something went wrong: " + e.getMessage(), "");
        }

    }
}
