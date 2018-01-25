package Cad2AmePiping.org.moflon.tie;

import java.io.IOException;
import org.apache.log4j.BasicConfigurator;
import org.moflon.tgg.algorithm.synchronization.SynchronizationHelper;

import CAD.CADContainer;
import Cad2AmePiping.Cad2AmePipingPackage;


public class Cad2AmePipingConsistencyCheck2 extends SynchronizationHelper{

   public Cad2AmePipingConsistencyCheck2()
   {
      super(Cad2AmePipingPackage.eINSTANCE, ".");
   }
	public static void main(String[] args) throws IOException {
		// Set up logging
        BasicConfigurator.configure();
        
        Cad2AmePipingConsistencyCheck2 helper = new Cad2AmePipingConsistencyCheck2();
        helper.loadSrc("instances/cc_result/src.xmi");
		helper.loadTrg("instances/cc_result/trg.xmi");
		System.out.println(((CADContainer)helper.src).getPrimitives().get(0).getParameters().get(0).getValue().getValue());
		((CADContainer)helper.src).getPrimitives().get(0).getParameters().get(0).getValue().setValue("-1000");
		System.out.println(((CADContainer)helper.src).getPrimitives().get(0).getParameters().get(0).getValue().getValue());

		boolean prepareDeltas = true;
		helper.createCorrespondences(prepareDeltas);
		
		if(prepareDeltas){
			//src and trg models are modified when preparing deltas.
			//save all files in a separate location
			helper.saveSrc("instances/cc_result2/src.xmi");
			helper.saveTrg("instances/cc_result2/trg.xmi");
			helper.saveCorr("instances/cc_result2/corr.xmi");
			helper.saveConsistencyCheckProtocol("instances/cc_result2/protocol.xmi");
			helper.saveInconsistentSourceDelta("instances/cc_result2/src.delta.xmi");
			helper.saveInconsistentTargetDelta("instances/cc_result2/trg.delta.xmi");
		}
		else{
			//src and trg models are not modified.
			//save correspondence model and protocol only
			helper.saveCorr("instances/corr.xmi");
			helper.saveConsistencyCheckProtocol("instances/protocol.xmi");
		}
	}
}