package Cad2AmePiping.csp.constraints;

import java.util.List;
import java.util.Optional;

import org.moflon.tgg.language.csp.Variable;
import org.moflon.tgg.language.csp.impl.TGGConstraintImpl;

public class ParameterMatch21 extends TGGConstraintImpl {
	public void solve(Variable id1, Variable value1, Variable title1, Variable unit1, Variable id2, Variable value2,
			Variable title2, Variable unit2, Variable name, Variable targetValue) {
		String bindingStates = getBindingStates(id1, value1, title1, unit1, id2, value2, title2, unit2, name,
				targetValue);

		List<Parameter> parameters;
		switch (bindingStates) {
		case "BBBBBBBBBB":
			parameters = Parameter.target.get(name.getValue());
			if (parameters == null)
				return;
			Optional<Parameter> p1 = parameters.stream()
					.filter(p -> p.check(id1, name, value1, targetValue, title1, unit1, "2:1")).findAny();
			Optional<Parameter> p2 = parameters.stream()
					.filter(p -> p.check(id2, name, value2, targetValue, title2, unit2, "2:1")).findAny();
			if (!p1.isPresent() || !p2.isPresent())
				setSatisfied(false);
			else
				setSatisfied(p1.get() != p2.get());
			break;
		case "FFFFFFFFBB":
			parameters = Parameter.target.get(name.getValue());
			if (parameters == null || parameters.size()<2)
				return;
			Parameter par1 = parameters.get(0);
			Parameter par2 = parameters.get(1);
			setSatisfied(par1.applySource(id1, value1, targetValue, title1, unit1) && par2.applySource(id2, value2, targetValue, title2, unit2));
			break;
		case "BBBBBBBBFF":
			parameters = Parameter.source.get(id1.getValue());
			if (parameters == null || parameters.isEmpty())
				return;
			List<Parameter> parameters2 = Parameter.source.get(id2.getValue());
			if (parameters2 == null || parameters2.isEmpty())
				return;
			Optional<Parameter> findAny = parameters.stream().filter(p->"2:1".equals(p.relation)).filter(p->parameters2.stream().anyMatch(p22->p.targetName.equals(p22.targetName))).findAny();
			if (findAny.isPresent()) {
				setSatisfied(findAny.get().applyTarget(name, value1, targetValue));
			}
			return;
		default:
			throw new UnsupportedOperationException(
					"This case in the constraint has not been implemented yet: " + bindingStates);
		}

	}
}