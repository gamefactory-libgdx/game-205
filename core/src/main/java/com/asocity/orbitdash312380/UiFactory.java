package com.asocity.orbitdash312380;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Neon button drawing helper.
 * Call drawButton() from each screen's render() after ending SpriteBatch.
 * The ShapeRenderer's projection matrix must be set by the caller before use.
 */
public class UiFactory {

    // Primary: #00B0FF
    private static final float P_R = 0f;
    private static final float P_G = 176f / 255f;
    private static final float P_B = 1f;

    private static final GlyphLayout layout = new GlyphLayout();

    /**
     * Draws a neon-style button:
     *   - outer glow: rect 4 px larger on each side, primary color at 30 % alpha
     *   - 2 px solid border: primary color at 100 % alpha
     *   - label: centered, primary color
     *
     * Preconditions:
     *   - batch must NOT be active (begin/end managed internally)
     *   - sr projection matrix must already be set to the screen's camera.combined
     */
    public static void drawButton(ShapeRenderer sr, SpriteBatch batch, BitmapFont font,
                                   String label, float x, float y, float w, float h) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Outer glow pass — filled rect 4 px wider on every side at 30 % alpha
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(P_R, P_G, P_B, 0.30f);
        sr.rect(x - 4f, y - 4f, w + 8f, h + 8f);
        sr.end();

        // 2 px solid border
        Gdx.gl.glLineWidth(2f);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(P_R, P_G, P_B, 1f);
        sr.rect(x, y, w, h);
        sr.end();

        // Label centered in button, primary color
        font.setColor(P_R, P_G, P_B, 1f);
        layout.setText(font, label);
        batch.begin();
        font.draw(batch, label,
                x + (w - layout.width)  / 2f,
                y + (h + layout.height) / 2f);
        batch.end();

        // Reset font color to white for subsequent uses
        font.setColor(1f, 1f, 1f, 1f);
    }
}
