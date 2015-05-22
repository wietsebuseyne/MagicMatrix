package whitetea.magicmatrix.model.observer;

import whitetea.magicmatrix.model.MagicMatrix;

public interface Observer {
	
	public void update(MagicMatrix updatedModel);

}
