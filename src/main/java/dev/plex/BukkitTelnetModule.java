package dev.plex;

import dev.plex.listener.BukkitTelnetListener;
import dev.plex.module.PlexModule;
import dev.plex.util.PlexLog;
import java.lang.reflect.Method;
import lombok.Getter;
import me.totalfreedom.bukkittelnet.BukkitTelnet;
import org.bukkit.Bukkit;

public class BukkitTelnetModule extends PlexModule
{
    @Getter
    private static BukkitTelnetModule module;

    private BukkitTelnet bukkitTelnet;

    boolean failed = false;

    @Override
    public void load()
    {
        module = this;
    }

    @Override
    public void enable()
    {
        if (getPlex().getSystem().equalsIgnoreCase("permissions") && !Bukkit.getPluginManager().isPluginEnabled("Vault"))
        {
            failed = true;
            PlexLog.error("Plex-BukkitTelnet requires the 'Vault' plugin as well as a Permissions plugin that hooks into 'Vault.' We recommend LuckPerms!");
            module.disable();
            return;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("BukkitTelnet"))
        {
            failed = true;
            PlexLog.error("The Plex-BukkitTelnet module requires the BukkitTelnet plugin to work. Please download it from: https://github.com/plexusorg/BukkitTelnet/releases");
            module.disable();
            return;
        }

        try
        {
            failed = true;
            Class<?> clazz = Class.forName("me.totalfreedom.bukkittelnet.BukkitTelnet");
            Method method = clazz.getDeclaredMethod("getPlugin");
        }
        catch (ClassNotFoundException | NoSuchMethodException ignored)
        {
            PlexLog.error("You are using an older version of BukkitTelnet that does not support Plex. Please download a version that does from: https://ci.plex.us.org/job/Plex-BukkitTelnet");
            module.disable();
            return;
        }

        this.registerListener(new BukkitTelnetListener());
        this.bukkitTelnet = BukkitTelnet.getPlugin();
    }

    @Override
    public void disable()
    {
        if (failed)
        {
            PlexLog.error("Disabling Module-BukkitTelnet. Please resolve the above error.");
        }
    }
}
