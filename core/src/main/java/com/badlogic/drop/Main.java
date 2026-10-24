
package com.badlogic.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {

    // Game images
    private Texture backgroundTexture;
    private Texture bucketTexture;
    private Texture dropTexture;

    // Game sounds
    private Sound dropSound;
    private Music music;

    // Drawing tools
    private SpriteBatch spriteBatch;
    private FitViewport viewport;

    // Bucket and raindrops
    private Sprite bucketSprite;
    private Vector2 touchPos;
    private Array<Sprite> dropSprites;

    // Timer for creating new drops
    private float dropTimer;

    // Collision detection
    private Rectangle bucketRectangle;
    private Rectangle dropRectangle;

    @Override
    public void create() {

        // Load images from the assets folder
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");

        // Load sounds
        dropSound = Gdx.audio.newSound(
            Gdx.files.internal("drop.mp3")
        );

        music = Gdx.audio.newMusic(
            Gdx.files.internal("music.mp3")
        );

        // Initialize drawing tools
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);

        // Create the bucket
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1, 1);
        bucketSprite.setPosition(3.5f, 0);

        // Initialize game objects
        touchPos = new Vector2();
        dropSprites = new Array<>();

        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();

        // Start background music
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();

        // Create the first raindrop
        createDroplet();
    }

    @Override
    public void render() {

        // 1. Read player input
        input();

        // 2. Update game logic
        logic();

        // 3. Draw everything
        draw();
    }

    private void input() {

        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        // Move bucket right
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite.translateX(speed * delta);
        }

        // Move bucket left
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite.translateX(-speed * delta);
        }

        // Move bucket using mouse or touch
        if (Gdx.input.isTouched()) {

            touchPos.set(
                Gdx.input.getX(),
                Gdx.input.getY()
            );

            viewport.unproject(touchPos);
            bucketSprite.setCenterX(touchPos.x);
        }
    }

    private void logic() {

        float worldWidth = viewport.getWorldWidth();

        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        // Prevent bucket from leaving the screen
        bucketSprite.setX(
            MathUtils.clamp(
                bucketSprite.getX(),
                0,
                worldWidth - bucketWidth
            )
        );

        float delta = Gdx.graphics.getDeltaTime();

        // Set bucket collision area
        bucketRectangle.set(
            bucketSprite.getX(),
            bucketSprite.getY(),
            bucketWidth,
            bucketHeight
        );

        // Update all raindrops
        for (int i = dropSprites.size - 1; i >= 0; i--) {

            Sprite dropSprite = dropSprites.get(i);

            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            // Make raindrop fall
            dropSprite.translateY(-2f * delta);

            // Set raindrop collision area
            dropRectangle.set(
                dropSprite.getX(),
                dropSprite.getY(),
                dropWidth,
                dropHeight
            );

            // Remove drops below the screen
            if (dropSprite.getY() < -dropHeight) {
                dropSprites.removeIndex(i);
            }

            // Check whether bucket catches a drop
            else if (bucketRectangle.overlaps(dropRectangle)) {

                dropSprites.removeIndex(i);

                // Play collection sound
                dropSound.play();
            }
        }

        // Create a new drop approximately every second
        dropTimer += delta;

        if (dropTimer > 1f) {
            dropTimer = 0;
            createDroplet();
        }
    }

    private void draw() {

        // Clear the screen
        ScreenUtils.clear(Color.BLACK);

        // Apply viewport
        viewport.apply();

        spriteBatch.setProjectionMatrix(
            viewport.getCamera().combined
        );

        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Draw background
        spriteBatch.draw(
            backgroundTexture,
            0,
            0,
            worldWidth,
            worldHeight
        );

        // Draw bucket
        bucketSprite.draw(spriteBatch);

        // Draw all raindrops
        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    private void createDroplet() {

        float dropWidth = 1;
        float dropHeight = 1;

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Create a new raindrop
        Sprite dropSprite = new Sprite(dropTexture);

        dropSprite.setSize(dropWidth, dropHeight);

        // Random horizontal position
        dropSprite.setX(
            MathUtils.random(
                0f,
                worldWidth - dropWidth
            )
        );

        // Start at the top of the screen
        dropSprite.setY(worldHeight);

        // Add to the list
        dropSprites.add(dropSprite);
    }

    @Override
    public void resize(int width, int height) {

        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // No action needed
    }

    @Override
    public void resume() {
        // No action needed
    }

    @Override
    public void dispose() {

        // Release resources
        backgroundTexture.dispose();
        bucketTexture.dispose();
        dropTexture.dispose();

        dropSound.dispose();
        music.dispose();

        spriteBatch.dispose();
    }
}
