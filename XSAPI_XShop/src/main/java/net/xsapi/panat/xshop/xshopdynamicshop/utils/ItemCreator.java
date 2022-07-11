package net.xsapi.panat.xshop.xshopdynamicshop.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Map;

public class ItemCreator {
    private static ItemFlag hideEnchants;

    public static ItemStack createItem(Material mat, int amount, int customModel, String name, ArrayList<String> lore, boolean isGlow
    ) {
        ItemStack itm;

        itm = new ItemStack(mat,amount);

        ItemMeta itmmeta = itm.getItemMeta();
        itmmeta.setCustomModelData(customModel);

        if (!name.isEmpty())
            itmmeta.setDisplayName(name.replace('&', '\u00A7'));
        if (!lore.isEmpty()) {
            ArrayList<String> lore_temp = new ArrayList<String>();

            for (String lores : lore) {
                lores = lores.replace('&', '\u00A7');
                lore_temp.add(lores);
            }

            itmmeta.setLore(lore_temp);
        }
        if (isGlow) {
            itmmeta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            itmmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itmmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itm.setItemMeta(itmmeta);
        return itm;
    }

    public static ItemStack createItem(Material mat, int amount, int customModel, String name, ArrayList<String> lore, boolean isGlow,
    Map<Enchantment,Integer> enchants) {
        ItemStack itm;

        itm = new ItemStack(mat,amount);

        ItemMeta itmmeta = itm.getItemMeta();
        itmmeta.setCustomModelData(customModel);

        if (!name.isEmpty())
            itmmeta.setDisplayName(name.replace('&', '\u00A7'));
        if (!lore.isEmpty()) {
            ArrayList<String> lore_temp = new ArrayList<String>();

            for (String lores : lore) {
                lores = lores.replace('&', '\u00A7');
                lore_temp.add(lores);
            }

            itmmeta.setLore(lore_temp);
        }
        if (isGlow) {
            itmmeta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            itmmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if(!enchants.isEmpty()) {
            for(Map.Entry<Enchantment,Integer> enchant : enchants.entrySet()) {
                itmmeta.addEnchant(enchant.getKey(),enchant.getValue(),true);
            }
        }

        itmmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itm.setItemMeta(itmmeta);
        return itm;
    }
}
