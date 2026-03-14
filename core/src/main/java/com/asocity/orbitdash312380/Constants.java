package com.asocity.orbitdash312380;

public class Constants {

    // ── Viewport ──────────────────────────────────────────────────────────────
    public static final float WORLD_WIDTH  = 480f;
    public static final float WORLD_HEIGHT = 854f;

    // ── Planet ────────────────────────────────────────────────────────────────
    public static final float PLANET_X              = 240f;
    public static final float PLANET_Y              = 500f;
    public static final float PLANET_RADIUS         = 90f;

    // ── Orbital rings (radii from planet centre) ──────────────────────────────
    public static final float RING_RADIUS_1         = 130f;
    public static final float RING_RADIUS_2         = 170f;
    public static final float RING_RADIUS_3         = 210f;
    public static final int   RING_COUNT            = 3;

    // ── Ship ──────────────────────────────────────────────────────────────────
    public static final float SHIP_SIZE             = 28f;
    public static final float SHIP_ORBIT_SPEED      = 1.8f;  // radians / second
    public static final float SHIP_JUMP_DURATION    = 0.18f; // seconds to move between rings

    // ── Asteroids ─────────────────────────────────────────────────────────────
    public static final float ASTEROID_SIZE         = 30f;
    public static final float ASTEROID_SPAWN_MIN    = 1.4f;  // min seconds between spawns
    public static final float ASTEROID_SPAWN_MAX    = 2.8f;  // max seconds between spawns
    public static final float ASTEROID_ORBIT_SPEED  = 0.8f;  // radians / second
    public static final float ASTEROID_ARC_SPAN     = 0.6f;  // radians of ring it occupies
    public static final float ASTEROID_LIFETIME     = 5.0f;  // seconds before despawn

    // ── Stars (collectibles) ──────────────────────────────────────────────────
    public static final float STAR_SIZE             = 26f;
    public static final float STAR_SPAWN_MIN        = 2.0f;
    public static final float STAR_SPAWN_MAX        = 4.5f;
    public static final float STAR_LIFETIME         = 4.0f;
    public static final int   STAR_VALUE            = 1;     // stars awarded per collect

    // ── Scoring ───────────────────────────────────────────────────────────────
    public static final int   SCORE_PER_LAP         = 10;
    public static final int   SCORE_PER_STAR        = 5;

    // ── World unlock thresholds ───────────────────────────────────────────────
    public static final int   WORLD_LAVA_UNLOCK     = 500;
    public static final int   WORLD_ICE_UNLOCK      = 1000;

    // ── Difficulty multipliers ────────────────────────────────────────────────
    public static final float DIFF_EASY_SPEED       = 0.7f;
    public static final float DIFF_NORMAL_SPEED     = 1.0f;
    public static final float DIFF_HARD_SPEED       = 1.5f;

    // ── Shop — power-ups ──────────────────────────────────────────────────────
    public static final int   SHOP_SHIELD_COST      = 20;
    public static final float SHOP_SHIELD_DURATION  = 5.0f;
    public static final int   SHOP_MAGNET_COST      = 30;
    public static final float SHOP_MAGNET_DURATION  = 10.0f;
    public static final float SHOP_MAGNET_RADIUS    = 120f;
    public static final int   SHOP_DOUBLE_COST      = 50;

    // ── Shop — skin prices ────────────────────────────────────────────────────
    public static final int   SKIN_DEFAULT_PRICE    = 0;
    public static final int   SKIN_GREEN_PRICE      = 100;
    public static final int   SKIN_PINK_PRICE       = 150;

    // ── Font sizes (sp / world-unit equivalent) ───────────────────────────────
    public static final int   FONT_SIZE_TITLE       = 52;
    public static final int   FONT_SIZE_HEADER      = 36;
    public static final int   FONT_SIZE_BODY        = 28;
    public static final int   FONT_SIZE_SMALL       = 20;
    public static final int   FONT_SIZE_SCORE_BIG   = 56;

    // ── Button sizes ──────────────────────────────────────────────────────────
    public static final float BTN_WIDTH_PRIMARY     = 240f;
    public static final float BTN_HEIGHT_PRIMARY    = 70f;
    public static final float BTN_WIDTH_SECONDARY   = 200f;
    public static final float BTN_HEIGHT_SECONDARY  = 60f;
    public static final float BTN_SIZE_ROUND        = 60f;
    public static final float BTN_SIZE_PAUSE        = 50f;

    // ── HUD ───────────────────────────────────────────────────────────────────
    public static final float HUD_ICON_SIZE         = 32f;
    public static final float HUD_MARGIN            = 20f;
    public static final float HUD_PADDING           = 10f;

    // ── UI padding / layout ───────────────────────────────────────────────────
    public static final float PAD_SMALL             = 10f;
    public static final float PAD_MEDIUM            = 20f;
    public static final float PAD_LARGE             = 40f;

    // ── Leaderboard ───────────────────────────────────────────────────────────
    public static final int   LEADERBOARD_MAX       = 10;

    // ── SharedPreferences keys ────────────────────────────────────────────────
    public static final String PREFS_NAME           = "GamePrefs";
    public static final String PREF_MUSIC           = "musicEnabled";
    public static final String PREF_SFX             = "sfxEnabled";
    public static final String PREF_HIGH_SCORE      = "highScore";
    public static final String PREF_STAR_BALANCE    = "starBalance";
    public static final String PREF_SKIN            = "selectedSkin";
    public static final String PREF_WORLD           = "selectedWorld";
    public static final String PREF_DIFFICULTY      = "difficulty";
    public static final String PREF_SHIELD_OWNED    = "shieldOwned";
    public static final String PREF_MAGNET_OWNED    = "magnetOwned";
    public static final String PREF_DOUBLE_OWNED    = "doubleOwned";
    public static final String PREF_SKIN_GREEN      = "skinGreenOwned";
    public static final String PREF_SKIN_PINK       = "skinPinkOwned";
    public static final String PREF_LEADERBOARD     = "leaderboard";
    public static final String PREF_SHIELD_ACTIVE   = "shieldActive";
    public static final String PREF_MAGNET_ACTIVE   = "magnetActive";
    public static final String PREF_DOUBLE_ACTIVE   = "doubleActive";

    // ── Difficulty values ─────────────────────────────────────────────────────
    public static final int   DIFFICULTY_EASY       = 0;
    public static final int   DIFFICULTY_NORMAL     = 1;
    public static final int   DIFFICULTY_HARD       = 2;

    // ── World IDs ─────────────────────────────────────────────────────────────
    public static final int   WORLD_DEFAULT         = 0;
    public static final int   WORLD_LAVA            = 1;
    public static final int   WORLD_ICE             = 2;
}
