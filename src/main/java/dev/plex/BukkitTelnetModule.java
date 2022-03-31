package dev.plex;

import dev.plex.listener.BukkitTelnetListener;
import dev.plex.module.PlexModule;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class BukkitTelnetModule extends PlexModule {

    @Getter
    private static BukkitTelnetModule module;

    @Getter
    private Permission permissions = null;

    @Override
    public void load() {
        module = this;
    }

    @Override
    public void enable() {
        if (!setupPermissions() && getPlex().getSystem().equalsIgnoreCase("permissions") && !Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            throw new RuntimeException("Plex-BukkitTelnet requires the 'Vault' plugin as well as a Permissions plugin that hooks into 'Vault.' We recommend LuckPerms!");
        }

        this.registerListener(new BukkitTelnetListener());
    }

    @Override
    public void disable() {

    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServicesManager().getRegistration(Permission.class);
        permissions = rsp.getProvider();
        return permissions != null;
    }
}
