package me.yoursole.smpplugin.discord;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.quickchart.QuickChart;
import me.yoursole.smpplugin.data.DataManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hypixel.api.HypixelAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.checkerframework.checker.units.qual.A;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
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

        Bukkit.broadcastMessage(ChatColor.BLUE+e.getAuthor().getName()+": "+e.getMessage().getContentRaw());
    }

    private void processCommand(String msg, User user) throws IOException, ExecutionException, InterruptedException {
        String[] words = msg.split(" ");

        switch (words[0]){
            case "?blockdata":{
                executeBlockData();
                break;
            }
            case "?link":{
                if(!(words.length>1))
                    break;
                String minecraftIGN = words[1];
                String uuid = Bot.getUUID(minecraftIGN);
                String discordHypixel = getDiscord(uuid);
                String discord = user.getAsTag();

                if(!discord.equals(discordHypixel)) {
                    Bot.sendMessage("Something went wrong -- Please make sure your account is linked" +
                            "on hypixel!", "");
                    break;
                }

                //get minecraft UUID
                //check if the name and discord ID match with hypixel API
                //if not explain how to link
                //else add discord ID and minecraft UUID to a list for future use

                //people can choose to use their discord name or minecraft name in all chats


                break;
            }
        }

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
    }
}
