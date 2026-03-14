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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.asocity.orbitdash312380.Constants;
import com.asocity.orbitdash312380.MainGame;
import com.asocity.orbitdash312380.UiFactory;

/**
 * Skin preview screen — shows a large render of the chosen ship skin
 * orbiting a simplified planet, with Equip / Back buttons.
 *
 * @param skinIndex  0=default, 1=green, 2=pink
 */
public class SkinPreviewScreen implements Screen {

    private final MainGame game;
    private final int      skinIndex;

    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      shapeRenderer;

    private final Texture bgTex;
    private final Texture skinTex;
    private final Texture starIconTex;

    // Skin metadata
    private static final String[] SKIN_NAMES  = {"DEFAULT SHIP", "GREEN SHIP",  "PINK SHIP"};
    private static final int[]    SKIN_PRICES  = {0, Constants.SKIN_GREEN_PRICE, Constants.SKIN_PINK_PRICE};
    private static final String[] SKIN_KEYS    = {"sprites/player_idle.png",
                                                   "sprites/player_idle_green.png",
                                                   "sprites/player_idle_pink.png"};

    // Preview ship orbit animation
    private float previewAngle = MathUtils.PI / 2f;

    // Live state
    private boolean skinOwned;
    private boolean skinEquipped;
    private int     starBalance;

    // ── Layout ────────────────────────────────────────────────────────────────
    private static final float BTN_W  = Constants.BTN_WIDTH_PRIMARY;
    private static final float BTN_H  = Constants.BTN_HEIGHT_PRIMARY;
    private static final float BTN_X  = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float EQUIP_Y = 120f;
    private static final float BACK_Y  = 40f;

    // Preview orbit parameters
    private static final float PREVIEW_CX = Constants.WORLD_WIDTH  / 2f;
    private static final float PREVIEW_CY = Constants.WORLD_HEIGHT * 0.52f;
    private static final float PLANET_R   = 80f;
    private static final float ORBIT_R    = 130f;
    private static final float PREVIEW_SHIP_SIZE = 52f;

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color BG_CLR  = new Color(0f, 8f / 255f, 20f / 255f, 1f);
    private static final Color ACCENT  = new Color(1f, 109f / 255f, 0f, 1f);
    private static final Color PRIMARY = new Color(0f, 176f / 255f, 1f, 1f);
    private static final Color RING_C  = new Color(0f, 176f / 255f, 1f, 0.5f);
    private static final Color PLANET_C= new Color(0f, 176f / 255f, 1f, 0.18f);
    private static final Color DIM     = new Color(1f, 1f, 1f, 0.5f);

    // ── Constructor ───────────────────────────────────────────────────────────

    public SkinPreviewScreen(MainGame game, int skinIndex) {
        this.game      = game;
        this.skinIndex = MathUtils.clamp(skinIndex, 0, 2);

        camera        = new OrthographicCamera();
        viewport      = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage         = new Stage(viewport, game.batch);
        shapeRenderer = new ShapeRenderer();

        bgTex       = game.manager.get("backgrounds/bg_planet_default.png", Texture.class);
        skinTex     = game.manager.get(SKIN_KEYS[this.skinIndex], Texture.class);
        starIconTex = game.manager.get("sprites/icon_star.png", Texture.class);

        loadState();
        buildStage();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new ShopScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    private void loadState() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        starBalance  = prefs.getInteger(Constants.PREF_STAR_BALANCE, 0);
        skinEquipped = prefs.getInteger(Constants.PREF_SKIN, 0) == skinIndex;

        if (skinIndex == 0) {
            skinOwned = true;
        } else if (skinIndex == 1) {
            skinOwned = prefs.getBoolean(Constants.PREF_SKIN_GREEN, false);
        } else {
            skinOwned = prefs.getBoolean(Constants.PREF_SKIN_PINK, false);
        }
    }

    private void buildStage() {
        TextButton.TextButtonStyle inv = invisibleStyle();

        // Equip / Buy button
        TextButton equipBtn = new TextButton("", inv);
        equipBtn.setSize(BTN_W, BTN_H);
        equipBtn.setPosition(BTN_X, EQUIP_Y);
        equipBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                handleEquipOrBuy();
            }
        });
        stage.addActor(equipBtn);

        // Back to ShopScreen
        TextButton backBtn = new TextButton("", inv);
        backBtn.setSize(BTN_W, BTN_H);
        backBtn.setPosition(BTN_X, BACK_Y);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                playClick();
                game.setScreen(new ShopScreen(game));
            }
        });
        stage.addActor(backBtn);
    }

    private void handleEquipOrBuy() {
        if (skinEquipped) return; // already equipped

        if (!skinOwned) {
            // Attempt purchase
            int cost = SKIN_PRICES[skinIndex];
            if (starBalance < cost) {
                return; // not enough stars — button visually shows cost
            }
            Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
            starBalance -= cost;
            prefs.putInteger(Constants.PREF_STAR_BALANCE, starBalance);
            String ownedKey = skinIndex == 1 ? Constants.PREF_SKIN_GREEN : Constants.PREF_SKIN_PINK;
            prefs.putBoolean(ownedKey, true);
            prefs.flush();
            skinOwned = true;
        }

        // Equip
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        prefs.putInteger(Constants.PREF_SKIN, skinIndex);
        prefs.flush();
        skinEquipped = true;
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_level_complete.ogg", Sound.class).play(1f);
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        previewAngle += Constants.SHIP_ORBIT_SPEED * 0.6f * delta;

        Gdx.gl.glClearColor(BG_CLR.r, BG_CLR.g, BG_CLR.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bgTex, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Planet + ring
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(PLANET_C);
        shapeRenderer.circle(PREVIEW_CX, PREVIEW_CY, PLANET_R, 64);
        shapeRenderer.end();

        Gdx.gl.glLineWidth(2f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(RING_C);
        shapeRenderer.circle(PREVIEW_CX, PREVIEW_CY, ORBIT_R, 80);
        shapeRenderer.end();

        // Ship on orbit
        float sx = PREVIEW_CX + ORBIT_R * MathUtils.cos(previewAngle);
        float sy = PREVIEW_CY + ORBIT_R * MathUtils.sin(previewAngle);
        float rotation = MathUtils.radiansToDegrees * previewAngle + 90f;
        float hs = PREVIEW_SHIP_SIZE / 2f;

        game.batch.begin();
        game.batch.draw(skinTex,
                sx - hs, sy - hs, hs, hs,
                PREVIEW_SHIP_SIZE, PREVIEW_SHIP_SIZE,
                1f, 1f, rotation,
                0, 0, skinTex.getWidth(), skinTex.getHeight(), false, false);
        game.batch.end();

        // UI text
        game.batch.begin();

        // Skin name (top-left)
        game.fontHeader.setColor(Color.WHITE);
        game.fontHeader.draw(game.batch, SKIN_NAMES[skinIndex],
                Constants.HUD_MARGIN, Constants.WORLD_HEIGHT - Constants.HUD_MARGIN);

        // Price or star balance (top-right)
        String priceStr;
        if (skinIndex == 0 || skinOwned) {
            priceStr = skinEquipped ? "EQUIPPED" : "OWNED";
            game.fontSmall.setColor(PRIMARY);
        } else {
            priceStr = SKIN_PRICES[skinIndex] + " STARS";
            game.fontSmall.setColor(ACCENT);
        }
        GlyphLayout prl = new GlyphLayout(game.fontSmall, priceStr);
        game.fontSmall.draw(game.batch, priceStr,
                Constants.WORLD_WIDTH - prl.width - Constants.HUD_MARGIN,
                Constants.WORLD_HEIGHT - Constants.HUD_MARGIN);
        game.fontSmall.setColor(Color.WHITE);

        // Star balance below price
        String balStr = "STARS: " + starBalance;
        GlyphLayout bl = new GlyphLayout(game.fontSmall, balStr);
        game.fontSmall.setColor(DIM);
        game.fontSmall.draw(game.batch, balStr,
                Constants.WORLD_WIDTH - bl.width - Constants.HUD_MARGIN,
                Constants.WORLD_HEIGHT - Constants.HUD_MARGIN - 30f);
        game.fontSmall.setColor(Color.WHITE);

        game.batch.end();

        // Action buttons
        shapeRenderer.setProjectionMatrix(camera.combined);
        String equipLabel;
        if (skinEquipped) equipLabel = "EQUIPPED";
        else if (skinOwned) equipLabel = "EQUIP";
        else equipLabel = "BUY  " + SKIN_PRICES[skinIndex];

        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, equipLabel,
                BTN_X, EQUIP_Y, BTN_W, BTN_H);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "BACK",
                BTN_X, BACK_Y, BTN_W, BTN_H);

        stage.act(delta);
        stage.draw();
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
