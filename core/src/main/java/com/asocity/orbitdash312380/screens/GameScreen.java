package com.asocity.orbitdash312380.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.asocity.orbitdash312380.Constants;
import com.asocity.orbitdash312380.MainGame;
import com.asocity.orbitdash312380.UiFactory;

public class GameScreen implements Screen {

    private final MainGame game;

    // ── Viewport ──────────────────────────────────────────────────────────────
    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final ShapeRenderer      shapeRenderer;

    // ── Assets ────────────────────────────────────────────────────────────────
    private final Texture bgTex;
    private final Texture shipTex;
    private final Texture asteroidTex;
    private final Texture starTex;

    // ── Ring radii ────────────────────────────────────────────────────────────
    private static final float[] RING_RADII = {
        Constants.RING_RADIUS_1,
        Constants.RING_RADIUS_2,
        Constants.RING_RADIUS_3
    };

    // ── Ship state ────────────────────────────────────────────────────────────
    private int   currentRing;
    private float shipAngle;       // radians, increases each frame
    private float shipRadius;      // current radius (animates during jump)
    private boolean isJumping;
    private float   jumpTimer;
    private float   jumpFromRadius;
    private float   jumpToRadius;

    // ── Scoring & laps ────────────────────────────────────────────────────────
    private float totalAngle;
    private int   lapCount;
    private int   score;
    private int   starsCollected;

    // ── Obstacles & collectibles: [ringIndex, angle, timer] ──────────────────
    private final Array<float[]> asteroids = new Array<>();
    private final Array<float[]> stars     = new Array<>();
    private float asteroidSpawnTimer;
    private float starSpawnTimer;

    // ── Power-ups ─────────────────────────────────────────────────────────────
    private boolean shieldActive;
    private float   shieldTimer;
    private boolean magnetActive;
    private float   magnetTimer;
    private boolean doubleScore;

    // ── Difficulty ────────────────────────────────────────────────────────────
    private final float diffMultiplier;

    // ── Pause overlay ─────────────────────────────────────────────────────────
    private boolean paused;
    private static final float PAUSE_BTN_X = Constants.WORLD_WIDTH  - Constants.BTN_SIZE_PAUSE - 10f;
    private static final float PAUSE_BTN_Y = Constants.WORLD_HEIGHT - Constants.BTN_SIZE_PAUSE - 10f;
    private static final float BTN_W       = Constants.BTN_WIDTH_PRIMARY;
    private static final float BTN_H       = Constants.BTN_HEIGHT_PRIMARY;
    private static final float BTN_X       = (Constants.WORLD_WIDTH - BTN_W) / 2f;
    private static final float RESUME_Y    = 470f;
    private static final float RESTART_Y   = 380f;
    private static final float MENU_Y      = 290f;

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color BG_CLR    = new Color(0f, 8f / 255f, 20f / 255f, 1f);
    private static final Color RING_CLR  = new Color(0f, 176f / 255f, 1f, 0.55f);
    private static final Color ACCENT    = new Color(1f, 109f / 255f, 0f, 1f);
    private static final Color SHIELD_C  = new Color(0f, 176f / 255f, 1f, 0.30f);
    private static final Color OVERLAY_C = new Color(0f, 8f / 255f, 20f / 255f, 0.82f);

    // ── Constructor ───────────────────────────────────────────────────────────

    public GameScreen(MainGame game) {
        this.game = game;

        camera        = new OrthographicCamera();
        viewport      = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();

        // Load asteroid texture (not pre-loaded by MainGame)
        if (!game.manager.isLoaded("sprites/asteroid_big.png", Texture.class)) {
            game.manager.load("sprites/asteroid_big.png", Texture.class);
            game.manager.finishLoading();
        }

        // Select background based on saved world
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int worldId = prefs.getInteger(Constants.PREF_WORLD, Constants.WORLD_DEFAULT);
        String bgKey = worldId == Constants.WORLD_LAVA ? "backgrounds/bg_planet_lava.png"
                     : worldId == Constants.WORLD_ICE  ? "backgrounds/bg_planet_ice.png"
                     : "backgrounds/bg_planet_default.png";
        bgTex = game.manager.get(bgKey, Texture.class);

        // Select ship skin
        int skinIndex = prefs.getInteger(Constants.PREF_SKIN, 0);
        String shipKey = skinIndex == 1 ? "sprites/player_idle_green.png"
                       : skinIndex == 2 ? "sprites/player_idle_pink.png"
                       : "sprites/player_idle.png";
        shipTex     = game.manager.get(shipKey, Texture.class);
        asteroidTex = game.manager.get("sprites/asteroid_big.png", Texture.class);
        starTex     = game.manager.get("sprites/icon_star.png", Texture.class);

        // Difficulty
        int diff = prefs.getInteger(Constants.PREF_DIFFICULTY, Constants.DIFFICULTY_NORMAL);
        diffMultiplier = diff == Constants.DIFFICULTY_EASY ? Constants.DIFF_EASY_SPEED
                       : diff == Constants.DIFFICULTY_HARD ? Constants.DIFF_HARD_SPEED
                       : Constants.DIFF_NORMAL_SPEED;

        // Activate power-ups purchased in shop and consume them for this run
        shieldActive = prefs.getBoolean(Constants.PREF_SHIELD_ACTIVE, false);
        magnetActive = prefs.getBoolean(Constants.PREF_MAGNET_ACTIVE, false);
        doubleScore  = prefs.getBoolean(Constants.PREF_DOUBLE_ACTIVE,  false);
        prefs.putBoolean(Constants.PREF_SHIELD_ACTIVE, false);
        prefs.putBoolean(Constants.PREF_MAGNET_ACTIVE, false);
        prefs.putBoolean(Constants.PREF_DOUBLE_ACTIVE,  false);
        prefs.flush();
        if (shieldActive) shieldTimer = Constants.SHOP_SHIELD_DURATION;
        if (magnetActive) magnetTimer = Constants.SHOP_MAGNET_DURATION;

        // Initial ship state
        currentRing  = 0;
        shipAngle    = MathUtils.PI / 2f;   // start at top of orbit
        shipRadius   = RING_RADII[0];
        totalAngle   = 0f;
        lapCount     = 0;
        score        = 0;
        starsCollected = 0;
        paused       = false;

        asteroidSpawnTimer = nextAsteroidSpawn();
        starSpawnTimer     = nextStarSpawn();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    if (paused) paused = false;
                    else game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int sx, int sy, int pointer, int button) {
                float wx = toWorldX(sx);
                float wy = toWorldY(sy);

                if (paused) {
                    if (inBounds(wx, wy, BTN_X, RESUME_Y, BTN_W, BTN_H)) {
                        paused = false;
                    } else if (inBounds(wx, wy, BTN_X, RESTART_Y, BTN_W, BTN_H)) {
                        playClick();
                        game.setScreen(new GameScreen(game));
                    } else if (inBounds(wx, wy, BTN_X, MENU_Y, BTN_W, BTN_H)) {
                        playClick();
                        game.setScreen(new MainMenuScreen(game));
                    }
                    return true; // consume all touches when paused
                }

                // Pause button (top-right)
                if (inBounds(wx, wy, PAUSE_BTN_X, PAUSE_BTN_Y, Constants.BTN_SIZE_PAUSE, Constants.BTN_SIZE_PAUSE)) {
                    paused = true;
                    return true;
                }

                // Jump ship to next ring
                if (!isJumping) {
                    currentRing    = (currentRing + 1) % Constants.RING_COUNT;
                    isJumping      = true;
                    jumpTimer      = 0f;
                    jumpFromRadius = shipRadius;
                    jumpToRadius   = RING_RADII[currentRing];
                    playSound("sounds/sfx/sfx_jump.ogg");
                }
                return true;
            }
        });
    }

    // ── Update logic ──────────────────────────────────────────────────────────

    private void update(float delta) {
        float orbitSpeed = Constants.SHIP_ORBIT_SPEED * diffMultiplier;
        shipAngle  += orbitSpeed * delta;
        totalAngle += orbitSpeed * delta;

        // Lap scoring
        int newLapCount = (int)(totalAngle / MathUtils.PI2);
        if (newLapCount > lapCount) {
            int lapPts = (newLapCount - lapCount) * Constants.SCORE_PER_LAP;
            score += doubleScore ? lapPts * 2 : lapPts;
            lapCount = newLapCount;
        }

        // Jump animation
        if (isJumping) {
            jumpTimer += delta;
            float t = Math.min(jumpTimer / Constants.SHIP_JUMP_DURATION, 1f);
            shipRadius = jumpFromRadius + (jumpToRadius - jumpFromRadius) * t;
            if (t >= 1f) {
                isJumping  = false;
                shipRadius = jumpToRadius;
            }
        }

        // Power-up timers
        if (shieldActive) { shieldTimer -= delta; if (shieldTimer <= 0f) shieldActive = false; }
        if (magnetActive) { magnetTimer -= delta; if (magnetTimer <= 0f) magnetActive = false; }

        float astSpeed = Constants.ASTEROID_ORBIT_SPEED * diffMultiplier;

        // Spawn asteroids
        asteroidSpawnTimer -= delta;
        if (asteroidSpawnTimer <= 0f) {
            spawnAsteroid();
            asteroidSpawnTimer = nextAsteroidSpawn();
        }

        // Update & check asteroids
        float shipX = Constants.PLANET_X + shipRadius * MathUtils.cos(shipAngle);
        float shipY = Constants.PLANET_Y + shipRadius * MathUtils.sin(shipAngle);

        for (int i = asteroids.size - 1; i >= 0; i--) {
            float[] ast = asteroids.get(i);
            ast[1] += astSpeed * delta;
            ast[2] += delta;
            if (ast[2] >= Constants.ASTEROID_LIFETIME) {
                asteroids.removeIndex(i);
                continue;
            }
            float ax = Constants.PLANET_X + RING_RADII[(int)ast[0]] * MathUtils.cos(ast[1]);
            float ay = Constants.PLANET_Y + RING_RADII[(int)ast[0]] * MathUtils.sin(ast[1]);
            float dist = dst(shipX, shipY, ax, ay);
            if (dist < (Constants.SHIP_SIZE / 2f + Constants.ASTEROID_SIZE / 2f)) {
                if (shieldActive) {
                    shieldActive = false;
                    shieldTimer  = 0f;
                    asteroids.removeIndex(i);
                    playSound("sounds/sfx/sfx_hit.ogg");
                } else {
                    triggerGameOver();
                    return;
                }
            }
        }

        // Spawn stars
        starSpawnTimer -= delta;
        if (starSpawnTimer <= 0f) {
            spawnStar();
            starSpawnTimer = nextStarSpawn();
        }

        // Update & check stars
        float starOrbitSpeed = astSpeed * 0.55f;
        for (int i = stars.size - 1; i >= 0; i--) {
            float[] st = stars.get(i);
            st[1] += starOrbitSpeed * delta;
            st[2] += delta;
            if (st[2] >= Constants.STAR_LIFETIME) {
                stars.removeIndex(i);
                continue;
            }
            float sx = Constants.PLANET_X + RING_RADII[(int)st[0]] * MathUtils.cos(st[1]);
            float sy = Constants.PLANET_Y + RING_RADII[(int)st[0]] * MathUtils.sin(st[1]);
            float dist = dst(shipX, shipY, sx, sy);

            boolean collect = dist < (Constants.SHIP_SIZE / 2f + Constants.STAR_SIZE / 2f);
            if (!collect && magnetActive) {
                collect = dst(shipX, shipY, sx, sy) < Constants.SHOP_MAGNET_RADIUS;
            }
            if (collect) {
                starsCollected += Constants.STAR_VALUE;
                int pts = Constants.SCORE_PER_STAR;
                score += doubleScore ? pts * 2 : pts;
                stars.removeIndex(i);
                playSound("sounds/sfx/sfx_coin.ogg");
            }
        }
    }

    private void spawnAsteroid() {
        int ring = MathUtils.random(0, Constants.RING_COUNT - 1);
        float angle = shipAngle + MathUtils.PI / 2f + MathUtils.random(0f, MathUtils.PI / 2f);
        asteroids.add(new float[]{ring, angle, 0f});
    }

    private void spawnStar() {
        int ring = MathUtils.random(0, Constants.RING_COUNT - 1);
        float angle = shipAngle + MathUtils.PI / 2f + MathUtils.random(0f, MathUtils.PI / 2f);
        stars.add(new float[]{ring, angle, 0f});
    }

    private float nextAsteroidSpawn() {
        return MathUtils.random(Constants.ASTEROID_SPAWN_MIN, Constants.ASTEROID_SPAWN_MAX);
    }

    private float nextStarSpawn() {
        return MathUtils.random(Constants.STAR_SPAWN_MIN, Constants.STAR_SPAWN_MAX);
    }

    private void triggerGameOver() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int balance = prefs.getInteger(Constants.PREF_STAR_BALANCE, 0) + starsCollected;
        prefs.putInteger(Constants.PREF_STAR_BALANCE, balance);
        prefs.flush();
        playSound("sounds/sfx/sfx_hit.ogg");
        game.setScreen(new GameOverScreen(game, score, starsCollected));
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        if (!paused) update(delta);

        Gdx.gl.glClearColor(BG_CLR.r, BG_CLR.g, BG_CLR.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bgTex, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Orbital rings
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glLineWidth(2f);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(RING_CLR);
        for (float r : RING_RADII) {
            shapeRenderer.circle(Constants.PLANET_X, Constants.PLANET_Y, r, 80);
        }
        shapeRenderer.end();

        // Ship shield glow
        if (shieldActive) {
            float sx = Constants.PLANET_X + shipRadius * MathUtils.cos(shipAngle);
            float sy = Constants.PLANET_Y + shipRadius * MathUtils.sin(shipAngle);
            Gdx.gl.glLineWidth(1f);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(SHIELD_C);
            shapeRenderer.circle(sx, sy, Constants.SHIP_SIZE * 0.85f, 32);
            shapeRenderer.end();
        }

        // Asteroids, stars, ship
        game.batch.begin();
        for (float[] ast : asteroids) {
            float ax = Constants.PLANET_X + RING_RADII[(int)ast[0]] * MathUtils.cos(ast[1]);
            float ay = Constants.PLANET_Y + RING_RADII[(int)ast[0]] * MathUtils.sin(ast[1]);
            game.batch.draw(asteroidTex,
                    ax - Constants.ASTEROID_SIZE / 2f, ay - Constants.ASTEROID_SIZE / 2f,
                    Constants.ASTEROID_SIZE, Constants.ASTEROID_SIZE);
        }
        for (float[] st : stars) {
            float sx = Constants.PLANET_X + RING_RADII[(int)st[0]] * MathUtils.cos(st[1]);
            float sy = Constants.PLANET_Y + RING_RADII[(int)st[0]] * MathUtils.sin(st[1]);
            game.batch.draw(starTex,
                    sx - Constants.STAR_SIZE / 2f, sy - Constants.STAR_SIZE / 2f,
                    Constants.STAR_SIZE, Constants.STAR_SIZE);
        }
        // Ship — rotated to face orbit direction
        float wx = Constants.PLANET_X + shipRadius * MathUtils.cos(shipAngle);
        float wy = Constants.PLANET_Y + shipRadius * MathUtils.sin(shipAngle);
        float rotation = MathUtils.radiansToDegrees * shipAngle + 90f;
        float hs = Constants.SHIP_SIZE / 2f;
        game.batch.draw(shipTex,
                wx - hs, wy - hs, hs, hs,
                Constants.SHIP_SIZE, Constants.SHIP_SIZE,
                1f, 1f, rotation,
                0, 0, shipTex.getWidth(), shipTex.getHeight(), false, false);
        game.batch.end();

        // HUD
        drawHud();

        // Pause button
        shapeRenderer.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontSmall, "II",
                PAUSE_BTN_X, PAUSE_BTN_Y, Constants.BTN_SIZE_PAUSE, Constants.BTN_SIZE_PAUSE);

        // Pause overlay
        if (paused) drawPauseOverlay();
    }

    private void drawHud() {
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        float topY = Constants.WORLD_HEIGHT - Constants.HUD_MARGIN;
        game.fontBody.setColor(Color.WHITE);
        game.fontBody.draw(game.batch, "SCORE: " + score, Constants.HUD_MARGIN, topY);
        game.fontSmall.setColor(ACCENT);
        game.fontSmall.draw(game.batch, "STARS: " + starsCollected, Constants.HUD_MARGIN, topY - 38f);

        if (shieldActive) {
            game.fontSmall.setColor(new Color(0f, 176f / 255f, 1f, 1f));
            game.fontSmall.draw(game.batch, "SHIELD " + (int)shieldTimer + "s",
                    Constants.HUD_MARGIN, topY - 66f);
        }
        if (magnetActive) {
            game.fontSmall.setColor(new Color(0f, 176f / 255f, 1f, 1f));
            game.fontSmall.draw(game.batch, "MAGNET " + (int)magnetTimer + "s",
                    Constants.HUD_MARGIN, topY - (shieldActive ? 94f : 66f));
        }
        if (doubleScore) {
            game.fontSmall.setColor(ACCENT);
            game.fontSmall.draw(game.batch, "2x SCORE", Constants.HUD_MARGIN, topY - 94f);
        }
        game.fontSmall.setColor(Color.WHITE);

        game.batch.end();
    }

    private void drawPauseOverlay() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(OVERLAY_C);
        shapeRenderer.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        shapeRenderer.end();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.fontTitle.setColor(Color.WHITE);
        GlyphLayout pl = new GlyphLayout(game.fontTitle, "PAUSED");
        game.fontTitle.draw(game.batch, "PAUSED",
                (Constants.WORLD_WIDTH - pl.width) / 2f, 620f);
        game.batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "RESUME",
                BTN_X, RESUME_Y, BTN_W, BTN_H);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "RESTART",
                BTN_X, RESTART_Y, BTN_W, BTN_H);
        UiFactory.drawButton(shapeRenderer, game.batch, game.fontBody, "MAIN MENU",
                BTN_X, MENU_Y, BTN_W, BTN_H);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private float toWorldX(int sx) {
        return sx * Constants.WORLD_WIDTH / Gdx.graphics.getWidth();
    }

    private float toWorldY(int sy) {
        return (Gdx.graphics.getHeight() - sy) * Constants.WORLD_HEIGHT / Gdx.graphics.getHeight();
    }

    private boolean inBounds(float wx, float wy, float bx, float by, float bw, float bh) {
        return wx >= bx && wx <= bx + bw && wy >= by && wy <= by + bh;
    }

    private float dst(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1, dy = y2 - y1;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    private void playSound(String path) {
        if (game.sfxEnabled)
            game.manager.get(path, Sound.class).play(1f);
    }

    private void playClick() {
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1f);
    }

    // ── Screen lifecycle ──────────────────────────────────────────────────────

    @Override
    public void show() {
        game.playMusic("sounds/music/music_gameplay.ogg");
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }

    @Override public void pause()  { paused = true; }
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        // Unload asteroid texture we loaded in this screen
        if (game.manager.isLoaded("sprites/asteroid_big.png", Texture.class))
            game.manager.unload("sprites/asteroid_big.png");
    }
}
