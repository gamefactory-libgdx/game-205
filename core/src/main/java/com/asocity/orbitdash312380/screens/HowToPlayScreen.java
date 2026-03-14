package com.asocity.orbitdash312380.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
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
 * How To Play screen — four instruction sections with icons drawn via ShapeRenderer.
 */
public class HowToPlayScreen implements Screen {

    private final MainGame game;

    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage              stage;
    private final ShapeRenderer      shapeRenderer;

    private final Texture bgTex;
    private final Texture starIconTex;
    private final Texture shipTex;

    // Neon primary color components
    private static final float P_R = 0f, P_G = 176f / 255f, P_B = 1f;

    // ── Layout ────────────────────────────────────────────────────────────────
    private static final float BTN_W  = Constants.BTN_WIDTH_PRIMARY;
    private static final float BTN_H  = Constants.BTN_HEIGHT_PRIMARY;
    private static final float BTN_X  = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float MENU_Y = 40f;

    private static final float SEC1_Y = 710f;
    private static final float SEC2_Y = 560f;
    private static final float SEC3_Y = 410f;
    private static final float SEC4_Y = 260f;

    private static final float ICON_SIZE  = 52f;
    private static final float ICON_X     = 30f;
    private static final float TEXT_X     = ICON_X + ICON_SIZE + 16f;
    private static final float TEXT_WIDTH = Constants.WORLD_WIDTH - TEXT_X - 20f;

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color BG_CLR  = new Color(0f, 8f / 255f, 20f / 255f, 1f);
    private static final Color ACCENT  = new Color(1f, 109f / 255f, 0f, 1f);
    private static final Color PRIMARY = new Color(P_R, P_G, P_B, 1f);
    private static final Color DIM     = new Color(1f, 1f, 1f, 0.75f);

    // ── Constructor ───────────────────────────────────────────────────────────

    public HowToPlayScreen(MainGame game) {
        this.game = game;

        camera        = new OrthographicCamera();
        viewport      = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage         = new Stage(viewport, game.batch);
        shapeRenderer = new ShapeRenderer();

        bgTex       = game.manager.get("backgrounds/bg_main.png",   Texture.class);
        starIconTex = game.manager.get("sprites/icon_star.png",      Texture.class);
        shipTex     = game.manager.get("sprites/player_idle.png",    Texture.class);

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

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BG_CLR.r, BG_CLR.g, BG_CLR.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bgTex, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Title
        game.fontTitle.setColor(ACCENT);
        GlyphLayout tl = new GlyphLayout(game.fontTitle, "HOW TO PLAY");
        game.fontTitle.draw(game.batch, "HOW TO PLAY",
                (Constants.WORLD_WIDTH - tl.width) / 2f, Constants.WORLD_HEIGHT - 40f);
        game.fontTitle.setColor(Color.WHITE);

        // ── Section 1: TAP TO JUMP ────────────────────────────────────────────
        game.batch.draw(shipTex, ICON_X, SEC1_Y - ICON_SIZE / 2f, ICON_SIZE, ICON_SIZE);
        drawSectionTitle("TAP TO JUMP", TEXT_X, SEC1_Y + 12f, PRIMARY);
        drawWrap("Tap the screen to jump your ship to the next orbital ring.",
                TEXT_X, SEC1_Y - 10f);

        // ── Section 2: DODGE ASTEROIDS ────────────────────────────────────────
        // Asteroid icon drawn with ShapeRenderer later
        drawSectionTitle("DODGE ASTEROIDS", TEXT_X, SEC2_Y + 12f, ACCENT);
        drawWrap("Asteroids appear on rings. Jump to another ring to avoid them. One hit = Game Over.",
                TEXT_X, SEC2_Y - 10f);

        // ── Section 3: COLLECT STARS ──────────────────────────────────────────
        game.batch.draw(starIconTex, ICON_X, SEC3_Y - ICON_SIZE / 2f, ICON_SIZE, ICON_SIZE);
        drawSectionTitle("COLLECT STARS", TEXT_X, SEC3_Y + 12f, PRIMARY);
        drawWrap("Stars appear briefly on rings. Fly through them to earn currency for the Shop.",
                TEXT_X, SEC3_Y - 10f);

        // ── Section 4: COMPLETE LAPS ──────────────────────────────────────────
        // Lap icon drawn with ShapeRenderer later
        drawSectionTitle("COMPLETE LAPS", TEXT_X, SEC4_Y + 12f, ACCENT);
        drawWrap("Every full orbit earns +10 score. Avoid asteroids as long as possible to beat your best!",
                TEXT_X, SEC4_Y - 10f);

        game.batch.end();

        // Asteroid icon (orange polygon)
        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        drawAsteroidIcon(ICON_X + ICON_SIZE / 2f, SEC2_Y, ICON_SIZE * 0.44f);

        // Lap icon (progress arc with checkmark)
        drawLapIcon(ICON_X + ICON_SIZE / 2f, SEC4_Y, ICON_SIZE * 0.44f);

        // Neon separator lines
        Gdx.gl.glLineWidth(1f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(P_R, P_G, P_B, 0.25f);
        float sepW = Constants.WORLD_WIDTH - 40f;
        shapeRenderer.line(20f, SEC2_Y + 40f, 20f + sepW, SEC2_Y + 40f);
        shapeRenderer.line(20f, SEC3_Y + 40f, 20f + sepW, SEC3_Y + 40f);
        shapeRenderer.line(20f, SEC4_Y + 40f, 20f + sepW, SEC4_Y + 40f);
        shapeRenderer.end();

        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "MAIN MENU",
                BTN_X, MENU_Y, BTN_W, BTN_H);

        stage.act(delta);
        stage.draw();
    }

    private void drawSectionTitle(String text, float x, float y, Color color) {
        game.fontBody.setColor(color);
        game.fontBody.draw(game.batch, text, x, y);
        game.fontBody.setColor(Color.WHITE);
    }

    private void drawWrap(String text, float x, float y) {
        game.fontSmall.setColor(DIM);
        // Manual line wrap — split on space, accumulate lines
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float lineY = y;
        float lineH = game.fontSmall.getLineHeight() + 2f;
        for (String word : words) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            GlyphLayout gl = new GlyphLayout(game.fontSmall, candidate);
            if (gl.width > TEXT_WIDTH && line.length() > 0) {
                game.fontSmall.draw(game.batch, line.toString(), x, lineY);
                lineY -= lineH;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (line.length() > 0) game.fontSmall.draw(game.batch, line.toString(), x, lineY);
        game.fontSmall.setColor(Color.WHITE);
    }

    private void drawAsteroidIcon(float cx, float cy, float r) {
        // Irregular polygon resembling an asteroid (7 vertices)
        float[] angles = {0f, 51f, 90f, 140f, 180f, 240f, 300f};
        float[] radii  = {r, r * 0.7f, r, r * 0.8f, r, r * 0.75f, r * 0.85f};

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(ACCENT.r, ACCENT.g, ACCENT.b, 0.85f);
        // Draw as fan of triangles from center
        for (int i = 0; i < angles.length; i++) {
            int next = (i + 1) % angles.length;
            float ax = cx + radii[i] * MathUtils.cosDeg(angles[i]);
            float ay = cy + radii[i] * MathUtils.sinDeg(angles[i]);
            float bx = cx + radii[next] * MathUtils.cosDeg(angles[next]);
            float by = cy + radii[next] * MathUtils.sinDeg(angles[next]);
            shapeRenderer.triangle(cx, cy, ax, ay, bx, by);
        }
        shapeRenderer.end();
    }

    private void drawLapIcon(float cx, float cy, float r) {
        // Progress arc (270° filled arc) + small checkmark
        Gdx.gl.glLineWidth(3f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(ACCENT.r, ACCENT.g, ACCENT.b, 1f);
        // Draw arc as line segments
        int segments = 48;
        float startDeg = 90f;
        float arcDeg   = 270f;
        for (int i = 0; i < segments; i++) {
            float a1 = startDeg + i       * arcDeg / segments;
            float a2 = startDeg + (i + 1) * arcDeg / segments;
            shapeRenderer.line(
                cx + r * MathUtils.cosDeg(a1), cy + r * MathUtils.sinDeg(a1),
                cx + r * MathUtils.cosDeg(a2), cy + r * MathUtils.sinDeg(a2));
        }
        // Checkmark at arc end
        float endDeg = startDeg + arcDeg;
        float ex = cx + r * MathUtils.cosDeg(endDeg);
        float ey = cy + r * MathUtils.sinDeg(endDeg);
        shapeRenderer.line(ex, ey, ex + 8f, ey - 8f);
        shapeRenderer.line(ex + 8f, ey - 8f, ex + 18f, ey + 10f);
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
