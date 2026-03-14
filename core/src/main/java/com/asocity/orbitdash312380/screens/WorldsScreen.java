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

/**
 * World / planet selection screen.
 * Worlds unlock based on all-time high score:
 *   Default  — always unlocked
 *   Lava     — unlock at WORLD_LAVA_UNLOCK score
 *   Ice      — unlock at WORLD_ICE_UNLOCK score
 */
public class WorldsScreen implements Screen {

    private final MainGame game;

    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      shapeRenderer;

    private final Texture bgTex;
    private final Texture previewDefault;
    private final Texture previewLava;
    private final Texture previewIce;
    private final Texture lockedIconTex;

    private int bestScore;
    private int selectedWorld;

    // ── Layout ────────────────────────────────────────────────────────────────
    private static final float BTN_W  = Constants.BTN_WIDTH_PRIMARY;
    private static final float BTN_H  = Constants.BTN_HEIGHT_PRIMARY;
    private static final float BTN_X  = (Constants.WORLD_WIDTH - BTN_W) / 2f;

    private static final float WORLD0_Y = 600f;
    private static final float WORLD1_Y = 460f;
    private static final float WORLD2_Y = 320f;
    private static final float MENU_Y   = 60f;

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color BG_CLR    = new Color(0f, 8f / 255f, 20f / 255f, 1f);
    private static final Color ACCENT    = new Color(1f, 109f / 255f, 0f, 1f);
    private static final Color PRIMARY   = new Color(0f, 176f / 255f, 1f, 1f);
    private static final Color LOCKED_C  = new Color(1f, 109f / 255f, 0f, 0.7f);
    private static final Color DIM       = new Color(1f, 1f, 1f, 0.45f);
    private static final Color SELECTED  = new Color(0f, 176f / 255f, 1f, 0.25f);

    // ── Constructor ───────────────────────────────────────────────────────────

    public WorldsScreen(MainGame game) {
        this.game = game;

        camera        = new OrthographicCamera();
        viewport      = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage         = new Stage(viewport, game.batch);
        shapeRenderer = new ShapeRenderer();

        bgTex          = game.manager.get("backgrounds/bg_main.png",           Texture.class);
        previewDefault = game.manager.get("backgrounds/bg_planet_default.png", Texture.class);
        previewLava    = game.manager.get("backgrounds/bg_planet_lava.png",    Texture.class);
        previewIce     = game.manager.get("backgrounds/bg_planet_ice.png",     Texture.class);
        lockedIconTex  = game.manager.get("sprites/icon_locked.png",           Texture.class);

        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        bestScore     = prefs.getInteger(Constants.PREF_HIGH_SCORE, 0);
        selectedWorld = prefs.getInteger(Constants.PREF_WORLD, Constants.WORLD_DEFAULT);

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

        // Default world (always unlocked)
        TextButton world0Btn = new TextButton("", inv);
        world0Btn.setSize(BTN_W, BTN_H);
        world0Btn.setPosition(BTN_X, WORLD0_Y);
        world0Btn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                selectWorld(Constants.WORLD_DEFAULT);
            }
        });
        stage.addActor(world0Btn);

        // Lava world
        TextButton world1Btn = new TextButton("", inv);
        world1Btn.setSize(BTN_W, BTN_H);
        world1Btn.setPosition(BTN_X, WORLD1_Y);
        world1Btn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                if (bestScore >= Constants.WORLD_LAVA_UNLOCK) selectWorld(Constants.WORLD_LAVA);
                else showLocked(Constants.WORLD_LAVA_UNLOCK);
            }
        });
        stage.addActor(world1Btn);

        // Ice world
        TextButton world2Btn = new TextButton("", inv);
        world2Btn.setSize(BTN_W, BTN_H);
        world2Btn.setPosition(BTN_X, WORLD2_Y);
        world2Btn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                if (bestScore >= Constants.WORLD_ICE_UNLOCK) selectWorld(Constants.WORLD_ICE);
                else showLocked(Constants.WORLD_ICE_UNLOCK);
            }
        });
        stage.addActor(world2Btn);

        // Main Menu
        TextButton menuBtn = new TextButton("", inv);
        menuBtn.setSize(BTN_W, BTN_H);
        menuBtn.setPosition(BTN_X, MENU_Y);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                playClick();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);
    }

    // ── Interaction ───────────────────────────────────────────────────────────

    private String lockedHint = "";
    private float  hintTimer  = 0f;

    private void selectWorld(int worldId) {
        selectedWorld = worldId;
        Gdx.app.getPreferences(Constants.PREFS_NAME)
                .putInteger(Constants.PREF_WORLD, worldId).flush();
        playClick();
        lockedHint = "";
    }

    private void showLocked(int requiredScore) {
        lockedHint = "REACH " + requiredScore + " TO UNLOCK";
        hintTimer  = 2.5f;
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        hintTimer = Math.max(0f, hintTimer - delta);

        Gdx.gl.glClearColor(BG_CLR.r, BG_CLR.g, BG_CLR.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bgTex, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Title
        game.fontTitle.setColor(ACCENT);
        GlyphLayout tl = new GlyphLayout(game.fontTitle, "WORLDS");
        game.fontTitle.draw(game.batch, "WORLDS",
                (Constants.WORLD_WIDTH - tl.width) / 2f, Constants.WORLD_HEIGHT - 50f);
        game.fontTitle.setColor(Color.WHITE);

        // Best score
        game.fontSmall.setColor(DIM);
        String bsStr = "BEST: " + bestScore;
        GlyphLayout bsl = new GlyphLayout(game.fontSmall, bsStr);
        game.fontSmall.draw(game.batch, bsStr,
                (Constants.WORLD_WIDTH - bsl.width) / 2f, Constants.WORLD_HEIGHT - 100f);
        game.fontSmall.setColor(Color.WHITE);

        // World preview thumbnails (left of buttons)
        float thumbW = 60f, thumbH = 40f;
        float thumbX = BTN_X - thumbW - 8f;

        // Clip thumbnails as small snippets from the full background textures
        float srcW = (float)previewDefault.getWidth() * 0.3f;
        float srcH = (float)previewDefault.getHeight() * 0.15f;
        float srcX = previewDefault.getWidth() * 0.35f;
        float srcY = previewDefault.getHeight() * 0.5f;

        game.batch.draw(previewDefault, thumbX, WORLD0_Y + (BTN_H - thumbH) / 2f, thumbW, thumbH,
                (int)srcX, (int)srcY, (int)srcW, (int)srcH, false, false);
        game.batch.draw(previewLava,    thumbX, WORLD1_Y + (BTN_H - thumbH) / 2f, thumbW, thumbH,
                (int)srcX, (int)srcY, (int)srcW, (int)srcH, false, false);
        game.batch.draw(previewIce,     thumbX, WORLD2_Y + (BTN_H - thumbH) / 2f, thumbW, thumbH,
                (int)srcX, (int)srcY, (int)srcW, (int)srcH, false, false);

        // Lock icons for locked worlds
        float lockSize = 28f;
        float lockX    = BTN_X + BTN_W + 8f;
        if (bestScore < Constants.WORLD_LAVA_UNLOCK) {
            game.batch.draw(lockedIconTex, lockX, WORLD1_Y + (BTN_H - lockSize) / 2f, lockSize, lockSize);
        }
        if (bestScore < Constants.WORLD_ICE_UNLOCK) {
            game.batch.draw(lockedIconTex, lockX, WORLD2_Y + (BTN_H - lockSize) / 2f, lockSize, lockSize);
        }

        // Labels above buttons
        drawWorldLabel("DEFAULT PLANET", "ALWAYS UNLOCKED", true,  WORLD0_Y);
        drawWorldLabel("LAVA WORLD",     "SCORE " + Constants.WORLD_LAVA_UNLOCK + " TO UNLOCK",
                bestScore >= Constants.WORLD_LAVA_UNLOCK, WORLD1_Y);
        drawWorldLabel("ICE WORLD",      "SCORE " + Constants.WORLD_ICE_UNLOCK  + " TO UNLOCK",
                bestScore >= Constants.WORLD_ICE_UNLOCK,  WORLD2_Y);

        // Locked hint
        if (hintTimer > 0f) {
            float alpha = Math.min(hintTimer, 1f);
            game.fontSmall.setColor(ACCENT.r, ACCENT.g, ACCENT.b, alpha);
            GlyphLayout hl = new GlyphLayout(game.fontSmall, lockedHint);
            game.fontSmall.draw(game.batch, lockedHint,
                    (Constants.WORLD_WIDTH - hl.width) / 2f, WORLD2_Y - 40f);
            game.fontSmall.setColor(Color.WHITE);
        }

        game.batch.end();

        // Selected highlight
        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if (selectedWorld == Constants.WORLD_DEFAULT) drawSelectedRect(WORLD0_Y);
        if (selectedWorld == Constants.WORLD_LAVA)    drawSelectedRect(WORLD1_Y);
        if (selectedWorld == Constants.WORLD_ICE)     drawSelectedRect(WORLD2_Y);

        // Neon buttons
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "DEFAULT PLANET",
                BTN_X, WORLD0_Y, BTN_W, BTN_H);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody,
                bestScore >= Constants.WORLD_LAVA_UNLOCK ? "LAVA WORLD" : "LOCKED",
                BTN_X, WORLD1_Y, BTN_W, BTN_H);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody,
                bestScore >= Constants.WORLD_ICE_UNLOCK ? "ICE WORLD" : "LOCKED",
                BTN_X, WORLD2_Y, BTN_W, BTN_H);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "MAIN MENU",
                BTN_X, MENU_Y, BTN_W, BTN_H);

        stage.act(delta);
        stage.draw();
    }

    private void drawWorldLabel(String name, String unlock, boolean unlocked, float y) {
        game.fontSmall.setColor(unlocked ? PRIMARY : LOCKED_C);
        game.fontSmall.draw(game.batch, name, BTN_X + 8f, y + BTN_H + 6f);

        game.fontSmall.setColor(DIM);
        game.fontSmall.draw(game.batch, unlock, BTN_X + 8f, y + BTN_H + 26f);
        game.fontSmall.setColor(Color.WHITE);
    }

    private void drawSelectedRect(float y) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(SELECTED);
        shapeRenderer.rect(BTN_X - 4f, y - 4f, BTN_W + 8f, BTN_H + 8f);
        shapeRenderer.end();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private TextButton.TextButtonStyle invisibleStyle() {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font = game.fontBody;
        return s;
    }

    private void playClick() {
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1f);
    }

    // ── Screen lifecycle ──────────────────────────────────────────────────────

    @Override public void show()  { game.playMusic("sounds/music/music_menu.ogg"); }
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
