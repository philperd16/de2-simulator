package hardware_model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Steps {

	Comparator<OperationElement> comparator;
	List<OperationElement> steps;
	
	public Steps(Comparator<OperationElement> comparator) {
		this.comparator = comparator;
		this.steps = new ArrayList<OperationElement>();
	}

	public Collection<OperationElement> getListedSteps() {
		return steps;
	}

	public void add(OperationElement element) {
		if ( steps.size() == 0 ){
			steps.add(element);
		}
		else{
			for ( int i=0; i<steps.size()+1; ){
				if ( i == steps.size() ){
					steps.add(element);
					break;
				}
				else if ( element.getPrecedence()%2 != 0 && comparator.compare(steps.get(i), element) <= 0 ){
					i++;
					continue;
				}
				steps.add(i, element);
				break;
			}
		}
	}
	
	@Override
	public String toString(){
		return steps.toString();
	}

}
