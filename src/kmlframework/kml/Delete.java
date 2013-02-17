package kmlframework.kml;

public class Delete extends UpdateElement {

	public Delete() {}
	
	public Delete(KmlObject kmlObject) {
		setKmlObject(kmlObject);
	}
	
	@Override
	public void setKmlObject(KmlObject kmlObject)
	{
		if(!(kmlObject instanceof Deletable))
			throw new IllegalArgumentException("Only deletable objects can be deleted");
		super.setKmlObject(kmlObject);
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<Delete>", 1);
		((Deletable) getKmlObject()).writeDelete(kml);
		kml.println(-1, "</Delete>");
	}
}
