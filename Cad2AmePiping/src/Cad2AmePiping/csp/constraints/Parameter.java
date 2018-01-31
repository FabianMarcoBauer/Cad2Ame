package Cad2AmePiping.csp.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moflon.tgg.language.csp.Variable;

class Parameter {
	public static final Map<String, List<Parameter>> source = new HashMap<>();
	public static final Map<String, List<Parameter>> target = new HashMap<>();

	static {
		new Parameter("h_dia_a", "diam", 1000, "Hydraulic diameter at port A", "m", "2:1");
		new Parameter("h_dia_b", "diam", 1000, "Hydraulic diameter at port B", "m", "2:1");
		new Parameter("h_dia_a", "diamc", 1000, "Hydraulic diameter at port A", "m", "2:1");
		new Parameter("h_dia_b", "diamc", 1000, "Hydraulic diameter at port B", "m", "2:1");
		new Parameter("h_dia_c", "diams", 1000, "Hydraulic diameter at port C", "m", "1:1");
		new Parameter("curv_rad", "rc", 1000, "Curvature radius", "m", "1:1");
		new Parameter("cent_ang", "delta", 1, "Center angle", "degree", "1:1");
		new Parameter("len", "le", 1, "Length", "m", "1:1");
	}

	public final String sourceId;
	public final String targetName;
	public final double multiplier;
	public final String sourceTitle;
	public final String sourceUnit;
	public final String relation;

	public Parameter(String sourceId, String targetName, double multiplier, String sourceTitle, String sourceUnit,
			String relation) {
		super();
		this.sourceId = sourceId;
		this.targetName = targetName;
		this.multiplier = multiplier;
		this.sourceTitle = sourceTitle;
		this.sourceUnit = sourceUnit;
		this.relation = relation;

		source.computeIfAbsent(sourceId, e -> new ArrayList<>()).add(this);
		target.computeIfAbsent(targetName, e -> new ArrayList<>()).add(this);
	}

	public boolean applyTarget(Variable name, Variable sourceValue, Variable targetValue) {
		name.bindToValue(targetName);
		targetValue.bindToValue(String.valueOf(Double.parseDouble((String) sourceValue.getValue()) * multiplier));
		return true;
	}

	public boolean applySource(Variable id, Variable sourceValue, Variable targetValue, Variable title, Variable unit) {
		id.bindToValue(sourceId);
		sourceValue.bindToValue(String.valueOf(Double.parseDouble((String) targetValue.getValue()) / multiplier));
		title.bindToValue(sourceTitle);
		unit.bindToValue(sourceUnit);
		return true;
	}

	public boolean check(Variable sourceId, Variable targetName, Variable sourceValue, Variable targetValue,
			Variable sourceTitle, Variable sourceUnit, String relation) {
		if (!this.relation.equals(relation))
			return false;
		if (!this.sourceId.equals(sourceId.getValue()))
			return false;
		if (!this.targetName.equals(targetName.getValue()))
			return false;
		if (!this.sourceTitle.equals(sourceTitle.getValue()))
			return false;
		if (!this.sourceUnit.equals(sourceUnit.getValue()))
			return false;
		double expectedTarget = Double.parseDouble((String) sourceValue.getValue()) * multiplier;
		double target = Double.parseDouble((String) targetValue.getValue());
		if (!(Math.abs(expectedTarget - target) < 0.1))
			return false;
		return true;
	}
	
	public boolean isRelation(String type) {
		return this.relation.equals(type);
	}
}