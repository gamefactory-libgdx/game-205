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

public class LeaderboardScreen implements Screen {

    private final MainGame game;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private ShapeRenderer shapeRenderer;

    private Texture bgTex;
    private int[] scores;

    // Button geometry
    private static final float BTN_W = Constants.BTN_WIDTH_PRIMARY;
    private static final float BTN_H = Constants.BTN_HEIGHT_PRIMARY;
    private static final float BTN_X = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float MENU_Y = 40f;

    // Colors
    private static final Color BG_CLR  = new Color(0f, 8f / 255f, 20f / 255f, 1f);
    private static final Color ACCENT  = new Color(1f, 109f / 255f, 0f, 1f);
    private static final Color PRIMARY = new Color(0f, 176f / 255f, 1f, 1f);
    private static final Color DIM     = new Color(1f, 1f, 1f, 0.5f);

    // ── Static helper ─────────────────────────────────────────────────────────

    /**
     * Insert a new score into the top-10 list stored in SharedPreferences.
     * Call this from GameOverScreen.
     */
    public static void addScore(int newScore) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int[] current = loadScores(prefs);

        // Insert and keep sorted descending
        int[] updated = new int[Constants.LEADERBOARD_MAX];
        boolean inserted = false;
        int j = 0;
        for (int i = 0; i < Constants.LEADERBOARD_MAX; i++) {
            if (!inserted && newScore > current[j]) {
                updated[i] = newScore;
                inserted = true;
            } else {
                if (j < Constants.LEADERBOARD_MAX) updated[i] = current[j++];
            }
        }
        if (!inserted) {
            // newScore is not in the top 10 — no update needed
        }

        saveScores(prefs, updated);
    }

    private static int[] loadScores(Preferences prefs) {
        int[] arr = new int[Constants.LEADERBOARD_MAX];
        for (int i = 0; i < Constants.LEADERBOARD_MAX; i++) {
            arr[i] = prefs.getInteger(Constants.PREF_LEADERBOARD + i, 0);
        }
        return arr;
    }

    private static void saveScores(Preferences prefs, int[] arr) {
        for (int i = 0; i < Constants.LEADERBOARD_MAX; i++) {
            prefs.putInteger(Constants.PREF_LEADERBOARD + i, arr[i]);
        }
        prefs.flush();
    }

    // ── Constructor ───────────────────────────────────────────────────────────

    public LeaderboardScreen(MainGame game) {
        this.game = game;

        camera        = new OrthographicCamera();
        viewport      = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage         = new Stage(viewport, game.batch);
        shapeRenderer = new ShapeRenderer();

        bgTex  = game.manager.get("backgrounds/bg_main.png", Texture.class);
        scores = loadScores(Gdx.app.getPreferences(Constants.PREFS_NAME));

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
        GlyphLayout tl = new GlyphLayout(game.fontTitle, "LEADERBOARD");
        game.fontTitle.draw(game.batch, "LEADERBOARD",
                (Constants.WORLD_WIDTH - tl.width) / 2f,
                Constants.WORLD_HEIGHT - 60f);
        game.fontTitle.setColor(Color.WHITE);

        // Score rows  — 10 entries, evenly spaced between y=730 and y=150
        float topY   = 720f;
        float rowGap = 55f;

        for (int i = 0; i < Constants.LEADERBOARD_MAX; i++) {
            float rowY = topY - i * rowGap;
            String rank = (i + 1) + ".";
            String val  = scores[i] > 0 ? String.valueOf(scores[i]) : "---";
            String line = String.format("%-4s  %s", rank, val);

            // Highlight top 3
            if (i == 0)      game.fontBody.setColor(ACCENT);
            else if (i <= 2) game.fontBody.setColor(PRIMARY);
            else             game.fontBody.setColor(DIM);

            GlyphLayout rl = new GlyphLayout(game.fontBody, line);
            game.fontBody.draw(game.batch, line,
                    (Constants.WORLD_WIDTH - rl.width) / 2f, rowY);
        }
        game.fontBody.setColor(Color.WHITE);

        game.batch.end();

        // Neon button
        shapeRenderer.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "MAIN MENU",
                BTN_X, MENU_Y, BTN_W, BTN_H);

        stage.act(delta);
        stage.draw();
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
