package la.aquare.spinver;

import java.util.ArrayList;
import java.util.List;

import la.aquare.spinver.lang.dict.Actions.Action;
import la.aquare.spinver.lang.dict.Item;
import la.aquare.spinver.lang.dict.ItemModel;
import la.aquare.spinver.lang.dict.ItemVersion;

/**
 * Re&uacute;ne as informações de um resultado de análise.
 */
public final class Result {
	public Action action;
	public Item item;
	public List<ItemModel> models;
	public ItemVersion version;
	
	public Result() {
		models = new ArrayList<ItemModel>();
	}
	
	public String getFullItemName() {
		return item + 
				((version==null?"":" " + version)) +
				((models == null || models.isEmpty())?
						"":" " + models.toString().replaceAll("[\\[\\],]", ""));
	}
	
	public String toString() {
		return action + ":" + item +
			((version==null?"":" " + version)) +
			((models == null || models.isEmpty())?
					"":" " + models.toString().replaceAll("[\\[\\],]", ""));
	}
	
	public String toLongString() {
		return action + ":" + item +
			((version==null?"":"; v: " + version)) +
			((models == null || models.isEmpty())?
					"":"; m: " + models.toString().replaceAll("[\\[\\],]", ""));
	}
}
