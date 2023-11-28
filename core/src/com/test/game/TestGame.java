package com.test.game;

import java.util.Iterator;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;

public class TestGame extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;

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
		dropImage = new Texture(Gdx.files.internal("drop.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		camera = new OrthographicCamera();
		batch = new SpriteBatch();
		bucket = new Rectangle();

		camera.setToOrtho(false, 800, 400);
		bucket.width = 64;
		bucket.height = 64;
		bucket.x = (float) 800 / 2 - bucket.width / 2;
		bucket.y = 20;

		raindrops = new Array<>();
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
		for (Rectangle drop : raindrops) {
			batch.draw(dropImage, drop.x, drop.y);
		}
		batch.draw(bucketImage, bucket.x, bucket.y);
		batch.end();

		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = MathFunctions.clamp(MathFunctions.lerp(bucket.x, touchPos.x - (float) 64 / 2, 5f * dt), 0f, (float) 800 - 64);
		}

		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0) iter.remove();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		bucketImage.dispose();
		dropImage.dispose();
	}
}
