package com.spillhuset.oddjob.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.OddJob;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class Tool {
    static HashMap<UUID, UUID> map = new HashMap<>();

    public static double round(double value) {
        long factor = (long) Math.pow(10, 0);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String jsonEncode(ItemStack itemStack) {
        Gson gson = new Gson();
        JsonObject itemJson = new JsonObject();
        String[] BYPASS_CLASS = {"CraftMetaBlockState", "CraftMetaItem"
                /*Glowstone Support*/, "GlowMetaItem"};


        itemJson.addProperty("type", itemStack.getType().name());

        if (itemStack.getAmount() != 1) itemJson.addProperty("amount", itemStack.getAmount());
        if (itemStack.hasItemMeta()) {
            JsonObject metaJson = new JsonObject();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (itemMeta instanceof Damageable damageable && damageable.hasDamage())
                    itemJson.addProperty("damage", damageable.getDamage());
                if (itemMeta.hasDisplayName())
                    metaJson.addProperty("displayname", itemMeta.getDisplayName());
                if (itemMeta.hasLore()) {
                    if (itemMeta.getLore() != null) {
                        JsonArray lore = new JsonArray();
                        itemMeta.getLore().forEach(str -> lore.add(new JsonPrimitive(str)));
                        metaJson.add("lore", lore);
                    }
                }
                if (itemMeta.hasEnchants()) {
                    JsonArray enchants = new JsonArray();
                    itemMeta.getEnchants().forEach((enchantment, integer) -> {
                        enchants.add(new JsonPrimitive(enchantment.getName() + ":" + integer));
                    });
                    metaJson.add("enchants", enchants);
                }
                if (!itemMeta.getItemFlags().isEmpty()) {
                    JsonArray flags = new JsonArray();
                    itemMeta.getItemFlags().stream().map(ItemFlag::name).forEach(str -> flags.add(new JsonPrimitive(str)));
                    metaJson.add("flags", flags);
                }
                if (itemMeta instanceof SkullMeta skullMeta) {
                    if (skullMeta.hasOwner()) {
                        JsonObject extraMeta = new JsonObject();
                        extraMeta.addProperty("owner", skullMeta.getOwner());
                        metaJson.add("extra-meta", extraMeta);
                    }
                } else if (itemMeta instanceof BannerMeta bannerMeta) {
                    JsonObject extraMeta = new JsonObject();
                    extraMeta.addProperty("base-color", bannerMeta.getBaseColor().name());
                    if (bannerMeta.numberOfPatterns() > 0) {
                        JsonArray patterns = new JsonArray();
                        bannerMeta.getPatterns()
                                .stream()
                                .map(pattern ->
                                        pattern.getColor().name() + ":" + pattern.getPattern().getIdentifier())
                                .forEach(str -> patterns.add(new JsonPrimitive(str)));
                        extraMeta.add("patterns", patterns);
                    }

                    metaJson.add("extra-meta", extraMeta);
                } else if (itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
                    if (enchantmentStorageMeta.hasStoredEnchants()) {
                        JsonObject extraMeta = new JsonObject();
                        JsonArray storedEnchants = new JsonArray();
                        enchantmentStorageMeta.getStoredEnchants().forEach((enchantment, integer) -> {
                            storedEnchants.add(new JsonPrimitive(enchantment.getName() + ":" + integer));
                        });
                        extraMeta.add("stored-enchants", storedEnchants);
                        metaJson.add("extra-meta", extraMeta);
                    }
                } else if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
                    JsonObject extraMeta = new JsonObject();
                    extraMeta.addProperty("color", Integer.toHexString(leatherArmorMeta.getColor().asRGB()));
                    metaJson.add("extra-meta", extraMeta);
                } else if (itemMeta instanceof BookMeta bookMeta) {
                    if (bookMeta.hasAuthor() || bookMeta.hasPages() || bookMeta.hasTitle()) {
                        JsonObject extraMeta = new JsonObject();
                        if (bookMeta.hasTitle()) {
                            extraMeta.addProperty("title", bookMeta.getTitle());
                        }
                        if (bookMeta.hasAuthor()) {
                            extraMeta.addProperty("author", bookMeta.getAuthor());
                        }
                        if (bookMeta.hasPages()) {
                            JsonArray pages = new JsonArray();
                            bookMeta.getPages().forEach(str -> pages.add(new JsonPrimitive(str)));
                            extraMeta.add("pages", pages);
                        }
                        metaJson.add("extra-meta", extraMeta);
                    }
                } else if (itemMeta instanceof PotionMeta potionMeta) {
                    if (potionMeta.hasCustomEffects()) {
                        JsonObject extraMeta = new JsonObject();

                        JsonArray customEffects = new JsonArray();
                        for (PotionEffect potionEffect : potionMeta.getCustomEffects()) {
                            customEffects.add(new JsonPrimitive(potionEffect.getType().getName() + ":" + potionEffect.getAmplifier() + ":" + potionEffect.getDuration() / 20));
                        }
                        /*potionMeta.getCustomEffects().forEach(potionEffect -> {
                            customEffects.add(new JsonPrimitive(potionEffect.getType().getName()
                                    + ":" + potionEffect.getAmplifier()
                                    + ":" + potionEffect.getDuration() / 20));
                        });*/
                        extraMeta.add("custom-effects", customEffects);

                        metaJson.add("extra-meta", extraMeta);
                    }
                } else if (itemMeta instanceof FireworkEffectMeta fireworkEffectMeta) {
                    if (fireworkEffectMeta.hasEffect()) {
                        FireworkEffect effect = fireworkEffectMeta.getEffect();
                        JsonObject extraMeta = new JsonObject();

                        extraMeta.addProperty("type", effect.getType().name());
                        if (effect.hasFlicker()) extraMeta.addProperty("flicker", true);
                        if (effect.hasTrail()) extraMeta.addProperty("trail", true);

                        if (!effect.getColors().isEmpty()) {
                            JsonArray colors = new JsonArray();
                            effect.getColors().forEach(color ->
                                    colors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                            extraMeta.add("colors", colors);
                        }

                        if (!effect.getFadeColors().isEmpty()) {
                            JsonArray fadeColors = new JsonArray();
                            effect.getFadeColors().forEach(color ->
                                    fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                            extraMeta.add("fade-colors", fadeColors);
                        }

                        metaJson.add("extra-meta", extraMeta);
                    }
                } else if (itemMeta instanceof FireworkMeta fireworkMeta) {

                    JsonObject extraMeta = new JsonObject();

                    extraMeta.addProperty("power", fireworkMeta.getPower());

                    if (fireworkMeta.hasEffects()) {
                        JsonArray effects = new JsonArray();
                        fireworkMeta.getEffects().forEach(effect -> {
                            JsonObject jsonObject = new JsonObject();

                            jsonObject.addProperty("type", effect.getType().name());
                            if (effect.hasFlicker()) jsonObject.addProperty("flicker", true);
                            if (effect.hasTrail()) jsonObject.addProperty("trail", true);

                            if (!effect.getColors().isEmpty()) {
                                JsonArray colors = new JsonArray();
                                effect.getColors().forEach(color ->
                                        colors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                                jsonObject.add("colors", colors);
                            }

                            if (!effect.getFadeColors().isEmpty()) {
                                JsonArray fadeColors = new JsonArray();
                                effect.getFadeColors().forEach(color ->
                                        fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                                jsonObject.add("fade-colors", fadeColors);
                            }

                            effects.add(jsonObject);
                        });
                        extraMeta.add("effects", effects);
                    }
                    metaJson.add("extra-meta", extraMeta);
                } else if (itemMeta instanceof MapMeta mapMeta) {
                    JsonObject extraMeta = new JsonObject();

                /* 1.11
                if(mapMeta.hasLocationName()) {
                    extraMeta.addProperty("location-name", mapMeta.getLocationName());
                }
                if(mapMeta.hasColor()) {
                    extraMeta.addProperty("color", Integer.toHexString(mapMeta.getColor().asRGB()));
                }*/
                    extraMeta.addProperty("scaling", mapMeta.isScaling());

                    metaJson.add("extra-meta", extraMeta);
                }

                itemJson.add("item-meta", metaJson);
            }
        }
        return gson.toJson(itemJson);
    }

    public static void announce(Player player,@Nonnull Guild guild) {
        String name = "";
        if (guild.getZone().equals(Zone.GUILD)) {
            name = guild.getZone().getColoredString(guild.getName());
        } else {
            name = guild.getZone().getColoredString();
        }
        Guild mine = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
        if (mine != null && mine.equals(guild)) {
            name = ChatColor.GOLD + name;
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(name));
    }
}
