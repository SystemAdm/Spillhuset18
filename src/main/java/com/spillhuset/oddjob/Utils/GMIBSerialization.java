package com.spillhuset.oddjob.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GMIBSerialization {

    public static String toDatabase(ItemStack[] inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeInt(inventory.length);
                for (ItemStack is : inventory) {
                    if (is != null && is.getType().equals(Material.PLAYER_HEAD)) {
                        if (is.hasItemMeta()) {
                            SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
                            if (skullMeta != null && skullMeta.getOwnerProfile() == null) {
                                is.setItemMeta(null);
                            }
                        }
                    }
                    dataOutput.writeObject(is);
                }
            }
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("io error", e);
        }
    }

    public static ItemStack[] fromDatabase(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            ItemStack[] inventory;
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                int size = dataInput.readInt();
                inventory = new ItemStack[size];
                for (int i = 0; i < size; i++) {
                    inventory[i] = (ItemStack) dataInput.readObject();
                }
            }
            return inventory;
        } catch(ClassNotFoundException e) {
            throw new IOException("class not found",e);
        }
    }
}
