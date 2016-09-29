package whitetea.magicmatrix.model.animation;

public enum AnimationType {

	EDITOR("Editor", null), 
	CUSTOM("Custom frames", new CustomFramesAnimation()), 
	METEOR("Meteor", new MeteorAnimation()), 
	SNOW("Snow", new SnowAnimation()), 
	RANDOM("Random", new RandomAnimation());

	private String string;
	private Animation animation;

	private AnimationType(String name, Animation animation) {
		string = name;
		this.animation = animation;
	}

	@Override
	public String toString() {
		return string;
	}

	public Animation getAnimation() {
		return animation;
	}

}
