package com.test.game;

import java.util.Iterator;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;

public class TestGame extends ApplicationAdapter {
	private Texture dropImage;
	private Sound dropSound;
	private Texture bucketImage;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private Vector2 lastMousePosition;
	private BitmapFont scoreFont;
	private GlyphLayout scoreGlyph;
	private int score;

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void create () {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParams.size = 48;

		dropImage = new Texture(Gdx.files.internal("drop.png"));
		dropSound = Gdx.audio.newSound(Gdx.files.internal("dropSound.wav"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		camera = new OrthographicCamera();
		batch = new SpriteBatch();
		bucket = new Rectangle();
		raindrops = new Array<>();
		lastMousePosition = new Vector2();
		scoreFont = generator.generateFont(fontParams);
		scoreGlyph = new GlyphLayout();
		score = 0;

		camera.setToOrtho(false, 800, 400);
		lastMousePosition.x = 400;
		bucket.width = 64;
		bucket.height = 64;
		bucket.x = (float) 800 / 2 - bucket.width / 2;
		bucket.y = 20;

		spawnRaindrop();
	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime();

		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		scoreGlyph.setText(scoreFont, Integer.toString(score));
		scoreFont.draw(batch, scoreGlyph, 400 - scoreGlyph.width/2, 300);
		for (Rectangle drop : raindrops) {
			batch.draw(dropImage, drop.x, drop.y);
		}
		batch.draw(bucketImage, bucket.x, bucket.y);
		batch.end();

		if(Gdx.input.isTouched()) {
			lastMousePosition.x = Gdx.input.getX();
			lastMousePosition.y = Gdx.input.getY();
		}

		Vector3 touchPos = new Vector3();
		touchPos.set(lastMousePosition.x, lastMousePosition.y, 0);
		camera.unproject(touchPos);
		bucket.x = MathFunctions.clamp(MathFunctions.lerp(bucket.x, touchPos.x - (float) 64 / 2, 5f * dt), 0f, (float) 800 - 64);


		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0) iter.remove();
			if(raindrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
				score += 1;
			}
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		bucketImage.dispose();
		dropImage.dispose();
		dropSound.dispose();
	}
}
