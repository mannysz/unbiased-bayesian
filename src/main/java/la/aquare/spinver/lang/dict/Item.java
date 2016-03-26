package la.aquare.spinver.lang.dict;

/**
 * Representa um item que é objeto de compra/venda.
 *
 */
public class Item implements Cloneable {
	public int id;
	public String name;
	public String keyword;

	public boolean equals(Object other) {
		boolean isEqual = false;
		if (other instanceof Item && other != null) {
			Item otherItem = (Item) other;
			if (name == null && otherItem.name == null) {
				isEqual = true;
			}
			if (name != null && otherItem != null &&
					name.equals(otherItem.name) ) {
				isEqual = true;
			}
		}
		return isEqual;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public String toString() {
//		return name + " {" + id + "}";
		return name;
//		return name + "|" + (keyword==null?"no keyword":keyword);
	}
	
	public String toLongString() {
		return name + "|" + (keyword==null?"no keyword":keyword);
	}
	
	public Item clone() {
		Item newItem = new Item();
		newItem.name = this.name;
		newItem.keyword = this.keyword;
		newItem.id = this.id;
		return newItem;
	}
}
