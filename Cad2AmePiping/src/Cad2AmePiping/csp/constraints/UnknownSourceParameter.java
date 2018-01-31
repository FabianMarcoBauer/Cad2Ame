package Cad2AmePiping.csp.constraints;

import java.util.List;

import org.moflon.tgg.language.csp.Variable;
import org.moflon.tgg.language.csp.impl.TGGConstraintImpl;

public class UnknownSourceParameter extends TGGConstraintImpl {
	public void solve(Variable var_0) {
		String bindingStates = getBindingStates(var_0);

		switch (bindingStates) {
		case "B":
			List<Parameter> list = Parameter.source.get(var_0.getValue());
			setSatisfied(list==null || list.isEmpty());
			return;
		default:
			throw new UnsupportedOperationException(
					"This case in the constraint has not been implemented yet: " + bindingStates);
		}

	}
}