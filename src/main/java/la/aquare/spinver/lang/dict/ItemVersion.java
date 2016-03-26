package la.aquare.spinver.lang.dict;


/**
 * Representa a versão de um item que é objeto de
 * compra/venda.
 */
public class ItemVersion extends ItemAttribute {
	public int id;
	public String name;
	public String keywords;
	
	public boolean equals(Object other) {
		boolean isEqual = false;
		if (other != null && other instanceof ItemVersion) {
			ItemVersion otherItemVersion = (ItemVersion) other;
			isEqual = (name == otherItemVersion.name);
		}
		return isEqual;
	}
	
	public int hashCode() {
		return System.identityHashCode(name);
	}
	
	public String toString() {
//		return name + " {" + id + "}";
		return name;
	}
	
	public String toLongString() {
		return name + ": " + keywords;
	}
}
