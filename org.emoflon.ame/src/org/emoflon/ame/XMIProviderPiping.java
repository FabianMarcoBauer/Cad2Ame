package org.emoflon.ame;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.emoflon.ame.language.AMEModel;

import com.google.inject.Injector;

public class XMIProviderPiping {

	public static void main(String[] args) throws IOException {
		new org.eclipse.emf.mwe.utils.StandaloneSetup().setPlatformUri("../");
		Injector injector = new LanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		Resource s = resourceSet.getResource(
		    URI.createURI("platform:/resource/org.emoflon.ame/instances/piping_demo.py"), true);
		AMEModel model = (AMEModel) s.getContents().get(0);
		
		Resource xmiResource = new XMIResourceImpl(URI.createFileURI("instances/AMEmodelPiping.xmi"));
		xmiResource.getContents().add(model);
		xmiResource.save(null);
	}

}
