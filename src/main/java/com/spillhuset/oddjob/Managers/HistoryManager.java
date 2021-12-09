package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Changed;

import java.util.UUID;

public class HistoryManager {
    /**
     * Saves change history to the database
     *
     * @param uuid      UUID of the Player or Guild
     * @param changed   Changed representation of what has been changed
     * @param oldString String changed from
     * @param newString String changed to
     */
    public static void add(UUID uuid, Changed changed, String oldString, String newString) {
    }
}
