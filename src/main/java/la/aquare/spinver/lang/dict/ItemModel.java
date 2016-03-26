package la.aquare.spinver.lang.dict;


/**
 * Representa o modelo de um item que é objeto de
 * compra/venda.
 */
public class ItemModel extends ItemAttribute {
	public int id;
	public String name;
	public String type;
	public String keywords;

	public boolean equals(Object other) {
		boolean isNameEqual = false, isTypeEqual = false;		
		if (other != null && other instanceof ItemModel) {
			ItemModel otherItemModel = (ItemModel) other;
			if (name == null && otherItemModel.name == null) {
				isNameEqual = true;
			}
			if (name != null && otherItemModel != null &&
					name.equals(otherItemModel.name) ) {
				isNameEqual = true;
			}
			if (type == null && otherItemModel.type == null) {
				isTypeEqual = true;
			}
			if (type != null && otherItemModel != null &&
					type.equals(otherItemModel.type) ) {
				isTypeEqual = true;
			}
		}
		return isNameEqual && isTypeEqual;
	}
	
	public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
	}
	
	public String toString() {
//		return  name + " {" + id + "}";
		return  name;
	}
	
	public String toLongString() {
		return  name + " (" + type + "): " + keywords;
	}

}
