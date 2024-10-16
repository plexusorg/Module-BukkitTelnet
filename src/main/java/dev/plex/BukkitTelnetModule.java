package dev.plex;

import dev.plex.automation.PatchedTelnetCompiler;
import dev.plex.listener.BukkitTelnetListener;
import dev.plex.module.PlexModule;
import dev.plex.util.PlexLog;
import lombok.Getter;
import me.totalfreedom.bukkittelnet.BukkitTelnet;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;

public class BukkitTelnetModule extends PlexModule
{
    @Getter
    private static BukkitTelnetModule module;
    boolean failed = false;
    private BukkitTelnet bukkitTelnet;

    @Override
    public void load()
    {
        module = this;
    }

    @Override
    public void enable()
    {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault"))
        {
            failed = true;
            PlexLog.error("Plex-BukkitTelnet requires the 'Vault' plugin as well as a Permissions plugin that hooks into 'Vault.' We recommend LuckPerms!");
            module.disable();
            return;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("BukkitTelnet"))
        {
            failed = true;
            PlexLog.warn("The BukkitTelnet module requires the BukkitTelnet plugin to work. I am automatically compiling BukkitTelnet plugin for you, however if something fails, please download it from: https://github.com/plexusorg/BukkitTelnet/releases");
            try
            {
                PatchedTelnetCompiler.execute();
                return;
            }
            catch (Exception e)
            {
                PlexLog.error("Failed to compile patched telnet.");
                e.printStackTrace();
            }

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
            PlexLog.warn("You are using an older version of BukkitTelnet that does not support Plex. I am automatically compiling a build that does work for you, however if something fails, please download a version that does from: https://ci.plex.us.org/job/Module-BukkitTelnet");

            try
            {
                PatchedTelnetCompiler.execute();
                return;
            }
            catch (Exception e)
            {
                PlexLog.error("Failed to compile patched telnet.");
                e.printStackTrace();
            }

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
