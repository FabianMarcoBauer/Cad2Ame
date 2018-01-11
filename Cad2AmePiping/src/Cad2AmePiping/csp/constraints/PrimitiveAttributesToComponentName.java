package Cad2AmePiping.csp.constraints;

import java.util.HashMap;
import java.util.Map;

import org.moflon.tgg.csp.constraints.*;
import org.moflon.tgg.language.csp.Variable;
import org.moflon.tgg.language.csp.impl.TGGConstraintImpl;

public class PrimitiveAttributesToComponentName extends TGGConstraintImpl {
	private static final Map<String, String> id2namePrefix = new HashMap<>();

	static {
		id2namePrefix.put("bend", "hrbend");
		id2namePrefix.put("t_junction", "hrtee90");
		id2namePrefix.put("pipe", "hrpipe");
	}

	public void solve(Variable var_0, Variable var_2, Variable var_3) {
		String bindingStates = getBindingStates(var_0, var_2, var_3);

		String id = null, name = null;
		Integer uId = null;

		if (var_0.isBound())
			id = (String) var_0.getValue();
		if (var_2.isBound())
			uId = (Integer) var_2.getValue();
		if (var_3.isBound())
			name = (String) var_3.getValue();

		switch (bindingStates) {
		case "BBB":
			setSatisfied(name.equals(id2namePrefix.get(id) + "_" + (uId == null ? "0" : uId.toString())));
			return;
		case "BBF":
			var_3.bindToValue(id2namePrefix.get(id) + "_" + (uId == null ? "0" : uId.toString()));
			setSatisfied(true);
			return;
		case "BFB":
			String[] split = name.split("_");
			if (split.length < 2) {
				var_2.bindToValue(null);
			} else {
				try {
					var_2.bindToValue(Integer.parseInt(split[split.length - 1]));
				} catch (NumberFormatException e) {
					var_2.bindToValue(null);
				}
			}
			setSatisfied(true);
			return;
		default:
			throw new UnsupportedOperationException(
					"This case in the constraint has not been implemented yet: " + bindingStates);
		}

	}
}