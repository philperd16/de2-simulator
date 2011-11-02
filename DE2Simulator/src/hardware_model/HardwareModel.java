package hardware_model;

import java.util.HashSet;
import java.util.Set;

public class HardwareModel {

	Set<ModuleDefinition> modules;
	
	public HardwareModel(){
		modules = new HashSet<ModuleDefinition>();
	}
	
	public Set<ModuleDefinition> getDefinedModules() {
		return modules;
	}

	public void defineModule(ModuleDefinition createdModule) {
		modules.add(createdModule);
	}

}
