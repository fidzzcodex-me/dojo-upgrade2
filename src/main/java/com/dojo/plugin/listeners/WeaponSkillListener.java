package com.dojo.plugin.listeners;

import com.dojo.plugin.items.DojoItems;
import com.dojo.plugin.skills.SkillManager;
import com.dojo.plugin.skills.WeaponSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WeaponSkillListener implements Listener {

    private final SkillManager skillManager;

    public WeaponSkillListener(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().name().contains("RIGHT_CLICK") == false) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        String itemId = DojoItems.getItemId(item);
        if (itemId == null) return;

        WeaponSkill skill = switch (itemId) {
            case DojoItems.KATANA_ID -> WeaponSkill.IAIDO_SLASH;
            case DojoItems.YARI_ID -> WeaponSkill.PIERCING_THRUST;
            case DojoItems.NUNCHAKU_ID -> WeaponSkill.WHIRLWIND_STRIKE;
            default -> null;
        };

        if (skill == null) return;

        event.setCancelled(true);
        skillManager.useSkill(player, skill);
    }
}
