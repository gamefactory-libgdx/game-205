package com.asocity.orbitdash312380;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.asocity.orbitdash312380.screens.MainMenuScreen;

public class MainGame extends Game {

    public SpriteBatch  batch;
    public AssetManager manager;

    // Shared fonts — generated once, used by all screens
    public BitmapFont fontTitle;   // Orbitron — titles, score display
    public BitmapFont fontHeader;  // Orbitron — section headers
    public BitmapFont fontBody;    // Roboto — labels, buttons, HUD
    public BitmapFont fontSmall;   // Roboto — small labels

    // Audio state
    public boolean musicEnabled = true;
    public boolean sfxEnabled   = true;
    public Music   currentMusic = null;

    @Override
    public void create() {
        batch   = new SpriteBatch();
        manager = new AssetManager();

        generateFonts();
        loadAssets();
        manager.finishLoading();

        setScreen(new MainMenuScreen(this));
    }

    // ── Font generation ────────────────────────────────────────────────────────

    private void generateFonts() {
        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Orbitron-Regular.ttf"));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Roboto-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();

        p.size = Constants.FONT_SIZE_TITLE;
        fontTitle = titleGen.generateFont(p);

        p.size = Constants.FONT_SIZE_HEADER;
        fontHeader = titleGen.generateFont(p);

        p.size = Constants.FONT_SIZE_BODY;
        fontBody = bodyGen.generateFont(p);

        p.size = Constants.FONT_SIZE_SMALL;
        fontSmall = bodyGen.generateFont(p);

        titleGen.dispose();
        bodyGen.dispose();
    }

    // ── Asset loading ──────────────────────────────────────────────────────────

    private void loadAssets() {
        // Backgrounds
        manager.load("backgrounds/bg_main.png",           Texture.class);
        manager.load("backgrounds/bg_planet_default.png", Texture.class);
        manager.load("backgrounds/bg_planet_lava.png",    Texture.class);
        manager.load("backgrounds/bg_planet_ice.png",     Texture.class);
        manager.load("backgrounds/bg_modal_dark.png",     Texture.class);

        // Sprites — ship skins
        manager.load("sprites/player_idle.png",       Texture.class);
        manager.load("sprites/player_idle_green.png", Texture.class);
        manager.load("sprites/player_idle_pink.png",  Texture.class);

        // Sprites — enemies / obstacles
        manager.load("sprites/enemy_gem_capsule_winged_f1.png", Texture.class);

        // Sprites — collectibles
        manager.load("sprites/icon_star.png",    Texture.class);
        manager.load("sprites/coin_gold.png",    Texture.class);

        // Sprites — UI buttons
        manager.load("sprites/button_blue.png",         Texture.class);
        manager.load("sprites/button_blue_pressed.png", Texture.class);
        manager.load("sprites/button_grey.png",         Texture.class);
        manager.load("sprites/button_grey_pressed.png", Texture.class);
        manager.load("sprites/button_green.png",        Texture.class);
        manager.load("sprites/button_green_pressed.png",Texture.class);
        manager.load("sprites/button_red.png",          Texture.class);
        manager.load("sprites/button_red_pressed.png",  Texture.class);

        // Sprites — HUD icons
        manager.load("sprites/icon_heart.png",         Texture.class);
        manager.load("sprites/icon_settings.png",      Texture.class);
        manager.load("sprites/icon_leaderboard.png",   Texture.class);
        manager.load("sprites/icon_locked.png",        Texture.class);
        manager.load("sprites/icon_unlocked.png",      Texture.class);
        manager.load("sprites/icon_close.png",         Texture.class);
        manager.load("sprites/icon_music_on.png",      Texture.class);
        manager.load("sprites/icon_music_off.png",     Texture.class);
        manager.load("sprites/icon_sfx_on.png",        Texture.class);
        manager.load("sprites/icon_sfx_off.png",       Texture.class);
        manager.load("sprites/icon_trophy.png",        Texture.class);

        // Music
        manager.load("sounds/music/music_menu.ogg",      Music.class);
        manager.load("sounds/music/music_gameplay.ogg",  Music.class);
        manager.load("sounds/music/music_game_over.ogg", Music.class);

        // SFX
        manager.load("sounds/sfx/sfx_button_click.ogg",   Sound.class);
        manager.load("sounds/sfx/sfx_button_back.ogg",    Sound.class);
        manager.load("sounds/sfx/sfx_toggle.ogg",         Sound.class);
        manager.load("sounds/sfx/sfx_coin.ogg",           Sound.class);
        manager.load("sounds/sfx/sfx_jump.ogg",           Sound.class);
        manager.load("sounds/sfx/sfx_hit.ogg",            Sound.class);
        manager.load("sounds/sfx/sfx_game_over.ogg",      Sound.class);
        manager.load("sounds/sfx/sfx_level_complete.ogg", Sound.class);
        manager.load("sounds/sfx/sfx_power_up.ogg",       Sound.class);
        manager.load("sounds/sfx/sfx_shoot.ogg",          Sound.class);
    }

    // ── Music helpers ──────────────────────────────────────────────────────────

    public void playMusic(String path) {
        Music requested = manager.get(path, Music.class);
        if (requested == currentMusic && currentMusic.isPlaying()) return;
        if (currentMusic != null) currentMusic.stop();
        currentMusic = requested;
        currentMusic.setLooping(true);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    public void playMusicOnce(String path) {
        if (currentMusic != null) currentMusic.stop();
        currentMusic = manager.get(path, Music.class);
        currentMusic.setLooping(false);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    // ── Dispose ───────────────────────────────────────────────────────────────

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        manager.dispose();
        fontTitle.dispose();
        fontHeader.dispose();
        fontBody.dispose();
        fontSmall.dispose();
    }
}
