package dev.plex;

import dev.plex.listener.BukkitTelnetListener;
import dev.plex.module.PlexModule;
import lombok.Getter;
import me.totalfreedom.bukkittelnet.BukkitTelnet;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class BukkitTelnetModule extends PlexModule
{
    @Getter
    private static BukkitTelnetModule module;

    private BukkitTelnet bukkitTelnet;

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
            throw new RuntimeException("Plex-BukkitTelnet requires the 'Vault' plugin as well as a Permissions plugin that hooks into 'Vault.' We recommend LuckPerms!");
        }

        this.registerListener(new BukkitTelnetListener());
        this.bukkitTelnet = BukkitTelnet.getPlugin();
    }

    @Override
    public void disable()
    {
    }
}
