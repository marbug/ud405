package com.udacity.gamedev.icicles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.udacity.gamedev.icicles.Constants.Difficulty;


public class IciclesScreen extends ScreenAdapter {

    public static final Color BACKGROUND_COLOR = Color.BLUE;

    Game game;
    Difficulty difficulty;

    ExtendViewport iciclesViewport;
    ShapeRenderer renderer;

    ScreenViewport hudViewport;
    SpriteBatch batch;
    BitmapFont font;

    Player player;
    Icicles icicles;

    int topScore;

    public IciclesScreen(Game game, Difficulty difficulty){
        this.game = game;
        this.difficulty = difficulty;
    }

    @Override
    public void show() {
        iciclesViewport = new ExtendViewport(Constants.WORLD_SIZE, Constants.WORLD_SIZE);

        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);

        hudViewport = new ScreenViewport();
        batch = new SpriteBatch();

        font = new BitmapFont();
        font.getData().setScale(Constants.HUD_FONT_SCALE);
        font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

        player = new Player(iciclesViewport);
        Gdx.input.setInputProcessor(player);
        icicles = new Icicles(iciclesViewport);

        topScore = 0;
    }

    @Override
    public void resize(int width, int height) {
        iciclesViewport.update(width, height, true);
        hudViewport.update(width, height, true);
        font.getData().setScale(Constants.HUD_FONT_SCALE * height / 450);

        player.init(iciclesViewport);
        icicles.init();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        batch.dispose();
    }


    @Override
    public void render(float delta) {
        icicles.update(delta);
        player.update(delta, iciclesViewport);
        if (player.hitByIcicle(icicles)) {
            icicles.init();
        }


        iciclesViewport.apply(true);
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setProjectionMatrix(iciclesViewport.getCamera().combined);
        renderer.begin(ShapeType.Filled);
        icicles.render(renderer);
        player.render(renderer);
        renderer.end();

        hudViewport.apply();
        batch.setProjectionMatrix(hudViewport.getCamera().combined);
        batch.begin();

        topScore = Math.max(topScore, icicles.iciclesDodged);

        font.draw(batch, "Deaths: " + player.deaths,
                Constants.HUD_MARGIN, hudViewport.getWorldHeight() - Constants.HUD_MARGIN);
        font.draw(batch, "Score: " + icicles.iciclesDodged + "\nTop Score: " + topScore,
                hudViewport.getWorldWidth() - Constants.HUD_MARGIN, hudViewport.getWorldHeight() - Constants.HUD_MARGIN,
                0, Align.right, false);



        batch.end();


    }

    public void setDifficulty(Difficulty difficulty){
        icicles.difficulty = difficulty;
    }

}
