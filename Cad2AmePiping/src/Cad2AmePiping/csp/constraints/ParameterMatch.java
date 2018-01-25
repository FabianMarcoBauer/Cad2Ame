package Cad2AmePiping.csp.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moflon.tgg.language.csp.Variable;
import org.moflon.tgg.language.csp.impl.TGGConstraintImpl;

public class ParameterMatch extends TGGConstraintImpl {
	private static class Parameter {
		private final String sourceId;
		private final String targetName;
		private final double multiplier;

		public Parameter(String sourceId, String targetName, double multiplier) {
			super();
			this.sourceId = sourceId;
			this.targetName = targetName;
			this.multiplier = multiplier;

			source.computeIfAbsent(sourceId, e -> new ArrayList<>()).add(this);
			target.computeIfAbsent(targetName, e -> new ArrayList<>()).add(this);
		}
	}

	private static final Map<String, List<Parameter>> source = new HashMap<>();
	private static final Map<String, List<Parameter>> target = new HashMap<>();

	static {
		new Parameter("h_dia_a", "diam", 1000);
		new Parameter("h_dia_b", "diam", 1000);
		new Parameter("h_dia_a", "diamc", 1000);
		new Parameter("h_dia_b", "diamc", 1000);
		new Parameter("h_dia_c", "diams", 1000);
		new Parameter("curv_rad", "rc", 1000);
		new Parameter("cent_ang", "delta", 1);
		new Parameter("len", "le", 1);

	}

	public void solve(Variable var_0, Variable var_1, Variable var_2, Variable var_3) {
		String bindingStates = getBindingStates(var_0, var_1, var_2, var_3);
		List<Parameter> parameters;
		switch (bindingStates) {
		case "BBBB":
			parameters = source.get(var_0.getValue());
			if (parameters == null)
				return;
			setSatisfied(source.get(var_0.getValue()).stream().filter(p -> p.sourceId.equals(var_0.getValue()))
					.filter(p -> p.targetName.equals(var_1.getValue()))
					.filter(p->{
						System.out.println(Double.parseDouble((String) var_2.getValue()) * p.multiplier+" "+Double.parseDouble((String) var_3.getValue()));
						System.out.println(Math.abs(Double.parseDouble((String) var_2.getValue()) * p.multiplier
								- Double.parseDouble((String) var_3.getValue())) < 0.1);
						return true;
					})
					.filter(p -> Math.abs(Double.parseDouble((String) var_2.getValue()) * p.multiplier
							- Double.parseDouble((String) var_3.getValue())) < 0.1)
					.findAny().isPresent());
			break;
		case "BFBF":
			parameters = source.get(var_0.getValue());
			if (parameters == null || parameters.isEmpty())
				return;
			Parameter p = parameters.get(0);
			var_1.bindToValue(p.targetName);
			var_3.bindToValue(String.valueOf(Double.parseDouble((String) var_2.getValue()) * p.multiplier));
			setSatisfied(true);
			return;
		case "FBFB":
			parameters = target.get(var_1.getValue());
			if (parameters == null || parameters.isEmpty())
				return;
			p = parameters.get(0);
			var_0.bindToValue(p.sourceId);
			var_2.bindToValue(String.valueOf(Double.parseDouble((String) var_3.getValue()) / p.multiplier));
			setSatisfied(true);
			return;
		}
	}
}