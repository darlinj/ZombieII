package com.zombiekiller.zombie;


import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.handler.IUpdateHandler;

/**
 * @author Nicolas Gramlich
 * @since 11:54:51 - 03.04.2010
 */
public class Main extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	private static final float DEMO_VELOCITY = 60.0f;
	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;
	private Texture mTexture;
	private TiledTextureRegion mBrainTextureRegion;

	private TiledTextureRegion mZombieTextureRegion;
	private TiledTextureRegion mZombieEatBrainTextureRegion;
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Engine onLoadEngine() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		this.mTexture = new Texture(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		TextureRegionFactory.setAssetBasePath("gfx/");


		this.mZombieTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "zombie.png", 0, 0, 3, 2);
		this.mBrainTextureRegion =  TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "brain_tiled.png", 0, 500, 6, 1);
//this.mZombieEatBrainTextureRegion =  TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "zombie_eat_brain.png", 0, 800, 6, 1);
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.09804f, 0.0274f, 0.8784f));

		/* Calculate the coordinates for the face, so its centered on the camera. */
		final int centerX = (CAMERA_WIDTH - (this.mBrainTextureRegion.getWidth()/6));
		final int centerY = 250; //this.mBrainTextureRegion.getHeight() * 2;

		/* Create the face and add it to the scene. */
		final AnimatedSprite brain = new AnimatedSprite(centerX, centerY, this.mBrainTextureRegion);
    brain.animate(400);
		scene.getLastChild().attachChild(brain);

    //final AnimatedSprite zombieEatBrain = new AnimatedSprite(centerX, centerY, this.mZombieTextureRegion);

		/* Zombie. */
		final Zombie zombie = new Zombie(0, 200, this.mZombieTextureRegion);
		final PhysicsHandler physicsHandler = new PhysicsHandler(zombie);
		zombie.registerUpdateHandler(physicsHandler);
		physicsHandler.setVelocity(DEMO_VELOCITY, 0.0f);

		scene.getLastChild().attachChild(zombie);

		scene.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				if(brain.collidesWith(zombie)) {
		      scene.getLastChild().detachChild(zombie);
		      scene.getLastChild().detachChild(brain);
          //zombieEatBrain.animate(400);
          //scene.getLastChild().attachChild(zombieEatBrain);
				}
			}
		});

		return scene;
	}

	@Override
	public void onLoadComplete() {

	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
  //

	private static class Zombie extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;

		public Zombie(final float pX, final float pY, final TiledTextureRegion pTextureRegion) {
			super(pX, pY, pTextureRegion);
      this.animate(200);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			if(this.mX < 0) {
				this.mPhysicsHandler.setVelocityX(DEMO_VELOCITY);
			} else if(this.mX + this.getWidth() > CAMERA_WIDTH) {
				this.mPhysicsHandler.setVelocityX(-DEMO_VELOCITY);
			}

			super.onManagedUpdate(pSecondsElapsed);
		}
	}
}
