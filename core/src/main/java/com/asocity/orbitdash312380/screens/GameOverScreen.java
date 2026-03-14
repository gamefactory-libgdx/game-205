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

public class GameOverScreen implements Screen {

    private final MainGame game;
    private final int score;
    private final int stars;          // extra: stars collected during the run
    private int personalBest;

    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private ShapeRenderer shapeRenderer;

    private Texture bgTex;

    // Button geometry
    private static final float BTN_W  = Constants.BTN_WIDTH_PRIMARY;
    private static final float BTN_H  = Constants.BTN_HEIGHT_PRIMARY;
    private static final float BTN_X  = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float RETRY_Y = 260f;
    private static final float MENU_Y  = 170f;

    // Colors
    private static final Color BG_CLR = new Color(0f, 8f / 255f, 20f / 255f, 1f);
    private static final Color ACCENT = new Color(1f, 109f / 255f, 0f, 1f);
    private static final Color DIM    = new Color(1f, 1f, 1f, 0.6f);

    public GameOverScreen(MainGame game, int score, int stars) {
        this.game  = game;
        this.score = score;
        this.stars = stars;

        camera        = new OrthographicCamera();
        viewport      = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage         = new Stage(viewport, game.batch);
        shapeRenderer = new ShapeRenderer();

        bgTex = game.manager.get("backgrounds/bg_main.png", Texture.class);

        // Update personal best
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        personalBest = prefs.getInteger(Constants.PREF_HIGH_SCORE, 0);
        if (score > personalBest) {
            personalBest = score;
            prefs.putInteger(Constants.PREF_HIGH_SCORE, personalBest);
            prefs.flush();
        }

        // Save score to leaderboard
        LeaderboardScreen.addScore(score);

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

        // Retry — fresh GameScreen instance
        TextButton retryBtn = new TextButton("", inv);
        retryBtn.setSize(BTN_W, BTN_H);
        retryBtn.setPosition(BTN_X, RETRY_Y);
        retryBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new GameScreen(game));
            }
        });
        stage.addActor(retryBtn);

        // Main Menu
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
        game.playMusicOnce("sounds/music/music_game_over.ogg");
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_game_over.ogg", Sound.class).play(1f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BG_CLR.r, BG_CLR.g, BG_CLR.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        game.batch.draw(bgTex, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // "GAME OVER" heading
        game.fontTitle.setColor(ACCENT);
        GlyphLayout goLayout = new GlyphLayout(game.fontTitle, "GAME OVER");
        game.fontTitle.draw(game.batch, "GAME OVER",
                (Constants.WORLD_WIDTH - goLayout.width) / 2f,
                Constants.WORLD_HEIGHT - 80f);
        game.fontTitle.setColor(Color.WHITE);

        // Score
        String scoreStr = "SCORE:  " + score;
        game.fontHeader.setColor(Color.WHITE);
        GlyphLayout sl = new GlyphLayout(game.fontHeader, scoreStr);
        game.fontHeader.draw(game.batch, scoreStr,
                (Constants.WORLD_WIDTH - sl.width) / 2f, 620f);

        // Personal best
        String pbStr = "BEST:   " + personalBest;
        boolean newRecord = score >= personalBest && score > 0;
        game.fontBody.setColor(newRecord ? ACCENT : DIM);
        GlyphLayout pb = new GlyphLayout(game.fontBody, pbStr);
        game.fontBody.draw(game.batch, pbStr,
                (Constants.WORLD_WIDTH - pb.width) / 2f, 555f);
        if (newRecord) {
            game.fontSmall.setColor(ACCENT);
            GlyphLayout nr = new GlyphLayout(game.fontSmall, "NEW RECORD!");
            game.fontSmall.draw(game.batch, "NEW RECORD!",
                    (Constants.WORLD_WIDTH - nr.width) / 2f, 520f);
            game.fontSmall.setColor(Color.WHITE);
        }
        game.fontBody.setColor(Color.WHITE);

        // Stars collected
        String starsStr = "STARS:  " + stars;
        game.fontBody.setColor(DIM);
        GlyphLayout stl = new GlyphLayout(game.fontBody, starsStr);
        game.fontBody.draw(game.batch, starsStr,
                (Constants.WORLD_WIDTH - stl.width) / 2f, 480f);
        game.fontBody.setColor(Color.WHITE);

        game.batch.end();

        // Neon buttons
        shapeRenderer.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "RETRY",
                BTN_X, RETRY_Y, BTN_W, BTN_H);
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
