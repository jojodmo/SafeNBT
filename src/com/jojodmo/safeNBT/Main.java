/*

   Copyright 2019-2021 jojodmo

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package com.jojodmo.safeNBT;

import com.jojodmo.safeNBT.api.SafeNBT;
import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin{

    public static Main that;

    @Override
    public void onEnable(){
        that = this;
        Bukkit.getLogger().log(Level.INFO, "[SafeNBT] Successfully enabled SafeNBT by jojodmo!");
    }

    private static final String prefix = ChatColor.BLUE + "[SafeNBT] " + ChatColor.AQUA;

    private static void sendMessage(CommandSender s, String m){
        s.sendMessage(prefix + m);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){
        if(command.getLabel().equalsIgnoreCase("safenbt")){
            if(args.length == 0){
                if(sender instanceof Player){
                    sendNBT((Player) sender);
                }
                else{
                    sendHelp(sender, label);
                }
            }
            else{
                if(args[0].toLowerCase().matches("^v|ver|version|pl|plugin$")){
                    sendMessage(sender, "Running SafeNBT v" + getDescription().getVersion() + " by jojodmo");
                }
                else if(args[0].toLowerCase().matches("^get|g|info|inspect$")){
                    if(sender instanceof Player){
                        sendNBT((Player) sender);
                    }
                    else{
                        sendMessage(sender, "Sorry! Only players can use this command!");
                    }
                }
                else if(args[0].toLowerCase().matches("^help|\\?$")){
                    sendHelp(sender, label);
                }
            }
            return true;
        }
        return false;
    }

    private static void sendHelp(CommandSender sender, String label){
        sendMessage(sender, "Correct Usage: /" + label + " <help/get/version>");
    }

    private static void sendNBT(Player p){
        if(!p.hasPermission("safenbt.get")){
            sendMessage(p, "You don't have permission to run this command!");
           return;
        }

        ItemStack is = p.getItemInHand();
        if(is == null || is.getType() == Material.AIR){
            sendMessage(p, "Run this command while holding an item in your hand to get its NBT tags!");
            return;
        }

        SafeNBT nbt = SafeNBT.get(is);
        String nbtStr = nbt == null ? ChatColor.RED + "<null>" : nbt.compoundString();
        String amt = is.getAmount() > 1 ? "x" + is.getAmount() : "";
        p.sendMessage(ChatColor.BLUE + "[SafeNBT] " + ChatColor.DARK_AQUA + " NBT for " + ChatColor.AQUA + "minecraft:" + is.getType() + amt);
        p.sendMessage(ChatColor.YELLOW + nbtStr);
    }

    public static final int MAJOR_VERSION;
    public static final int MINOR_VERSION;
    public static final int UPDATE_VERSION;
    public static final String VERSION_STRING;
    public static final String VERSION_STRING_FULL;

    public static boolean greaterOrEqual(int major, int minor){
        return MAJOR_VERSION > major || (MAJOR_VERSION >= major && MINOR_VERSION >= minor);
    }

    public static boolean greaterOrEqual(int major, int minor, int update){
        return MAJOR_VERSION > major || (MAJOR_VERSION >= major && (MINOR_VERSION > minor || (MINOR_VERSION >= minor && UPDATE_VERSION >= update)));
    }

    static{
        int major = 1;
        int minor = 17;
        int update = 0;

        try{
            String v = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            String[] vs = v.split("_");

            try{
                major = Integer.parseInt(vs[0].replaceAll("[^0-9]", ""));
                minor = Integer.parseInt(vs[1].replaceAll("[^0-9]", ""));
                update = vs.length > 2 ? Integer.parseInt(vs[2].replaceAll("[^0-9]", "")) : 0;
            }
            catch(Exception ex){ex.printStackTrace();}
        }
        catch(Exception ignore){}
        finally{
            MAJOR_VERSION = major;
            MINOR_VERSION = minor;
            UPDATE_VERSION = update;

            VERSION_STRING = MAJOR_VERSION + "." + MINOR_VERSION;
            VERSION_STRING_FULL = UPDATE_VERSION == 0 ? VERSION_STRING : MAJOR_VERSION + "." + MINOR_VERSION + "." + UPDATE_VERSION;
        }
    }

}
