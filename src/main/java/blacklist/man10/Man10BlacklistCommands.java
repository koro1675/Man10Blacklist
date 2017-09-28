package blacklist.man10;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;


public class Man10BlacklistCommands implements CommandExecutor {

    String prefix = "§b[§7Man10BlackList§b]";
    int i;

    Man10Blacklist plugin = null;
    public Man10BlacklistCommands(Man10Blacklist plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;

        //引数がない場合
        if (args.length == 0){
            p.sendMessage(prefix + "ver1.1");
            return true;
        }
        /////////////////////////////////////
        // help
        /////////////////////////////////////
        if (args[0].equalsIgnoreCase("help")){
            help(p);
            return true;
        }
        /////////////////////////////////////
        // add
        /////////////////////////////////////
        if (args[0].equalsIgnoreCase("add")){
            if (!p.hasPermission("man10.blacklist.add")){
                p.sendMessage(prefix + "§4Do not have permission");
                return true;
            }
            if (args.length != 3) {
                p.sendMessage(prefix + "§7/blacklist add [player] [reason]");
                return true;
            }
            Player pp = Bukkit.getPlayer(args[1]);
            String reason = String.valueOf(args[2]);
            String filename = pp.getUniqueId().toString();
            File userdata = new File((Bukkit.getServer().getPluginManager().getPlugin("Man10Blacklist").getDataFolder()), File.separator + "blacklist");
            File f = new File(userdata, File.separator + filename + ".yml");
            FileConfiguration data = YamlConfiguration.loadConfiguration(f);
            f.delete();

            if (!f.exists()){
                try {
                    plugin.blacklistuser.remove(data.get("Username"));
                    data.set("UserName", pp.getName());
                    data.set("reason", reason);
                    data.set("executer", p.getName());
                    data.save(f);
                    plugin.blacklistuser.remove(pp.getName());
                    plugin.blacklistuser.add(pp.getName());
                    plugin.getConfig().set("config.blacklistID", plugin.blacklistuser);
                    plugin.saveConfig();
                    p.sendMessage(prefix + pp.getName() + "さんをブラックリストに『" + reason + "』の理由で追加しました");
                } catch (IOException exception){
                    p.sendMessage(prefix + "§4保存に失敗");
                    exception.printStackTrace();
                    return true;
                }
            }
        }
        /////////////////////////////////////
        // remove
        /////////////////////////////////////
        if (args[0].equalsIgnoreCase("remove")){
            if (!p.hasPermission("man10.blacklist.remove")){
                p.sendMessage(prefix + "§4Do not have permission");
                return true;
            }
            if (args.length != 2) {
                p.sendMessage(prefix + "§7/blacklist remove [player]");
                return true;
            }
            Player pp = Bukkit.getPlayer(args[1]);
            String filename = pp.getUniqueId().toString();
            File userdata = new File((Bukkit.getServer().getPluginManager().getPlugin("Man10Blacklist").getDataFolder()), File.separator + "blacklist");
            File f = new File(userdata, File.separator + filename + ".yml");

            if (f.delete()){
                plugin.blacklistuser.remove(pp.getName());
                plugin.saveConfig();
                p.sendMessage(prefix + pp.getName() + "さんをブラックリストから外しました");
            }else {
                p.sendMessage(prefix + "§4失敗しました");
            }
        }
        /////////////////////////////////////
        // check
        /////////////////////////////////////
        if (args[0].equalsIgnoreCase("check")){
            if (!p.hasPermission("man10.blacklist.check")){
                p.sendMessage(prefix + "§4Do not have permission");
                return true;
            }
            if (args.length != 2) {
                p.sendMessage(prefix + "§7/blacklist check [player]");
                return true;
            }
            Player pp = Bukkit.getPlayer(args[1]);
            String filename = pp.getUniqueId().toString();
            File userdata = new File((Bukkit.getServer().getPluginManager().getPlugin("Man10Blacklist").getDataFolder()), File.separator + "blacklist");
            File f = new File(userdata, File.separator + filename + ".yml");
            FileConfiguration data = YamlConfiguration.loadConfiguration(f);
            if (f.exists()){
                p.sendMessage("§8==========" + prefix + "§8==========");
                p.sendMessage("§b" + pp.getName() + "§7さんはブラックリストに入っています");
                p.sendMessage("§7UUID:" + "§b" + pp.getUniqueId());
                p.sendMessage("§7理由:§b『" + data.get("reason") + "』");
                p.sendMessage("§7追加したユーザー:§b『" + data.get("executer") + "』");
                return true;
            }
            p.sendMessage(prefix + pp.getName() + "§7さんはブラックリストに入っていません");
            return true;
        }
        /////////////////////////////////////
        // list
        /////////////////////////////////////
        if (args[0].equalsIgnoreCase("list")){
            if (!p.hasPermission("man10.blacklist.list")){
                p.sendMessage(prefix + "§4Do not have permission");
                return true;
            }
            p.sendMessage("§8==========" + prefix + "§8==========");
            for (i = 0; i < plugin.blacklistuser.size(); i++){
                p.sendMessage(plugin.blacklistuser.get(i));
            }
        }
        /////////////////////////////////////
        // reload
        /////////////////////////////////////
        if (args[0].equalsIgnoreCase("reload")){
            if (!p.hasPermission("man10.blacklist.reload")){
                p.sendMessage(prefix + "§4Do not have permission");
                return true;
            }
            plugin.reloadConfig();
        }
        return true;
    }
    void help(Player p){
        p.sendMessage("§7§l=============================================");
        p.sendMessage(prefix);
        p.sendMessage("§7/blacklist add [player] [reason]:プレーヤーをリストに追加");
        p.sendMessage("§7/blacklist remove [Player]:プレーヤーをリストから削除");
        p.sendMessage("§7/blacklist check [Player]:プレーヤーがリストに入っているか確認");
        p.sendMessage("§7/blacklist reload:コンフィグをリロード");
        p.sendMessage("§6§lcreated by koro1675");
    }
}
