package blacklist.man10;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Man10Blacklist extends JavaPlugin implements Listener {

    List<String> blacklistuser = getConfig().getStringList("config.blacklistID");
    String prefix = "§b[§7Man10BlackList§b]";
    int i = 0;
    ArrayList<Player> showjoinmessage = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        getCommand("blacklist").setExecutor(new Man10BlacklistCommands(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getConfig().set("config.sendmessageplayer", showjoinmessage);
        saveConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (p.hasPermission("man10.blacklist.joinmessage")){
            showjoinmessage.add(p);
        }
        String filename = p.getUniqueId().toString();
        File userdata = new File((Bukkit.getServer().getPluginManager().getPlugin("Man10Blacklist").getDataFolder()), File.separator + "blacklist");
        File f = new File(userdata, File.separator + filename + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        if (f.exists()) {
            String s = data.get("reason").toString();
            String ss = data.get("executer").toString();
            for (i = 0; i < showjoinmessage.size(); i++) {
                showjoinmessage.get(i).sendMessage(prefix + getConfig().getString("config.BlackListPlayerJoinMessage") + p.getName() + "理由:" + s + ", 実行者:" + ss);
            }
            if (p.getName() == data.get("UserName")) {
                return;
            }
            try {
                data.set("UserName", p.getName());
                data.save(f);
                blacklistuser.remove(p.getName());
                blacklistuser.remove(data.get("UserName"));
                blacklistuser.add(p.getName());
                saveConfig();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if (p.hasPermission("man10.blacklist.joinmessage")){
            showjoinmessage.remove(p);
        }
        String filename = p.getUniqueId().toString();
        File userdata = new File((Bukkit.getServer().getPluginManager().getPlugin("Man10Blacklist").getDataFolder()), File.separator + "blacklist");
        File f = new File(userdata, File.separator + filename + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        String s = data.get("reason").toString();
        String ss = data.get("executer").toString();
        if (f.exists()) {
            for (i = 0; i < showjoinmessage.size(); i++){
                showjoinmessage.get(i).sendMessage(prefix + getConfig().getString("config.BlackListPlayerQuitMessage") + p.getName() + "理由:" + s + ", 実行者:" + ss);
            }
            return;
        }
    }
}
