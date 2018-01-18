package Cad2AmePiping.org.moflon.tie;

import java.io.IOException;
import org.apache.log4j.BasicConfigurator;
import org.moflon.tgg.algorithm.synchronization.SynchronizationHelper;


import org.moflon.tgg.algorithm.modelgenerator.ModelGenerator;
import org.moflon.tgg.algorithm.modelgenerator.controller.*;

import Cad2AmePiping.Cad2AmePipingPackage;


public class Cad2AmePipingModelGen extends SynchronizationHelper{

   public Cad2AmePipingModelGen()
   {
      super(Cad2AmePipingPackage.eINSTANCE, ".");
   }
	
	public static void main(String[] args) throws IOException {
		// Set up logging
		BasicConfigurator.configure();

		AbstractModelGenerationController controller = new DefaultModelGenController();
		controller.addContinuationController(new MaxRulePerformCounterController(20));
      	controller.addContinuationController(new TimeoutController(5000));
      	controller.setRuleSelector(new LimitedRandomRuleSelector().addRuleLimit("<enter rule name>", 1));

		ModelGenerator gen = new ModelGenerator(Cad2AmePipingPackage.eINSTANCE, controller);
		gen.generate();
	}
}