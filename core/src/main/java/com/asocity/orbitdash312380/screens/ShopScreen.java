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
 * Shop screen — power-ups (one-time activation) and ship skin unlocks.
 * Star balance is persisted via SharedPreferences.
 */
public class ShopScreen implements Screen {

    private final MainGame    game;
    private final Preferences prefs;

    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      shapeRenderer;

    private final Texture bgTex;
    private final Texture shipTex0;
    private final Texture shipTex1;
    private final Texture shipTex2;
    private final Texture starIconTex;

    // Live state (refreshed from prefs in constructor)
    private int     starBalance;
    private boolean shieldOwned;
    private boolean magnetOwned;
    private boolean doubleOwned;
    private boolean greenOwned;
    private boolean pinkOwned;
    private int     equippedSkin;

    // Latest purchase feedback (shown for one draw pass)
    private String  feedbackMsg  = "";
    private float   feedbackTimer = 0f;

    // ── Layout constants ──────────────────────────────────────────────────────
    private static final float BTN_W  = Constants.BTN_WIDTH_PRIMARY;
    private static final float BTN_H  = Constants.BTN_HEIGHT_PRIMARY;
    private static final float BTN_X  = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float MENU_Y = 40f;

    // Power-up button Y positions
    private static final float SHIELD_Y = 610f;
    private static final float MAGNET_Y = 510f;
    private static final float DOUBLE_Y = 410f;

    // Skin button Y positions
    private static final float SKIN0_Y = 290f;
    private static final float SKIN1_Y = 200f;
    private static final float SKIN2_Y = 110f;

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color BG_CLR = new Color(0f, 8f / 255f, 20f / 255f, 1f);
    private static final Color ACCENT = new Color(1f, 109f / 255f, 0f, 1f);
    private static final Color DIM    = new Color(1f, 1f, 1f, 0.45f);
    private static final Color OK_CLR = new Color(0f, 176f / 255f, 1f, 1f);

    // ── Constructor ───────────────────────────────────────────────────────────

    public ShopScreen(MainGame game) {
        this.game  = game;
        this.prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);

        camera        = new OrthographicCamera();
        viewport      = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage         = new Stage(viewport, game.batch);
        shapeRenderer = new ShapeRenderer();

        bgTex      = game.manager.get("backgrounds/bg_modal_dark.png", Texture.class);
        shipTex0   = game.manager.get("sprites/player_idle.png",       Texture.class);
        shipTex1   = game.manager.get("sprites/player_idle_green.png", Texture.class);
        shipTex2   = game.manager.get("sprites/player_idle_pink.png",  Texture.class);
        starIconTex= game.manager.get("sprites/icon_star.png",         Texture.class);

        loadState();
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

    private void loadState() {
        starBalance  = prefs.getInteger(Constants.PREF_STAR_BALANCE, 0);
        shieldOwned  = prefs.getBoolean(Constants.PREF_SHIELD_OWNED,  false);
        magnetOwned  = prefs.getBoolean(Constants.PREF_MAGNET_OWNED,  false);
        doubleOwned  = prefs.getBoolean(Constants.PREF_DOUBLE_OWNED,  false);
        greenOwned   = prefs.getBoolean(Constants.PREF_SKIN_GREEN,     false);
        pinkOwned    = prefs.getBoolean(Constants.PREF_SKIN_PINK,      false);
        equippedSkin = prefs.getInteger(Constants.PREF_SKIN,           0);
    }

    private void buildStage() {
        TextButton.TextButtonStyle inv = invisibleStyle();

        // ── Power-up buttons ──────────────────────────────────────────────────

        // Shield
        TextButton shieldBtn = new TextButton("", inv);
        shieldBtn.setSize(BTN_W, BTN_H);
        shieldBtn.setPosition(BTN_X, SHIELD_Y);
        shieldBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                purchasePowerUp(Constants.PREF_SHIELD_OWNED, Constants.PREF_SHIELD_ACTIVE,
                        Constants.SHOP_SHIELD_COST, "Shield");
            }
        });
        stage.addActor(shieldBtn);

        // Magnet
        TextButton magnetBtn = new TextButton("", inv);
        magnetBtn.setSize(BTN_W, BTN_H);
        magnetBtn.setPosition(BTN_X, MAGNET_Y);
        magnetBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                purchasePowerUp(Constants.PREF_MAGNET_OWNED, Constants.PREF_MAGNET_ACTIVE,
                        Constants.SHOP_MAGNET_COST, "Magnet");
            }
        });
        stage.addActor(magnetBtn);

        // Double score
        TextButton doubleBtn = new TextButton("", inv);
        doubleBtn.setSize(BTN_W, BTN_H);
        doubleBtn.setPosition(BTN_X, DOUBLE_Y);
        doubleBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                purchasePowerUp(Constants.PREF_DOUBLE_OWNED, Constants.PREF_DOUBLE_ACTIVE,
                        Constants.SHOP_DOUBLE_COST, "2x Score");
            }
        });
        stage.addActor(doubleBtn);

        // ── Skin buttons ──────────────────────────────────────────────────────

        // Default skin (equip only)
        TextButton skin0Btn = new TextButton("", inv);
        skin0Btn.setSize(BTN_W, BTN_H);
        skin0Btn.setPosition(BTN_X, SKIN0_Y);
        skin0Btn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { equipSkin(0); }
        });
        stage.addActor(skin0Btn);

        // Green skin
        TextButton skin1Btn = new TextButton("", inv);
        skin1Btn.setSize(BTN_W, BTN_H);
        skin1Btn.setPosition(BTN_X, SKIN1_Y);
        skin1Btn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                if (!greenOwned) purchaseSkin(1, Constants.SKIN_GREEN_PRICE,
                        Constants.PREF_SKIN_GREEN, "Green Ship");
                else equipSkin(1);
            }
        });
        stage.addActor(skin1Btn);

        // Pink skin
        TextButton skin2Btn = new TextButton("", inv);
        skin2Btn.setSize(BTN_W, BTN_H);
        skin2Btn.setPosition(BTN_X, SKIN2_Y);
        skin2Btn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                if (!pinkOwned) purchaseSkin(2, Constants.SKIN_PINK_PRICE,
                        Constants.PREF_SKIN_PINK, "Pink Ship");
                else equipSkin(2);
            }
        });
        stage.addActor(skin2Btn);

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

    // ── Purchase logic ────────────────────────────────────────────────────────

    private void purchasePowerUp(String ownedKey, String activeKey, int cost, String name) {
        if (starBalance < cost) {
            showFeedback("NEED " + cost + " STARS");
            return;
        }
        starBalance -= cost;
        prefs.putInteger(Constants.PREF_STAR_BALANCE, starBalance);
        prefs.putBoolean(ownedKey, true);
        prefs.putBoolean(activeKey, true);
        prefs.flush();
        loadState();
        showFeedback(name + " ACTIVATED!");
        playClick();
    }

    private void purchaseSkin(int skinIndex, int cost, String ownedKey, String name) {
        if (starBalance < cost) {
            showFeedback("NEED " + cost + " STARS");
            return;
        }
        starBalance -= cost;
        prefs.putInteger(Constants.PREF_STAR_BALANCE, starBalance);
        prefs.putBoolean(ownedKey, true);
        prefs.flush();
        loadState();
        equipSkin(skinIndex);
        showFeedback(name + " UNLOCKED!");
        playClick();
    }

    private void equipSkin(int skinIndex) {
        equippedSkin = skinIndex;
        prefs.putInteger(Constants.PREF_SKIN, skinIndex);
        prefs.flush();
        showFeedback("SKIN EQUIPPED");
        playClick();
    }

    private void showFeedback(String msg) {
        feedbackMsg   = msg;
        feedbackTimer = 2.0f;
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        feedbackTimer = Math.max(0f, feedbackTimer - delta);

        Gdx.gl.glClearColor(BG_CLR.r, BG_CLR.g, BG_CLR.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        game.batch.draw(bgTex, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Title
        game.fontTitle.setColor(ACCENT);
        GlyphLayout tl = new GlyphLayout(game.fontTitle, "SHOP");
        game.fontTitle.draw(game.batch, "SHOP",
                (Constants.WORLD_WIDTH - tl.width) / 2f,
                Constants.WORLD_HEIGHT - 50f);
        game.fontTitle.setColor(Color.WHITE);

        // Star balance
        game.fontBody.setColor(ACCENT);
        String balStr = "STARS: " + starBalance;
        GlyphLayout bl = new GlyphLayout(game.fontBody, balStr);
        game.fontBody.draw(game.batch, balStr,
                Constants.WORLD_WIDTH - bl.width - Constants.HUD_MARGIN,
                Constants.WORLD_HEIGHT - Constants.HUD_MARGIN);
        game.fontBody.setColor(Color.WHITE);

        // ── Section header: Power-Ups ─────────────────────────────────────────
        drawSectionHeader("POWER-UPS", 720f);

        // Shield row
        drawItemRow("SHIELD  (5s invincibility)",
                Constants.SHOP_SHIELD_COST, shieldOwned,
                prefs.getBoolean(Constants.PREF_SHIELD_ACTIVE, false), SHIELD_Y);

        // Magnet row
        drawItemRow("MAGNET  (10s auto-collect)",
                Constants.SHOP_MAGNET_COST, magnetOwned,
                prefs.getBoolean(Constants.PREF_MAGNET_ACTIVE, false), MAGNET_Y);

        // Double score row
        drawItemRow("2x SCORE  (next run)",
                Constants.SHOP_DOUBLE_COST, doubleOwned,
                prefs.getBoolean(Constants.PREF_DOUBLE_ACTIVE, false), DOUBLE_Y);

        // ── Section header: Ship Skins ────────────────────────────────────────
        drawSectionHeader("SHIP SKINS", 370f);

        // Skin previews (small icons left of button)
        float iconSize = 36f;
        drawSkinRow(shipTex0, "DEFAULT SHIP", 0, 0, false, SKIN0_Y, iconSize);
        drawSkinRow(shipTex1, "GREEN SHIP",  Constants.SKIN_GREEN_PRICE, 1, !greenOwned, SKIN1_Y, iconSize);
        drawSkinRow(shipTex2, "PINK SHIP",   Constants.SKIN_PINK_PRICE,  2, !pinkOwned,  SKIN2_Y, iconSize);

        // Feedback message
        if (feedbackTimer > 0f) {
            float alpha = Math.min(feedbackTimer, 1f);
            game.fontBody.setColor(OK_CLR.r, OK_CLR.g, OK_CLR.b, alpha);
            GlyphLayout fl = new GlyphLayout(game.fontBody, feedbackMsg);
            game.fontBody.draw(game.batch, feedbackMsg,
                    (Constants.WORLD_WIDTH - fl.width) / 2f, 185f);
            game.fontBody.setColor(Color.WHITE);
        }

        game.batch.end();

        // Neon buttons
        shapeRenderer.setProjectionMatrix(camera.combined);
        drawNeonButton("SHIELD  (5s invincibility)", Constants.SHOP_SHIELD_COST, shieldOwned, SHIELD_Y);
        drawNeonButton("MAGNET  (10s auto-collect)", Constants.SHOP_MAGNET_COST, magnetOwned, MAGNET_Y);
        drawNeonButton("2x SCORE  (next run)",       Constants.SHOP_DOUBLE_COST, doubleOwned, DOUBLE_Y);
        drawSkinNeonButton(0, SKIN0_Y);
        drawSkinNeonButton(1, SKIN1_Y);
        drawSkinNeonButton(2, SKIN2_Y);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "MAIN MENU",
                BTN_X, MENU_Y, BTN_W, BTN_H);

        stage.act(delta);
        stage.draw();
    }

    private void drawSectionHeader(String text, float y) {
        game.fontSmall.setColor(DIM);
        GlyphLayout l = new GlyphLayout(game.fontSmall, text);
        game.fontSmall.draw(game.batch, text, (Constants.WORLD_WIDTH - l.width) / 2f, y);
        game.fontSmall.setColor(Color.WHITE);
    }

    private void drawItemRow(String name, int cost, boolean owned, boolean active, float y) {
        // Draw item name and price label above the button area
        String label = owned ? (active ? name + "  [ACTIVE]" : name + "  [OWNED]") : name + "  " + cost + " STARS";
        game.fontSmall.setColor(owned ? (active ? OK_CLR : DIM) : Color.WHITE);
        game.fontSmall.draw(game.batch, label, BTN_X + 8f, y + BTN_H + 6f);
        game.fontSmall.setColor(Color.WHITE);
    }

    private void drawSkinRow(Texture tex, String name, int cost, int idx, boolean locked, float y, float iconSize) {
        boolean equipped = (equippedSkin == idx);
        String label = equipped ? name + "  [EQUIPPED]" : (locked ? name + "  " + cost + " STARS" : name + "  [OWNED]");
        game.fontSmall.setColor(equipped ? OK_CLR : (locked ? ACCENT : DIM));
        game.fontSmall.draw(game.batch, label, BTN_X + iconSize + 12f, y + BTN_H + 6f);
        game.fontSmall.setColor(Color.WHITE);

        // Small skin icon to the left of button
        game.batch.draw(tex, BTN_X - iconSize - 4f, y + (BTN_H - iconSize) / 2f, iconSize, iconSize);
    }

    private void drawNeonButton(String label, int cost, boolean owned, float y) {
        String text = owned ? "REACTIVATE" : ("BUY  " + cost);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, text, BTN_X, y, BTN_W, BTN_H);
    }

    private void drawSkinNeonButton(int idx, float y) {
        boolean owned    = (idx == 0) || (idx == 1 && greenOwned) || (idx == 2 && pinkOwned);
        boolean equipped = (equippedSkin == idx);
        int cost         = idx == 1 ? Constants.SKIN_GREEN_PRICE : Constants.SKIN_PINK_PRICE;
        String text      = equipped ? "EQUIPPED" : (owned ? "EQUIP" : ("BUY  " + cost));
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, text, BTN_X, y, BTN_W, BTN_H);
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

    @Override
    public void show() {
        game.playMusic("sounds/music/music_menu.ogg");
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
