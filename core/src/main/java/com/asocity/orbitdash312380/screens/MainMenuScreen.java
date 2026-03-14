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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.asocity.orbitdash312380.Constants;
import com.asocity.orbitdash312380.MainGame;
import com.asocity.orbitdash312380.UiFactory;

public class MainMenuScreen implements Screen {

    private final MainGame game;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private ShapeRenderer shapeRenderer;

    private Texture bgTex;

    // Button geometry (bottom-left origin)
    private static final float BTN_W = Constants.BTN_WIDTH_PRIMARY;
    private static final float BTN_H = Constants.BTN_HEIGHT_PRIMARY;
    private static final float BTN_X = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float PLAY_Y        = 510f;
    private static final float SETTINGS_Y   = 420f;
    private static final float LEADERBOARD_Y = 330f;

    // Colors
    private static final Color ACCENT = new Color(1f, 109f / 255f, 0f, 1f);  // #FF6D00
    private static final Color BG_CLR = new Color(0f, 8f / 255f, 20f / 255f, 1f); // #000814

    public MainMenuScreen(MainGame game) {
        this.game = game;

        camera        = new OrthographicCamera();
        viewport      = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage         = new Stage(viewport, game.batch);
        shapeRenderer = new ShapeRenderer();

        bgTex = game.manager.get("backgrounds/bg_main.png", Texture.class);

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

        TextButton playBtn = new TextButton("", inv);
        playBtn.setSize(BTN_W, BTN_H);
        playBtn.setPosition(BTN_X, PLAY_Y);
        playBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new GameScreen(game));
            }
        });
        stage.addActor(playBtn);

        TextButton settingsBtn = new TextButton("", inv);
        settingsBtn.setSize(BTN_W, BTN_H);
        settingsBtn.setPosition(BTN_X, SETTINGS_Y);
        settingsBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new SettingsScreen(game));
            }
        });
        stage.addActor(settingsBtn);

        TextButton lbBtn = new TextButton("", inv);
        lbBtn.setSize(BTN_W, BTN_H);
        lbBtn.setPosition(BTN_X, LEADERBOARD_Y);
        lbBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new LeaderboardScreen(game));
            }
        });
        stage.addActor(lbBtn);
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

        // Background
        game.batch.draw(bgTex, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Title
        game.fontTitle.setColor(ACCENT);
        GlyphLayout tl = new GlyphLayout(game.fontTitle, "ORBIT DASH");
        game.fontTitle.draw(game.batch, "ORBIT DASH",
                (Constants.WORLD_WIDTH - tl.width) / 2f,
                Constants.WORLD_HEIGHT - 80f);
        game.fontTitle.setColor(Color.WHITE);

        // Subtitle
        game.fontSmall.setColor(1f, 1f, 1f, 0.7f);
        GlyphLayout sl = new GlyphLayout(game.fontSmall, "SURVIVE THE ORBIT");
        game.fontSmall.draw(game.batch, "SURVIVE THE ORBIT",
                (Constants.WORLD_WIDTH - sl.width) / 2f,
                Constants.WORLD_HEIGHT - 148f);
        game.fontSmall.setColor(Color.WHITE);

        game.batch.end();

        // Neon buttons
        shapeRenderer.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "PLAY",
                BTN_X, PLAY_Y, BTN_W, BTN_H);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "SETTINGS",
                BTN_X, SETTINGS_Y, BTN_W, BTN_H);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "LEADERBOARD",
                BTN_X, LEADERBOARD_Y, BTN_W, BTN_H);

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
