
public class Variables {
	public Variables(String name, String type, String returnType,String packageImport) {
		super();
		this.name = name;
		this.type = type;
		this.returnType = returnType;
		this.packageImport = packageImport;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Variables other = (Variables) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	String name;
	public String getName() {
		return name;
	}

	String type;
	public void setType(String type) {
		this.type = type;
		if(type=="variable"){
			this.packageImport = "NA";
		}
		else
			this.returnType = "NA";
	}

	String returnType;
	String packageImport;
	

//	@Override
//    public boolean equals (Variables other) {
//        boolean destinationSame = false;
//        destinationsame = this.getDestination().equals(other.getDestination());
//        Integer thisQuantity = (Integer) this.getQuantity();
//        Integer otherQuantity = (Integer) other.getQuantity();
//        quantitySame = thisQuantity.equals(otherQuantity);
//        return destinationSame && quantitySame;
//    }
//
//    public int hashCode() {
//        return destination.hashCode() + quantity;
//    }
}
