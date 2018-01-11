package Cad2AmePiping.csp.constraints;

import org.moflon.tgg.language.csp.Variable;
import org.moflon.tgg.language.csp.impl.TGGConstraintImpl;

public class PortNumberToIdAndTitle extends TGGConstraintImpl {
	public void solve(Variable var_0, Variable var_1, Variable var_2) {
		String bindingStates = getBindingStates(var_0, var_1, var_2);

		char charactera = 0, characterb = 0;
		if (var_0.isBound()) {
			charactera = (char) ((int) (var_0.getValue()) + 97);
		}
		if (var_1.isBound()) {
			String value = (String) var_1.getValue();
			characterb = value.charAt(value.length() - 1);
		}

		switch (bindingStates) {
		case "BBB":
			setSatisfied(charactera == characterb);
			return;
		case "BFF":
			var_1.bindToValue("port_" + charactera);
			var_2.bindToValue("Port  " + Character.toUpperCase(charactera));
			setSatisfied(true);
			return;
		default:
			throw new UnsupportedOperationException(
					"This case in the constraint has not been implemented yet: " + bindingStates);
		}

	}
}