package whitetea.magicmatrix.model.animation;

public enum AnimationType {

	EDITOR("Editor", null), 
	CUSTOM("Custom frames", new CustomFramesAnimation()),
	PLASMA("Plasma", null),
	SNOW("Snow", new SnowAnimation()), 
	METEOR("Meteor", new MeteorAnimation()), 
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
