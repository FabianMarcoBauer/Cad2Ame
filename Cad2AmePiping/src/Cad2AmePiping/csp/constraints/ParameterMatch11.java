package Cad2AmePiping.csp.constraints;

import java.util.List;

import org.moflon.tgg.language.csp.Variable;
import org.moflon.tgg.language.csp.impl.TGGConstraintImpl;

public class ParameterMatch11 extends TGGConstraintImpl {

	public void solve(Variable var_0, Variable var_1, Variable var_2, Variable var_3, Variable title, Variable unit) {
		String bindingStates = getBindingStates(var_0, var_1, var_2, var_3, title, unit);
		List<Parameter> parameters;
		switch (bindingStates) {
		case "BBBBBB":
			parameters = Parameter.source.get(var_0.getValue());
			if (parameters == null)
				return;
			setSatisfied(parameters.stream().filter(p -> p.check(var_0, var_1, var_2, var_3, title, unit, "1:1")).findAny().isPresent());
			break;
		case "BFBFBB":
			parameters = Parameter.source.get(var_0.getValue());
			if (parameters == null || parameters.isEmpty())
				return;
			setSatisfied(parameters.get(0).isRelation("1:1") && parameters.get(0).applyTarget(var_1, var_2, var_3));
			return;
		case "FBFBFF":
			parameters = Parameter.target.get(var_1.getValue());
			if (parameters == null || parameters.isEmpty())
				return;
			setSatisfied(parameters.get(0).isRelation("1:1") && parameters.get(0).applySource(var_0, var_2, var_3, title, unit));
			return;
		}
	}
}