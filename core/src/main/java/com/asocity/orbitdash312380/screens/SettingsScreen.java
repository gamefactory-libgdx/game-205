package com.asocity.orbitdash312380.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.asocity.orbitdash312380.Constants;
import com.asocity.orbitdash312380.MainGame;
import com.asocity.orbitdash312380.UiFactory;

public class SettingsScreen implements Screen {

    private final MainGame game;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private ShapeRenderer shapeRenderer;

    private Texture bgTex;
    private Preferences prefs;

    // Button geometry
    private static final float BTN_W  = Constants.BTN_WIDTH_PRIMARY;
    private static final float BTN_H  = Constants.BTN_HEIGHT_PRIMARY;
    private static final float BTN_X  = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float MUSIC_Y  = 550f;
    private static final float SFX_Y    = 450f;
    private static final float MENU_Y   = 280f;

    // Colors
    private static final Color BG_CLR  = new Color(0f, 8f / 255f, 20f / 255f, 1f);
    private static final Color ACCENT  = new Color(1f, 109f / 255f, 0f, 1f);
    private static final Color DIM     = new Color(1f, 1f, 1f, 0.4f);

    public SettingsScreen(MainGame game) {
        this.game = game;

        camera        = new OrthographicCamera();
        viewport      = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage         = new Stage(viewport, game.batch);
        shapeRenderer = new ShapeRenderer();

        bgTex = game.manager.get("backgrounds/bg_main.png", Texture.class);
        prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);

        // Restore saved state
        game.musicEnabled = prefs.getBoolean(Constants.PREF_MUSIC, true);
        game.sfxEnabled   = prefs.getBoolean(Constants.PREF_SFX,   true);

        buildStage();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    private void buildStage() {
        TextButton.TextButtonStyle inv = invisibleStyle();

        // Music toggle
        TextButton musicBtn = new TextButton("", inv);
        musicBtn.setSize(BTN_W, BTN_H);
        musicBtn.setPosition(BTN_X, MUSIC_Y);
        musicBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.musicEnabled = !game.musicEnabled;
                prefs.putBoolean(Constants.PREF_MUSIC, game.musicEnabled);
                prefs.flush();
                if (game.currentMusic != null) {
                    if (game.musicEnabled) game.currentMusic.play();
                    else                   game.currentMusic.pause();
                }
                playToggle();
            }
        });
        stage.addActor(musicBtn);

        // SFX toggle
        TextButton sfxBtn = new TextButton("", inv);
        sfxBtn.setSize(BTN_W, BTN_H);
        sfxBtn.setPosition(BTN_X, SFX_Y);
        sfxBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.sfxEnabled = !game.sfxEnabled;
                prefs.putBoolean(Constants.PREF_SFX, game.sfxEnabled);
                prefs.flush();
                playToggle();
            }
        });
        stage.addActor(sfxBtn);

        // Main Menu button
        TextButton menuBtn = new TextButton("", inv);
        menuBtn.setSize(BTN_W, BTN_H);
        menuBtn.setPosition(BTN_X, MENU_Y);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);
    }

    private TextButton.TextButtonStyle invisibleStyle() {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font = game.fontBody;
        return s;
    }

    private void playClick() {
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1f);
    }

    private void playToggle() {
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_toggle.ogg", Sound.class).play(0.5f);
    }

    // ── Screen lifecycle ───────────────────────────────────────────────────────

    @Override
    public void show() {
        game.playMusic("sounds/music/music_menu.ogg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BG_CLR.r, BG_CLR.g, BG_CLR.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        game.batch.draw(bgTex, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Title
        game.fontTitle.setColor(ACCENT);
        GlyphLayout tl = new GlyphLayout(game.fontTitle, "SETTINGS");
        game.fontTitle.draw(game.batch, "SETTINGS",
                (Constants.WORLD_WIDTH - tl.width) / 2f,
                Constants.WORLD_HEIGHT - 80f);
        game.fontTitle.setColor(Color.WHITE);

        // Music state label above button
        String musicLabel  = "MUSIC: " + (game.musicEnabled ? "ON" : "OFF");
        String sfxLabel    = "SFX:   " + (game.sfxEnabled   ? "ON" : "OFF");

        drawSectionLabel(musicLabel, MUSIC_Y + BTN_H + 14f);
        drawSectionLabel(sfxLabel,   SFX_Y   + BTN_H + 14f);

        game.batch.end();

        // Neon buttons
        shapeRenderer.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, musicLabel,
                BTN_X, MUSIC_Y, BTN_W, BTN_H);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, sfxLabel,
                BTN_X, SFX_Y, BTN_W, BTN_H);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "MAIN MENU",
                BTN_X, MENU_Y, BTN_W, BTN_H);

        stage.act(delta);
        stage.draw();
    }

    private void drawSectionLabel(String text, float y) {
        game.fontSmall.setColor(DIM);
        GlyphLayout l = new GlyphLayout(game.fontSmall, text);
        game.fontSmall.draw(game.batch, text,
                (Constants.WORLD_WIDTH - l.width) / 2f, y);
        game.fontSmall.setColor(Color.WHITE);
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
